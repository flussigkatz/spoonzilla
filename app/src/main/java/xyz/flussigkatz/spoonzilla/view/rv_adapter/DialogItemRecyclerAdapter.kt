package xyz.flussigkatz.spoonzilla.view.rv_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.flussigkatz.spoonzilla.databinding.DialogItemBinding
import xyz.flussigkatz.spoonzilla.view.rv_viewholder.DialogItemViewHolder

class DialogItemRecyclerAdapter(
    private val markedItems: List<String>,
    private val allItems: List<String>,
    private val clickListener: OnCheckedChangeListener
) : RecyclerView.Adapter<DialogItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DialogItemViewHolder(DialogItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: DialogItemViewHolder, position: Int) {
        val binding = holder.binding
        val item = allItems[position]
        val state = markedItems.contains(item)
        binding.titleDialogItem.text = item
        binding.checkboxDialogItem.isChecked = state
        binding.checkboxDialogItem.setOnClickListener {
            clickListener.checkedChange(item, state)
        }
    }

    override fun getItemCount(): Int = allItems.size
    interface OnCheckedChangeListener   {
        fun checkedChange(item: String, state: Boolean)
    }

}