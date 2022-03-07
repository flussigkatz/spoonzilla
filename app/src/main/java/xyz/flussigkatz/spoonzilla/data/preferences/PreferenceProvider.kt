package xyz.flussigkatz.spoonzilla.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class PreferenceProvider(context: Context) {
    private val preference: SharedPreferences = context.getSharedPreferences(
        SETTINGS_FILE_NAME,
        Context.MODE_PRIVATE
    )

    fun saveAppTheme(theme: Int) {
        preference.edit() { putInt(KEY_APP_THEME, theme) }
    }

    fun getAppTheme() = preference.getInt(KEY_APP_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

    fun putCuisine(cuisine: Set<String>) {
        preference.edit() { putStringSet(KEY_CUISINE, cuisine) }
    }

    fun getCuisine(): MutableSet<String>? = preference.getStringSet(KEY_CUISINE, mutableSetOf())

    fun putDiets(diets: Set<String>) {
        preference.edit() { putStringSet(KEY_DIETS, diets) }
    }

    fun getDiets(): MutableSet<String>? = preference.getStringSet(KEY_DIETS, mutableSetOf())

    fun putIntolerances(intolerances: Set<String>) {
        preference.edit() { putStringSet(KEY_INTOLERANCES, intolerances) }
    }

    fun getIntolerances(): MutableSet<String>? =
        preference.getStringSet(KEY_INTOLERANCES, mutableSetOf())

    fun putMealTypes(mealTypes: Set<String>) {
        preference.edit() { putStringSet(KEY_MEAL_TYPES, mealTypes) }
    }

    fun getMealTypes(): MutableSet<String>? =
        preference.getStringSet(KEY_MEAL_TYPES, mutableSetOf())

    fun setAdvancedInfoState(state: Boolean) {
        preference.edit() { putBoolean(KEY_ADVANCED_INFO_STATE, state) }
    }

    fun getAdvancedInfoState() = preference.getBoolean(KEY_ADVANCED_INFO_STATE, true
    )

    companion object {
        private const val KEY_APP_THEME = "key_app_theme"
        private const val KEY_ADVANCED_INFO_STATE = "key_advanced_info_state"
        private const val KEY_CUISINE = "key_cuisine"
        private const val KEY_DIETS = "key_diets"
        private const val KEY_INTOLERANCES = "key_intolerances"
        private const val KEY_MEAL_TYPES = "key_meal_types"
        private const val SETTINGS_FILE_NAME = "settings_spoonzilla"
    }
}