package xyz.flussigkatz.spoonzilla.view.fragment

import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matcher
import org.hamcrest.Matchers.any
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import xyz.flussigkatz.spoonzilla.R

@RunWith(AndroidJUnit4::class)
class MarkedFragmentTest {
    private lateinit var scenario: FragmentScenario<MarkedFragment>

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
        onView(withId(R.id.marked_recycler)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                (5..19).random(),
                scrollTo()
            )
        )
    }

    @Test
    fun testMarkRecyclerViewItemsClick() {
        (0..19).random().let {
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                it,
                scrollTo()
            )
            onView(withId(R.id.marked_recycler))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        it, clickToItemWithId(R.id.dish_mark_check_box)
                    )
                )
        }
        (0..19).random().let {
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                it,
                scrollTo()
            )
            onView(withId(R.id.marked_recycler))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        it, clickToItemWithId(R.id.dish_mark_check_box)
                    )
                )
        }
        (0..19).random().let {
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                it,
                scrollTo()
            )
            onView(withId(R.id.marked_recycler))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        it, clickToItemWithId(R.id.dish_mark_check_box)
                    )
                )
        }
    }

    private fun clickToItemWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return any(View::class.java)
            }

            override fun getDescription(): String {
                return "Click on child view with specified id"
            }

            override fun perform(uiController: UiController?, view: View?) {
                view?.let {
                    val v = it.findViewById<View>(id)
                    v.performClick()
                }
            }
        }
    }
}