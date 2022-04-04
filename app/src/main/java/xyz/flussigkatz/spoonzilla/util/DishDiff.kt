package xyz.flussigkatz.spoonzilla.util

import androidx.recyclerview.widget.DiffUtil
import xyz.flussigkatz.core_api.entity.Dish

class DishDiff(
    private var oldList: List<Dish>,
    private val newList: List<Dish>
) : DiffUtil.Callback() {


    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].title == newList[newItemPosition].title &&
                oldList[oldItemPosition].image == newList[newItemPosition].image &&
                oldList[oldItemPosition].mark == newList[newItemPosition].mark
    }
}