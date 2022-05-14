package xyz.flussigkatz.spoonzilla.view.fragment

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import xyz.flussigkatz.spoonzilla.R

@RunWith(AndroidJUnit4::class)
class AdvancedSearchSettingsFragmentTest {
    private lateinit var scenario: FragmentScenario<AdvancedSearchSettingsFragment>

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
    fun checkEnabledButtonCuisine() {
        onView(withId(R.id.include_cuisine_button)).check(matches(isEnabled()))
        onView(withId(R.id.cuisine_switch)).perform(click())
        onView(withId(R.id.include_cuisine_button)).check(matches(not(isEnabled())))
    }

    @Test
    fun checkEnabledButtonDiet() {
        onView(withId(R.id.include_diet_button)).check(matches(isEnabled()))
        onView(withId(R.id.diet_switch)).perform(click())
        onView(withId(R.id.include_diet_button)).check(matches(not(isEnabled())))
    }

    @Test
    fun checkEnabledButtonIntolerances() {
        onView(withId(R.id.include_intolerances_button)).check(matches(isEnabled()))
        onView(withId(R.id.intolerances_switch)).perform(click())
        onView(withId(R.id.include_intolerances_button)).check(matches(not(isEnabled())))
    }

    @Test
    fun checkEnabledButtonMealType() {
        onView(withId(R.id.include_meal_type_button)).check(matches(isEnabled()))
        onView(withId(R.id.meal_type_switch)).perform(click())
        onView(withId(R.id.include_meal_type_button)).check(matches(not(isEnabled())))
    }

    @Test
    fun timeCookEditTextViewExist() {
        onView(withId(R.id.time_cook)).check(matches(isDisplayed()))
    }

    @Test
    fun instructionsSwitchViewExist() {
        onView(withId(R.id.instructions_switch)).check(matches(isDisplayed()))
    }

    @Test
    fun searchButtonExistAndEnabled() {
        onView(withId(R.id.advanced_search_button)).check(
            matches(
                allOf(
                    isDisplayed(),
                    isEnabled()
                )
            )
        )
    }
}