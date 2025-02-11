package com.gmwapp.hi_dude.retrofit.responses

data class SendGiftResponse(
    val success: Boolean,
    val message: String,
    val data: GiftData?
)

data class SentGiftData(
    val sender_name: String,
    val receiver_name: String,
    val gift_id: Int,
    val gift_icon: String,
    val gift_coins: Int
)
