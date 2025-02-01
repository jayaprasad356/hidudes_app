package com.gmwapp.hi_dude.retrofit.responses

data class WithdrawlistResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<WithdrawlistModel>
)

data class WithdrawlistModel(
    val id: Int,
    val link: String,
    val app_version: Int,
    val description: String
)


