package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.EarningsResponse
import javax.inject.Inject

class EarningsRepositories @Inject constructor(private val apiManager: ApiManager) {
  fun getEarnings(userId: Int, callback: NetworkCallback<EarningsResponse>) {
        apiManager.getEarnings(userId, callback)
    }
}