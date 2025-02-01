package com.gmwapp.hi_dude.retrofit.responses

data class RandomUsersResponse(
    val success: Boolean,
    val message: String,
    val data: RandomUsersResponseData?,
)

data class RandomUsersResponseData(
    val call_id: Int,
    val user_id: Int,
    val call_user_id: Int,
    val coins_spend: Int,
    val income: Int,
    val user_name: String,
    val call_user_name: String,
    val type: String,
    val started_time: String,
    val ended_time: String,
    val balance_time: String,
)