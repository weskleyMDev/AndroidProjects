package com.mbweskley.mobileproject.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mbweskley.mobileproject.R

class FirebaseHelper {

    companion object {
        fun getDatabase() = FirebaseDatabase.getInstance().reference
        private fun getAuth() = FirebaseAuth.getInstance()
        fun getIdUser() = getAuth().uid
        fun isAuthenticated() = getAuth().currentUser != null
        fun validError(error: String): Int {
            return when {
                error.contains("There is no user record corresponding to this identifier") -> {
                    R.string.ACC_NOT_REG
                }
                error.contains("The email address is badly formatted") -> {
                    R.string.INV_EMAIL_REG
                }
                error.contains("The password is invalid or the user does not have a password") -> {
                    R.string.INV_PASS_REG
                }
                error.contains("Password should be at least 6 characters") -> {
                    R.string.SHORT_PASS_REG
                }
                error.contains("The email address is already in use by another account") -> {
                    R.string.EMAIL_ALREADY_REG
                }
                else -> {
                    R.string.ERROR
                }
            }
        }
    }
}