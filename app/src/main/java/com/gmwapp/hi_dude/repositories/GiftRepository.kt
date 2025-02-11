package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.SendGiftResponse
import javax.inject.Inject

class GiftRepository @Inject constructor(private val apiManager: ApiManager) {
    fun sendGift(
        userId: Int,
        receiverId: Int,
        giftId: Int,
        callback: NetworkCallback<SendGiftResponse>
    ) {
        apiManager.sendGift(userId, receiverId, giftId, callback)
    }
}