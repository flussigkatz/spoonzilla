package xyz.flussigkatz.spoonzilla.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.spoonzilla.databinding.FragmentAdvancedSearchBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.IS_SCROLL_FLAG
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_CUISINE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DIET
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_ID
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_INTOLERANCE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_MAX_READY_TIME
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_MEAl_TYPE
import xyz.flussigkatz.spoonzilla.util.AppConst.NAVIGATE_TO_DETAILS
import xyz.flussigkatz.spoonzilla.util.AppConst.PADDING_DP
import xyz.flussigkatz.spoonzilla.util.AppConst.REMAINDER_OF_ELEMENTS
import xyz.flussigkatz.spoonzilla.util.AppConst.SEARCH_DEBOUNCE_TIME_MILLISECONDS
import xyz.flussigkatz.spoonzilla.util.AutoDisposable
import xyz.flussigkatz.spoonzilla.util.addTo
import xyz.flussigkatz.spoonzilla.view.MainActivity
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DishRecyclerAdapter
import xyz.flussigkatz.spoonzilla.view.rv_adapter.rv_decoration.SpacingItemDecoration
import xyz.flussigkatz.spoonzilla.viewmodel.AdvancedSearchFragmentViewModel
import java.util.concurrent.TimeUnit

class AdvancedSearchFragment : Fragment() {
    private val viewModel: AdvancedSearchFragmentViewModel by activityViewModels()
    private lateinit var dishAdapter: DishRecyclerAdapter
    private lateinit var binding: FragmentAdvancedSearchBinding
    private val autoDisposable = AutoDisposable()
    private var isLoadingFromApi = false
    private var mQuery: String? = null
    private val advancedSearchFragmentScope = CoroutineScope(Dispatchers.IO)
    private lateinit var keyCuisine: String
    private lateinit var keyDiet: String
    private lateinit var keyIntolerance: String
    private lateinit var keyMeatType: String
    private var maxReadyTime: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        autoDisposable.bindTo(lifecycle)
        binding = FragmentAdvancedSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDishAdapter()
        initSearch()
        initSearchSettings()
        initLoadingState()
        initContent()
        initRefreshLayout()
    }

    private fun initLoadingState() {
        viewModel.loadingState.subscribeOn(Schedulers.io())
            .subscribe(
                { isLoadingFromApi = it },
                { Timber.d(it, "initIsLoading onError") }
            ).addTo(autoDisposable)
    }

    private fun initRefreshLayout() {
        binding.advancedSearchRefreshLayout.setOnRefreshListener {
            (requireActivity() as MainActivity).mainSearchViewClearFocus()
            getSearchedRecipes()
            viewModel.loadingState.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { binding.advancedSearchRefreshLayout.isRefreshing = it },
                    { Timber.d(it, "initRefreshLayout onError") }
                ).addTo(autoDisposable)
        }
    }

    private fun initSearch() {
        viewModel.searchPublishSubject.subscribeOn(Schedulers.io())
            .debounce(SEARCH_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    mQuery = it
                    getSearchedRecipes()
                },
                { Timber.d(it, "initSearch onError") }
            ).addTo(autoDisposable)
    }

    private fun initContent() {
        getSearchedRecipes()
        viewModel.dishList.subscribeOn(Schedulers.io())
            .filter { !it.isNullOrEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { dishAdapter.updateData(it) },
                { Timber.d(it, "initContent onError") }
            ).addTo(autoDisposable)
    }

    private fun initDishAdapter() {
        val mLayoutManager = LinearLayoutManager(context)
        val clickListener = object : DishRecyclerAdapter.OnItemClickListener {
            override fun click(dishId: Int) {
                val intent = Intent().apply {
                    action = NAVIGATE_TO_DETAILS
                    val bundle = Bundle().apply { putInt(KEY_DISH_ID, dishId) }
                    putExtra(KEY_DISH_ID, bundle)
                }
                requireActivity().sendBroadcast(intent)
            }
        }
        val checkedChangeListener = object : DishRecyclerAdapter.OnCheckedChangeListener {
            override fun checkedChange(dish: Dish, isChecked: Boolean) {
                if (dish.mark != isChecked) {
                    dish.mark = isChecked
                    advancedSearchFragmentScope.launch { viewModel.setDishMark(dish) }
                }
            }
        }
        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy != IS_SCROLL_FLAG) {
                    (requireActivity() as MainActivity).apply {
                        hideBottomSheet()
                        mainSearchViewClearFocus()
                    }
                }
                if (dy > IS_SCROLL_FLAG && !isLoadingFromApi) paginationCheck(
                    mLayoutManager.childCount,
                    mLayoutManager.itemCount,
                    mLayoutManager.findFirstVisibleItemPosition()
                )
            }
        }
        binding.advancedSearchRecycler.apply {
            dishAdapter = DishRecyclerAdapter(clickListener, checkedChangeListener).apply {
                stateRestorationPolicy = PREVENT_WHEN_EMPTY
            }
            layoutManager = mLayoutManager
            adapter = dishAdapter
            addOnScrollListener(scrollListener)
            addItemDecoration(SpacingItemDecoration(PADDING_DP))
        }
    }

    fun paginationCheck(visibleItemCount: Int, totalItemCount: Int, pastVisibleItems: Int) {
        if (totalItemCount - (visibleItemCount + pastVisibleItems) <= REMAINDER_OF_ELEMENTS) {
            viewModel.doSearchedRecipesPagination(
                query = mQuery,
                maxReadyTime = maxReadyTime,
                offset = totalItemCount,
                keyCuisine = keyCuisine,
                keyDiet = keyDiet,
                keyIntolerance = keyIntolerance,
                keyMeatType = keyMeatType
            )
        }
    }

    private fun initSearchSettings() {
        arguments?.apply {
            keyCuisine = getString(KEY_CUISINE) ?: KEY_CUISINE
            keyDiet = getString(KEY_DIET) ?: KEY_DIET
            keyIntolerance = getString(KEY_INTOLERANCE) ?: KEY_INTOLERANCE
            keyMeatType = getString(KEY_MEAl_TYPE) ?: KEY_MEAl_TYPE
            getInt(KEY_MAX_READY_TIME).let { maxReadyTime = if (it != 0) it else null }
        }
    }

    private fun getSearchedRecipes() {
        viewModel.getAdvancedSearchedRecipes(
            query = mQuery,
            maxReadyTime = maxReadyTime,
            keyCuisine = keyCuisine,
            keyDiet = keyDiet,
            keyIntolerance = keyIntolerance,
            keyMeatType = keyMeatType
        )
        binding.advancedSearchRecycler.smoothScrollToPosition(FIRST_POSITION)
    }

    override fun onDestroy() {
        advancedSearchFragmentScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val FIRST_POSITION = 0
    }

}