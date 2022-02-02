package xyz.flussigkatz.spoonzilla.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import xyz.flussigkatz.spoonzilla.databinding.DialogIntolerancesBinding


class IntolerancesDialogFragment : DialogFragment() {
    private lateinit var binding: DialogIntolerancesBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DialogIntolerancesBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.checkboxIntoleranceDairy.setOnClickListener {
            if((it as CheckBox).isChecked) {
                Toast.makeText(requireContext(), "dairy", Toast.LENGTH_SHORT).show()
            }
        }
    }

}