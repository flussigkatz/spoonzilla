package xyz.flussigkatz.spoonzilla.view.rv_adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import xyz.flussigkatz.spoonzilla.data.entity.instructions.InstructionsItem
import xyz.flussigkatz.spoonzilla.databinding.InstructionsItemBinding
import xyz.flussigkatz.spoonzilla.databinding.StepItemBinding
import xyz.flussigkatz.spoonzilla.view.rv_viewholder.InstructionsItemViewHolder
import xyz.flussigkatz.spoonzilla.view.rv_viewholder.StepViewHolder

class InstructionsItemRecyclerAdapter : RecyclerView.Adapter<InstructionsItemViewHolder>() {
    private lateinit var stepAdapter: StepRecyclerAdapter
    private var items = listOf<InstructionsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionsItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return InstructionsItemViewHolder(
            InstructionsItemBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InstructionsItemViewHolder, position: Int) {
        val instructionsItem = items[position]
        val binding = holder.binding
        binding.instructionsItemName.apply {
            text = instructionsItem.name
            visibility = View.VISIBLE
        }
        binding.stepsRecycler.apply {
            stepAdapter = StepRecyclerAdapter()
            adapter = stepAdapter
            layoutManager = LinearLayoutManager(context)
        }
        stepAdapter.addItems(instructionsItem.steps)

    }

    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(instructionsItem: List<InstructionsItem>) {
        items = instructionsItem
        this.notifyDataSetChanged()
    }
}