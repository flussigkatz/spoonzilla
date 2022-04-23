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
}