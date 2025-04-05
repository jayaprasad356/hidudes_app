package com.gmwapp.hi_dude.repositories


import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.UserAvatarResponse
import javax.inject.Inject

class UserAvatarRepository @Inject constructor(private val apiManager: ApiManager) {

    fun getUserAvatar(
        userId: Int,
        callback: NetworkCallback<UserAvatarResponse>
    ) {
        apiManager.getUserAvatar(userId, callback)
    }
}
