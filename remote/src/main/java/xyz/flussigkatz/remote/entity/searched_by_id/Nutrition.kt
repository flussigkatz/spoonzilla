package xyz.flussigkatz.remote.entity.searched_by_id


import com.google.gson.annotations.SerializedName

data class Nutrition(
    @SerializedName("caloricBreakdown")
    val caloricBreakdown: CaloricBreakdown,
    @SerializedName("flavonoids")
    val flavonoids: List<Flavonoid>,
    @SerializedName("ingredients")
    val ingredients: List<Ingredient>,
    @SerializedName("nutrients")
    val nutrients: List<NutrientX>,
    @SerializedName("properties")
    val properties: List<Property>,
    @SerializedName("weightPerServing")
    val weightPerServing: WeightPerServing
)