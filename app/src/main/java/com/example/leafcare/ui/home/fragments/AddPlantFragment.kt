package com.example.leafcare.ui.home.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.leafcare.AuthActivity
import com.example.leafcare.LeafViewModelFactory
import com.example.leafcare.R
import com.example.leafcare.data.model.ApiStatus
import com.example.leafcare.data.storage.TokenManager
import com.example.leafcare.databinding.FragmentAddPlantBinding
import com.example.leafcare.ui.home.HomeViewModel

class AddPlantFragment : Fragment() {

    private var _binding: FragmentAddPlantBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels() {
        LeafViewModelFactory(
            TokenManager(requireContext()) // Provide TokenManager
        )
    }

    // photo var
    private var imageBitmap: Bitmap? = null

    // Register launchers for camera and gallery
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    // Request CAMERA permission
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                showPermissionDeniedDialog()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPlantBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Initialize the launchers
        initLaunchers()

        // Setup Plant Type Dropdown
        viewModel.getAvailablePlantTypes()
        viewModel.plantTypesNames.observe(viewLifecycleOwner) { plantTypesNames ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, plantTypesNames)
            binding.plantTypeDropdown.setAdapter(adapter)
        }

        // Handle Add Photo Button Click
        binding.btnAddPhoto.setOnClickListener {
            showImageOptions()
        }

        // Handle Save Button Click
        binding.btnSavePlant.setOnClickListener {
            val name = binding.etPlantName.text?.trim()
            val type = binding.plantTypeDropdown.text?.trim()
            val height = binding.etPlantHeight.text
            val description = binding.etPlantDescription.text?.trim()

            if (name.isNullOrEmpty() || type.isNullOrEmpty()
                || height.isNullOrEmpty() || description.isNullOrEmpty() || imageBitmap == null) {
//                Toast.makeText(context, "Пожалуйста, заполните все поля и добавьте фото!", Toast.LENGTH_SHORT).show()
                binding.tvError.text = getString(R.string.fill_fields_and_add_photo)
            } else {
//                Toast.makeText(context, getString(R.string.loading), Toast.LENGTH_SHORT).show()
                Log.d("ADD PLANT", "name: $name\ntype:$type\nheight:$height\ndescription:$description\nbitmap:$imageBitmap")
                viewModel.addPlant(name.toString(),
                    type.toString(), imageBitmap!!, description.toString(), height.toString().toBigDecimal().toDouble())
            }
        }

        // Handle navigation on successful plant add
        viewModel.addPlantStatus.observe(viewLifecycleOwner) { status ->
            if (status == ApiStatus.DONE) {
                Toast.makeText(context, getString(R.string.plant_saved), Toast.LENGTH_SHORT).show()
                viewModel.getMyPlants()
                // Remove the current fragment from the back stack
                findNavController().popBackStack()
//                // Navigate to homeFragment
//                findNavController().navigate(R.id.action_addPlantFragment_to_navigation_home)
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



    private fun initLaunchers() {
        // Camera Result Launcher
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as? Bitmap
                bitmap?.let {
                    binding.btnAddPhoto.text = "Фото добавлено"
                    binding.btnAddPhoto.isClickable = false
                    imageBitmap = bitmap
                }
            }
        }

        // Gallery Result Launcher
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                imageUri?.let { uri ->
                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                    binding.btnAddPhoto.text = "Фото добавлено"
                    binding.btnAddPhoto.isClickable = false
                    imageBitmap = bitmap
                }
            }
        }
    }

    private fun showImageOptions() {
        val options = arrayOf("Сделать фото", "Выбрать из галереи")

        // Show dialog for user to choose camera or gallery
        AlertDialog.Builder(requireContext())
            .setTitle("Добавить фото")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> openGallery()
                }
            }.show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            // Request permission
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }


    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("В разрешении отказано")
            .setMessage("Доступ к камере необходим для добавления фото.")
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}