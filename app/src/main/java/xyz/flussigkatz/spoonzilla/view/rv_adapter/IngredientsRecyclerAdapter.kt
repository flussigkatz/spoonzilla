package xyz.flussigkatz.spoonzilla.view.rv_adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import xyz.flussigkatz.core_api.entity.ingredients.IngredientItem
import xyz.flussigkatz.spoonzilla.databinding.IngredientItemBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.INGREDIENT_VALUE_KEY
import xyz.flussigkatz.spoonzilla.util.diffutill.IngredientDiff
import xyz.flussigkatz.spoonzilla.view.rv_viewholder.IngredientViewHolder
import kotlin.math.pow
import kotlin.math.roundToInt

class IngredientsRecyclerAdapter(private var servings: Int) :
    RecyclerView.Adapter<IngredientViewHolder>() {

    private var items = listOf<IngredientItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return IngredientViewHolder(
            IngredientItemBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = items[position]
        val binding = holder.binding
        val unit = ingredient.unit
        val value = ingredient.value?.let { formatIngredientValue(it) }
        val amount = "$value $unit"
        setDishImage(ingredient.image, binding.ingredientImage)
        binding.ingredientName.text = ingredient.name
        binding.ingredientAmount.text = amount
    }

    override fun onBindViewHolder(
        holder: IngredientViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            (payloads.first() as Bundle).apply {
                val ingredient = items[position]
                val binding = holder.binding
                val unit = ingredient.unit
                val value = formatIngredientValue(getDouble(INGREDIENT_VALUE_KEY))
                val amount = "$value $unit"
                binding.ingredientAmount.text = amount
            }
        } else super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemCount() = items.size

    fun updateItems(newList: List<IngredientItem>) {
        val diffResult = DiffUtil.calculateDiff(IngredientDiff(items, newList))
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateServingsCount(newServings: Int) {
        val newList = items.map { it.copy() }
        newList.forEach { item ->
            item.value?.let {item.value =  it / servings * newServings }
        }
        servings = newServings
        updateItems(newList)
    }

    private fun setDishImage(image: String?, imageView: ImageView) {
        Picasso.get()
            .load(IMAGE_PATH + image)
            .fit()
            .centerInside()
            .into(imageView)

    }

    private fun formatIngredientValue(value: Double): Number {
        return if (value % ZERO_FRACTION_FLAG > ZERO_FRACTION) doubleRound(value) else value.toInt()
    }

    private fun doubleRound(value: Double): Double {
        val scale = 10.0.pow(2.0)
        return (value * scale).roundToInt() / scale
    }

    companion object {
        private const val IMAGE_PATH = "https://spoonacular.com/cdn/ingredients_100x100/"
        private const val ZERO_FRACTION = 0.0
        private const val ZERO_FRACTION_FLAG = 1
    }
}