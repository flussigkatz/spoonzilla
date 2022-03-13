package xyz.flussigkatz.spoonzilla.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class PreferenceProvider(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        SETTINGS_FILE_NAME,
        Context.MODE_PRIVATE
    )

    fun saveAppTheme(theme: Int) {
        preferences.edit() { putInt(KEY_APP_THEME, theme) }
    }

    fun getAppTheme() = preferences.getInt(KEY_APP_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

    fun putDialogItems(key: String, set: Set<String>) {
        preferences.edit() { putStringSet(key, set) }
    }

    fun getDialogItems(key: String): MutableSet<String>? = preferences.getStringSet(key, null)

    fun setAdvancedInfoState(state: Boolean) {
        preferences.edit() { putBoolean(KEY_ADVANCED_INFO_STATE, state) }
    }

    fun getAdvancedInfoState() = preferences.getBoolean(KEY_ADVANCED_INFO_STATE, true)

    fun setProfile(profile: String?) {
        preferences.edit() { putString(KEY_PROFILE, profile) }
    }

    fun getProfile() = preferences.getString(KEY_PROFILE, null)

    fun saveAdvancedSearchSwitchState(key: String, state: Boolean) {
        preferences.edit() { putBoolean(key, state) }
    }

    fun getAdvancedSearchSwitchState(key: String) = preferences.getBoolean(key, false)

    companion object {
        private const val KEY_APP_THEME = "key_app_theme"
        private const val KEY_PROFILE = "key_profile"
        private const val KEY_ADVANCED_INFO_STATE = "key_advanced_info_state"
        private const val SETTINGS_FILE_NAME = "settings_spoonzilla"
    }
}