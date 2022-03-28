package xyz.flussigkatz.remote.entity.instructions_by_id


import com.google.gson.annotations.SerializedName
import xyz.flussigkatz.remote.entity.random.Equipment
import xyz.flussigkatz.remote.entity.random.Ingredient

data class Step(
    @SerializedName("equipment")
    val equipment: List<Equipment>?,
    @SerializedName("ingredients")
    val ingredients: List<Ingredient>?,
    @SerializedName("length")
    val length: Length?,
    @SerializedName("number")
    val number: Int,
    @SerializedName("step")
    val step: String
)