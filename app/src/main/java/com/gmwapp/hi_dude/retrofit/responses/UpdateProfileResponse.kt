package com.gmwapp.hi_dude.retrofit.responses

data class UpdateProfileResponse(
    val success: Boolean,
    val message: String,
    val data: UserData?,
)