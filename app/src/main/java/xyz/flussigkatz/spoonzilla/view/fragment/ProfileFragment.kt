package xyz.flussigkatz.spoonzilla.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import xyz.flussigkatz.spoonzilla.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileSign.setOnClickListener {
            inDevelopingToast()
        }
        binding.profileCreateNew.setOnClickListener {
            inDevelopingToast()
        }
    }

    private fun inDevelopingToast() {
        Toast.makeText(requireContext(), IN_DEVELOPING, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val IN_DEVELOPING = "In developing"
    }
}