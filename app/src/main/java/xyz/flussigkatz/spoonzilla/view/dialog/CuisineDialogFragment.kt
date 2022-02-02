package xyz.flussigkatz.spoonzilla.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import xyz.flussigkatz.spoonzilla.databinding.DialogCuisineBinding


class CuisineDialogFragment : DialogFragment() {
    private lateinit var binding: DialogCuisineBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DialogCuisineBinding.inflate(inflater, container, false)
        return binding.root
    }

}