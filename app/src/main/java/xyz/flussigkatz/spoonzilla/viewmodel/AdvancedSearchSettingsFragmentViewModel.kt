package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
import xyz.flussigkatz.spoonzilla.view.dialog.DietsDialogFragment
import xyz.flussigkatz.spoonzilla.view.dialog.IntolerancesDialogFragment
import xyz.flussigkatz.spoonzilla.view.dialog.MealTypesDialogFragment
import javax.inject.Inject

class AdvancedSearchSettingsFragmentViewModel : ViewModel() {

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    fun saveAdvancedSearchSwitchState(key: String, state: Boolean) {
        interactor.saveAdvancedSearchSwitchState(key, state)
    }

    fun getAdvancedSearchSwitchState(key: String) = interactor.getAdvancedSearchSwitchState(key)

    fun existProfile() = !interactor.getProfile().isNullOrEmpty()

    fun putDialogItemsToPreference(key: String, set: Set<String>) {
        interactor.putDialogItemsToPreference(key, set)
    }

    fun getDialogItemsFromPreference(key: String): MutableList<String> {
        return interactor.getSearchSettings(key).orEmpty().toMutableList()
    }
}