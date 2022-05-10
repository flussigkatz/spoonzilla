package xyz.flussigkatz.spoonzilla.util.diffutill

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import xyz.flussigkatz.core_api.entity.ingredients.IngredientItem
import xyz.flussigkatz.spoonzilla.util.AppConst.INGREDIENT_VALUE_KEY

class IngredientDiff(
    private var oldList: List<IngredientItem>,
    private val newList: List<IngredientItem>
) : DiffUtil.Callback() {

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return if (oldItem.value != newItem.value) {
            newItem.value?.let {
                Bundle().apply { putDouble(INGREDIENT_VALUE_KEY, it) }
            }
        } else super.getChangePayload(oldItemPosition, newItemPosition)
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].value == newList[newItemPosition].value
    }
}