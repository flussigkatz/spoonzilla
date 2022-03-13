package xyz.flussigkatz.spoonzilla.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import xyz.flussigkatz.spoonzilla.databinding.FragmentAdvancedSearchSettingsBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_ADVANCED_SEARCH_QUERY
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_INSTRUCTIONS_SWITCH
import xyz.flussigkatz.spoonzilla.util.AppConst.NAVIGATE_TO_ADVANCED_SEARCH_ACTION
import xyz.flussigkatz.spoonzilla.viewmodel.AdvancedSearchSettingsFragmentViewModel

class AdvancedSearchSettingsFragment : Fragment() {
    private val viewModel: AdvancedSearchSettingsFragmentViewModel by activityViewModels()
    private lateinit var binding: FragmentAdvancedSearchSettingsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdvancedSearchSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCuisines()

        initDiets()

        initIntolerances()

        initMealTypes()

        initInstructions()

    }

    private fun initCuisines() {
        viewModel.getAdvancedSearchSwitchState(KEY_CUISINE_SWITCH).let {
            binding.includeCuisineButton.isEnabled = !it
            binding.cuisineSwitch.isChecked = it
        }
        binding.cuisineSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.includeCuisineButton.isEnabled = !isChecked
            viewModel.saveAdvancedSearchSwitchState(KEY_CUISINE_SWITCH, isChecked)
        }
        binding.includeCuisineButton.setOnClickListener {
            viewModel.getCuisineDialogFragment().show(parentFragmentManager, KEY_CUISINE)
        }
    }

    private fun initDiets() {
        viewModel.getAdvancedSearchSwitchState(KEY_DIETS_SWITCH).let {
            binding.includeDietButton.isEnabled = !it
            binding.dietSwitch.isChecked = it
        }
        binding.dietSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.includeDietButton.isEnabled = !isChecked
            viewModel.saveAdvancedSearchSwitchState(KEY_DIETS_SWITCH, isChecked)
        }
        binding.includeDietButton.setOnClickListener {
            viewModel.getDietsDialogFragment().show(parentFragmentManager, KEY_DIETS)
        }
    }

    private fun initIntolerances() {
        viewModel.getAdvancedSearchSwitchState(KEY_INTOLERANCES_SWITCH).let {
            binding.includeIntolerancesButton.isEnabled = !it
            binding.intolerancesSwitch.isChecked = it
        }
        binding.intolerancesSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.includeIntolerancesButton.isEnabled = !isChecked
            viewModel.saveAdvancedSearchSwitchState(KEY_INTOLERANCES_SWITCH, isChecked)
        }
        binding.includeIntolerancesButton.setOnClickListener {
            viewModel.getIntolerancesDialogFragment()
                .show(parentFragmentManager, KEY_INTOLERANCES)
        }
    }

    private fun initMealTypes() {
        viewModel.getAdvancedSearchSwitchState(KEY_MEAL_TYPES_SWITCH).let {
            binding.includeMealTypeButton.isEnabled = !it
            binding.mealTypeSwitch.isChecked = it
        }
        binding.mealTypeSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.includeMealTypeButton.isEnabled = !isChecked
            viewModel.saveAdvancedSearchSwitchState(KEY_MEAL_TYPES_SWITCH, isChecked)
        }
        binding.includeMealTypeButton.setOnClickListener {
            viewModel.getMealTypesDialogFragment().show(parentFragmentManager, KEY_MEAL_TYPES)
        }
    }

    private fun initInstructions() {
        viewModel.getAdvancedSearchSwitchState(KEY_INSTRUCTIONS_SWITCH).let {
            binding.instructionsSwitch.isChecked = it
        }
        binding.instructionsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveAdvancedSearchSwitchState(KEY_INSTRUCTIONS_SWITCH, isChecked)
        }
    }

    private fun initSearch() {
        binding.advancedSearchButton.setOnClickListener {
            if (!binding.advancedSearchView.text.isNullOrBlank()) {
                val query = binding.advancedSearchView.text.toString()
                val intent = Intent().apply {
                    action = NAVIGATE_TO_ADVANCED_SEARCH_ACTION
                    val bundle = Bundle().apply {
                        putString(KEY_ADVANCED_SEARCH_QUERY, query)
                    }
                    putExtra(KEY_ADVANCED_SEARCH_QUERY, bundle)
                }
                requireActivity().sendBroadcast(intent)
            }
        }
    }


    companion object {
        private const val KEY_CUISINE = "key_cuisine"
        private const val KEY_DIETS = "key_diets"
        private const val KEY_INTOLERANCES = "key_intolerances"
        private const val KEY_MEAL_TYPES = "key_meal_types"
        private const val KEY_CUISINE_SWITCH = "key_cuisine_switch"
        private const val KEY_DIETS_SWITCH = "key_diets_switch"
        private const val KEY_INTOLERANCES_SWITCH = "key_intolerances_switch"
        private const val KEY_MEAL_TYPES_SWITCH = "key_meal_types_switch"
    }
}