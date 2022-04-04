package xyz.flussigkatz.spoonzilla.view.rv_adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.flussigkatz.core_api.entity.nutrient.NutrientItem
import xyz.flussigkatz.spoonzilla.databinding.NutrientItemBinding
import xyz.flussigkatz.spoonzilla.view.rv_viewholder.NutrientViewHolder

class NutrientRecyclerAdapter : RecyclerView.Adapter<NutrientViewHolder>() {

    private var items = listOf<NutrientItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutrientViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NutrientViewHolder(
            NutrientItemBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NutrientViewHolder, position: Int) {
        val item = items[position]
        val binding = holder.binding

        val textTitle = "${item.title}: ${item.amount}"
        val textPercent = "${PERCENT_OF_DAILY_NEEDS + item.percentOfDailyNeeds}%"
        binding.nutrientTitleAmount.text = textTitle
        binding.nutrientPercentAtDay.text = textPercent
    }

    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(list: List<NutrientItem>) {
        items = list
        this.notifyDataSetChanged()
    }

    companion object {
        private const val PERCENT_OF_DAILY_NEEDS = "Percent of daily needs: "
    }
}