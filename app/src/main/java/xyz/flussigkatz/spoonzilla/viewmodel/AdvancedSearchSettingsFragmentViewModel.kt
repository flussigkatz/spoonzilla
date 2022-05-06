package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
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

    fun putDialogItemsToPreference(key: String, list: ArrayList<String>) {
        interactor.putDialogItemsToPreference(key, list.toSet())
    }

    fun getDialogItemsFromPreference(key: String): MutableList<String> {
        return interactor.getSearchSettings(key).orEmpty().toMutableList()
    }
}