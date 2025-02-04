package com.gmwapp.hi_dude.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.hi_dude.repositories.AccountRepositories
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.SettingsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class AccountViewModel @Inject constructor(private val accountRepositories: AccountRepositories) : ViewModel() {

    val settingsLiveData = MutableLiveData<SettingsResponse>()
    fun getSettings() {
        viewModelScope.launch {
            accountRepositories.getSettings(object:NetworkCallback<SettingsResponse> {
                override fun onResponse(
                    call: Call<SettingsResponse>,
                    response: Response<SettingsResponse>
                ) {
                    settingsLiveData.postValue(response.body());
                    Log.d("settingResponse", "Request URL: ${call.request().url}")
                    Log.d("supportUrl", "Request URL: ${response.body()?.data?.firstOrNull()?.support_mail}")



                }

                override fun onFailure(call: Call<SettingsResponse>, t: Throwable) {
                }

                override fun onNoNetwork() {
                }
            })
        }
    }

}

