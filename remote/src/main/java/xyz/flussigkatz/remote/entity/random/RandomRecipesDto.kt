package xyz.flussigkatz.remote.entity.random


import com.google.gson.annotations.SerializedName

data class RandomRecipesDto(
    @SerializedName("recipes")
    val recipes: List<Recipe>
)