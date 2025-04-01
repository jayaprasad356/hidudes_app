package com.gmwapp.hi_dude.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.hi_dude.repositories.AccountRepositories
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.SettingsResponse
import com.gmwapp.hi_dude.retrofit.responses.UpdateImageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class AccountViewModel @Inject constructor(private val accountRepositories: AccountRepositories, application: Application // ✅ Inject application context
) : AndroidViewModel(application) { // ✅ Change ViewModel to AndroidViewModel

    val settingsLiveData = MutableLiveData<SettingsResponse>()
    val updateImageLiveData = MutableLiveData<UpdateImageResponse>()
    fun getSettings() {
        viewModelScope.launch {
            accountRepositories.getSettings(object : NetworkCallback<SettingsResponse> {
                override fun onResponse(
                    call: Call<SettingsResponse>,
                    response: Response<SettingsResponse>
                ) {
                    settingsLiveData.postValue(response.body());
                    Log.d("settingResponse", "Request URL: ${call.request().url}")
                    Log.d(
                        "supportUrl",
                        "Request URL: ${response.body()?.data?.firstOrNull()?.support_mail}"
                    )
                }

                override fun onFailure(call: Call<SettingsResponse>, t: Throwable) {
                }

                override fun onNoNetwork() {
                }
            })
        }
    }

    fun doUpdateImage(userId: RequestBody, filePath: MultipartBody.Part?) { // ✅ Non-null type
        Log.d("doUpdateImage", "userId: $userId")
        Log.d("doUpdateImage", "filePath: $filePath")

        viewModelScope.launch {
            accountRepositories.updateImage(userId, filePath, object : NetworkCallback<UpdateImageResponse> {
                override fun onResponse(
                    call: Call<UpdateImageResponse>,
                    response: Response<UpdateImageResponse>
                ) {
                    updateImageLiveData.postValue(response.body())
                    Toast.makeText(getApplication(), "" + response.body()?.message , Toast.LENGTH_LONG).show()
                    Log.d("doUpdateImage", "Request URL: ${call.request().url}")
                    Log.d("doUpdateImage", "Name: ${response.body()?.data?.name}")
                }

                override fun onFailure(call: Call<UpdateImageResponse>, t: Throwable) {
                    Log.e("doUpdateImage", "Failed: ${t.message}")
                }

                override fun onNoNetwork() {
                    Log.e("doUpdateImage", "No Network Available")
                }
            })
        }
    }


}

