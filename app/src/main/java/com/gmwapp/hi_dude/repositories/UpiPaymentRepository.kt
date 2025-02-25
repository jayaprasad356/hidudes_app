package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.UpiPaymentResponse
import javax.inject.Inject

class UpiPaymentRepository @Inject constructor(private val apiManager: ApiManager) {

    fun createUpiPayment(
        userId: Int,
        clientTxnId: String,
        amount: String,
        callback: NetworkCallback<UpiPaymentResponse>
    ) {
        apiManager.createUpiPayment(userId, clientTxnId, amount,callback)
    }
}