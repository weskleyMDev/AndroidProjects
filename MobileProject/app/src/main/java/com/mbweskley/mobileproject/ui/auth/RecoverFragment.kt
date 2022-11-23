package com.mbweskley.mobileproject.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mbweskley.mobileproject.R
import com.mbweskley.mobileproject.databinding.FragmentRecoverBinding
import com.mbweskley.mobileproject.helper.BaseFragment
import com.mbweskley.mobileproject.helper.FirebaseHelper
import com.mbweskley.mobileproject.helper.backToolbar
import com.mbweskley.mobileproject.helper.showBottomSheet

class RecoverFragment : BaseFragment() {

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
        backToolbar(binding.tbVoltar)
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
            hideKeyboard()
            binding.progressBar.isVisible = true
            recoverUserAccount(email)
        } else {
            showBottomSheet(message = R.string.EMAIL_VAZIO)
        }
    }

    private fun recoverUserAccount(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    showBottomSheet(message = R.string.VERIFICAR_EMAIL)
                } else {
                    showBottomSheet(
                        message = FirebaseHelper.validError(
                            task.exception?.message ?: ""
                        )
                    )
                    binding.progressBar.isVisible = false
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}