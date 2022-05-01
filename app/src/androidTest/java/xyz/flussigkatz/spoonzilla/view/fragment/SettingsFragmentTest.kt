package xyz.flussigkatz.spoonzilla.view.fragment

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import xyz.flussigkatz.spoonzilla.R


@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest {
    private lateinit var scenario: FragmentScenario<SettingsFragment>

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_Spoonzilla)
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @After
    fun endUp() {
        scenario.moveToState(Lifecycle.State.DESTROYED)
    }

    @Test
    fun settingsThemeSpinnerExist() {
        onView(withId(R.id.settings_theme_spinner)).check(matches(isDisplayed()))
    }

    @Test
    fun settingsCuisineExist() {
        onView(withId(R.id.settings_cuisine)).check(matches(isDisplayed()))
    }

    @Test
    fun settingsDietExist() {
        onView(withId(R.id.settings_diets)).check(matches(isDisplayed()))
    }

    @Test
    fun settingsMealTypesExist() {
        onView(withId(R.id.settings_meal_types)).check(matches(isDisplayed()))
    }

    @Test
    fun settingsIntolerancesExist() {
        onView(withId(R.id.settings_intolerances)).check(matches(isDisplayed()))
    }

    @Test
    fun settingsProfilePreferencesSwitchExist() {
        onView(withId(R.id.settings_profile_preferences_switch)).check(matches(isDisplayed()))
    }

    @Test
    fun settingsMetricSwitchExist() {
        onView(withId(R.id.settings_metric_switch)).check(matches(isDisplayed()))
    }
}