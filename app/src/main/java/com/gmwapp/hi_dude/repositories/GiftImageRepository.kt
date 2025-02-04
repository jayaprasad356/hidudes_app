package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.GiftImageResponse
import javax.inject.Inject

class GiftImageRepository@Inject constructor(private val apiManager: ApiManager) {

    fun getGiftImages(callback: NetworkCallback<GiftImageResponse>) {
        apiManager.getGiftImages(callback)
    }
}