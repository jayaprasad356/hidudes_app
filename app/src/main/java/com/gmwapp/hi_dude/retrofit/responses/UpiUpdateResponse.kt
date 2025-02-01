package com.gmwapp.hi_dude.retrofit.responses

data class UpiUpdateResponse(
    val success: Boolean,
    val message: String,
    val data: UpiUpdateModel
)

data class UpiUpdateModel(
    val id: Int,
    val name: String,
    val user_gender: String,
    val language: String,
    val mobile: String,
    val avatar_id: Int,
    val image: String,
    val gender: String,
    val age: Int,
    val interests: String,
    val describe_yourself: String,
    val voice: String,
    val status: Int,
    val balance: Int,
    val coins: Int,
    val audio_status: Int,
    val video_status: Int,
    val bank: String,
    val account_num: String,
    val branch: String,
    val ifsc: String,
    val holder_name: String,
    val upi_id: String,
    val datetime: String,
    val updated_at: String,
    val created_at: String
)


