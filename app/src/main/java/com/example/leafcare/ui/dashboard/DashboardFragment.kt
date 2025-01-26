package com.example.leafcare.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.leafcare.AuthActivity
import com.example.leafcare.LeafViewModelFactory
import com.example.leafcare.R
import com.example.leafcare.data.model.PlantType
import com.example.leafcare.data.storage.TokenManager
import com.example.leafcare.databinding.FragmentDashboardBinding
import com.example.leafcare.ui.dashboard.adapters.PlantTypeLinearAdapter
import com.example.leafcare.ui.home.HomeFragmentDirections
import com.example.leafcare.ui.home.HomeViewModel
import com.example.leafcare.ui.home.adapters.PlantLinearAdapter
import com.example.leafcare.ui.home.adapters.PlantListener

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by activityViewModels() {
        LeafViewModelFactory(
            TokenManager(requireContext()) // Provide TokenManager
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.bRetry.setOnClickListener { viewModel.getMySchedules() }
//        binding.rvMyPlants.adapter = ScheduleLinearAdapter()
        binding.rvMyPlants.adapter = PlantTypeLinearAdapter()

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}