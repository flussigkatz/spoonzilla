package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
import xyz.flussigkatz.spoonzilla.util.Converter
import javax.inject.Inject

class MainActivityViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    fun putSearchQuery(query: String) {
        interactor.putSearchQuery(query)
    }

    fun getRecentlyViewedDishes() = interactor.getRecentlyViewedDishes()

    fun setDishMark(dish: Dish, isChecked: Boolean) {
        if (isChecked) interactor.putMarkedDishToDB(Converter.convertDishToDishMarked(dish))
        else interactor.deleteMarkedDishFromDb(dish.id)
        interactor.updateDish(dish)
    }
}