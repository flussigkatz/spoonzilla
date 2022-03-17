package xyz.flussigkatz.spoonzilla.view

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.databinding.ActivityMainBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_ADVANCED_SEARCH_SETTINGS
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_ID
import xyz.flussigkatz.spoonzilla.util.AppConst.NAVIGATE_TO_ADVANCED_SEARCH_ACTION
import xyz.flussigkatz.spoonzilla.util.AppConst.NAVIGATE_TO_DETAILS_ACTION
import xyz.flussigkatz.spoonzilla.util.NavigationHelper
import xyz.flussigkatz.spoonzilla.viewmodel.MainActivityViewModel


class MainActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(MainActivityViewModel::class.java)
    }
    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    private val receiver = Receiver()
    private var timeOnPressed = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()

        initQuickSearch()

        initReceiver()

    }

    private fun initQuickSearch() {
        binding.mainQuickSearch.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.mainAppbar.setExpanded(true)
        }
        binding.mainQuickSearch.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (binding.mainQuickSearch.hasFocus()) {
                        viewModel.putSearchQuery(query ?: "")
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (binding.mainQuickSearch.hasFocus()) {
                        viewModel.putSearchQuery(newText ?: "")
                    }
                    return false
                }

            })
    }

    private fun initNavigation() {
        setSupportActionBar(binding.mainToolbar)
        binding.mainNavigationView.menu.findItem(R.id.homeFragment).isChecked = true
        navController = Navigation.findNavController(this, R.id.main_nav_host_fragment)
        binding.mainNavigationView.menu.findItem(R.id.logOutMenu).isEnabled = false
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.homeFragment,
                R.id.advancedSearchSettingsFragment,
                R.id.profileFragment,
                R.id.settingsFragment
            ),
            drawerLayout = binding.mainDrawerLayout
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        binding.mainNavigationView.setNavigationItemSelectedListener { menuItem ->
            binding.mainQuickSearch.apply {
                queryHint = if (menuItem.itemId == R.id.homeFragment) getText(R.string.search_hint)
                else getText(R.string.advanced_search_hint)
            }
            binding.mainDrawerLayout.closeDrawers()
            val onScreenFragmentId = navController.currentDestination?.id
            if (onScreenFragmentId != menuItem.itemId) {
                binding.mainQuickSearch.setQuery(null, false)
                NavigationHelper.navigate(navController, menuItem.itemId, onScreenFragmentId)
            }
            true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        binding.mainAppbar.setExpanded(true)
        val onScreenFragmentId = navController.currentDestination?.id
        if (
            onScreenFragmentId == R.id.detailsFragment ||
            onScreenFragmentId == R.id.advancedSearchFragment
        ) {
            navController.popBackStack()
        } else {
            binding.mainQuickSearch.clearFocus()
            binding.mainDrawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initReceiver() {
        val filter = IntentFilter().also {
            it.addAction(NAVIGATE_TO_DETAILS_ACTION)
            it.addAction(NAVIGATE_TO_ADVANCED_SEARCH_ACTION)
        }
        registerReceiver(receiver, filter)
    }

    override fun onBackPressed() {
        if (
            timeOnPressed + EXIT_TIME_INTERVAL < System.currentTimeMillis() &&
            navController.currentDestination?.id == R.id.homeFragment
        ) {
            timeOnPressed = System.currentTimeMillis()
            Toast.makeText(this, resources.getText(R.string.exit_message), Toast.LENGTH_SHORT)
                .show()
        } else super.onBackPressed()
    }

    private inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                NAVIGATE_TO_DETAILS_ACTION -> {
                    NavigationHelper.navigateToDetailsFragment(
                        navController,
                        intent.getBundleExtra(KEY_DISH_ID)
                    )
                    binding.mainAppbar.setExpanded(true)
                }
                NAVIGATE_TO_ADVANCED_SEARCH_ACTION -> {
                    NavigationHelper.navigateToAdvancedSearchFragment(
                        navController,
                        intent.getBundleExtra(KEY_ADVANCED_SEARCH_SETTINGS)
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val EXIT_TIME_INTERVAL = 2000L

        fun searchFieldSwitcher(activity: Activity, state: Boolean) {
            activity.findViewById<SearchView>(R.id.main_quick_search)?.apply {
                visibility = if (state) View.VISIBLE
                else View.GONE
            }
        }
    }
}