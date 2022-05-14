package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
import javax.inject.Inject

class DetailsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    fun getDishAdvancedInfoFromDb(dishId: Int) = interactor.getDishAdvancedInfoFromDb(dishId)

    fun getRecipeByIdFromApi(dishId: Int) {
        interactor.getRecipeByIdFromApi(dishId)
    }
}