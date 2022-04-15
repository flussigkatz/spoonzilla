package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.core_api.entity.DishAlarm
import xyz.flussigkatz.spoonzilla.domain.Interactor
import javax.inject.Inject

class DishAlarmFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val dishAlarmsList: Observable<List<DishAlarm>>

    init {
        App.instance.dagger.inject(this)
        dishAlarmsList = interactor.getDishAlarmsFromDb()
    }

    fun updateDishRemind(dishAlarm: DishAlarm) {
        interactor.updateDishAlarm(dishAlarm)
    }

    fun deleteDishAlarm(localId: Int) {
        interactor.deleteDishAlarm(localId)
    }
}