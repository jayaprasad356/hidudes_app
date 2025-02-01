package com.gmwapp.hi_dude.retrofit.responses

data class CallsListResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<CallsListResponseData>?,
)

data class CallsListResponseData(
    val id: Int,
    val name: String,
    val image: String,
    val started_time: String,
    val duration: String,
    val income: Int,
    val audio_status: Int,
    val video_status: Int,
)