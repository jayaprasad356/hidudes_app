package com.gmwapp.hi_dude.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.hi_dude.repositories.FcmTokenRepository
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.FcmTokenResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class FcmTokenViewModel @Inject constructor(
    private val repository: FcmTokenRepository
) : ViewModel() {

    val tokenResponseLiveData = MutableLiveData<FcmTokenResponse>()
    val tokenErrorLiveData = MutableLiveData<String>()

    fun sendToken(userId: Int, token: String) {
        viewModelScope.launch {
            repository.sendFcmToken(userId, token, object :
                NetworkCallback<FcmTokenResponse> {
                override fun onResponse(
                    call: Call<FcmTokenResponse>,
                    response: Response<FcmTokenResponse>
                ) {
                    tokenResponseLiveData.postValue(response.body())
                    Log.d("tokenResponseLiveData", "${response.body()?.data}")
                }

                override fun onFailure(call: Call<FcmTokenResponse>, t: Throwable) {
                    tokenErrorLiveData.postValue("API Failure: ${t.message}")
                    Log.d("tokenResponseLiveData", "${t.message}")
                }

                override fun onNoNetwork() {
                    tokenErrorLiveData.postValue("No Network Connection")
                }
            })
        }
    }
}

