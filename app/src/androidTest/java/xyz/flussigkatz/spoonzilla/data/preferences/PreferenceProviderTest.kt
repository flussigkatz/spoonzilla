package xyz.flussigkatz.spoonzilla.data.preferences

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferenceProviderTest {
    private lateinit var preferenceProvider: PreferenceProvider

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        preferenceProvider = PreferenceProvider(context as Application)
    }

    @Test
    fun checkPutAndGetNightModeValue() {
        preferenceProvider.saveNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        var nightMode = preferenceProvider.getNightMode()
        assertEquals(nightMode, AppCompatDelegate.MODE_NIGHT_YES)
        preferenceProvider.saveNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        nightMode = preferenceProvider.getNightMode()
        assertEquals(nightMode, AppCompatDelegate.MODE_NIGHT_NO)
    }

    @Test
    fun checkPutAndGetDialogItems() {
        val dialogItems = setOf("1", "2", "3")
        val key = "KEY_DIALOG_ITEMS_TEST"
        preferenceProvider.putDialogItems(key, dialogItems)
        val res = preferenceProvider.getDialogItems(key)
        assertEquals(dialogItems, res)
    }

    @Test
    fun checkPutAndGetMetricValue() {
        val metric = preferenceProvider.getMetric()
        preferenceProvider.setMetric(!metric)
        val res = preferenceProvider.getMetric()
        assertNotEquals(metric, res)
    }

    @Test
    fun checkPutAndGetProfile() {
        val profile = "testprofile@testmailserv.com"
        preferenceProvider.setProfile(profile)
        val res = preferenceProvider.getProfile()
        assertEquals(profile, res)
    }

    @Test
    fun checkPutAndGetAdvancedSearchSwitchState() {
        val key = "KEY_SWITCH_STATE_TEST"
        val state = preferenceProvider.getAdvancedSearchSwitchState(key)
        preferenceProvider.saveAdvancedSearchSwitchState(key, !state)
        val res = preferenceProvider.getAdvancedSearchSwitchState(key)
        assertNotEquals(state, res)
    }

    @Test
    fun checkPutAndGetPersonalPreferencesSwitchState() {
        val key = "KEY_SWITCH_STATE_TEST"
        val state = preferenceProvider.getPersonalPreferencesSwitchState(key)
        preferenceProvider.savePersonalPreferencesSwitchState(key, !state)
        val res = preferenceProvider.getPersonalPreferencesSwitchState(key)
        assertNotEquals(state, res)
    }
}