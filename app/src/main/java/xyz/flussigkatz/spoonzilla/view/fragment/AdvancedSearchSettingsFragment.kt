package xyz.flussigkatz.spoonzilla.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.viewmodel.AdvancedSearchSettingsFragmentViewModel

class AdvancedSearchSettingsFragment : Fragment() {
    private val viewModel: AdvancedSearchSettingsFragmentViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_advanced_search_settings, container, false)
    }

}