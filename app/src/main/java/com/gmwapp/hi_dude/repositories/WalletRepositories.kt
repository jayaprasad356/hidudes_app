package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.CoinsResponse
import javax.inject.Inject

class WalletRepositories @Inject constructor(private val apiManager: ApiManager) {
  fun getCoins(userId: Int, callback: NetworkCallback<CoinsResponse>) {
        apiManager.getCoins(userId, callback)
    }

 }