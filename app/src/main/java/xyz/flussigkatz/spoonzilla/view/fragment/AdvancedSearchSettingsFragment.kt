package xyz.flussigkatz.spoonzilla.view.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import xyz.flussigkatz.spoonzilla.databinding.FragmentAdvancedSearchSettingsBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_ADVANCED_SEARCH_SETTINGS
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_CUISINE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_CUISINES_DIALOG
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_CUISINE_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DIET
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DIETS_DIALOG
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DIET_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_INSTRUCTIONS_SWITCH
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_INTOLERANCE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_INTOLERANCES_DIALOG
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_INTOLERANCE_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_MAX_READY_TIME
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_MEAL_TYPES_DIALOG
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_MEAL_TYPE_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_MEAl_TYPE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_RESULT_REQUEST_DIALOG
import xyz.flussigkatz.spoonzilla.util.AppConst.NAVIGATE_TO_ADVANCED_SEARCH
import xyz.flussigkatz.spoonzilla.util.AutoDisposable
import xyz.flussigkatz.spoonzilla.view.dialog.CuisineDialogFragment
import xyz.flussigkatz.spoonzilla.view.dialog.DietsDialogFragment
import xyz.flussigkatz.spoonzilla.view.dialog.IntolerancesDialogFragment
import xyz.flussigkatz.spoonzilla.view.dialog.MealTypesDialogFragment
import xyz.flussigkatz.spoonzilla.viewmodel.AdvancedSearchSettingsFragmentViewModel

