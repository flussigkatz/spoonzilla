package xyz.flussigkatz.spoonzilla.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.flussigkatz.spoonzilla.data.enums.Cuisines
import xyz.flussigkatz.spoonzilla.databinding.DialogCuisineBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_CUISINE
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DialogItemRecyclerAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.CuisineDialogFragmentViewModel


class CuisineDialogFragment : DialogFragment() {
    private lateinit var binding: DialogCuisineBinding
    private val viewModel: CuisineDialogFragmentViewModel by activityViewModels()
    private lateinit var mAdapter: DialogItemRecyclerAdapter
    private lateinit var markedItems: MutableList<String>
    private val allItems = Cuisines.values().map { it.nameCuisine }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCuisineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerCuisine.apply {
            viewModel.getDialogItemsFromPreference(KEY_CUISINE).let {
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
        viewModel.putDialogItemsToPreference(KEY_CUISINE, markedItems.toSet())
        super.onStop()
    }

}