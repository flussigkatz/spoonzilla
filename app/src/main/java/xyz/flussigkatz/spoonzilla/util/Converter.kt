package xyz.flussigkatz.spoonzilla.util

import xyz.flussigkatz.remote.entity.random.RandomRecipesDto
import xyz.flussigkatz.remote.entity.seached.SearchedRecipesDto
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.remote.entity.ingredients_by_id.IngredientsByIdDto
import xyz.flussigkatz.remote.entity.equipment_by_id.EquipmentByIdDto
import xyz.flussigkatz.remote.entity.instructions_by_id.InstructionsByIdDto
import xyz.flussigkatz.remote.entity.searched_by_id.SearchedRecipeByIdDto
import xyz.flussigkatz.spoonzilla.data.entity.*
import xyz.flussigkatz.spoonzilla.data.entity.instructions.InstructionsItem
import xyz.flussigkatz.spoonzilla.data.entity.instructions.Step
import java.math.BigDecimal
import java.math.RoundingMode

object Converter {

    private const val DOLLAR_RATIO = 100

    fun convertRandomRecipeFromApi(randomRecipesDto: RandomRecipesDto): List<Dish> {
        return randomRecipesDto.recipes.map {
            if (it.cheap) println(it.title)
            Dish(
                id = it.id,
                title = it.title,
                image = it.image
            )
        }
    }

    fun convertSearchedRecipeBasicInfoFromApi(searchedRecipesDto: SearchedRecipesDto): List<Dish> {
        return searchedRecipesDto.results.map {
            Dish(
                id = it.id,
                title = it.title,
                image = it.image
            )
        }
    }

    fun convertRecipeByIdFromApi(searchedRecipeByIdDto: SearchedRecipeByIdDto): DishAdvancedInfo {
        val pricePerServing = BigDecimal(
            searchedRecipeByIdDto.pricePerServing / DOLLAR_RATIO
        ).setScale(2, RoundingMode.HALF_EVEN).toDouble()

        return DishAdvancedInfo(
            aggregateLikes = searchedRecipeByIdDto.aggregateLikes,
            cheap = searchedRecipeByIdDto.cheap,
            cuisines = searchedRecipeByIdDto.cuisines,
            diets = searchedRecipeByIdDto.diets,
            id = searchedRecipeByIdDto.id,
            image = searchedRecipeByIdDto.image,
            pricePerServing = pricePerServing,
            readyInMinutes = searchedRecipeByIdDto.readyInMinutes,
            servings = searchedRecipeByIdDto.servings,
            sourceUrl = searchedRecipeByIdDto.sourceUrl,
            summary = searchedRecipeByIdDto.summary,
            title = searchedRecipeByIdDto.title,
        )
    }

    fun convertIngredientsByIdFromApi(
        ingredientsByIdDto: IngredientsByIdDto,
        metric: Boolean
    ): List<Ingredient> {
        return ingredientsByIdDto.ingredients.map {
            Ingredient(
                image = it.image,
                name = it.name,
                unit = if (metric) it.amount.metric.unit else it.amount.us.unit,
                value = if (metric) it.amount.metric.value else it.amount.us.value,
            )
        }
    }

    fun convertEquipmentsByIdFromApi(equipmentByIdDto: EquipmentByIdDto): List<Equipment> {
        return equipmentByIdDto.equipment.map {
            Equipment(
                image = it.image,
                name = it.name,
            )
        }
    }

    fun convertInstructionsByIdFromApi(instructionsByIdDto: InstructionsByIdDto): List<InstructionsItem> {
        val steps = instructionsByIdDto.flatMap { it.steps }
        return instructionsByIdDto.map { instructionsItem ->
            InstructionsItem(
                name = instructionsItem.name,
                steps = steps.map { step ->
                    Step(
                        equipment = step.equipment?.map {
                            Equipment(image = it.image, name = it.name)
                        }.orEmpty(),
                        ingredients = step.ingredients?.map {
                            Ingredient(image = it.image, name = it.name, unit = null, value = null)
                        }.orEmpty(),
                        number = step.number,
                        step = step.step,
                        numberLength = step.length?.number,
                        unitLength = step.length?.unit
                    )
                }
            )
        }
    }
}