package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.core_api.entity.DishAlarm
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
import xyz.flussigkatz.spoonzilla.util.Converter
import javax.inject.Inject

class DishOverviewFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    fun getRecipeByIdFromApi(id: Int) {
        interactor.getRecipeByIdFromApi(id)
    }

    fun getIngredientsByIdFromApi(id: Int) {
        interactor.getIngredientsByIdFromApi(id)
    }

    fun getEquipmentsByIdFromApi(id: Int) {
        interactor.getEquipmentsByIdFromApi(id)
    }

    fun getInstructionsByIdFromApi(id: Int) {
        interactor.getInstructionsByIdFromApi(id)
    }

    fun getNutrientByIdFromApi(id: Int) {
        interactor.getNutrientByIdFromApi(id)
    }

    fun getDishAdvancedInfoFromDb(id: Int) = interactor.getDishAdvancedInfoFromDb(id)

    fun setDishMark(dishAdvancedInfo: DishAdvancedInfo) {
        interactor.setDishMark(Converter.convertDishAdvancedInfoToDish(dishAdvancedInfo))
    }

    fun putDishAlarmToDb(dishAlarm: DishAlarm) {
        interactor.putDishAlarmToDb(dishAlarm)
    }
}