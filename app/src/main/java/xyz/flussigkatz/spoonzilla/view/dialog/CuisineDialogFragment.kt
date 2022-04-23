package xyz.flussigkatz.spoonzilla.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.flussigkatz.spoonzilla.data.enums.Cuisines
import xyz.flussigkatz.spoonzilla.databinding.DialogCuisineBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_CUISINE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_RESULT_REQUEST_DIALOG
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DialogItemRecyclerAdapter


class CuisineDialogFragment(private val markedItems: MutableList<String>) : DialogFragment() {
    private lateinit var binding: DialogCuisineBinding
    private lateinit var mAdapter: DialogItemRecyclerAdapter
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
        setFragmentResult(KEY_RESULT_REQUEST_DIALOG, bundleOf(KEY_CUISINE to markedItems))
        super.onStop()
    }

}