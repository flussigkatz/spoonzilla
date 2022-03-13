package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
import javax.inject.Inject

class MealTypesDialogFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    fun putDialogItemsToPreference(key: String, set: Set<String>) {
        interactor.putDialogItemsToPreference(key, set)
    }

    fun getDialogItemsFromPreference(key: String): MutableSet<String>? {
        return interactor.getDialogItemsFromPreference(key)
    }

    fun profileExist() = !interactor.getProfile().isNullOrEmpty()
}