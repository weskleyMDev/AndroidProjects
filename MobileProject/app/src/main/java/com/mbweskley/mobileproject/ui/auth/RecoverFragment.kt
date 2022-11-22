package com.mbweskley.mobileproject.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mbweskley.mobileproject.R
import com.mbweskley.mobileproject.databinding.FragmentRecoverBinding
import com.mbweskley.mobileproject.helper.FirebaseHelper

class RecoverFragment : Fragment() {

    private var _binding: FragmentRecoverBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        initClicks()
    }

    private fun initClicks() {
        binding.btRecuperar.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val email = binding.etEmail.text.toString().trim()

        if (email.isNotEmpty()) {
            binding.progressBar.isVisible = true
            recoverUserAccount(email)
        } else {
            Toast.makeText(requireContext(), "Campo E-mail está em branco", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun recoverUserAccount(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(
                        R.id.action_recoverFragment_to_loginFragment
                    )
                    Toast.makeText(
                        requireContext(),
                        "Verifique seu e-mail para recuperar sua conta!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        FirebaseHelper.validError(task.exception?.message ?: ""),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar.isVisible = false
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}