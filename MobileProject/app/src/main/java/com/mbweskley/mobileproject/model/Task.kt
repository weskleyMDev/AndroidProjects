package com.mbweskley.mobileproject.model

import android.os.Parcelable
import com.mbweskley.mobileproject.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    var id: String = "",
    var titulo: String = "",
    var descricao: String = "",
    var endereco: String = "",
    var data: String = "",
    var status: Int = 0,
) : Parcelable {
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}
