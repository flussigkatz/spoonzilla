package xyz.flussigkatz.spoonzilla.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.databinding.FragmentAdvancedSearchBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_CUISINE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DIET
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_ID
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_INTOLERANCE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_MEAl_TYPE
import xyz.flussigkatz.spoonzilla.util.AppConst.NAVIGATE_TO_DETAILS_ACTION
import xyz.flussigkatz.spoonzilla.util.AppConst.PADDING_DP
import xyz.flussigkatz.spoonzilla.util.AppConst.REMAINDER_OF_ELEMENTS
import xyz.flussigkatz.spoonzilla.util.AppConst.SEARCH_DEBOUNCE_TIME_MILLISECONDS
import xyz.flussigkatz.spoonzilla.util.AutoDisposable
import xyz.flussigkatz.spoonzilla.util.addTo
import xyz.flussigkatz.spoonzilla.view.MainActivity
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DishRecyclerAdapter
import xyz.flussigkatz.spoonzilla.view.rv_adapter.SpacingItemDecoration
import xyz.flussigkatz.spoonzilla.viewmodel.AdvancedSearchFragmentViewModel
import java.util.concurrent.TimeUnit

class AdvancedSearchFragment : Fragment() {
    private val viewModel: AdvancedSearchFragmentViewModel by activityViewModels()
    private lateinit var dishAdapter: DishRecyclerAdapter
    private lateinit var binding: FragmentAdvancedSearchBinding
    private val autoDisposable = AutoDisposable()
    private var isLoadingFromApi = false
    private var mquery: String? = null
    private lateinit var keyCuisine: String
    private lateinit var keyDiet: String
    private lateinit var keyIntolerance: String
    private lateinit var keyMeatType: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdvancedSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        autoDisposable.bindTo(lifecycle)

        initDishAdapter()

        initSearch()

        initSearchSettings()

        initLoadingState()

        initContent()

    }

    private fun initLoadingState() {
        viewModel.loadingState.subscribeOn(Schedulers.io())
            .subscribe(
                { isLoadingFromApi = it },
                { println("$TAG initIsLoading onError: ${it.localizedMessage}") }
            ).addTo(autoDisposable)
    }

    private fun initSearch() {
        viewModel.searchPublishSubject.subscribeOn(Schedulers.io())
            .debounce(SEARCH_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    mquery = it
                    getSearchedRecipes()
                },
                { println("$TAG initSearch onError: ${it.localizedMessage}") }
            ).addTo(autoDisposable)
    }

    private fun initContent() {
        getSearchedRecipes()
        viewModel.dishList.subscribeOn(Schedulers.io())
            .filter { !it.isNullOrEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { dishAdapter.updateData(it) },
                { println("$TAG initContent onError: ${it.localizedMessage}") }
            ).addTo(autoDisposable)
    }

    private fun initDishAdapter() {
        val mLayoutManager = LinearLayoutManager(context)
        val clickListener = object : DishRecyclerAdapter.OnItemClickListener {
            override fun click(dishId: Int) {
                val intent = Intent().apply {
                    action = NAVIGATE_TO_DETAILS_ACTION
                    val bundle = Bundle().apply {
                        putString(KEY_DISH_ID, dishId.toString())
                    }
                    putExtra(KEY_DISH_ID, bundle)
                }
                requireActivity().sendBroadcast(intent)
            }
        }
        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy != 0) {
                    requireActivity().findViewById<SearchView>(R.id.main_quick_search)
                        .clearFocus()
                }
                if (dy > 0 && !isLoadingFromApi) {
                    paginationCheck(
                        mLayoutManager.childCount,
                        mLayoutManager.itemCount,
                        mLayoutManager.findFirstVisibleItemPosition()
                    )
                }
            }
        }
        binding.advancedSearchRecycler.apply {
            dishAdapter = DishRecyclerAdapter(clickListener)
            layoutManager = mLayoutManager
            adapter = dishAdapter
            addOnScrollListener(scrollListener)
            addItemDecoration(SpacingItemDecoration(PADDING_DP))
        }
    }

    fun paginationCheck(visibleItemCount: Int, totalItemCount: Int, pastVisibleItems: Int) {
        if (totalItemCount - (visibleItemCount + pastVisibleItems) <= REMAINDER_OF_ELEMENTS) {
            viewModel.doSearchedRecipesPagination(
                query = mquery,
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
        }
    }

    private fun getSearchedRecipes() {
            viewModel.getAdvancedSearchedRecipes(
                query = mquery,
                keyCuisine = keyCuisine,
                keyDiet = keyDiet,
                keyIntolerance = keyIntolerance,
                keyMeatType = keyMeatType
            )
            binding.advancedSearchRecycler.smoothScrollToPosition(FIRST_RECYCLER_POSITION)
    }

    override fun onStart() {
        MainActivity.searchFieldSwitcher(requireActivity(), true)
        super.onStart()
    }

    override fun onStop() {
        MainActivity.searchFieldSwitcher(requireActivity(), false)
        super.onStop()
    }

    companion object {
        private const val TAG = "AdvancedSearchFragment"
        private const val FIRST_RECYCLER_POSITION = 0
    }

}