package xyz.flussigkatz.spoonzilla.view

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.flussigkatz.spoonzilla.R

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun checkMainTintExistAndAlphaIsZero() {
        onView(withId(R.id.main_tint)).check(matches(allOf(isDisplayed(), withAlpha(0f))))
    }

    @Test
    fun checkMainQuickSearchExist() {
        onView(withId(R.id.main_quick_search)).check(matches(isDisplayed()))
    }

    @Test
    fun checkMainRecentlyViewedIsHide() {
        onView(withId(R.id.main_recently_viewed)).check(matches(not(isDisplayed())))
    }

    @Test
    fun checkMainNavigationView() {
        onView(withId(R.id.main_navigation_view)).check(matches(not(isDisplayed())))
        onView(childAtPosition(withId(R.id.main_toolbar), 0)).perform(click())
        onView(withId(R.id.main_navigation_view)).check(matches(isDisplayed()))
        onView(withId(R.id.homeFragment)).check(matches(isDisplayed()))
        onView(withId(R.id.advancedSearchSettingsFragment)).check(matches(isDisplayed()))
        onView(withId(R.id.markedFragment)).check(matches(isDisplayed()))
        onView(withId(R.id.dishRemindsFragment)).check(matches(isDisplayed()))
        onView(withId(R.id.profileFragment)).check(matches(isDisplayed()))
        onView(withId(R.id.settingsFragment)).check(matches(isDisplayed()))
        onView(withId(R.id.logOutMenu)).check(matches(allOf(isDisplayed(), isNotEnabled())))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}