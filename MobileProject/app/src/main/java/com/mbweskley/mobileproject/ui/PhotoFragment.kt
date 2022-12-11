package com.mbweskley.mobileproject.ui

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mbweskley.mobileproject.databinding.FragmentPhotoBinding
import com.mbweskley.mobileproject.helper.backToolbar
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class PhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 100
    }

    private var imagemView: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentPhotoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
        backToolbar(binding.tbVoltar)
        initClicks()
    }

    private fun initClicks() {
        binding.fbCapturar.setOnClickListener {
            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.mbSalvar.setOnClickListener {
            val bitmap = imagemView
            if (bitmap != null) {
                saveStorage(bitmap)
                findNavController().popBackStack()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Imagem em branco",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveStorage(bitmap: Bitmap) {
        val imageName = "imagem${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requireContext().contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES
                    )
                }
                val imageUri: Uri? = resolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                fos = imageUri?.let {
                    resolver.openOutputStream(it)
                }
            }
        } else {
            val imageDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imageDirectory, imageName)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(
                requireContext(),
                "A imagem foi salva na sua galeria!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.ivPhoto.setImageBitmap(imageBitmap)
            imagemView = binding.ivPhoto.drawable.toBitmap()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}