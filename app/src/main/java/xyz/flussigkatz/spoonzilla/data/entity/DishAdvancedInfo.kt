package xyz.flussigkatz.spoonzilla.data.entity

data class DishAdvancedInfo(
    val aggregateLikes: Int,
    val cheap: Boolean,
    val cuisines: List<String>,
    val diets: List<String>,
    val id: Int,
    val image: String?,
    val pricePerServing: Double,
    val readyInMinutes: Int,
    val servings: Int,
    val sourceUrl: String,
    val summary: String,
    val title: String,
)