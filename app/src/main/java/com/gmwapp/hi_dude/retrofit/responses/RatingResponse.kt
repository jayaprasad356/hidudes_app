package com.gmwapp.hi_dude.retrofit.responses

data class RatingResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<RatingModel>
)

data class RatingModel(
    val id: Int,
    val user_id: String,
    val call_user_id: String,
    val ratings: String,
    val title: String,
    val description: String,
    val updated_at: String,
    val created_at: String
)
