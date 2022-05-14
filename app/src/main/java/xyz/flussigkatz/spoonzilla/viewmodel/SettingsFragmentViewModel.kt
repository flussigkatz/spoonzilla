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
    var homePageContent: String

    init {
        App.instance.dagger.inject(this)
        nightMode = getNightModeFromPreferences()
        homePageContent = getHomePageContentFromPreferences()
    }

    fun setNightMode(mode: Int, activity: Activity) {
        if (nightMode != mode) {
            interactor.setNightMode(mode)
            nightMode = mode
            activity.recreate()
        }
    }

    fun setHomePageContentFlag(flag: String) {
        if (homePageContent != flag){
            interactor.setHomePageContent(flag)
            homePageContent = flag
        }
    }

    private fun getNightModeFromPreferences() = interactor.getNightModeFromPreferences()

    private fun getHomePageContentFromPreferences() = interactor.getHomePageContent()

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