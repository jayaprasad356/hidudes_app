package com.gmwapp.hi_dude.activities

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("dwpay/add_coins_requests.php")
    fun addCoins(
        @Field("buyer_name") name: String,
        @Field("amount") amount: String,
        @Field("email") email: String,
        @Field("phone") mobile: String,
        @Field("purpose") userId: String
    ): Call<ApiResponse>
}