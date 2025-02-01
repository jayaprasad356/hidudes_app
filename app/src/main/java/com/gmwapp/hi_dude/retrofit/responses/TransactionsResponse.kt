package com.gmwapp.hi_dude.retrofit.responses

data class TransactionsResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<TransactionsResponseData>?,
)

data class TransactionsResponseData(
    val id: Int,
    val user_id: Int,
    val type: String,
    val amount: Int,
    val coins: Int,
    val payment_type: String,
    val datetime: String,
)

