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
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.databinding.FragmentSettingsBinding
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
        val items = arrayOf(
            MODE_NIGHT_NO,
            MODE_NIGHT_YES,
            MODE_NIGHT_SYSTEM_FOLLOW,
            MODE_NIGHT_AUTO_BATTERY
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
                    val mode = when (it.adapter.getItem(position)) {
                        MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_NO
                        MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_YES
                        MODE_NIGHT_SYSTEM_FOLLOW -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        MODE_NIGHT_AUTO_BATTERY -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
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

    companion object {
        private const val MODE_NIGHT_NO = "No"
        private const val MODE_NIGHT_YES = "Yes"
        private const val MODE_NIGHT_SYSTEM_FOLLOW = "System follow"
        private const val MODE_NIGHT_AUTO_BATTERY = "Auto battery"
        private const val MODE_NIGHT_NO_INDEX = 0
        private const val MODE_NIGHT_YES_INDEX = 1
        private const val MODE_NIGHT_SYSTEM_FOLLOW_INDEX = 2
        private const val MODE_NIGHT_AUTO_BATTERY_INDEX = 3
    }
}