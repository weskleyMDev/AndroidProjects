package com.mbweskley.mobileproject.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mbweskley.mobileproject.R
import com.mbweskley.mobileproject.databinding.FragmentFormBinding
import com.mbweskley.mobileproject.helper.BaseFragment
import com.mbweskley.mobileproject.helper.FirebaseHelper
import com.mbweskley.mobileproject.helper.backToolbar
import java.text.SimpleDateFormat
import java.util.*
import com.mbweskley.mobileproject.model.Task as Task1

class FormFragment : BaseFragment(), DatePickerDialog.OnDateSetListener,
    OnMapReadyCallback {

    private val args: FormFragmentArgs by navArgs()

    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: Task1
    private var newTask: Boolean = true
    private var setStatus = 0

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
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
            if (newTask) task = Task1()
            task.titulo = title
            task.descricao = desc
            task.endereco = andress
            task.data = date
            task.status = setStatus
            saveTasks()
        } else {
            Toast.makeText(
                requireContext(),
                "Preencha todos os campos!",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun saveTasks() {
        FirebaseHelper.getDatabase().child("Compromissos")
            .child(FirebaseHelper.getIdUser() ?: "")
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        fetchLocation()
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        val deviceLocation = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true
        deviceLocation.addOnSuccessListener {
            if (it != null) {
                lastLocation = it
                val currentLatLong = LatLng(it.latitude, it.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLong))
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        currentLatLong,
                        15.0f
                    )
                )
                mMap.addMarker(
                    MarkerOptions().position(currentLatLong)
                        .title("$currentLatLong")
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}