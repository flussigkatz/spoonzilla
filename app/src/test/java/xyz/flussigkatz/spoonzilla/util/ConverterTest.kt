package xyz.flussigkatz.spoonzilla.util

import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.kotlin.mock
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.core_api.entity.DishMarked
import xyz.flussigkatz.core_api.entity.equipments.EquipmentItem
import xyz.flussigkatz.core_api.entity.equipments.Equipments
import xyz.flussigkatz.core_api.entity.ingredients.IngredientItem
import xyz.flussigkatz.core_api.entity.ingredients.Ingredients
import xyz.flussigkatz.core_api.entity.instructions.Instructions
import xyz.flussigkatz.core_api.entity.instructions.InstructionsItem
import xyz.flussigkatz.core_api.entity.nutrient.NutrientItem
import xyz.flussigkatz.core_api.entity.nutrient.Nutrients
import xyz.flussigkatz.remote.entity.equipment_by_id.EquipmentByIdDto
import xyz.flussigkatz.remote.entity.ingredients_by_id.IngredientsByIdDto
import xyz.flussigkatz.remote.entity.instructions_by_id.InstructionsByIdDto
import xyz.flussigkatz.remote.entity.nutrient_by_id.NutrientByIdDto
import xyz.flussigkatz.remote.entity.random.RandomRecipesDto
import xyz.flussigkatz.remote.entity.random.Recipe
import xyz.flussigkatz.remote.entity.seached.Result
import xyz.flussigkatz.remote.entity.seached.SearchedRecipesDto
import xyz.flussigkatz.remote.entity.searched_by_id.SearchedRecipeByIdDto

@RunWith(JUnit4::class)
class ConverterTest {
    private val ids = mock<List<Int>>()
    private val id = 0

    @Test
    fun checkReturnedObjectIsDish1() {
        val recipe = mock<Recipe>()
        Mockito.`when`(recipe.title).thenReturn("title")
        val randomRecipesDto = mock<RandomRecipesDto>()
        Mockito.`when`(randomRecipesDto.recipes).thenReturn(listOf(recipe))
        val res = Converter.convertRandomRecipeFromApi(randomRecipesDto, ids)
        assertThat(res.first(), instanceOf(Dish::class.java))
    }

    @Test
    fun checkReturnedObjectIsDish2() {
        val result = mock<Result>()
        Mockito.`when`(result.title).thenReturn("title")
        val searchedRecipesDto = mock<SearchedRecipesDto>()
        Mockito.`when`(searchedRecipesDto.results).thenReturn(listOf(result))
        val res = Converter.convertSearchedRecipeBasicInfoFromApi(searchedRecipesDto, ids)
        assertThat(res.first(), instanceOf(Dish::class.java))
    }

    @Test
    fun checkReturnedObjectIsDishAdvancedInfo() {
        val searchedRecipeByIdDto = mock<SearchedRecipeByIdDto>()
        Mockito.`when`(searchedRecipeByIdDto.sourceUrl).thenReturn("sourceUrl")
        Mockito.`when`(searchedRecipeByIdDto.summary).thenReturn("summary")
        Mockito.`when`(searchedRecipeByIdDto.title).thenReturn("title")
        val res = Converter.convertRecipeByIdFromApi(searchedRecipeByIdDto, ids)
        assertThat(res, instanceOf(DishAdvancedInfo::class.java))
    }

    @Test
    fun checkReturnedObjectIsDish3() {
        val dishAdvancedInfo = mock<DishAdvancedInfo>()
        Mockito.`when`(dishAdvancedInfo.title).thenReturn("title")
        val res = Converter.convertDishAdvancedInfoToDish(dishAdvancedInfo)
        assertThat(res, instanceOf(Dish::class.java))
    }

    @Test
    fun checkReturnedObjectIsDishMarked() {
        val dish = mock<Dish>()
        Mockito.`when`(dish.title).thenReturn("title")
        val res = Converter.convertDishToDishMarked(dish)
        assertThat(res, instanceOf(DishMarked::class.java))
    }

    @Test
    fun checkReturnedObjectIsDish4() {
        val dishMarked = mock<DishMarked>()
        Mockito.`when`(dishMarked.title).thenReturn("title")
        val res = Converter.convertDishMarkedToDish(dishMarked)
        assertThat(res, instanceOf(Dish::class.java))
    }

    @Test
    fun checkReturnedObjectIsIngredientItem() {
        val ingredient = "[{\"image\":\"image\",\"name\":\"name\",\"unit\":\"unit\",\"value\":0.0}]"
        val ingredients = mock<Ingredients>()
        Mockito.`when`(ingredients.ingredient).thenReturn(ingredient)
        val res = Converter.convertIngredientsFromDb(ingredients)
        assertThat(res.first(), instanceOf(IngredientItem::class.java))
    }

    @Test
    fun checkReturnedObjectIsIngredients() {
        val ingredientsByIdDto = mock<IngredientsByIdDto>()
        val res = Converter.convertIngredientsFromApi(ingredientsByIdDto, true, id)
        assertThat(res, instanceOf(Ingredients::class.java))
    }

    @Test
    fun checkReturnedObjectIsEquipmentItem() {
        val equipment = "[{\"image\":\"image\",\"name\":\"name\"}]"
        val equipments = mock<Equipments>()
        Mockito.`when`(equipments.equipment).thenReturn(equipment)
        val res = Converter.convertEquipmentsByIdFromDb(equipments)
        assertThat(res.first(), instanceOf(EquipmentItem::class.java))
    }

    @Test
    fun checkReturnedObjectIsEquipments() {
        val equipmentByIdDto = mock<EquipmentByIdDto>()
        val res = Converter.convertEquipmentsFromApi(equipmentByIdDto, id)
        assertThat(res, instanceOf(Equipments::class.java))
    }

    @Test
    fun checkReturnedObjectIsInstructionsItem() {
        val instruction =
            "[{\"name\":\"name\",\"steps\":[{\"equipmentItems\":[],\"ingredientItems\":[],\"number\":0,\"step\":\"step\"}]}]"
        val instructions = mock<Instructions>()
        Mockito.`when`(instructions.instructionsItems).thenReturn(instruction)
        val res = Converter.convertInstructionsByIdFromDb(instructions)
        assertThat(res.first(), instanceOf(InstructionsItem::class.java))
    }

    @Test
    fun checkReturnedObjectIsInstructions() {
        val instructionsByIdDto = InstructionsByIdDto()
        instructionsByIdDto.add(mock())
        val res = Converter.convertInstructionsByIdFromApi(instructionsByIdDto, id)
        assertThat(res, instanceOf(Instructions::class.java))
    }

    @Test
    fun checkReturnedObjectIsNutrientItem() {
        val nutrient =
            "[{\"amount\":\"amount\",\"good\":true,\"percentOfDailyNeeds\":0.0,\"title\":\"title\"}]"
        val nutrients = mock<Nutrients>()
        Mockito.`when`(nutrients.nutrientItems).thenReturn(nutrient)
        val res = Converter.convertNutrientsByIdFromDb(nutrients)
        assertThat(res.first(), instanceOf(NutrientItem::class.java))
    }

    @Test
    fun checkReturnedObjectIsNutrients() {
        val nutrientByIdDto = mock<NutrientByIdDto>()
        val res = Converter.convertNutrientsFromApi(nutrientByIdDto, id)
        assertThat(res, instanceOf(Nutrients::class.java))
    }
}