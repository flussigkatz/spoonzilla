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
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.core_api.entity.DishMarked
import xyz.flussigkatz.spoonzilla.databinding.FragmentMarkedBinding
import xyz.flussigkatz.spoonzilla.util.AppConst
import xyz.flussigkatz.spoonzilla.util.AppConst.PADDING_DP
import xyz.flussigkatz.spoonzilla.util.AutoDisposable
import xyz.flussigkatz.spoonzilla.util.Converter
import xyz.flussigkatz.spoonzilla.util.addTo
import xyz.flussigkatz.spoonzilla.view.MainActivity
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DishRecyclerAdapter
import xyz.flussigkatz.spoonzilla.view.rv_adapter.rv_decoration.SpacingItemDecoration
import xyz.flussigkatz.spoonzilla.viewmodel.MarkedFragmentViewModel
import java.util.concurrent.TimeUnit

class MarkedFragment : Fragment() {
    private val viewModel: MarkedFragmentViewModel by activityViewModels()
    private lateinit var binding: FragmentMarkedBinding
    private lateinit var dishAdapter: DishRecyclerAdapter
    private val autoDisposable = AutoDisposable()
    private val markedFragmentScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        autoDisposable.bindTo(lifecycle)
        binding = FragmentMarkedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getMarkedDishes(viewModel.getMarkedDishesFromDb())
        initDishAdapter()
        initMarkedSearch()
    }

    private fun getMarkedDishes(observable: Observable<List<DishMarked>>) {
        observable.subscribeOn(Schedulers.io())
            .filter { !it.isNullOrEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { dishAdapter.updateData(it.map { list -> Converter.convertDishMarkedToDish(list) }) },
                { Timber.d(it, "getMarkedDishes onError") }
            ).addTo(autoDisposable)
    }

    private fun initDishAdapter() {
        val mLayoutManager = LinearLayoutManager(context)
        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy != IS_SCROLL_FLAG) {
                    (requireActivity() as MainActivity).apply{
                        hideBottomSheet()
                        mainSearchViewClearFocus()
                    }
                }
            }
        }
        val clickListener = object : DishRecyclerAdapter.OnItemClickListener {
            override fun click(dishId: Int) {
                val intent = Intent().apply {
                    action = AppConst.NAVIGATE_TO_DETAILS
                    val bundle = Bundle().apply { putInt(AppConst.KEY_DISH_ID, dishId) }
                    putExtra(AppConst.KEY_DISH_ID, bundle)
                }
                requireActivity().sendBroadcast(intent)
            }
        }
        val checkedChangeListener = object : DishRecyclerAdapter.OnCheckedChangeListener {
            override fun checkedChange(dish: Dish, isChecked: Boolean) {
                if (dish.mark != isChecked) {
                    dish.mark = isChecked
                    markedFragmentScope.launch { viewModel.setDishMark(dish) }
                }
            }
        }
        binding.markedRecycler.apply {
            dishAdapter = DishRecyclerAdapter(clickListener, checkedChangeListener).apply {
                stateRestorationPolicy = PREVENT_WHEN_EMPTY
            }
            layoutManager = mLayoutManager
            adapter = dishAdapter
            addOnScrollListener(scrollListener)
            addItemDecoration(SpacingItemDecoration(PADDING_DP))
        }

    }

    private fun initMarkedSearch() {
        viewModel.searchPublishSubject.subscribeOn(Schedulers.io())
            .debounce(AppConst.SEARCH_DEBOUNCE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it.isEmpty()) binding.markedRecycler.smoothScrollToPosition(FIRST_POSITION)
                    getMarkedDishes(viewModel.getMarkedDishesFromDb(it))
                },
                { Timber.d(it, "initMarkedSearch onError") }
            ).addTo(autoDisposable)
    }

    override fun onDestroy() {
        markedFragmentScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val IS_SCROLL_FLAG = 0
        private const val FIRST_POSITION = 0

    }
}