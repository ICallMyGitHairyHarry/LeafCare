package com.example.leafcare.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.leafcare.AuthActivity
import com.example.leafcare.data.storage.TokenManager
import com.example.leafcare.databinding.FragmentHomeBinding
import com.example.leafcare.LeafViewModelFactory
import com.example.leafcare.R
import com.example.leafcare.ui.home.adapters.PlantLinearAdapter
import com.example.leafcare.ui.home.adapters.PlantListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels() {
        LeafViewModelFactory(
            TokenManager(requireContext()) // Provide TokenManager
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        // get plants
//        viewModel.getMyPlants()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.bRetry.setOnClickListener { viewModel.getMyPlants() }
        binding.rvMyPlants.adapter = PlantLinearAdapter(PlantListener { plant ->
            viewModel.onPlantClicked(plant)
            // plantName to set the app bar title, plantId to be able to retry request on network error
            val action = HomeFragmentDirections.actionNavigationHomeToMyPlantFragment(
                plantName = plant.name, plantId = 1)
            // Navigate using action
            findNavController().navigate(action)
        })

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_addPlantFragment)
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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}