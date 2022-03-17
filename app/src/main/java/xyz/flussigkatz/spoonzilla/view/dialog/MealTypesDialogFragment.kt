package xyz.flussigkatz.spoonzilla.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.flussigkatz.spoonzilla.data.enums.MealTypes
import xyz.flussigkatz.spoonzilla.databinding.DialogMealTypesBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_MEAl_TYPE
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DialogItemRecyclerAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.MealTypesDialogFragmentViewModel

class MealTypesDialogFragment : DialogFragment() {
    private lateinit var binding: DialogMealTypesBinding
    private val viewModel: MealTypesDialogFragmentViewModel by activityViewModels()
    private lateinit var mAdapter: DialogItemRecyclerAdapter
    private lateinit var markedItems: MutableList<String>
    private val allItems = MealTypes.values().map { it.typeName }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DialogMealTypesBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerMealTypes.apply {
            viewModel.getDialogItemsFromPreference(KEY_MEAl_TYPE).let {
                markedItems = if (it.isNullOrEmpty()) mutableListOf()
                else it.toMutableList()
            }
            val clickListener = object : DialogItemRecyclerAdapter.OnCheckedChangeListener {
                override fun checkedChange(item: String, state: Boolean) {
                    if (state) markedItems.remove(item)
                    else markedItems.add(item)
                }
            }
            mAdapter = DialogItemRecyclerAdapter(markedItems, allItems, clickListener)
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onStop() {
        viewModel.putDialogItemsToPreference(KEY_MEAl_TYPE, markedItems.toSet())
        super.onStop()
    }

}