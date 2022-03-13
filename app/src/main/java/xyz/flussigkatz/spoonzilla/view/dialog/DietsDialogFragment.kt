package xyz.flussigkatz.spoonzilla.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.flussigkatz.spoonzilla.data.enums.Diets
import xyz.flussigkatz.spoonzilla.databinding.DialogDietsBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DIET
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DialogItemRecyclerAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.DietsDialogFragmentViewModel

class DietsDialogFragment : DialogFragment() {
    private lateinit var binding: DialogDietsBinding
    private val viewModel: DietsDialogFragmentViewModel by activityViewModels()
    private lateinit var mAdapter: DialogItemRecyclerAdapter
    private lateinit var markedItems: MutableList<String>
    private val allItems = Diets.values().map { it.dietName }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDietsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerDiets.apply {
            viewModel.getDialogItemsFromPreference(KEY_DIET).let {
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
        viewModel.putDialogItemsToPreference(KEY_DIET, markedItems.toSet())
        super.onStop()
    }


}