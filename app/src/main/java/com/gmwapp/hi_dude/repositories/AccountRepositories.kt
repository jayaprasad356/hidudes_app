package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.BankUpdateResponse
import com.gmwapp.hi_dude.retrofit.responses.SettingsResponse
import com.gmwapp.hi_dude.retrofit.responses.UpdateImageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AccountRepositories @Inject constructor(private val apiManager: ApiManager) {
  fun getSettings(callback: NetworkCallback<SettingsResponse>) {
        apiManager.getSettings(callback)
    }


    fun updateImage(
        userId: RequestBody,
        filePath: MultipartBody.Part?, // ✅ Ensure this is non-null
        callback: NetworkCallback<UpdateImageResponse>
    ) {
        apiManager.updateImage(userId, filePath, callback)
    }
}