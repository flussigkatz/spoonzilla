package xyz.flussigkatz.spoonzilla.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.data.preferences.PreferenceProvider
import xyz.flussigkatz.spoonzilla.databinding.ActivityMainBinding
import xyz.flussigkatz.spoonzilla.domain.Interactor
import xyz.flussigkatz.spoonzilla.util.AppConst
import xyz.flussigkatz.spoonzilla.util.NavigationHelper
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var preferences: PreferenceProvider

    @Inject
    lateinit var interactor: Interactor

    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    private val receiver = Receiver()
    private var timeOnPressed = 0L

    init {
        App.instance.dagger.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()

        initQuickSearch()

        initReceiver()
    }

    private fun initQuickSearch() {
        binding.mainQuickSearch.setOnQueryTextListener(
            object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!binding.mainQuickSearch.isIconified) interactor.putSearchQuery(query)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (!binding.mainQuickSearch.isIconified) interactor.putSearchQuery(newText)
                    return false
                }

            })
    }

    private fun initNavigation() {
        setSupportActionBar(binding.mainToolbar)
        binding.mainNavigationView.menu.findItem(R.id.homeFragment).isChecked = true
        navController = Navigation.findNavController(this, R.id.main_nav_host_fragment)
        binding.mainNavigationView.menu.findItem(R.id.logOutMenu).isEnabled = false
        val appBarConfiguration1 = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.homeFragment,
                R.id.profileFragment,
                R.id.settingsFragment,
                R.id.advancedSearchSettingsFragment
            ),
            drawerLayout = binding.mainDrawerLayout
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration1)

        binding.mainToolbar.setNavigationOnClickListener {
            val onScreenFragmentId = navController.currentDestination?.id
            if (
                onScreenFragmentId == R.id.detailsFragment ||
                onScreenFragmentId == R.id.advancedSearchFragment
            ) {
                navController.popBackStack()
                binding.mainNavigationView.menu.findItem(R.id.homeFragment).isChecked = true
            } else {
                binding.mainQuickSearch.clearFocus()
                binding.mainDrawerLayout.openDrawer(GravityCompat.START)
            }
        }

        binding.mainNavigationView.setNavigationItemSelectedListener { menuItem ->
            binding.mainDrawerLayout.closeDrawers()
            val onScreenFragmentId = navController.currentDestination?.id
            NavigationHelper.navigate(navController, menuItem.itemId, onScreenFragmentId)
            true
        }
    }

    private fun initReceiver() {
        val filter = IntentFilter().also {
            it.addAction(AppConst.NAVIGATE_TO_DETAILS_ACTION)
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
            if (intent?.action == AppConst.NAVIGATE_TO_DETAILS_ACTION) {
                NavigationHelper.navigateToDetailsFragment(
                    navController,
                    intent.getBundleExtra(AppConst.DISH_ID_KEY)
                )
                binding.mainNavigationView.menu.findItem(R.id.homeFragment).isChecked = false
                binding.mainAppbar.setExpanded(true)
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val EXIT_TIME_INTERVAL = 2000L
    }
}