package com.gmwapp.hi_dude.retrofit.responses

data class AddPointsResponse(
    val success: Boolean,
    val message : String,
    val id: String? = "",
    val longurl: String? = ""
)
