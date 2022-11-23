package com.mbweskley.mobileproject.helper

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mbweskley.mobileproject.R
import com.mbweskley.mobileproject.databinding.BottomSheetBinding

fun Fragment.backToolbar(toolbar: Toolbar) {
    (activity as AppCompatActivity).setSupportActionBar(toolbar)
    (activity as AppCompatActivity).title = ""
    (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    toolbar.setTitleTextColor(Color.parseColor("#FFFFFFFF"))
}

fun Fragment.showBottomSheet(
    titleDialog: Int? = null,
    titleButton: Int? = null,
    message: Int,
    onClick: () -> Unit = {}
) {
    val bottomSheetDialog = BottomSheetDialog(
        requireContext(),
        R.style.BottomSheetDialog
    )
    val bottomSheetBinding: BottomSheetBinding =
        BottomSheetBinding.inflate(layoutInflater, null, false)

    bottomSheetBinding.tvTitle.text = getString(titleDialog ?: R.string.TV_WARNING)
    bottomSheetBinding.tvMessage.text = getText(message)
    bottomSheetBinding.btClick.text = getString(titleButton ?: R.string.BT_ENTENDI)
    bottomSheetBinding.btClick.setOnClickListener {
        onClick()
        bottomSheetDialog.dismiss()
    }

    bottomSheetDialog.setContentView(bottomSheetBinding.root)
    bottomSheetDialog.show()
}