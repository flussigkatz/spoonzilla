package xyz.flussigkatz.spoonzilla.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.view.dialog.IntolerancesDialogFragment
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.data.preferences.PreferenceProvider
import xyz.flussigkatz.spoonzilla.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    @Inject
    lateinit var preferences: PreferenceProvider

    init {
        App.instance.dagger.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = Navigation.findNavController(this, R.id.main_nav_host_fragment)

        binding.mainNavigationView.menu.findItem(R.id.homeFragment).isChecked = true

        binding.mainTopAppBar.setNavigationOnClickListener {
            binding.mainDrawerLayout.openDrawer(GravityCompat.START)
        }


        binding.mainNavigationView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.isCheckable) menuItem.isChecked = true
            binding.mainDrawerLayout.closeDrawers()
            true
        }
    }
}