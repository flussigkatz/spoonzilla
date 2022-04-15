package xyz.flussigkatz.spoonzilla.data.db

import xyz.flussigkatz.core_api.db.DishDao
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.core_api.entity.DishAlarm
import xyz.flussigkatz.core_api.entity.DishMarked
import xyz.flussigkatz.core_api.entity.equipments.Equipments
import xyz.flussigkatz.core_api.entity.ingredients.Ingredients
import xyz.flussigkatz.core_api.entity.instructions.Instructions
import xyz.flussigkatz.core_api.entity.nutrient.Nutrients

class MainRepository(private val dishDao: DishDao) {

    //    Dish
    fun putDishesToDb(list: List<Dish>) {
        dishDao.insertAllDishes(list)
    }

    fun updateDish(dish: Dish) {
        dishDao.updateDish(dish)
    }

    fun getAllDishesFromDb() =  dishDao.getCashedDishes()

    fun getDishFromDb(dishId: Int) =  dishDao.getCashedDish(dishId)

    fun clearDishTable() {
        val dishes = dishDao.getCashedDishesToList()
        dishDao.deleteDishes(dishes)
    }


    //    DishAlarm
    fun putDishAlarmToDb(dishAlarm: DishAlarm) {
        dishDao.insertDishAlarm(dishAlarm)
    }

    fun updateDishAlarm(dishAlarm: DishAlarm) {
        dishDao.updateDishAlarm(dishAlarm)
    }

    fun getAllDishAlarmsFromDb() = dishDao.getDishAlarms()

    fun deleteDishAlarm(localId: Int) {
        val dishAlarm = dishDao.getDishAlarm(localId)
        dishDao.deleteDishAlarm(dishAlarm)
    }

    //    DishMarked
    fun putMarkedDishToDb(dish: DishMarked) {
        dishDao.insertMarkedDishes(dish)
    }

    fun deleteMarkedDishFromDb(dishId: Int) {
        val dish = dishDao.getOneCashedMarkedDish(dishId)
        dishDao.deleteMarkedDish(dish)
    }

    fun getAllMarkedDishesFromDb(query: String) = dishDao.getCashedMarkedDishes(query)

    fun getIdsMarkedDishesFromDbToList() = dishDao.getIdsCashedMarkedDishesToList()

    //    DishAdvancedInfo
    fun putAdvancedInfoDishToDb(dishAdvancedInfo: DishAdvancedInfo) {
        dishDao.insertAdvancedInfoDish(dishAdvancedInfo)
    }

    fun updateAdvancedInfoDish(dishAdvancedInfo: DishAdvancedInfo) {
        dishDao.updateAdvancedInfoDish(dishAdvancedInfo)
    }

    fun getAdvancedInfoDishFromDb(dishId: Int) = dishDao.getCashedObservableAdvancedInfoDish(dishId)

    fun getAdvancedInfoDishFromDbToList(dishId: Int): List<DishAdvancedInfo> {
        return dishDao.getCashedObservableAdvancedInfoDishToList(dishId)
    }

    fun deleteAdvancedInfoDishFromDb(dishId: Int) {
        val dishAdvancedInfo = dishDao.getCashedAdvancedInfoDish(dishId)
        dishDao.deleteAdvancedInfoDish(dishAdvancedInfo)
    }

    //    RecentlyViewed
    fun getRecentlyViewedDishes() = dishDao.getAllCashedAdvancedInfoDishes()

    //    Ingredients
    fun getIngredients(dishId: Int) = dishDao.getIngredients(dishId)

    fun putIngredients(ingredients: Ingredients) {
        dishDao.insertIngredients(ingredients)
    }

    //    Equipments
    fun getEquipments(dishId: Int) = dishDao.getEquipments(dishId)

    fun putEquipments(equipments: Equipments) {
        dishDao.insertEquipments(equipments)
    }

    //    Instructions
    fun getInstructions(dishId: Int) = dishDao.getInstructions(dishId)

    fun putInstructions(instructions: Instructions) {
        dishDao.insertInstructions(instructions)
    }

    //    Nutrients
    fun getNutrients(dishId: Int) = dishDao.getNutrients(dishId)

    fun putNutrients(nutrients: Nutrients) {
        dishDao.insertNutrients(nutrients)
    }

}