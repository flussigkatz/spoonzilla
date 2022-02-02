package xyz.flussigkatz.spoonzilla.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import xyz.flussigkatz.spoonzilla.databinding.DialogDietsBinding

class DietsDialogFragment : DialogFragment() {
    private lateinit var binding: DialogDietsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DialogDietsBinding.inflate(inflater, container, false)
        return binding.root
    }

}