package com.mbweskley.mobileproject.model

import com.google.android.gms.maps.model.LatLng

data class Place(
    val name: String,
    val lating: LatLng,
    val address: LatLng,
    val rating: Float,
)
