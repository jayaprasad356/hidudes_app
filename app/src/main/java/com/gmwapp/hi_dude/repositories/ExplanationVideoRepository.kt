package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.ExplanationVideoResponse
import javax.inject.Inject

class ExplanationVideoRepository @Inject constructor(private val apiManager: ApiManager) {

    fun getExplanationVideos(
        language: String,
        callback: NetworkCallback<ExplanationVideoResponse>
    ) {
        apiManager.getExplanationvideos(language, callback)
    }
}