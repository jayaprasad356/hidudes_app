package com.gmwapp.hi_dude.retrofit.responses

data class SettingsResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<SettingsResponseData>?,
)

data class SettingsResponseData(
    val id: Int,
    val privacy_policy: String,
    val support_mail: String,
    val demo_video: String,
    val minimum_withdrawals: Int,
)