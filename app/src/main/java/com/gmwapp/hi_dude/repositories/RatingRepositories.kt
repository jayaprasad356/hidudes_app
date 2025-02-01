package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.RatingResponse
import javax.inject.Inject


class RatingRepositories @Inject constructor(private val apiManager: ApiManager) {

        fun updaterating(
            userId: Int,
            call_user_id: Int,
            ratings: String,
            title: String,
            description: String,
            callback: NetworkCallback<RatingResponse>
        ) {
            apiManager.updateRating(userId,call_user_id,ratings,title,description, callback)
        }
    }

