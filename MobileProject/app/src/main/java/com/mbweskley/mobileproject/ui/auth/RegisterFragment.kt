package com.mbweskley.mobileproject.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mbweskley.mobileproject.R
import com.mbweskley.mobileproject.databinding.FragmentRegisterBinding
import com.mbweskley.mobileproject.helper.BaseFragment
import com.mbweskley.mobileproject.helper.FirebaseHelper
import com.mbweskley.mobileproject.helper.backToolbar

class RegisterFragment : BaseFragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backToolbar(binding.tbVoltar)
        auth = Firebase.auth
        initClick()
    }

    private fun initClick() {
        binding.btRegistro.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val email = binding.etEmail.text.toString().trim()
        val senha = binding.etSenha.text.toString().trim()

        if (email.isNotEmpty()) {
            if (senha.isNotEmpty()) {
                hideKeyboard()
                binding.progressBar.isVisible = true
                registerUser(email, senha)
            } else {
                Toast.makeText(requireContext(), "Campo Senha está em branco", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(requireContext(), "Campo E-mail está em branco", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun registerUser(email: String, senha: String) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    Toast.makeText(
                        requireContext(),
                        "Conta criada com sucesso!",
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