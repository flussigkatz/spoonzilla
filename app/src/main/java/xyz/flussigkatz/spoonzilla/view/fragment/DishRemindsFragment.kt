package xyz.flussigkatz.spoonzilla.view.fragment

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.DishAlarm
import xyz.flussigkatz.spoonzilla.view.notification.NotificationHelper
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.databinding.FragmentDishRemindsBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.IS_SCROLL_FLAG
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_ID
import xyz.flussigkatz.spoonzilla.util.AppConst.NAVIGATE_TO_DETAILS
import xyz.flussigkatz.spoonzilla.util.AppConst.PADDING_DP
import xyz.flussigkatz.spoonzilla.util.AutoDisposable
import xyz.flussigkatz.spoonzilla.util.addTo
import xyz.flussigkatz.spoonzilla.view.MainActivity
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DishAlarmRecyclerAdapter
import xyz.flussigkatz.spoonzilla.view.rv_adapter.rv_decoration.SpacingItemDecoration
import xyz.flussigkatz.spoonzilla.viewmodel.DishAlarmFragmentViewModel
import java.util.*
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE

class DishRemindsFragment : Fragment() {
    private val viewModel: DishAlarmFragmentViewModel by activityViewModels()
    private lateinit var binding: FragmentDishRemindsBinding
    private val autoDisposable = AutoDisposable()
    private val dishRemindsScope = CoroutineScope(Dispatchers.IO)
    private lateinit var mAdapter: DishAlarmRecyclerAdapter
    private val mCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        autoDisposable.bindTo(lifecycle)
        binding = FragmentDishRemindsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initContent()
    }

    private fun getTimePicker(dishAlarm: DishAlarm): MaterialTimePicker {
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dishAlarm.alarmTime
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(calendar.get(HOUR_OF_DAY))
            .setMinute(calendar.get(MINUTE))
            .build()
        timePicker.addOnPositiveButtonClickListener {
            mCalendar.set(HOUR_OF_DAY, timePicker.hour)
            mCalendar.set(MINUTE, timePicker.minute)
            if (mCalendar.timeInMillis > System.currentTimeMillis()) {
                dishRemindsScope.launch {
                    val dishAlarmCopy = DishAlarm(
                        localId = dishAlarm.localId,
                        id = dishAlarm.id,
                        title = dishAlarm.title,
                        image = dishAlarm.image,
                        alarmTime = mCalendar.timeInMillis,
                    )
                    viewModel.updateDishRemind(dishAlarmCopy)
                    return@launch
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getText(R.string.time_picker_text_past_time),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return timePicker
    }

    private fun getDatePicker(dishAlarm: DishAlarm): MaterialDatePicker<Long> {
        val calendarConstraints = CalendarConstraints.Builder()
        calendarConstraints.setValidator(DateValidatorPointForward.now())
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(calendarConstraints.build())
            .setSelection(dishAlarm.alarmTime)
            .build()
        datePicker.addOnPositiveButtonClickListener {
            mCalendar.timeInMillis = it
            getTimePicker(dishAlarm).show(parentFragmentManager, TIME_PICKER_TAG)
        }
        return datePicker
    }

    private fun initContent() {
        viewModel.dishAlarmsList.subscribeOn(Schedulers.io())
            .filter { !it.isNullOrEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { mAdapter.updateData(it) },
                { Timber.d(it, "initContent onError") }
            ).addTo(autoDisposable)
    }

    private fun initAdapter() {
        val mLayoutManager = LinearLayoutManager(context)
        val itemClickListener = object : DishAlarmRecyclerAdapter.OnItemClickListener {
            override fun click(dishId: Int) {
                val intent = Intent().apply {
                    action = NAVIGATE_TO_DETAILS
                    val bundle = Bundle().apply { putInt(KEY_DISH_ID, dishId) }
                    putExtra(KEY_DISH_ID, bundle)
                }
                requireActivity().sendBroadcast(intent)
            }
        }
        val alarmClickListener = object : DishAlarmRecyclerAdapter.OnAlarmItemClickListener {
            override fun click(dishAlarm: DishAlarm) {
                getDatePicker(dishAlarm).show(parentFragmentManager, DATE_PICKER_TAG)
            }
        }
        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy != IS_SCROLL_FLAG) {
                    (requireActivity() as MainActivity).apply {
                        mainSearchViewClearFocus()
                        hideBottomSheet()
                    }
                }
            }
        }
        binding.dishRemindsRecycler.apply {
            mAdapter = DishAlarmRecyclerAdapter(itemClickListener, alarmClickListener).apply {
                stateRestorationPolicy = PREVENT_WHEN_EMPTY
            }
            val swipeToDelete = ItemTouchHelper(SwipeToDeleteItemTouchHelper(mAdapter))
            swipeToDelete.attachToRecyclerView(this)
            layoutManager = mLayoutManager
            adapter = mAdapter
            addOnScrollListener(scrollListener)
            addItemDecoration(SpacingItemDecoration(PADDING_DP))
        }
    }

    override fun onDestroy() {
        dishRemindsScope.cancel()
        super.onDestroy()
    }

    inner class SwipeToDeleteItemTouchHelper(val adapter: DishAlarmRecyclerAdapter) :
        ItemTouchHelper.Callback() {

        override fun isLongPressDragEnabled() = false

        override fun isItemViewSwipeEnabled() = true

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags = ItemTouchHelper.ACTION_STATE_IDLE
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            dishRemindsScope.launch {
                val dishAlarm = adapter.getDishAlarm(viewHolder.absoluteAdapterPosition)
                NotificationHelper.cancelDishRemind(
                    requireContext(),
                    dishAlarm.localId
                )
                viewModel.deleteDishAlarmFromDb(dishAlarm.localId)
            }
        }
    }

    companion object {
        private const val DATE_PICKER_TAG = "datePicker"
        private const val TIME_PICKER_TAG = "timePicker"
    }
}