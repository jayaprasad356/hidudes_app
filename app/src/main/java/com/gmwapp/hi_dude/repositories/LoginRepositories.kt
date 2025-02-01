package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.AppUpdateResponse
import com.gmwapp.hi_dude.retrofit.responses.LoginResponse
import com.gmwapp.hi_dude.retrofit.responses.SendOTPResponse
import javax.inject.Inject

class LoginRepositories @Inject constructor(private val apiManager: ApiManager) {
    fun login(mobile: String, callback: NetworkCallback<LoginResponse>) {
        apiManager.login(mobile, callback)
    }


    fun appupdate(callback: NetworkCallback<AppUpdateResponse>) {
        apiManager.appUpdate(callback)
    }





    fun sendOTP(
        mobile: String,
        countryCode: Int,
        otp: Int,
        callback: NetworkCallback<SendOTPResponse>
    ) {
        apiManager.sendOTP(mobile, countryCode, otp, callback)
    }
}