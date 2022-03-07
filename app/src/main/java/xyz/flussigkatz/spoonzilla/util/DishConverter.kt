package xyz.flussigkatz.spoonzilla.util

import xyz.flussigkatz.remote.entity.random.RandomRecipesDto
import xyz.flussigkatz.remote.entity.seached.SearchedRecipesDto
import xyz.flussigkatz.spoonzilla.data.entity.Dish

object DishConverter {

    fun convertRandomRecipeFromApi(randomRecipesDto: RandomRecipesDto): List<Dish> {
        val result = mutableListOf<Dish>()
        randomRecipesDto.recipes.forEach {
            result.add(
                Dish(
                    id = it.id,
                    title = it.title,
                    image = it.image
                )
            )
        }
        return result
    }

    fun convertSearchedRecipeBasicInfoFromApi(searchedRecipesDto: SearchedRecipesDto): List<Dish> {
        val result = mutableListOf<Dish>()
        searchedRecipesDto.results.forEach {
            result.add(
                Dish(
                    id = it.id,
                    title = it.title,
                    image = it.image
                )
            )
        }
        return result
    }
}