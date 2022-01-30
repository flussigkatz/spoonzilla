package xyz.flussigkatz.spoonzilla

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import xyz.flussigkatz.spoonzilla.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        binding.mainNavigation.menu.findItem(R.id.homeFragment).isChecked = true

        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }


        binding.mainNavigation.setNavigationItemSelectedListener { menuItem ->
            if(menuItem.isCheckable) menuItem.isChecked = true
            binding.drawerLayout.closeDrawers()
            true
        }
    }
}