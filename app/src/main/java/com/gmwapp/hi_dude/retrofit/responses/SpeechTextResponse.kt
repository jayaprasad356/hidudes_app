package com.gmwapp.hi_dude.retrofit.responses

data class SpeechTextResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<SpeechTextData>?,
    )

data class SpeechTextData(
    val id: Int,
    val text: String,
)