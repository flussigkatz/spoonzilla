package xyz.flussigkatz.core_api.db

import androidx.room.*
import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.core_api.entity.DishAlarm
import xyz.flussigkatz.core_api.entity.DishMarked
import xyz.flussigkatz.core_api.entity.equipments.Equipments
import xyz.flussigkatz.core_api.entity.ingredients.Ingredients
import xyz.flussigkatz.core_api.entity.instructions.Instructions
import xyz.flussigkatz.core_api.entity.nutrient.Nutrients

@Dao
interface DishDao {
    //Dish
    @Query("SELECT * FROM cashed_dishes ORDER BY localId")
    fun getCashedDishes(): Observable<List<Dish>>

    @Query("SELECT * FROM cashed_dishes WHERE id LIKE :dishId")
    fun getCashedDish(dishId: Int): Dish

    @Query("SELECT * FROM cashed_dishes")
    fun getCashedDishesToList(): List<Dish>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllDishes(list: List<Dish>)

    @Update
    fun updateDish(dish: Dish)

    @Delete
    fun deleteDishes(list: List<Dish>): Int

    //DishAlarm
    @Query("SELECT * FROM dishes_alarms ORDER BY localId")
    fun getDishAlarms(): Observable<List<DishAlarm>>

    @Query("SELECT * FROM dishes_alarms ORDER BY localId")
    fun getDishAlarmsToList(): List<DishAlarm>

    @Query("SELECT * FROM dishes_alarms WHERE localId LIKE :localId")
    fun getDishAlarm(localId: Int): DishAlarm

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDishAlarm(dishAlarm: DishAlarm)

    @Update
    fun updateDishAlarm(dishAlarm: DishAlarm)

    @Delete
    fun deleteDishAlarm(dishAlarm: DishAlarm): Int

    //DishMarked
    @Query(
        "SELECT * FROM cashed_marked_dishes " +
                "WHERE title LIKE '%' || :query || '%' ORDER BY localId DESC"
    )
    fun getCashedMarkedDishes(query: String): Observable<List<DishMarked>>

    @Query("SELECT id FROM cashed_marked_dishes")
    fun getIdsCashedMarkedDishesToList(): List<Int>

    @Query("SELECT * FROM cashed_marked_dishes WHERE id LIKE :id")
    fun getOneCashedMarkedDish(id: Int): DishMarked

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMarkedDishes(dish: DishMarked)

    @Delete
    fun deleteMarkedDish(dish: DishMarked): Int

    //DishAdvancedInfo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAdvancedInfoDish(dishAdvancedInfo: DishAdvancedInfo)

    @Query("SELECT * FROM cashed_dishes_advanced_info WHERE id LIKE :id")
    fun getCashedObservableAdvancedInfoDish(id: Int): Observable<List<DishAdvancedInfo>>

    @Query("SELECT * FROM cashed_dishes_advanced_info WHERE id LIKE :id")
    fun getCashedObservableAdvancedInfoDishToList(id: Int): List<DishAdvancedInfo>

    @Query("SELECT * FROM cashed_dishes_advanced_info WHERE id LIKE :id")
    fun getSingleCashedAdvancedInfoDish(id: Int): DishAdvancedInfo

    @Update
    fun updateAdvancedInfoDish(dishAdvancedInfo: DishAdvancedInfo)

    @Delete
    fun deleteAdvancedInfoDish(dishAdvancedInfo: DishAdvancedInfo): Int

    //    RecentlyViewed
    @Query(
        "SELECT localId, id, title, image, mark  " +
                "FROM cashed_dishes_advanced_info ORDER BY localId DESC"
    )
    fun getAllCashedAdvancedInfoDishes(): Observable<List<Dish>>

    //Ingredients
    @Query("SELECT * FROM cashed_dishes_ingredients WHERE dishId LIKE :dishId")
    fun getIngredients(dishId: Int): Observable<Ingredients>

    @Query("SELECT * FROM cashed_dishes_ingredients WHERE dishId LIKE :dishId")
    fun getIngredientsToList(dishId: Int): List<Ingredients>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIngredients(ingredients: Ingredients)

    //Equipments
    @Query("SELECT * FROM cashed_dishes_equipments WHERE dishId LIKE :dishId")
    fun getEquipments(dishId: Int): Observable<Equipments>

    @Query("SELECT * FROM cashed_dishes_equipments WHERE dishId LIKE :dishId")
    fun getEquipmentsToList(dishId: Int): List<Equipments>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEquipments(equipments: Equipments)

    //Instructions
    @Query("SELECT * FROM cashed_dishes_instructions WHERE dishId LIKE :dishId")
    fun getInstructions(dishId: Int): Observable<Instructions>

    @Query("SELECT * FROM cashed_dishes_instructions WHERE dishId LIKE :dishId")
    fun getInstructionsToList(dishId: Int): List<Instructions>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInstructions(instructions: Instructions)

    //Nutrients
    @Query("SELECT * FROM cashed_dishes_nutrients WHERE dishId LIKE :dishId")
    fun getNutrients(dishId: Int): Observable<Nutrients>

    @Query("SELECT * FROM cashed_dishes_nutrients WHERE dishId LIKE :dishId")
    fun getNutrientsToList(dishId: Int): List<Nutrients>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNutrients(nutrients: Nutrients)
}