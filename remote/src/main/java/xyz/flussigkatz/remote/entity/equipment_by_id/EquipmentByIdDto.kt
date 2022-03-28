package xyz.flussigkatz.remote.entity.equipment_by_id


import com.google.gson.annotations.SerializedName

data class EquipmentByIdDto(
    @SerializedName("equipment")
    val equipment: List<Equipment>
)