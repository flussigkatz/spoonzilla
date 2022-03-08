package xyz.flussigkatz.spoonzilla.data.db

import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.core_api.db.DishDao
import xyz.flussigkatz.core_api.entity.Dish

class MainRepository(private val dishDao: DishDao) {


    fun putFilmToDB(dishes: List<Dish>) {
        dishDao.insertAllDishes(dishes)
    }

    fun getAllFilmsFromDB(): Observable<List<Dish>>{
        return dishDao.getCashedDishes()
    }

    fun clearDB(): Int {
        val films = dishDao.getCashedDishesToList()
        return dishDao.deleteDishes(films)
    }
}