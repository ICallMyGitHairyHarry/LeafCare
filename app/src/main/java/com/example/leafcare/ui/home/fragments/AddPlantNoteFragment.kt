package com.example.leafcare.ui.home.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.leafcare.AuthActivity
import com.example.leafcare.LeafViewModelFactory
import com.example.leafcare.R
import com.example.leafcare.data.model.ApiStatus
import com.example.leafcare.data.storage.TokenManager
import com.example.leafcare.databinding.FragmentAddPlantNoteBinding
import com.example.leafcare.ui.home.HomeViewModel


class AddPlantNoteFragment : Fragment() {

    private var _binding: FragmentAddPlantNoteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels() {
        LeafViewModelFactory(
            TokenManager(requireContext()) // Provide TokenManager
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPlantNoteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Handle Save Button Click
        binding.btnSaveNote.setOnClickListener {
            val height = binding.etPlantHeight.text
            val description = binding.etPlantDescription.text?.trim()
//            Log.d("ADD PLANT", "$height")
//            Log.d("ADD PLANT", height.toString())
//            Log.d("ADD PLANT", "${height.toString().toDouble()}")

            if (height.isNullOrEmpty() || description.isNullOrEmpty()) {
                binding.tvError.text = getString(R.string.fill_fields)
            } else {
//                Toast.makeText(context, getString(R.string.loading), Toast.LENGTH_SHORT).show()
                viewModel.addPlantNote(
                    description.toString(), height.toString().toDouble()
                )
            }
        }

        // Handle navigation on successful plant note add
        viewModel.addPlantNoteStatus.observe(viewLifecycleOwner) { status ->
            if (status == ApiStatus.DONE) {
                Toast.makeText(context, getString(R.string.note_saved), Toast.LENGTH_SHORT).show()
                // Remove the current fragment from the back stack
                findNavController().popBackStack()
//                // Navigate to plantFragment
//                findNavController().navigate(R.id.action_addPlantNoteFragment_to_myPlantFragment)
            }
        }

        // Handle navigation in case of auth error
        viewModel.isLoggedIn.observe(viewLifecycleOwner) { loggedIn ->
            if (loggedIn == false) {
                // Navigate to AuthActivity
                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

    }


}