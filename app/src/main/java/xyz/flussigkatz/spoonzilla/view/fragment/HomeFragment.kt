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
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.databinding.FragmentHomeBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_ID
import xyz.flussigkatz.spoonzilla.util.AppConst.NAVIGATE_TO_DETAILS_ACTION
import xyz.flussigkatz.spoonzilla.util.AppConst.PADDING_DP
import xyz.flussigkatz.spoonzilla.util.AppConst.REMAINDER_OF_ELEMENTS
import xyz.flussigkatz.spoonzilla.util.AppConst.SEARCH_DEBOUNCE_TIME_MILLISECONDS
import xyz.flussigkatz.spoonzilla.util.AutoDisposable
import xyz.flussigkatz.spoonzilla.util.addTo
import xyz.flussigkatz.spoonzilla.view.MainActivity
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DishRecyclerAdapter
import xyz.flussigkatz.spoonzilla.view.rv_adapter.SpacingItemDecoration
import xyz.flussigkatz.spoonzilla.viewmodel.HomeFragmentViewModel
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    private lateinit var dishAdapter: DishRecyclerAdapter
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeFragmentViewModel by activityViewModels()
    private val autoDisposable = AutoDisposable()
    private var isLoadingFromApi = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        autoDisposable.bindTo(lifecycle)

        initDishAdapter()

        initContent()

        initQuickSearch()

        initRefreshLayout()

        initLoadingState()

    }

    private fun initLoadingState() {
        viewModel.loadingState.subscribeOn(Schedulers.io())
            .subscribe(
                { isLoadingFromApi = it },
                { println("$TAG initIsLoading onError: ${it.localizedMessage}") }
            ).addTo(autoDisposable)
    }

    private fun initRefreshLayout() {
        binding.homeRefreshLayout.setOnRefreshListener {
            requireActivity().findViewById<SearchView>(R.id.main_quick_search).apply {
                setQuery(null, false)
                clearFocus()
            }
            viewModel.getRandomRecipe()
            viewModel.loadingState.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { binding.homeRefreshLayout.isRefreshing = it },
                    { println("$TAG initRefreshLayout onError: ${it.localizedMessage}") }
                ).addTo(autoDisposable)
        }
    }

    private fun initQuickSearch() {
        viewModel.searchPublishSubject.subscribeOn(Schedulers.io())
            .debounce(SEARCH_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { getSearchedRecipes(it) },
                { println("$TAG initQuickSearch onError: ${it.localizedMessage}") }
            ).addTo(autoDisposable)
    }

    private fun initContent() {
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
                    val bundle = Bundle().apply { putInt(KEY_DISH_ID, dishId) }
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
        binding.homeRecycler.apply {
            dishAdapter = DishRecyclerAdapter(clickListener).apply {
                stateRestorationPolicy = PREVENT_WHEN_EMPTY
            }
            layoutManager = mLayoutManager
            adapter = dishAdapter
            addOnScrollListener(scrollListener)
            addItemDecoration(SpacingItemDecoration(PADDING_DP))
        }

    }

    private fun getSearchedRecipes(query: String?) {
        if (lifecycle.currentState == RESUMED) {
            if (!query.isNullOrBlank())
                viewModel.getSearchedRecipes(query)
            else viewModel.getRandomRecipe()
        }
    }

    override fun onStart() {
        MainActivity.searchFieldSwitcher(requireActivity(), true)
        super.onStart()
    }

    override fun onStop() {
        MainActivity.searchFieldSwitcher(requireActivity(), false)
        super.onStop()
    }

    fun paginationCheck(visibleItemCount: Int, totalItemCount: Int, pastVisibleItems: Int) {
        if (totalItemCount - (visibleItemCount + pastVisibleItems) <= REMAINDER_OF_ELEMENTS) {
            val mainSearchQuery = requireActivity()
                .findViewById<SearchView>(R.id.main_quick_search)
                .query
            if (mainSearchQuery.isNullOrBlank()) viewModel.doRandomRecipePagination()
            else viewModel.doSearchedRecipesPagination(mainSearchQuery.toString(), totalItemCount)
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }

}