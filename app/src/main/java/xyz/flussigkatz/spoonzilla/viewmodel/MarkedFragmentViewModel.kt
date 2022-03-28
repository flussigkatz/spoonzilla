package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.core_api.entity.DishMarked
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
import xyz.flussigkatz.spoonzilla.util.Converter
import javax.inject.Inject

class MarkedFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val searchPublishSubject: PublishSubject<String>

    init {
        App.instance.dagger.inject(this)
        searchPublishSubject = interactor.getSearchPublishSubject()
    }

    fun setDishMark(dish: Dish, isChecked: Boolean) {
        if (isChecked) interactor.putMarkedDishToDB(Converter.convertDishToDishMarked(dish))
        else interactor.deleteMarkedDishFromDb(dish.id)
        interactor.updateDish(dish)
    }

    fun getMarkedDishesFromDb(query: String? = null) = interactor.getMarkedDishesFromDb(query.orEmpty())

}