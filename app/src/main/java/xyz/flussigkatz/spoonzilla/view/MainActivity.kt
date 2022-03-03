package xyz.flussigkatz.spoonzilla.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
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

    init {
        App.instance.dagger.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()

        initSearch()

        initReceiver()
    }

    private fun initSearch() {
        binding.mainSearch.setOnQueryTextListener(
            object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!binding.mainSearch.isIconified) interactor.putSearchQuery(query)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (!binding.mainSearch.isIconified) interactor.putSearchQuery(newText)
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
                R.id.settingsFragment
            ),
            drawerLayout = binding.mainDrawerLayout
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration1)

        binding.mainToolbar.setNavigationOnClickListener {
            val onScreenFragmentId = navController.currentDestination?.id
            if (onScreenFragmentId == R.id.detailsFragment) {
                navController.popBackStack()
                binding.mainNavigationView.menu.findItem(R.id.homeFragment).isChecked = true
            } else {
                binding.mainSearch.clearFocus()
                binding.mainDrawerLayout.openDrawer(GravityCompat.START)
            }
        }


        binding.mainNavigationView.setNavigationItemSelectedListener { menuItem ->
            binding.mainDrawerLayout.closeDrawers()
            if (menuItem.itemId == R.id.homeFragment) binding.mainSearch.visibility = View.VISIBLE
            else binding.mainSearch.visibility = View.GONE
            val onScreenFragmentId = navController.backQueue.last().destination.id
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

    private inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AppConst.NAVIGATE_TO_DETAILS_ACTION) {
                binding.mainSearch.visibility = View.GONE
                NavigationHelper.navigateToDetailsFragment(
                    navController,
                    intent.getBundleExtra(AppConst.DISH_ID_KEY)
                )
                binding.mainNavigationView.menu.findItem(R.id.homeFragment).isChecked = false
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}