package xyz.flussigkatz.spoonzilla.view.rv_adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import xyz.flussigkatz.core_api.entity.ingredients.IngredientItem
import xyz.flussigkatz.spoonzilla.databinding.IngredientItemBinding
import xyz.flussigkatz.spoonzilla.view.rv_viewholder.IngredientViewHolder

class IngredientsRecyclerAdapter : RecyclerView.Adapter<IngredientViewHolder>() {

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
        val value = ingredient.value?.let { if (it % 1 > 0.0) it else it.toInt() }
        val amount = "$value $unit"
        setDishImage(ingredient.image, binding.ingredientImage)
        binding.ingredientName.text = ingredient.name
        binding.ingredientAmount.text = amount
    }

    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(ingredientItems: List<IngredientItem>) {
        items = ingredientItems
        this.notifyDataSetChanged()
    }

    private fun setDishImage(image: String?, imageView: ImageView) {
        Picasso.get()
            .load(IMAGE_PATH + image)
            .fit()
            .centerInside()
            .into(imageView)

    }

    companion object {
        private const val IMAGE_PATH = "https://spoonacular.com/cdn/ingredients_100x100/"
    }
}