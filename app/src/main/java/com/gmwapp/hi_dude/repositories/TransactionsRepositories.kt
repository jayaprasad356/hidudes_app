package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.TransactionsResponse
import javax.inject.Inject

class TransactionsRepositories @Inject constructor(private val apiManager: ApiManager) {
    fun getTransactions(userId: Int, offset: Int, limit: Int,callback: NetworkCallback<TransactionsResponse>) {
        apiManager.getTransactions(userId,offset,limit, callback)
    }
}