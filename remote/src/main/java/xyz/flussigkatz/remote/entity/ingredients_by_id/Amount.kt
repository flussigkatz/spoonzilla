package xyz.flussigkatz.remote.entity.ingredients_by_id


import com.google.gson.annotations.SerializedName

data class Amount(
    @SerializedName("metric")
    val metric: Metric,
    @SerializedName("us")
    val us: Us
)