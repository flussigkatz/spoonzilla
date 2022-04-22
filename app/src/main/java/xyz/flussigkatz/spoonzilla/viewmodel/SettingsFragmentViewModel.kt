package xyz.flussigkatz.spoonzilla.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
import javax.inject.Inject

class SettingsFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val nightMode: Int

    init {
        App.instance.dagger.inject(this)
        nightMode = getNightModeFromPreferences()
    }

    fun setNightMode(mode: Int, activity: Activity) {
        val mMode = interactor.getNightModeFromPreferences()
        if (mMode != mode) {
            interactor.setNightMode(mode)
            activity.recreate()
            getNightModeFromPreferences()
        }
    }

    private fun getNightModeFromPreferences() = interactor.getNightModeFromPreferences()
}