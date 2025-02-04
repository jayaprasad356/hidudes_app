package com.gmwapp.hi_dude.retrofit.responses

data class GiftImageResponse(
    val success: Boolean,
    val message: String,
    val data: List<GiftData>
)
data class GiftData(
    val id: Int,
    val gift_icon: String,
    val coins: Int,
    val updated_at: String,
    val created_at: String
)