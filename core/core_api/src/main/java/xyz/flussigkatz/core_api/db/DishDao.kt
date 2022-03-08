package xyz.flussigkatz.core_api.db

import androidx.room.*
import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.core_api.entity.Dish

@Dao
interface DishDao {
    @Query("SELECT * FROM cashed_dishes")
    fun getCashedDishes(): Observable<List<Dish>>

    @Query("SELECT * FROM cashed_dishes")
    fun getCashedDishesToList(): List<Dish>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllDishes(list: List<Dish>)


    @Delete
    fun deleteDishes(films: List<Dish>): Int

}