package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.SettingsResponse
import javax.inject.Inject

class AccountRepositories @Inject constructor(private val apiManager: ApiManager) {
  fun getSettings(callback: NetworkCallback<SettingsResponse>) {
        apiManager.getSettings(callback)
    }
}