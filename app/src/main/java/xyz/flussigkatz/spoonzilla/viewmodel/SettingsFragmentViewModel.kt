package xyz.flussigkatz.spoonzilla.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
import javax.inject.Inject

class SettingsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    var nightMode: Int

    init {
        App.instance.dagger.inject(this)
        nightMode = getNightModeFromPreferences()
    }

    fun setNightMode(mode: Int, activity: Activity) {
        nightMode = getNightModeFromPreferences()
        if (nightMode != mode) {
            interactor.setNightMode(mode)
            activity.recreate()
        }
    }

    private fun getNightModeFromPreferences() = interactor.getNightModeFromPreferences()

    fun putDialogItemsToPreference(key: String, list: ArrayList<String>) {
        interactor.putDialogItemsToPreference(key, list.toSet())
    }

    fun getDialogItemsFromPreference(key: String): MutableList<String> {
        return interactor.getSearchSettings(key).orEmpty().toMutableList()
    }

    fun setSwitchState(key: String, state: Boolean) {
        interactor.setPersonalPreferencesSwitchState(key, state)
    }

    fun getSwitchState(key: String) = interactor.getPersonalPreferencesSwitchState(key)

    fun setMetric(metric: Boolean) {
        interactor.setMetric(metric)
    }

    fun getMetric() = interactor.getMetric()
}