class AdvancedSearchSettingsFragment : Fragment() {
    private val viewModel: AdvancedSearchSettingsFragmentViewModel by activityViewModels()
    private lateinit var binding: FragmentAdvancedSearchSettingsBinding
    private val autoDisposable = AutoDisposable()
    private lateinit var keyCuisine: String
    private lateinit var keyDiet: String
    private lateinit var keyIntolerance: String
    private lateinit var keyMeatType: String
    private var maxReadyTime: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        autoDisposable.bindTo(lifecycle)
        binding = FragmentAdvancedSearchSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragmentResultListener()
        initCuisines()
        initDiets()
        initIntolerances()
        initMealTypes()
        initInstructions()
        initSearch()
        initTimeCookEditText()
    }

    private fun initFragmentResultListener() {
        setFragmentResultListener(KEY_RESULT_REQUEST_DIALOG) { _, bundle ->
            bundle.getStringArrayList(KEY_CUISINE)?.let {
                viewModel.putDialogItemsToPreference(KEY_CUISINE, it)
            }
            bundle.getStringArrayList(KEY_DIET)?.let {
                viewModel.putDialogItemsToPreference(KEY_DIET, it)
            }
            bundle.getStringArrayList(KEY_INTOLERANCE)?.let {
                viewModel.putDialogItemsToPreference(KEY_INTOLERANCE, it)
            }
            bundle.getStringArrayList(KEY_MEAl_TYPE)?.let {
                viewModel.putDialogItemsToPreference(KEY_MEAl_TYPE, it)
            }
        }
    }

    private fun initTimeCookEditText() {
        binding.timeCook.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank()) maxReadyTime = s.toString().toInt()
            }
        })
        val inputFilter = InputFilter { source, _, _, dest, _, _ ->
            if (!dest.isNullOrBlank()) {
                val formatSource = if (source.isDigitsOnly()) source else ""
                val intValue = (dest.toString() + formatSource.toString()).toInt()
                if (intValue in 0..999) {
                    source
                } else {
                    ""
                }
            } else source
        }
        binding.timeCook.filters = arrayOf(inputFilter)
    }

    private fun initCuisines() {
        viewModel.getAdvancedSearchSwitchState(KEY_CUISINE_SWITCH).let {
            binding.includeCuisineButton.isEnabled = !it
            binding.cuisineSwitch.isChecked = it
            keyCuisine = if (it) KEY_CUISINE_FROM_PROFILE
            else KEY_CUISINE
        }
        binding.cuisineSwitch.setOnCheckedChangeListener { _, isChecked ->
            timeCookEditTextClearFocus()
            binding.includeCuisineButton.isEnabled = !isChecked
            viewModel.saveAdvancedSearchSwitchState(KEY_CUISINE_SWITCH, isChecked)
            keyCuisine = if (isChecked) KEY_CUISINE_FROM_PROFILE
            else KEY_CUISINE
        }
        binding.includeCuisineButton.setOnClickListener {
            timeCookEditTextClearFocus()
            val markedItems = viewModel.getDialogItemsFromPreference(KEY_CUISINE)
            val dialog = CuisineDialogFragment(markedItems)
            dialog.show(parentFragmentManager, KEY_CUISINES_DIALOG)
        }
    }

    private fun initDiets() {
        viewModel.getAdvancedSearchSwitchState(KEY_DIETS_SWITCH).let {
            binding.includeDietButton.isEnabled = !it
            binding.dietSwitch.isChecked = it
            keyDiet = if (it) KEY_DIET_FROM_PROFILE
            else KEY_DIET
        }
        binding.dietSwitch.setOnCheckedChangeListener { _, isChecked ->
            timeCookEditTextClearFocus()
            binding.includeDietButton.isEnabled = !isChecked
            viewModel.saveAdvancedSearchSwitchState(KEY_DIETS_SWITCH, isChecked)
            keyDiet = if (isChecked) KEY_DIET_FROM_PROFILE
            else KEY_DIET
        }
        binding.includeDietButton.setOnClickListener {
            timeCookEditTextClearFocus()
            val markedItems = viewModel.getDialogItemsFromPreference(KEY_DIET)
            val dialog = DietsDialogFragment(markedItems)
            dialog.show(parentFragmentManager, KEY_DIETS_DIALOG)
        }
    }

    private fun initIntolerances() {
        viewModel.getAdvancedSearchSwitchState(KEY_INTOLERANCES_SWITCH).let {
            binding.includeIntolerancesButton.isEnabled = !it
            binding.intolerancesSwitch.isChecked = it
            keyIntolerance = if (it) KEY_INTOLERANCE_FROM_PROFILE
            else KEY_INTOLERANCE
        }
        binding.intolerancesSwitch.setOnCheckedChangeListener { _, isChecked ->
            timeCookEditTextClearFocus()
            binding.includeIntolerancesButton.isEnabled = !isChecked
            viewModel.saveAdvancedSearchSwitchState(KEY_INTOLERANCES_SWITCH, isChecked)
            keyIntolerance = if (isChecked) KEY_INTOLERANCE_FROM_PROFILE
            else KEY_INTOLERANCE
        }
        binding.includeIntolerancesButton.setOnClickListener {
            timeCookEditTextClearFocus()
            val markedItems = viewModel.getDialogItemsFromPreference(KEY_INTOLERANCE)
            val dialog = IntolerancesDialogFragment(markedItems)
            dialog.show(parentFragmentManager, KEY_INTOLERANCES_DIALOG)
        }
    }

    private fun initMealTypes() {
        viewModel.getAdvancedSearchSwitchState(KEY_MEAL_TYPES_SWITCH).let {
            binding.includeMealTypeButton.isEnabled = !it
            binding.mealTypeSwitch.isChecked = it
            keyMeatType = if (it) KEY_MEAL_TYPE_FROM_PROFILE
            else KEY_MEAl_TYPE
        }
        binding.mealTypeSwitch.setOnCheckedChangeListener { _, isChecked ->
            timeCookEditTextClearFocus()
            binding.includeMealTypeButton.isEnabled = !isChecked
            viewModel.saveAdvancedSearchSwitchState(KEY_MEAL_TYPES_SWITCH, isChecked)
            keyMeatType = if (isChecked) KEY_MEAL_TYPE_FROM_PROFILE
            else KEY_MEAl_TYPE
        }
        binding.includeMealTypeButton.setOnClickListener {
            timeCookEditTextClearFocus()
            val markedItems = viewModel.getDialogItemsFromPreference(KEY_MEAl_TYPE)
            val dialog = MealTypesDialogFragment(markedItems)
            dialog.show(parentFragmentManager, KEY_MEAL_TYPES_DIALOG)
        }
    }

    private fun initInstructions() {
        viewModel.getAdvancedSearchSwitchState(KEY_INSTRUCTIONS_SWITCH).let {
            binding.instructionsSwitch.isChecked = it
        }
        binding.instructionsSwitch.setOnCheckedChangeListener { _, isChecked ->
            timeCookEditTextClearFocus()
            viewModel.saveAdvancedSearchSwitchState(KEY_INSTRUCTIONS_SWITCH, isChecked)
        }
    }

    private fun initSearch() {
        binding.advancedSearchButton.setOnClickListener {
            val intent = Intent().apply {
                val bundle = Bundle().apply {
                    putString(KEY_CUISINE, keyCuisine)
                    putString(KEY_DIET, keyDiet)
                    putString(KEY_INTOLERANCE, keyIntolerance)
                    putString(KEY_MEAl_TYPE, keyMeatType)
                    maxReadyTime?.let { putInt(KEY_MAX_READY_TIME, it) }

                }
                putExtra(KEY_ADVANCED_SEARCH_SETTINGS, bundle)
                action = NAVIGATE_TO_ADVANCED_SEARCH
            }
            requireActivity().sendBroadcast(intent)
        }
    }

    private fun timeCookEditTextClearFocus() {
        binding.timeCook.clearFocus()
    }

    companion object {
        private const val KEY_CUISINE_SWITCH = "key_cuisine_switch"
        private const val KEY_DIETS_SWITCH = "key_diets_switch"
        private const val KEY_INTOLERANCES_SWITCH = "key_intolerances_switch"
        private const val KEY_MEAL_TYPES_SWITCH = "key_meal_types_switch"

    }
}