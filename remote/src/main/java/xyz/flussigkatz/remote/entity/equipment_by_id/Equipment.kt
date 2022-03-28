package xyz.flussigkatz.remote.entity.equipment_by_id


import com.google.gson.annotations.SerializedName

data class Equipment(
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String
)