package xyz.flussigkatz.spoonzilla.view.fragment

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import xyz.flussigkatz.spoonzilla.R

@RunWith(AndroidJUnit4::class)
class DishRemindsFragmentTest {
    private lateinit var scenario: FragmentScenario<DishRemindsFragment>

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_Spoonzilla)
        scenario.moveToState(STARTED)
    }

    @After
    fun endUp() {
        scenario.moveToState(Lifecycle.State.DESTROYED)
    }

    @Test
    fun testScrollRecyclerView() {
        onView(withId(R.id.dish_reminds_recycler)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                (5..19).random(),
                scrollTo()
            )
        )
    }
}