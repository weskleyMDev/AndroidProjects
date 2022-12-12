package com.mbweskley.mobileproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mbweskley.mobileproject.R
import com.mbweskley.mobileproject.databinding.FragmentConvertBinding
import com.mbweskley.mobileproject.helper.BaseFragment
import com.mbweskley.mobileproject.helper.backToolbar
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class ConvertFragment : BaseFragment() {

    private var _binding: FragmentConvertBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentConvertBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backToolbar(binding.tbVoltar)
        initclick()
    }

    private fun initclick() {
        binding.btConvert.setOnClickListener {
            converter()
            hideKeyboard()
        }
    }

    private fun converter() {

        val selectGroup = binding.rgMoeda

        val source = when (selectGroup.checkedRadioButtonId) {
            R.id.rb_dolar -> "USD"
            else -> "EUR"
        }

        val value = binding.etValor.text.toString()
        if (value.isEmpty()) return

        binding.tvLabel.text = source
        binding.tvLabel.visibility = View.VISIBLE

        Thread {
            val url =
                URL("https://api.apilayer.com/currency_data/convert?to=BRL&from=${source}&amount=${value}&apikey=GRZVbeGbFM4vh8ouURA4eQClgggElcsF")
            val connection = url.openConnection() as HttpsURLConnection

            try {
                val data = connection.inputStream.bufferedReader().readText()

                val obj = JSONObject(data)

                requireActivity().runOnUiThread {
                    val res = obj.getDouble("result")
                    binding.tvResultado.text = String.format("R$ %.2f", res)
                    binding.tvResultado.visibility = View.VISIBLE
                }

            } finally {
                connection.disconnect()
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}