package com.example.leafcare.ui.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.leafcare.LeafViewModelFactory
import com.example.leafcare.R
import com.example.leafcare.data.storage.TokenManager
import com.example.leafcare.databinding.FragmentMyPlantBinding
import com.example.leafcare.databinding.FragmentPlantNoteBinding
import com.example.leafcare.ui.home.HomeViewModel


class PlantNoteFragment : Fragment() {

    private var _binding: FragmentPlantNoteBinding? = null

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
        _binding = FragmentPlantNoteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}