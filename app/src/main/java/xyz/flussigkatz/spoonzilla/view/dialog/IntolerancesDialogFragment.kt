package xyz.flussigkatz.spoonzilla.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.flussigkatz.spoonzilla.data.enums.Intolerances
import xyz.flussigkatz.spoonzilla.databinding.DialogIntolerancesBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_INTOLERANCE
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DialogItemRecyclerAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.IntolerancesDialogFragmentViewModel


class IntolerancesDialogFragment : DialogFragment() {
    private lateinit var binding: DialogIntolerancesBinding
    private val viewModel: IntolerancesDialogFragmentViewModel by activityViewModels()
    private lateinit var mAdapter: DialogItemRecyclerAdapter
    private lateinit var markedItems: MutableList<String>
    private val allItems = Intolerances.values().map { it.intoleranceName }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogIntolerancesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerIntolerances.apply {
            viewModel.getDialogItemsFromPreference(KEY_INTOLERANCE).let {
                markedItems = it.orEmpty().toMutableList()
            }
            val clickListener = object : DialogItemRecyclerAdapter.OnCheckedChangeListener {
                override fun checkedChange(item: String, state: Boolean) {
                    if (state) markedItems.remove(item) else markedItems.add(item)
                }
            }
            mAdapter = DialogItemRecyclerAdapter(markedItems, allItems, clickListener)
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onStop() {
        viewModel.putDialogItemsToPreference(KEY_INTOLERANCE, markedItems.toSet())
        super.onStop()
    }

}