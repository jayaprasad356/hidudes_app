package com.gmwapp.hi_dude.repositories

import android.util.Log
import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.AddCoinsResponse
import com.gmwapp.hi_dude.retrofit.responses.CoinsResponse
import com.gmwapp.hi_dude.retrofit.responses.UpiPaymentResponse
import javax.inject.Inject

class WalletRepositories @Inject constructor(private val apiManager: ApiManager) {
  fun getCoins(userId: Int, callback: NetworkCallback<CoinsResponse>) {
        apiManager.getCoins(userId, callback)
  }

    fun addCoins(
        userId: Int,
        coinId: Int,
        status: Int,
        orderId: Int,
        message: String,
        callback: NetworkCallback<AddCoinsResponse>
    ) {
        apiManager.addCoins(userId, coinId, status, orderId, message, callback)
    }

    fun tryCoins(
        userId: Int,
        coinId: Int,
        status: Int,
        orderId: Int,
        massage: String,
        callback: NetworkCallback<AddCoinsResponse>
    ) {
        apiManager.tryCoins(userId, coinId, status, orderId, massage, callback)
    }

 }