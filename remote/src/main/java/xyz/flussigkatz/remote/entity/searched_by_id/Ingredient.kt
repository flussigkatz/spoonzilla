package xyz.flussigkatz.remote.entity.searched_by_id


import com.google.gson.annotations.SerializedName

data class Ingredient(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("nutrients")
    val nutrients: List<Nutrient>,
    @SerializedName("unit")
    val unit: String
)