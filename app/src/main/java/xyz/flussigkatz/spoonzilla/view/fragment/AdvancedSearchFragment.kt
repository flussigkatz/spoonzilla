package xyz.flussigkatz.spoonzilla.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.adapters.AdapterViewBindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DishRecyclerAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.AdvancedSearchFragmentViewModel

class AdvancedSearchFragment : Fragment() {
    private lateinit var dishAdapter: DishRecyclerAdapter
    private lateinit var binding: AdapterViewBindingAdapter
    private val viewModel: AdvancedSearchFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_advanced_search, container, false)
    }

    private fun initSearch() {
    }

    private fun initContent() {
    }

    companion object {
        private const val TAG = "AdvancedSearchFragment"
        private const val PADDING_DP = 2
    }

}