package com.gmwapp.hi_dude.retrofit.responses

data class VoiceUpdateResponse(
    val success: Boolean,
    val message: String,
    val data: UserData?,
)
