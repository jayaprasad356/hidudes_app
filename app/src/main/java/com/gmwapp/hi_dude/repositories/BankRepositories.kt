package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.BankUpdateResponse
import javax.inject.Inject


class BankRepositories @Inject constructor(private val apiManager: ApiManager) {

        fun updatebank(
            userId: Int,
            bank: String,
            accountNum: String,
            branch: String,
            ifsc: String,
            holderName: String,
            callback: NetworkCallback<BankUpdateResponse>
        ) {
            apiManager.updateBank(userId, bank, accountNum, branch, ifsc, holderName, callback)
        }
    }

