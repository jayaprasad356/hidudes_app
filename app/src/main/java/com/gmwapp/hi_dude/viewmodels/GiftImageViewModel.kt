package com.gmwapp.hi_dude.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.hi_dude.repositories.GiftImageRepository
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.GiftImageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class GiftImageViewModel @Inject constructor(
    private val repository: GiftImageRepository
) : ViewModel() {

    val giftResponseLiveData = MutableLiveData<GiftImageResponse>()
    val giftErrorLiveData = MutableLiveData<String>()

    fun fetchGiftImages() {
        viewModelScope.launch {
            repository.getGiftImages(object : NetworkCallback<GiftImageResponse> {
                override fun onResponse(
                    call: Call<GiftImageResponse>,
                    response: Response<GiftImageResponse>
                ) {
                    Log.d("GiftIcon", "Gift Icon URL: ${response.body()}")

                    giftResponseLiveData.postValue(response.body())
                }

                override fun onFailure(call: Call<GiftImageResponse>, t: Throwable) {
                    giftErrorLiveData.postValue("API Failure: ${t.message}")
                }

                override fun onNoNetwork() {
                    giftErrorLiveData.postValue("No Network Connection")
                }
            })
        }
    }
}