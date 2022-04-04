package xyz.flussigkatz.remote.entity.nutrient_by_id


import com.google.gson.annotations.SerializedName

data class NutrientByIdDto(
    @SerializedName("bad")
    val bad: List<Bad>,
    @SerializedName("calories")
    val calories: String,
    @SerializedName("carbs")
    val carbs: String,
    @SerializedName("expires")
    val expires: Long,
    @SerializedName("fat")
    val fat: String,
    @SerializedName("good")
    val good: List<Good>,
    @SerializedName("isStale")
    val isStale: Boolean,
    @SerializedName("protein")
    val protein: String
)