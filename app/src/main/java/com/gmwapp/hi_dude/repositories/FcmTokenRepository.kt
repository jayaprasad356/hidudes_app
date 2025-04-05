package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import javax.inject.Inject
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.FcmTokenResponse


class FcmTokenRepository @Inject constructor(private val apiManager: ApiManager) {
    fun sendFcmToken(
        userId: Int,
        token: String,
        callback: NetworkCallback<FcmTokenResponse>
    ) {
        apiManager.sendFcmToken(userId, token, callback)
    }
}
