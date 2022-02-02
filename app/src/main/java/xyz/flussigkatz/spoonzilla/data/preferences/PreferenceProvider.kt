package xyz.flussigkatz.spoonzilla.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class PreferenceProvider(context: Context) {
    private val appContext = context.applicationContext
    private val preference: SharedPreferences = appContext.getSharedPreferences(
        "settings_spoonzilla",
        Context.MODE_PRIVATE
    )

    fun saveAppTheme(theme: Int) {
        preference.edit() { putInt(KEY_APP_THEME, theme) }
    }

    fun getAppTheme(): Int {
        return preference.getInt(KEY_APP_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    companion object {
        private const val KEY_APP_THEME = "app_theme"
    }
}