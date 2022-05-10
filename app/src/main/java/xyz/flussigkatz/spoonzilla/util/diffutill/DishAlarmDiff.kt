package xyz.flussigkatz.spoonzilla.util.diffutill

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import xyz.flussigkatz.core_api.entity.DishAlarm
import xyz.flussigkatz.spoonzilla.util.AppConst.ALARM_TIME_VALUE_KEY

class DishAlarmDiff(
    private var oldList: List<DishAlarm>,
    private val newList: List<DishAlarm>
) : DiffUtil.Callback() {

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return if (oldItem.alarmTime != newItem.alarmTime) {
                Bundle().apply { putLong(ALARM_TIME_VALUE_KEY, newItem.alarmTime) }
        } else super.getChangePayload(oldItemPosition, newItemPosition)
    }


    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].localId == newList[newItemPosition].localId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id &&
                oldList[oldItemPosition].title == newList[newItemPosition].title &&
                oldList[oldItemPosition].image == newList[newItemPosition].image &&
                oldList[oldItemPosition].alarmTime == newList[newItemPosition].alarmTime
    }
}