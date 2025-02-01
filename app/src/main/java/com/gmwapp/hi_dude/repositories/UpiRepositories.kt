package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.AddPointsResponse
import com.gmwapp.hi_dude.retrofit.responses.UpiUpdateResponse
import javax.inject.Inject


class UpiRepositories @Inject constructor(private val apiManager: ApiManager) {

        fun updateUpi(
            userId: Int,
            upiId: String,
            callback: NetworkCallback<UpiUpdateResponse>
        ) {
            apiManager.updateUpi(userId, upiId, callback)
        }



    suspend fun addPoints(
        buyerName: String,
        amount: String, email: String, phone: String,
        purpose: String,
        callback: NetworkCallback<AddPointsResponse>
    ) = apiManager.addPoints(
        buyerName, amount, email, phone, purpose, callback
    )

    }

