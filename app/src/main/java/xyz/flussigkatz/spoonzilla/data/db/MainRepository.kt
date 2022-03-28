package xyz.flussigkatz.spoonzilla.data.db

import xyz.flussigkatz.core_api.db.DishDao
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.core_api.entity.DishMarked
import xyz.flussigkatz.core_api.entity.equipments.Equipments
import xyz.flussigkatz.core_api.entity.ingredients.Ingredients
import xyz.flussigkatz.core_api.entity.instructions.Instructions

class MainRepository(private val dishDao: DishDao) {


    fun putDishesToDb(list: List<Dish>) {
        dishDao.insertAllDishes(list)
    }

    fun updateDish(dish: Dish) {
        dishDao.updateDish(dish)
    }

    fun getAllDishesFromDb() =  dishDao.getCashedDishes()

    fun clearDishTable() {
        val dishes = dishDao.getCashedDishesToList()
        dishDao.deleteDishes(dishes)
    }


    fun putMarkedDishToDb(dish: DishMarked) {
        dishDao.insertMarkedDishes(dish)
    }

    fun deleteMarkedDishFromDb(dishId: Int) {
        val dish = dishDao.getOneCashedMarkedDish(dishId)
        dishDao.deleteMarkedDish(dish)
    }

    fun getAllMarkedDishesFromDb(query: String) = dishDao.getCashedMarkedDishes(query)

    fun getIdsMarkedDishesFromDbToList() = dishDao.getIdsCashedMarkedDishesToList()


    fun putAdvancedInfoDishToDb(dishAdvancedInfo: DishAdvancedInfo) {
        dishDao.insertAdvancedInfoDish(dishAdvancedInfo)
    }

    fun getAdvancedInfoDishFromDb(dishId: Int) = dishDao.getCashedObservableAdvancedInfoDish(dishId)

    fun deleteAdvancedInfoDishFromDb(dishId: Int) {
        val dishAdvancedInfo = dishDao.getCashedAdvancedInfoDish(dishId)
        dishDao.deleteAdvancedInfoDish(dishAdvancedInfo)
    }


    fun getRecentlyViewedDishes() = dishDao.getAllCashedAdvancedInfoDishes()


    fun getIngredients(dishId: Int) = dishDao.getIngredients(dishId)

    fun putIngredients(ingredients: Ingredients) {
        dishDao.insertIngredients(ingredients)
    }

    fun getEquipments(dishId: Int) = dishDao.getEquipments(dishId)

    fun putEquipments(equipments: Equipments) {
        dishDao.insertEquipments(equipments)
    }

    fun getInstructions(dishId: Int) = dishDao.getInstructions(dishId)

    fun putInstructions(instructions: Instructions) {
        dishDao.insertInstructions(instructions)
    }
}