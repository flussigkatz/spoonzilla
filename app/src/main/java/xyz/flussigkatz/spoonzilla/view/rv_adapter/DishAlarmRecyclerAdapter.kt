package xyz.flussigkatz.spoonzilla.view.rv_adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import timber.log.Timber
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.core_api.entity.DishAlarm
import xyz.flussigkatz.spoonzilla.databinding.DishAlarmItemBinding
import xyz.flussigkatz.spoonzilla.util.diffutill.DishAlarmDiff
import xyz.flussigkatz.spoonzilla.view.rv_viewholder.DishAlarmViewHolder
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class DishAlarmRecyclerAdapter(
    private val itemClickListener: OnItemClickListener,
    private val alarmClickListener: OnAlarmItemClickListener
) : RecyclerView.Adapter<DishAlarmViewHolder>() {
    private var items = listOf<DishAlarm>()
    private var darkRed: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishAlarmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        darkRed = ContextCompat.getColor(parent.context, R.color.dark_red)
        return DishAlarmViewHolder(DishAlarmItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: DishAlarmViewHolder, position: Int) {
        val dishAlarm = getDishAlarm(position)
        val binding = holder.binding
        setDishImage(dishAlarm.image, binding.dishAlarmImage)
        binding.rootDishAlarmItem.setOnClickListener {
            itemClickListener.click(dishAlarm.id)
        }
        binding.dishAlarmTime.setOnClickListener {
            alarmClickListener.click(dishAlarm)
        }
        binding.dishAlarmTitle.text = dishAlarm.title
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dishAlarm.alarmTime
        val dateFormatter = SimpleDateFormat("HH:mm | dd.MM.yyyy", Locale.getDefault())
        val alarm = dateFormatter.format(calendar.time)
        binding.dishAlarmTime.text = alarm
        if (dishAlarm.alarmTime < System.currentTimeMillis()) {
            darkRed?.let {
                binding.dishAlarmTime.setTextColor(it)
                binding.alarmIcon.setColorFilter(it)
            }
        }
    }

    override fun getItemCount() = items.size

    fun getDishAlarm(position: Int) = items[position]

    fun updateData(newList: List<DishAlarm>) {
        val sortedList = newList.sortedWith(compareBy { it.alarmTime })
        val diffResult = DiffUtil.calculateDiff(DishAlarmDiff(items, sortedList))
        items = sortedList
        diffResult.dispatchUpdatesTo(this)
    }

    private fun setDishImage(image: String?, dishView: ImageView) {
        val callbackPicasso = object : Callback {
            override fun onSuccess() {
                dishView.setBackgroundColor(Color.TRANSPARENT)
            }

            override fun onError(e: Exception?) {
                Timber.d(e, "callbackPicasso onError")
            }

        }
        Picasso.get()
            .load(image)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.ic_food)
            .error(R.drawable.ic_food)
            .into(dishView, callbackPicasso)
    }

    interface OnItemClickListener {
        fun click(dishId: Int)
    }

    interface OnAlarmItemClickListener {
        fun click(dishAlarm: DishAlarm)
    }
}