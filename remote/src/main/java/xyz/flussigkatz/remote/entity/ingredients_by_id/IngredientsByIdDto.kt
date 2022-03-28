package xyz.flussigkatz.remote.entity.ingredients_by_id


import com.google.gson.annotations.SerializedName

data class IngredientsByIdDto(
    @SerializedName("ingredients")
    val ingredients: List<Ingredient>
)