package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
import javax.inject.Inject

class CuisineDialogFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    fun putCuisine(cuisine: Set<String>) {
        interactor.putCuisineToPreference(cuisine)
    }

    fun getCuisine(): MutableSet<String>? = interactor.getCuisineFromPreference()
}