package xyz.flussigkatz.remote.entity.taste


import com.google.gson.annotations.SerializedName

data class TasteByIdDto(
    @SerializedName("bitterness")
    val bitterness: Double,
    @SerializedName("fattiness")
    val fattiness: Double,
    @SerializedName("saltiness")
    val saltiness: Double,
    @SerializedName("savoriness")
    val savoriness: Double,
    @SerializedName("sourness")
    val sourness: Double,
    @SerializedName("spiciness")
    val spiciness: Double,
    @SerializedName("sweetness")
    val sweetness: Double
)