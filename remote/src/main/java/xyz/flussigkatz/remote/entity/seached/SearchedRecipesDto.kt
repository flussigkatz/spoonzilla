package xyz.flussigkatz.remote.entity.seached


import com.google.gson.annotations.SerializedName

data class SearchedRecipesDto(
    @SerializedName("number")
    val number: Int,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("totalResults")
    val totalResults: Int
)