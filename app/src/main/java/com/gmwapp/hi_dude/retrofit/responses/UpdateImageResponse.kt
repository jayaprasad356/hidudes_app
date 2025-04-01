package com.gmwapp.hi_dude.retrofit.responses

data class UpdateImageResponse(
    val success: Boolean,
    val message: String,
    val data: UpdateImageResponseData // ✅ Expect an object, not an array
)

data class UpdateImageResponseData(
    val id: Int,  // ✅ Change to Int if it's a number in the JSON
    val name: String,
    val mobile: String,
    val image: String, // ✅ Match the API field name exactly
    val avatar_id: Int,
    val user_gender: String,
    val language: String,
    val gender: String,
    val age: Int,
    val interests: String,
    val describe_yourself: String?,
    val voice: String?,
    val status: Int,
    val balance: Int,
    val coins: Int,
    val audio_status: Int,
    val video_status: Int,
    val datetime: String,
    val updated_at: String,
    val created_at: String
)
