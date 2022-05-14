package xyz.flussigkatz.spoonzilla.data.preferences

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.core.content.edit
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_CUISINE_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DIET_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_INTOLERANCE_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_MEAL_TYPE_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.SORT_FLAG_POPULAR

class PreferenceProvider(application: Application) {
    private var context: Context = application.applicationContext
    private val preferences: SharedPreferences = context.getSharedPreferences(
        SETTINGS_FILE_NAME,
        Context.MODE_PRIVATE
    )

    fun saveNightMode(mode: Int) {
        preferences.edit() { putInt(KEY_NIGHT_MODE, mode) }
    }

    fun getNightMode() = preferences.getInt(KEY_NIGHT_MODE, MODE_NIGHT_FOLLOW_SYSTEM)

    fun saveHomePageContent(flag: String) {
        preferences.edit() { putString(KEY_HOME_PAGE_CONTENT_KEY, flag) }
    }

    fun getHomePageContent() = preferences.getString(KEY_HOME_PAGE_CONTENT_KEY, SORT_FLAG_POPULAR)

    fun putDialogItems(key: String, set: Set<String>?) {
        preferences.edit() { putStringSet(key, set) }
    }

    fun getDialogItems(key: String): MutableSet<String>? = preferences.getStringSet(key, null)

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
            putDialogItems(KEY_MEAL_TYPE_FROM_PROFILE, null)
        }
    }

    fun getProfile() = preferences.getString(KEY_PROFILE, null)

    fun saveAdvancedSearchSwitchState(key: String, state: Boolean) {
        preferences.edit() { putBoolean(key, state) }
    }

    fun getAdvancedSearchSwitchState(key: String) = preferences.getBoolean(key, false)

    fun savePersonalPreferencesSwitchState(key: String, state: Boolean) {
        preferences.edit() { putBoolean(key, state) }
    }

    fun getPersonalPreferencesSwitchState(key: String) = preferences.getBoolean(key, true)

    companion object {
        private const val KEY_HOME_PAGE_CONTENT_KEY = "key_home_page_content_key"
        private const val KEY_NIGHT_MODE = "key_app_theme"
        private const val KEY_PROFILE = "key_profile"
        private const val KEY_METRIC = "key_metric"
        private const val SETTINGS_FILE_NAME = "settings_spoonzilla"
    }
}