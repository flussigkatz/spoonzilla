package xyz.flussigkatz.spoonzilla.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.adapters.AdapterViewBindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DishRecyclerAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.AdvancedSearchFragmentViewModel

class AdvancedSearchFragment : Fragment() {
    private lateinit var dishAdapter: DishRecyclerAdapter
    private lateinit var binding: AdapterViewBindingAdapter
    private val viewModel: AdvancedSearchFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_advanced_search, container, false)
    }

    private fun initSearch() {
    }

    private fun initContent() {
    }

   /* private fun initDishAdapter() {
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
                        requireActivity().findViewById<SearchView>(R.id.main_search).clearFocus()
                    }
                }
            }
            dishAdapter = DishRecyclerAdapter(clickListener)
            adapter = dishAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(scrollListener)
            addItemDecoration(SpacingItemDecoration(PADDING_DP))
        }
    }*/

    companion object {
        private const val TAG = "AdvancedSearchFragment"
        private const val PADDING_DP = 2
    }

}