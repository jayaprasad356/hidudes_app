package com.gmwapp.hi_dude.activities

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val id: String? = "",
    val longurl: String? = ""
)