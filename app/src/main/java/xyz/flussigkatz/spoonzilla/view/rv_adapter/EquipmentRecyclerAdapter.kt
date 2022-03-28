package xyz.flussigkatz.spoonzilla.view.rv_adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import xyz.flussigkatz.core_api.entity.equipments.EquipmentItem
import xyz.flussigkatz.spoonzilla.databinding.EquipmentItemBinding
import xyz.flussigkatz.spoonzilla.view.rv_viewholder.EquipmentViewHolder

class EquipmentRecyclerAdapter : RecyclerView.Adapter<EquipmentViewHolder>() {

    private var items = listOf<EquipmentItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EquipmentViewHolder(
            EquipmentItemBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EquipmentViewHolder, position: Int) {
        val equipment = items[position]
        val binding = holder.binding
        setDishImage(equipment.image, binding.equipmentImage)
        binding.equipmentName.text = equipment.name
    }

    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(equipmentItems: List<EquipmentItem>) {
        items = equipmentItems
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
        private const val TAG = "EquipmentRecyclerAdapter"
        private const val IMAGE_PATH = "https://spoonacular.com/cdn/equipment_100x100/"
    }
}