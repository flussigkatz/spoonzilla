package xyz.flussigkatz.spoonzilla.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.flussigkatz.spoonzilla.data.enums.Diets
import xyz.flussigkatz.spoonzilla.databinding.DialogDietsBinding
import xyz.flussigkatz.spoonzilla.util.AppConst
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DIET
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_RESULT_REQUEST_DIALOG
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DialogItemRecyclerAdapter
import java.util.*

class DietsDialogFragment(private val markedItems: MutableList<String>) : DialogFragment() {
    private lateinit var binding: DialogDietsBinding
    private lateinit var mAdapter: DialogItemRecyclerAdapter
    private lateinit var allItems: List<String>

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
        allItems = Diets.values().map { getCuisineFromResources(it.name) }
        binding.recyclerDiets.apply {
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
        setFragmentResult(KEY_RESULT_REQUEST_DIALOG, bundleOf(KEY_DIET to markedItems))
        super.onStop()
    }

    private fun getCuisineFromResources(cuisine: String): String {
        return resources.getString(
            resources.getIdentifier(
                cuisine.lowercase(Locale.getDefault()),
                AppConst.DEF_TYPE,
                AppConst.DEF_PACKAGE
            )
        )
    }
}