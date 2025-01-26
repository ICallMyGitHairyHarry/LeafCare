package com.example.leafcare.ui.home.fragments

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.leafcare.AuthActivity
import com.example.leafcare.LeafViewModelFactory
import com.example.leafcare.R
import com.example.leafcare.data.storage.TokenManager
import com.example.leafcare.databinding.FragmentMyPlantBinding
import com.example.leafcare.ui.home.HomeViewModel
import com.example.leafcare.ui.home.adapters.NoteLinearAdapter
import com.example.leafcare.ui.home.adapters.NoteListener

class MyPlantFragment : Fragment() {

    private var _binding: FragmentMyPlantBinding? = null

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

        _binding = FragmentMyPlantBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // get plant notes
        viewModel.getMyPlantNotes()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Attach MenuProvider to handle app bar menu
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_my_plant, menu) // Inflate your menu XML
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_delete_plant -> {
                        showDeleteConfirmationDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner) // Bind to Fragment lifecycle

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.plant.value?.let {
            val bitmap = convertBase64ToBitmap(it.photo)
            binding.ivPlantImage.setImageBitmap(bitmap)
        }

        binding.btnAddAction.setOnClickListener {
            findNavController().navigate(R.id.action_myPlantFragment_to_addPlantNoteFragment)
        }

        binding.rvNoteHistory.adapter = NoteLinearAdapter(NoteListener { note ->
            viewModel.onNoteClicked(note)
            // Navigate to note screen
            findNavController().navigate(R.id.action_myPlantFragment_to_plantNoteFragment)
        })

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

    private fun convertBase64ToBitmap(base64String: String): Bitmap {
        val cleanedBase64 = base64String.replace("\n", "").replace("\\n", "")
        val decodedBytes = Base64.decode(cleanedBase64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }


    private fun showDeleteConfirmationDialog() {
        val plantName = viewModel.plant.value?.name
        var alertDialog: AlertDialog? = null

        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Удаление из Моих растений")
            .setMessage("Вы уверены, что хотите удалить \"$plantName\" из Моих растений?")
            .setPositiveButton("Да, удалить") { _, _ ->
                viewModel.deletePlant(
                    onSuccess = {
                        Toast.makeText(requireContext(), "Растение удалено", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                        viewModel.getMyPlants()
                    }, onFailure = {
                        Toast.makeText(requireContext(), "Не удалось удалить растение, попробуйте позже", Toast.LENGTH_SHORT).show()
                    })
            }
            .setNegativeButton("Назад", null)
            .create()

        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}