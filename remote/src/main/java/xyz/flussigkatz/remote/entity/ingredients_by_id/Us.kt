package xyz.flussigkatz.remote.entity.ingredients_by_id


import com.google.gson.annotations.SerializedName

data class Us(
    @SerializedName("unit")
    val unit: String,
    @SerializedName("value")
    val value: Double
)