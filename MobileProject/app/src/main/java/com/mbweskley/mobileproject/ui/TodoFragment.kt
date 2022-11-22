package com.mbweskley.mobileproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.mbweskley.mobileproject.R
import com.mbweskley.mobileproject.databinding.FragmentTodoBinding
import com.mbweskley.mobileproject.helper.FirebaseHelper
import com.mbweskley.mobileproject.model.Task
import com.mbweskley.mobileproject.ui.adapter.TaskAdapter

class TodoFragment : Fragment() {

    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    private val tasksList = mutableListOf<Task>()

    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClicks()
        getTasks()
    }

    private fun initClicks() {
        binding.fbAdicionar.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToFormFragment(null)
            findNavController().navigate(action)
        }

    }

    private fun getTasks() {
        FirebaseHelper.getDatabase().child("Compromissos").child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        tasksList.clear()
                        for (snap in snapshot.children) {
                            val task = snap.getValue(Task::class.java) as Task
                            if (task.status == 0) tasksList.add(task)
                        }
                        binding.tvLoading.text = ""
                        tasksList.reverse()
                        initAdapter()
                    } else {
                        binding.tvLoading.text = getString(R.string.TV_LISTA_VAZIA)
                    }
                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "Erro ao mostrar Comprmissos",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    private fun initAdapter() {
        binding.rvFazer.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFazer.setHasFixedSize(true)
        taskAdapter = TaskAdapter(tasksList) { task, select ->
            optionSelect(task, select)
        }
        binding.rvFazer.adapter = taskAdapter
    }

    private fun optionSelect(task: Task, select: Int) {
        when (select) {
            TaskAdapter.SELECT_REMOVE -> {
                deleteTask(task)
            }
            TaskAdapter.SELECT_EDIT -> {
                val action = HomeFragmentDirections.actionHomeFragmentToFormFragment(task)
                findNavController().navigate(action)
            }
            TaskAdapter.SELECT_NEXT -> {
                task.status = 1
                updateStatusTask(task)
            }
        }
    }

    private fun updateStatusTask(task: Task) {
        FirebaseHelper.getDatabase().child("Compromissos").child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id).setValue(task).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Compromisso Movido com Sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Erro! Compromisso não pode ser movido!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false

            }
    }

    private fun deleteTask(task: Task) {
        FirebaseHelper.getDatabase().child("Compromissos").child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id).removeValue()

        tasksList.remove(task)
        taskAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}