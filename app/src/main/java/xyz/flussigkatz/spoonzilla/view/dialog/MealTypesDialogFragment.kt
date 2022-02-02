package xyz.flussigkatz.spoonzilla.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import xyz.flussigkatz.spoonzilla.databinding.DialogMealTypesBinding

class MealTypesDialogFragment : DialogFragment() {
    private lateinit var binding: DialogMealTypesBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DialogMealTypesBinding.inflate(inflater, container, false)
        return binding.root
    }

}