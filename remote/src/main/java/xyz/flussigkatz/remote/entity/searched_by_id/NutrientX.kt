package xyz.flussigkatz.remote.entity.searched_by_id


import com.google.gson.annotations.SerializedName

data class NutrientX(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("name")
    val name: String,
    @SerializedName("percentOfDailyNeeds")
    val percentOfDailyNeeds: Double,
    @SerializedName("unit")
    val unit: String
)