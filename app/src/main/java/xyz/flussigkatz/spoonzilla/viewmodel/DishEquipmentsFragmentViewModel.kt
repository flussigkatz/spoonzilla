package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
import javax.inject.Inject

class DishEquipmentsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    fun getEquipmentsByIdFromDb(id: Int) = interactor.getEquipmentsByIdFromDb(id)

    fun getEquipmentsByIdFromApi(dishId: Int) {
        interactor.getEquipmentsByIdFromApi(dishId)
    }

    fun getEquipmentsToListByIdFromDb(dishId: Int) = interactor.getEquipmentsToListByIdFromDb(dishId)

}