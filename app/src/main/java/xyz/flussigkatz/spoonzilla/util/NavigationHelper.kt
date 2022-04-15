package xyz.flussigkatz.spoonzilla.util

import android.os.Bundle
import androidx.navigation.NavController
import xyz.flussigkatz.spoonzilla.R

object NavigationHelper {

    fun navigate(
        navController: NavController,
        menuItemId: Int,
        onScreenFragmentId: Int?
    ) {
        when (onScreenFragmentId) {
            R.id.homeFragment -> {
                when (menuItemId) {
                    R.id.profileFragment -> {
                        navController.navigate(R.id.action_homeFragment_to_profileFragment)
                    }
                    R.id.settingsFragment -> {
                        navController.navigate(R.id.action_homeFragment_to_settingsFragment)
                    }
                    R.id.advancedSearchSettingsFragment -> {
                        navController.navigate(R.id.action_homeFragment_to_advancedSearchSettingsFragment)
                    }
                    R.id.markedFragment -> {
                        navController.navigate(R.id.action_homeFragment_to_markedFragment)
                    }
                    R.id.dishRemindsFragment -> {
                        navController.navigate(R.id.action_homeFragment_to_dishRemindsFragment)
                    }
                }
            }
            R.id.profileFragment -> {
                when (menuItemId) {
                    R.id.homeFragment -> {
                        navController.navigate(R.id.action_profileFragment_to_homeFragment)
                    }
                    R.id.settingsFragment -> {
                        navController.navigate(R.id.action_profileFragment_to_settingsFragment)
                    }
                    R.id.advancedSearchSettingsFragment -> {
                        navController.navigate(R.id.action_profileFragment_to_advancedSearchSettingsFragment)
                    }
                    R.id.markedFragment -> {
                        navController.navigate(R.id.action_profileFragment_to_markedFragment)
                    }
                    R.id.dishRemindsFragment -> {
                        navController.navigate(R.id.action_profileFragment_to_dishRemindsFragment)
                    }
                }
            }
            R.id.settingsFragment -> {
                when (menuItemId) {
                    R.id.homeFragment -> {
                        navController.navigate(R.id.action_settingsFragment_to_homeFragment)
                    }
                    R.id.profileFragment -> {
                        navController.navigate(R.id.action_settingsFragment_to_profileFragment)
                    }
                    R.id.advancedSearchSettingsFragment -> {
                        navController.navigate(R.id.action_settingsFragment_to_advancedSearchSettingsFragment)
                    }
                    R.id.markedFragment -> {
                        navController.navigate(R.id.action_settingsFragment_to_markedFragment)
                    }
                    R.id.dishRemindsFragment -> {
                        navController.navigate(R.id.action_settingsFragment_to_dishRemindsFragment)
                    }
                }
            }
            R.id.advancedSearchSettingsFragment -> {
                when (menuItemId) {
                    R.id.homeFragment -> {
                        navController.navigate(R.id.action_advancedSearchSettingsFragment_to_homeFragment)
                    }
                    R.id.profileFragment -> {
                        navController.navigate(R.id.action_advancedSearchSettingsFragment_to_profileFragment)
                    }
                    R.id.settingsFragment -> {
                        navController.navigate(R.id.action_advancedSearchSettingsFragment_to_settingsFragment)
                    }
                    R.id.markedFragment -> {
                        navController.navigate(R.id.action_advancedSearchSettingsFragment_to_markedFragment)
                    }
                    R.id.dishRemindsFragment -> {
                        navController.navigate(R.id.action_advancedSearchSettingsFragment_to_dishRemindsFragment)
                    }
                }
            }
            R.id.markedFragment -> {
                when (menuItemId) {
                    R.id.homeFragment -> {
                        navController.navigate(R.id.action_markedFragment_to_homeFragment)
                    }
                    R.id.profileFragment -> {
                        navController.navigate(R.id.action_markedFragment_to_profileFragment)
                    }
                    R.id.settingsFragment -> {
                        navController.navigate(R.id.action_markedFragment_to_settingsFragment)
                    }
                    R.id.advancedSearchSettingsFragment -> {
                        navController.navigate(R.id.action_markedFragment_to_advancedSearchSettingsFragment)
                    }
                    R.id.dishRemindsFragment -> {
                        navController.navigate(R.id.action_markedFragment_to_dishRemindsFragment)
                    }
                }
            }
            R.id.dishRemindsFragment -> {
                when (menuItemId) {
                    R.id.homeFragment -> {
                        navController.navigate(R.id.action_dishRemindsFragment_to_homeFragment)
                    }
                    R.id.markedFragment -> {
                        navController.navigate(R.id.action_dishRemindsFragment_to_markedFragment)
                    }
                    R.id.profileFragment -> {
                        navController.navigate(R.id.action_dishRemindsFragment_to_profileFragment)
                    }
                    R.id.settingsFragment -> {
                        navController.navigate(R.id.action_dishRemindsFragment_to_settingsFragment)
                    }
                    R.id.advancedSearchSettingsFragment -> {
                        navController.navigate(R.id.action_dishRemindsFragment_to_advancedSearchSettingsFragment)
                    }
                }
            }
            else -> throw IllegalArgumentException("Invalid argument: onScreenFragmentId")
        }

    }

    fun navigateToDetailsFragment(
        navController: NavController,
        bundle: Bundle?,
        onScreenFragmentId: Int?
    ) {
        when (onScreenFragmentId) {
            R.id.homeFragment -> {
                navController.navigate(
                    R.id.action_homeFragment_to_detailsFragment,
                    bundle
                )
            }
            R.id.advancedSearchFragment -> {
                navController.navigate(
                    R.id.action_advancedSearchFragment_to_detailsFragment,
                    bundle
                )
            }
            R.id.markedFragment -> {
                navController.navigate(
                    R.id.action_markedFragment_to_detailsFragment,
                    bundle
                )
            }
            R.id.dishRemindsFragment -> {
                navController.navigate(
                    R.id.action_dishRemindsFragment_to_detailsFragment,
                    bundle
                )
            }
            else -> navController.navigate(
                R.id.action_global_detailsFragment,
                bundle
            )
        }
    }

    fun navigateToAdvancedSearchFragment(
        navController: NavController,
        bundle: Bundle?,
    ) {
        navController.navigate(
            R.id.action_advancedSearchSettingsFragment_to_advancedSearchFragment,
            bundle
        )
    }
}