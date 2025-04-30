package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.ExplanationVideoResponse
import com.gmwapp.hi_dude.retrofit.responses.WhatsappLinkResponse
import javax.inject.Inject

class WhatsappLinkRepository  @Inject constructor(private val apiManager: ApiManager) {

    fun getWhatsappLink(
        language: String,
        callback: NetworkCallback<WhatsappLinkResponse>
    ) {
        apiManager.getWhatsappLink(language, callback)
    }
}