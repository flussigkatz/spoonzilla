package xyz.flussigkatz.spoonzilla.view.fragment

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import xyz.flussigkatz.spoonzilla.R


@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest {
    private lateinit var scenario: FragmentScenario<ProfileFragment>

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
    fun profileAvatarImageViewExist() {
        onView(withId(R.id.profile_avatar)).check(matches(isDisplayed()))
    }

    @Test
    fun profileStatusTextViewExistAndHaveText() {
        onView(withId(R.id.profile_status)).check(
            matches(
                allOf(
                    isDisplayed(),
                    withText("Anonymous")
                )
            )
        )
    }

    @Test
    fun profileCreateNewButtonExistAndEnabled() {
        onView(withId(R.id.profile_create_new)).check(matches(allOf(isDisplayed(), isEnabled())))
    }

    @Test
    fun profileSignButtonExistAndEnabled() {
        onView(withId(R.id.profile_sign)).check(matches(allOf(isDisplayed(), isEnabled())))
    }
}