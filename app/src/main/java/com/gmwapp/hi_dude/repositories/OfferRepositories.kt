package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.OfferResponse
import javax.inject.Inject


class OfferRepositories @Inject constructor(private val apiManager: ApiManager) {

        fun getoffer(
            userId: Int,
            callback: NetworkCallback<OfferResponse>
        ) {
            apiManager.getOffer(userId,  callback)
        }
    }

