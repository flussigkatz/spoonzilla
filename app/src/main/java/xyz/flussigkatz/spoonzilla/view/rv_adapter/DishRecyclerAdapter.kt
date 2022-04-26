package xyz.flussigkatz.spoonzilla.view.rv_adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import timber.log.Timber
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.spoonzilla.databinding.DishItemBinding
import xyz.flussigkatz.spoonzilla.util.diffutill.DishDiff
import xyz.flussigkatz.spoonzilla.view.rv_viewholder.DishViewHolder
import java.lang.Exception

class DishRecyclerAdapter(
    private val clickListener: OnItemClickListener,
    private val checkedChangeListener: OnCheckedChangeListener
) : RecyclerView.Adapter<DishViewHolder>() {
    private var items = listOf<Dish>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DishViewHolder(DishItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val dish = items[position]
        val binding = holder.binding
        binding.dish = dish
        setDishImage(dish.image, binding.dishImage)
        binding.rootDishItem.setOnClickListener {
            dish.id.let { id -> clickListener.click(id) }
        }
        binding.dishMarkCheckBox.setOnCheckedChangeListener { _, isChecked ->
            checkedChangeListener.checkedChange(dish, isChecked)
        }
        binding.dishMarkCheckBox.apply {
            isChecked = dish.mark
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newList: List<Dish>) {
        val diffResult = DiffUtil.calculateDiff(DishDiff(items, newList))
        items = newList
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

    interface OnCheckedChangeListener {
        fun checkedChange(dish: Dish, isChecked: Boolean)
    }
}