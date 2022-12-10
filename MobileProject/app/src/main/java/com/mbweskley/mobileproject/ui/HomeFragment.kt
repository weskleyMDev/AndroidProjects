package com.mbweskley.mobileproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mbweskley.mobileproject.R
import com.mbweskley.mobileproject.databinding.FragmentHomeBinding
import com.mbweskley.mobileproject.ui.adapter.ViewPageAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        configTabLayout()
        initClicks()
    }

    private fun configTabLayout() {
        val adapter = ViewPageAdapter(requireActivity())
        binding.vpViewPage.adapter = adapter

        adapter.addFragment(TodoFragment(), "A Fazer")
        adapter.addFragment(DoingFragment(), "Pendente")
        adapter.addFragment(DoneFragment(), "Concluído")

        binding.vpViewPage.offscreenPageLimit = adapter.itemCount

        TabLayoutMediator(binding.tabs, binding.vpViewPage) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()
    }

    private fun initClicks() {
        binding.ibLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        auth.signOut()
        findNavController().navigate(R.id.action_homeFragment_to_authentication)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}