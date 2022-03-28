package xyz.flussigkatz.spoonzilla.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.core.content.edit
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_CUISINE_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DIET_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_INTOLERANCE_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_MEAT_TYPE_FROM_PROFILE

class PreferenceProvider(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        SETTINGS_FILE_NAME,
        Context.MODE_PRIVATE
    )

    fun saveAppTheme(theme: Int) {
        preferences.edit() { putInt(KEY_APP_THEME, theme) }
    }

    fun getAppTheme() = preferences.getInt(KEY_APP_THEME, MODE_NIGHT_FOLLOW_SYSTEM)

    fun putDialogItems(key: String, set: Set<String>?) {
        preferences.edit() { putStringSet(key, set) }
    }

    fun getDialogItems(key: String): MutableSet<String>? = preferences.getStringSet(key, null)

    fun setAdvancedInfoState(state: Boolean) {
        preferences.edit() { putBoolean(KEY_ADVANCED_INFO_STATE, state) }
    }

    fun getAdvancedInfoState() = preferences.getBoolean(KEY_ADVANCED_INFO_STATE, true)

    fun setMetric(metric: Boolean) {
        preferences.edit() { putBoolean(KEY_METRIC, metric) }
    }

    fun getMetric() = preferences.getBoolean(KEY_METRIC, true)

    fun setProfile(profile: String?) {
        preferences.edit() { putString(KEY_PROFILE, profile) }
        if (profile.isNullOrEmpty()) {
            putDialogItems(KEY_CUISINE_FROM_PROFILE, null)
            putDialogItems(KEY_DIET_FROM_PROFILE, null)
            putDialogItems(KEY_INTOLERANCE_FROM_PROFILE, null)
            putDialogItems(KEY_MEAT_TYPE_FROM_PROFILE, null)
        }
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
        private const val KEY_METRIC = "key_metric"
        private const val SETTINGS_FILE_NAME = "settings_spoonzilla"
    }
}