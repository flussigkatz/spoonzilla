package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.core_api.entity.DishAlarm
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
import javax.inject.Inject

class MainActivityViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val dishAlarmsList: Observable<List<DishAlarm>>

    init {
        App.instance.dagger.inject(this)
        dishAlarmsList = interactor.getDishAlarmsFromDb()
    }

    fun putSearchQuery(query: String) {
        interactor.putSearchQuery(query)
    }

    fun getRecentlyViewedDishes() = interactor.getRecentlyViewedDishes()

    fun getSingleCashedAdvancedInfoDishFromDb(dishId: Int) =
        interactor.getSingleCashedAdvancedInfoDishFromDb(dishId)

    fun setDishMark(dish: Dish) {
        interactor.setDishMark(dish)
    }

    fun deleteDishAlarmFromDb(localId: Int) {
        interactor.deleteDishAlarmFromDb(localId)
    }
}