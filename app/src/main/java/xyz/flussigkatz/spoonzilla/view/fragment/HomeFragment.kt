package xyz.flussigkatz.spoonzilla.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.databinding.FragmentHomeBinding
import xyz.flussigkatz.spoonzilla.util.AppConst
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DishRecyclerAdapter
import xyz.flussigkatz.spoonzilla.view.rv_adapter.SpacingItemDecoration
import xyz.flussigkatz.spoonzilla.viewmodel.HomeFragmentViewModel
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    private lateinit var dishAdapter: DishRecyclerAdapter
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDishAdapter()

        initContent()

        initQuickSearch()

    }

    private fun initQuickSearch() {
        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        viewModel.searchPublishSubject.subscribeOn(Schedulers.io())
            .debounce(SEARCH_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
            .doOnNext { if (it.isNullOrBlank()) initContent()}
            .filter { !it.isNullOrBlank() }
            .map { viewModel.getSearchedRecipes(it!!.lowercase().trim()) }
            .flatMap { it }.observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it.isNullOrEmpty()) Toast.makeText(
                        requireContext(),
                        resources.getText(R.string.home_search_empty_message),
                        Toast.LENGTH_SHORT
                    ).show()
                    else dishAdapter.updateData(it)
                },
                { println("$TAG initSearch onError: ${it.localizedMessage}") }
            )
    }

    private fun initContent() {
        viewModel.dishList.filter { !it.isNullOrEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { dishAdapter.updateData(it) },
                { println("$TAG initContent onError: ${it.localizedMessage}") }
            )
    }

    private fun initDishAdapter() {
        binding.homeRecycler.apply {
            val clickListener = object : DishRecyclerAdapter.OnItemClickListener {
                override fun click(dishId: Int) {
                    val intent = Intent().apply {
                        action = AppConst.NAVIGATE_TO_DETAILS_ACTION
                        val bundle = Bundle().apply {
                            putString(AppConst.DISH_ID_KEY, dishId.toString())
                        }
                        putExtra(AppConst.DISH_ID_KEY, bundle)
                    }
                    requireActivity().sendBroadcast(intent)
                }
            }
            val scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy != CANCEL_FOCUS_ON_SCROLL) {
                        requireActivity().findViewById<SearchView>(R.id.main_quick_search)
                            .clearFocus()
                    }
                }
            }
            dishAdapter = DishRecyclerAdapter(clickListener)
            adapter = dishAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(scrollListener)
            addItemDecoration(SpacingItemDecoration(PADDING_DP))
        }
    }

    override fun onStart() {
        quickSearchSwitcher(true)
        super.onStart()
    }

    override fun onStop() {
        quickSearchSwitcher(false)
        super.onStop()
    }

    private fun quickSearchSwitcher(state: Boolean) {
        requireActivity().findViewById<SearchView>(R.id.main_quick_search).apply {
            visibility = if (state) View.VISIBLE
            else View.GONE
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
        private const val PADDING_DP = 2
        private const val CANCEL_FOCUS_ON_SCROLL = 0
        private const val SEARCH_DEBOUNCE_TIME_MILLISECONDS = 1000L
    }

}