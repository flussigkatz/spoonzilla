package xyz.flussigkatz.spoonzilla.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import xyz.flussigkatz.remote.entity.random.RandomRecipesDto
import xyz.flussigkatz.remote.entity.seached.SearchedRecipesDto
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.core_api.entity.DishMarked
import xyz.flussigkatz.core_api.entity.equipments.EquipmentItem
import xyz.flussigkatz.core_api.entity.equipments.Equipments
import xyz.flussigkatz.core_api.entity.ingredients.IngredientItem
import xyz.flussigkatz.core_api.entity.ingredients.Ingredients
import xyz.flussigkatz.core_api.entity.instructions.Instructions
import xyz.flussigkatz.core_api.entity.instructions.InstructionsItem
import xyz.flussigkatz.core_api.entity.instructions.Step
import xyz.flussigkatz.core_api.entity.nutrient.NutrientItem
import xyz.flussigkatz.core_api.entity.nutrient.Nutrients
import xyz.flussigkatz.remote.entity.ingredients_by_id.IngredientsByIdDto
import xyz.flussigkatz.remote.entity.equipment_by_id.EquipmentByIdDto
import xyz.flussigkatz.remote.entity.instructions_by_id.InstructionsByIdDto
import xyz.flussigkatz.remote.entity.nutrient_by_id.NutrientByIdDto
import xyz.flussigkatz.remote.entity.searched_by_id.SearchedRecipeByIdDto
import java.math.BigDecimal
import java.math.RoundingMode

object Converter {

    private const val DOLLAR_RATIO = 100
    private val gson = Gson()

    fun convertRandomRecipeFromApi(
        randomRecipesDto: RandomRecipesDto,
        ids: List<Int>
    ): List<Dish> {
        return randomRecipesDto.recipes.map {
            Dish(
                id = it.id,
                title = it.title,
                image = it.image,
                mark = ids.contains(it.id)
            )
        }
    }

    fun convertSearchedRecipeBasicInfoFromApi(
        searchedRecipesDto: SearchedRecipesDto,
        ids: List<Int>
    ): List<Dish> {
        return searchedRecipesDto.results.map {
            Dish(
                id = it.id,
                title = it.title,
                image = it.image,
                mark = ids.contains(it.id)
            )
        }
    }

    fun convertRecipeByIdFromApi(
        searchedRecipeByIdDto: SearchedRecipeByIdDto,
        ids: List<Int>
    ): DishAdvancedInfo {
        val pricePerServing = BigDecimal(
            searchedRecipeByIdDto.pricePerServing / DOLLAR_RATIO
        ).setScale(2, RoundingMode.HALF_EVEN).toDouble()

        return DishAdvancedInfo(
            aggregateLikes = searchedRecipeByIdDto.aggregateLikes,
            cheap = searchedRecipeByIdDto.cheap,
            id = searchedRecipeByIdDto.id,
            image = searchedRecipeByIdDto.image,
            pricePerServing = pricePerServing,
            readyInMinutes = searchedRecipeByIdDto.readyInMinutes,
            servings = searchedRecipeByIdDto.servings,
            sourceUrl = searchedRecipeByIdDto.sourceUrl,
            summary = searchedRecipeByIdDto.summary,
            title = searchedRecipeByIdDto.title,
            mark = ids.contains(searchedRecipeByIdDto.id)
        )
    }

    fun convertDishAdvancedInfoToDish(dishAdvancedInfo: DishAdvancedInfo): Dish {
        return Dish(
            id = dishAdvancedInfo.id,
            title = dishAdvancedInfo.title,
            image = dishAdvancedInfo.image,
            mark = dishAdvancedInfo.mark
        )
    }

    fun convertDishToDishMarked(dish: Dish): DishMarked {
        return DishMarked(
            id = dish.id,
            title = dish.title,
            image = dish.image,
            mark = dish.mark
        )
    }

