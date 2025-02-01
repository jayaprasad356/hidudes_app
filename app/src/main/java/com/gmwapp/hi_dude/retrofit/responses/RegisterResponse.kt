package com.gmwapp.hi_dude.retrofit.responses

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: UserData?,
    val token: String?,
    )