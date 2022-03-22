package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
import javax.inject.Inject

class DishInstructionsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    fun getInstructionsById(id: Int) = interactor.getInstructionsById(id)
}