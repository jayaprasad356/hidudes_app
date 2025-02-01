package com.gmwapp.hi_dude.retrofit.responses

data class OfferResponse(
    val success: Boolean,
    val message: String,
    val total: Int,
    val data: ArrayList<OfferModel>
)

data class OfferModel(
    val id: Int,
    val price: Int,
    val coins: Int,
    val save: Int,
    val popular: Int,
    val best_offer: Int,
    val updated_at: String,
    val created_at: String
)

