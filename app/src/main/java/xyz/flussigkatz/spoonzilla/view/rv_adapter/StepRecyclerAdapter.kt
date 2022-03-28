package xyz.flussigkatz.spoonzilla.view.rv_adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.flussigkatz.core_api.entity.instructions.Step
import xyz.flussigkatz.spoonzilla.databinding.StepItemBinding
import xyz.flussigkatz.spoonzilla.view.rv_viewholder.StepViewHolder

class StepRecyclerAdapter : RecyclerView.Adapter<StepViewHolder>() {

    private var items = listOf<Step>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StepViewHolder(
            StepItemBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = items[position]
        val binding = holder.binding
        binding.stepItemNumber.text = step.number.toString()
        binding.stepIngredients.apply {
            if (step.ingredientItems.isNotEmpty()) {
                val ingredients = "$INGREDIENTS ${step.ingredientItems.map { it.name }.joinToString()}"
                text = ingredients
                visibility = View.VISIBLE
            }
        }
        binding.stepEquipments.apply {
            if (step.equipmentItems.isNotEmpty()) {
                val equipments = "$EQUIPMENTS ${step.equipmentItems.joinToString { it.name }}"
                text = equipments
                visibility = View.VISIBLE
            }
        }
        binding.stepDescription.text = step.step
    }

    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(step: List<Step>) {
        items = step
        this.notifyDataSetChanged()
    }

    companion object {
        private const val INGREDIENTS = "Ingredients:"
        private const val EQUIPMENTS = "Equipments:"
    }
}