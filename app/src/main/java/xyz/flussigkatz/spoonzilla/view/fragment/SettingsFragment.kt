package xyz.flussigkatz.spoonzilla.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.databinding.FragmentSettingsBinding
import xyz.flussigkatz.spoonzilla.util.AppConst
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_CUISINE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_CUISINES_DIALOG
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_CUISINE_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DIET
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DIETS_DIALOG
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DIET_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_INTOLERANCE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_INTOLERANCES_DIALOG
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_INTOLERANCE_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_MEAL_TYPES_DIALOG
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_MEAL_TYPE_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_MEAl_TYPE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_PERSONAL_PREFERENCES
import xyz.flussigkatz.spoonzilla.util.AppConst.SORT_FLAG_POPULAR
import xyz.flussigkatz.spoonzilla.util.AppConst.SORT_FLAG_RANDOM
import xyz.flussigkatz.spoonzilla.view.dialog.CuisineDialogFragment
import xyz.flussigkatz.spoonzilla.view.dialog.DietsDialogFragment
import xyz.flussigkatz.spoonzilla.view.dialog.IntolerancesDialogFragment
import xyz.flussigkatz.spoonzilla.view.dialog.MealTypesDialogFragment
import xyz.flussigkatz.spoonzilla.viewmodel.SettingsFragmentViewModel
import java.lang.IllegalArgumentException

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragmentResultListener()
        initNightModeSpinner()
        initHomePageContentSpinner()
        initCuisines()
        initDiets()
        initIntolerances()
        initMealTypes()
        initProfilePreferencesSwitch()
        initMetricSwitch()
    }

    private fun initMetricSwitch() {
        binding.settingsMetricSwitch.apply {
            isChecked = viewModel.getMetric()
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.setMetric(isChecked)
            }
        }
    }

    private fun initProfilePreferencesSwitch() {
        binding.settingsProfilePreferencesSwitch.apply {
            isChecked = viewModel.getSwitchState(KEY_PERSONAL_PREFERENCES)
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.setSwitchState(KEY_PERSONAL_PREFERENCES, isChecked)
            }
        }
    }

    private fun initNightModeSpinner() {
        val items = arrayOf(
            resources.getText(R.string.mode_night_no),
            resources.getText(R.string.mode_night_yes),
            resources.getText(R.string.mode_night_system_follow),
            resources.getText(R.string.mode_night_auto_battery)
        )
        val mAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, items)
        val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                parent?.let {
                    val mode = when (position) {
                        MODE_NIGHT_NO_INDEX -> AppCompatDelegate.MODE_NIGHT_NO
                        MODE_NIGHT_YES_INDEX -> AppCompatDelegate.MODE_NIGHT_YES
                        MODE_NIGHT_SYSTEM_FOLLOW_INDEX -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        MODE_NIGHT_AUTO_BATTERY_INDEX -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                        else -> throw IllegalArgumentException("Wrong argument for night mode")
                    }
                    viewModel.setNightMode(mode, requireActivity())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        binding.settingsThemeSpinner.apply {
            adapter = mAdapter
            val mode = when (viewModel.nightMode) {
                AppCompatDelegate.MODE_NIGHT_NO -> MODE_NIGHT_NO_INDEX
                AppCompatDelegate.MODE_NIGHT_YES -> MODE_NIGHT_YES_INDEX
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> MODE_NIGHT_SYSTEM_FOLLOW_INDEX
                AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> MODE_NIGHT_AUTO_BATTERY_INDEX
                else -> throw IllegalArgumentException("Wrong argument for night mode spinner")
            }
            setSelection(mode)
            onItemSelectedListener = itemSelectedListener
        }
    }

    private fun initHomePageContentSpinner() {
        val items = arrayOf(
            resources.getText(R.string.home_page_content_popular),
            resources.getText(R.string.home_page_content_random),
        )
        val mAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, items)
        val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                parent?.let {
                    val flag = when (position) {
                        POPULAR_INDEX -> SORT_FLAG_POPULAR
                        RANDOM_INDEX -> SORT_FLAG_RANDOM
                        else -> throw IllegalArgumentException("Wrong argument for home page content")
                    }
                    viewModel.setHomePageContentFlag(flag)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        binding.settingsHomePageContentSpinner.apply {
            adapter = mAdapter
            val flag = when (viewModel.homePageContent) {
                SORT_FLAG_POPULAR -> POPULAR_INDEX
                SORT_FLAG_RANDOM -> RANDOM_INDEX
                else -> throw IllegalArgumentException("Wrong argument for home page content")
            }
            setSelection(flag)
            onItemSelectedListener = itemSelectedListener
        }
    }

    private fun initCuisines() {
        binding.settingsCuisine.setOnClickListener {
            val markedItems = viewModel.getDialogItemsFromPreference(KEY_CUISINE_FROM_PROFILE)
            val dialog = CuisineDialogFragment(markedItems)
            dialog.show(parentFragmentManager, KEY_CUISINES_DIALOG)
        }
    }

    private fun initDiets() {
        binding.settingsDiets.setOnClickListener {
            val markedItems = viewModel.getDialogItemsFromPreference(KEY_DIET_FROM_PROFILE)
            val dialog = DietsDialogFragment(markedItems)
            dialog.show(parentFragmentManager, KEY_DIETS_DIALOG)
        }
    }

    private fun initIntolerances() {
        binding.settingsIntolerances.setOnClickListener {
            val markedItems = viewModel.getDialogItemsFromPreference(KEY_INTOLERANCE_FROM_PROFILE)
            val dialog = IntolerancesDialogFragment(markedItems)
            dialog.show(parentFragmentManager, KEY_INTOLERANCES_DIALOG)
        }
    }

    private fun initMealTypes() {
        binding.settingsMealTypes.setOnClickListener {
            val markedItems = viewModel.getDialogItemsFromPreference(KEY_MEAL_TYPE_FROM_PROFILE)
            val dialog = MealTypesDialogFragment(markedItems)
            dialog.show(parentFragmentManager, KEY_MEAL_TYPES_DIALOG)
        }
    }

    private fun initFragmentResultListener() {
        setFragmentResultListener(AppConst.KEY_RESULT_REQUEST_DIALOG) { _, bundle ->
            bundle.getStringArrayList(KEY_CUISINE)?.let {
                viewModel.putDialogItemsToPreference(KEY_CUISINE_FROM_PROFILE, it)
            }
            bundle.getStringArrayList(KEY_DIET)?.let {
                viewModel.putDialogItemsToPreference(KEY_DIET_FROM_PROFILE, it)
            }
            bundle.getStringArrayList(KEY_INTOLERANCE)?.let {
                viewModel.putDialogItemsToPreference(KEY_INTOLERANCE_FROM_PROFILE, it)
            }
            bundle.getStringArrayList(KEY_MEAl_TYPE)?.let {
                viewModel.putDialogItemsToPreference(KEY_MEAL_TYPE_FROM_PROFILE, it)
            }
        }
    }

    companion object {
        private const val MODE_NIGHT_NO_INDEX = 0
        private const val MODE_NIGHT_YES_INDEX = 1
        private const val MODE_NIGHT_SYSTEM_FOLLOW_INDEX = 2
        private const val MODE_NIGHT_AUTO_BATTERY_INDEX = 3
        private const val POPULAR_INDEX = 0
        private const val RANDOM_INDEX = 1
    }
}