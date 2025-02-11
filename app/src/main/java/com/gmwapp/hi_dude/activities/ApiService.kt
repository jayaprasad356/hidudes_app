package com.gmwapp.hi_dude.activities

import com.gmwapp.hi_dude.retrofit.responses.RazorPayApiResponse
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

    @FormUrlEncoded
    @POST("razorpay/add_coins_requests.php")
    fun addCoinsRazorPay(
        @Field("reference_id") referenceId: String,
        @Field("buyer_name") buyerName: String,
        @Field("amount") amount: String,
        @Field("email") email: String,
        @Field("phone") phone: String
    ): Call<RazorPayApiResponse>
}