    fun convertDishMarkedToDish(dishMarked: DishMarked): Dish {
        return Dish(
            id = dishMarked.id,
            title = dishMarked.title,
            image = dishMarked.image,
            mark = dishMarked.mark
        )
    }

    fun convertIngredientsFromDb(
        ingredients: Ingredients
    ): List<IngredientItem> {
        val type = object : TypeToken<List<IngredientItem>>() {}.type
        return gson.fromJson(ingredients.ingredient, type)

    }

    fun convertIngredientsFromApi(
        ingredientsByIdDto: IngredientsByIdDto,
        metric: Boolean,
        id: Int
    ): Ingredients {
        val ingredient = ingredientsByIdDto.ingredients.map {
            IngredientItem(
                image = it.image,
                name = it.name,
                unit = if (metric) it.amount.metric.unit else it.amount.us.unit,
                value = if (metric) it.amount.metric.value else it.amount.us.value,
            )
        }
        return Ingredients(dishId = id, ingredient = gson.toJson(ingredient))
    }

    fun convertEquipmentsByIdFromDb(
        equipments: Equipments
    ): List<EquipmentItem> {
        val type = object : TypeToken<List<EquipmentItem>>() {}.type
        return gson.fromJson(equipments.equipment, type)
    }

    fun convertEquipmentsFromApi(
        equipmentByIdDto: EquipmentByIdDto,
        id: Int
    ): Equipments {
        val equipment = equipmentByIdDto.equipment.map {
            EquipmentItem(
                image = it.image,
                name = it.name,
            )
        }
        return Equipments(dishId = id, equipment = gson.toJson(equipment))
    }

    fun convertInstructionsByIdFromDb(
        instructions: Instructions
    ): List<InstructionsItem> {
        val type = object : TypeToken<List<InstructionsItem>>() {}.type
        return gson.fromJson(instructions.instructionsItems, type)
    }

    fun convertInstructionsByIdFromApi(
        instructionsByIdDto: InstructionsByIdDto,
        id: Int
    ): Instructions {
        val steps = instructionsByIdDto.flatMap { it.steps }
        val instructionsItems = instructionsByIdDto.map { instructionsItem ->
            InstructionsItem(
                name = instructionsItem.name,
                steps = steps.map { step ->
                    Step(
                        equipmentItems = step.equipment?.map {
                            EquipmentItem(image = it.image, name = it.name)
                        }.orEmpty(),
                        ingredientItems = step.ingredients?.map {
                            IngredientItem(
                                image = it.image,
                                name = it.name,
                                unit = null,
                                value = null
                            )
                        }.orEmpty(),
                        number = step.number,
                        step = step.step,
                        numberLength = step.length?.number,
                        unitLength = step.length?.unit
                    )
                }
            )
        }
        return Instructions(dishId = id, instructionsItems = gson.toJson(instructionsItems))
    }

    fun convertNutrientsByIdFromDb(
        nutrients: Nutrients
    ): List<NutrientItem> {
        val type = object : TypeToken<List<NutrientItem>>() {}.type
        return gson.fromJson(nutrients.nutrientItems, type)
    }

    fun convertNutrientsFromApi(
        nutrientByIdDto: NutrientByIdDto,
        id: Int
    ): Nutrients {
        val nutrient = mutableListOf<NutrientItem>()
        nutrient.addAll(nutrientByIdDto.bad.map {
            NutrientItem(
                amount = it.amount,
                percentOfDailyNeeds = it.percentOfDailyNeeds,
                title = it.title,
                good = false
            )
        })
        nutrient.addAll(nutrientByIdDto.good.map {
            NutrientItem(
                amount = it.amount,
                percentOfDailyNeeds = it.percentOfDailyNeeds,
                title = it.title,
                good = true
            )
        })
        return Nutrients(
            dishId = id,
            nutrientItems = gson.toJson(nutrient),
        )
    }
}