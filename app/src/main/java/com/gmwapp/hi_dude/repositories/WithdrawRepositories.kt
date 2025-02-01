package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.WithdrawResponse
import javax.inject.Inject


class WithdrawRepositories @Inject constructor(private val apiManager: ApiManager) {

    fun addWithdrawal(
        userId: Int,
        amount: Int,
        paymentMethod: String,
        callback: NetworkCallback<WithdrawResponse>
    ) {
        apiManager.addWithdrawal(userId, amount, paymentMethod, callback)
    }
}


