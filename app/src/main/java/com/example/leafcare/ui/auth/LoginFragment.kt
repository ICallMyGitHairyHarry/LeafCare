package com.example.leafcare.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.leafcare.LeafViewModelFactory
import com.example.leafcare.MainActivity
import com.example.leafcare.R
import com.example.leafcare.data.model.ApiStatus
import com.example.leafcare.data.storage.TokenManager
import com.example.leafcare.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val viewModel: AuthViewModel by activityViewModels() {
        LeafViewModelFactory(
            TokenManager(requireContext()) // Provide TokenManager
        )
    }


    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun hideKeyboard() {
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val currentFocusedView = requireActivity().currentFocus

            if (currentFocusedView != null) {
                inputMethodManager.hideSoftInputFromWindow(currentFocusedView.windowToken, 0)
            }
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Handle login button click
        binding.btnLogin.setOnClickListener {
            hideKeyboard()

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                binding.tvError.text = getString(R.string.fill_fields)
            } else {
                // Login
                viewModel.login(email, password)
            }
        }

        // Handle navigation on successful auth
        viewModel.status.observe(viewLifecycleOwner) { status ->
            if (status == ApiStatus.DONE) {
                // Navigate to MainActivity
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        // Handle signup link click
        binding.tvSignupLink.setOnClickListener {
            // Navigate to signup screen
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
            viewModel.clearErrorOnNav()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}