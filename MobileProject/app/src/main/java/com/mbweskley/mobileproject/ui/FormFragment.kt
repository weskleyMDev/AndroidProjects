package com.mbweskley.mobileproject.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mbweskley.mobileproject.R
import com.mbweskley.mobileproject.databinding.FragmentFormBinding
import com.mbweskley.mobileproject.helper.BaseFragment
import com.mbweskley.mobileproject.helper.FirebaseHelper
import com.mbweskley.mobileproject.helper.backToolbar
import com.mbweskley.mobileproject.model.Task
import java.text.SimpleDateFormat
import java.util.*

class FormFragment : BaseFragment(), DatePickerDialog.OnDateSetListener {

    private val args: FormFragmentArgs by navArgs()

    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: Task
    private var newTask: Boolean = true
    private var setStatus = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backToolbar(binding.tbVoltar)
        initClicks()
        getArgs()
    }

    private fun initClicks() {
        binding.btData.setOnClickListener {
            showDatePicker()
        }
        binding.btSalvar.setOnClickListener {
            validateData()
        }
        binding.rgTaskGroup.setOnCheckedChangeListener { _, id ->
            setStatus = when (id) {
                R.id.rb_fazer -> 0
                R.id.rb_andamento -> 1
                else -> 2
            }
        }
    }

    private fun validateData() {
        val title = binding.etTitulo.text.toString().trim()
        val desc = binding.etDescricao.text.toString().trim()
        val andress = binding.etEndereco.text.toString().trim()
        val date = binding.tvData.text.toString()
        if (title.isNotEmpty() && desc.isNotEmpty() && andress.isNotEmpty() && date.isNotEmpty()) {
            hideKeyboard()
            binding.progressBar.isVisible = true
            if (newTask) task = Task()
            task.titulo = title
            task.descricao = desc
            task.endereco = andress
            task.data = date
            task.status = setStatus
            saveTasks()
        } else {
            Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun saveTasks() {
        FirebaseHelper.getDatabase().child("Compromissos").child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id).setValue(task).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (newTask) {
                        findNavController().popBackStack()
                        Toast.makeText(
                            requireContext(),
                            "Compromisso Salvo Com Sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        findNavController().popBackStack()
                        Toast.makeText(
                            requireContext(),
                            "Compromisso Atualizado Com Sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Erro! Compromisso não pode ser salvo!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false

            }
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), this, year, month, day).show()
    }

    private fun formatDate(timestamp: Long) {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        binding.tvData.text = formatter.format(timestamp)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        val c = Calendar.getInstance()
        c.set(year, month, day)
        formatDate(c.timeInMillis)
    }


    private fun getArgs() {
        args.task.let {
            if (it != null) {
                task = it
                configTask()
            }
        }
    }

    private fun configTask() {
        newTask = false
        setStatus = task.status
        binding.formToolbar.text = getString(R.string.APPBAR_EDITAR)
        binding.etTitulo.setText(task.titulo)
        binding.etDescricao.setText(task.descricao)
        binding.etEndereco.setText(task.endereco)
        binding.tvData.text = task.data
        getStatus()
    }

    private fun getStatus() {
        binding.rgTaskGroup.check(
            when (task.status) {
                0 -> {
                    R.id.rb_fazer
                }
                1 -> {
                    R.id.rb_andamento
                }
                else -> {
                    R.id.rb_concluido
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}