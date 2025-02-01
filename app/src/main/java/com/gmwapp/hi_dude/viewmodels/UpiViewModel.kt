package com.gmwapp.hi_dude.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.repositories.UpiRepositories
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.AddPointsResponse
import com.gmwapp.hi_dude.retrofit.responses.UpiUpdateResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject




@HiltViewModel
class UpiViewModel @Inject constructor(private val upiRepositories: UpiRepositories) : ViewModel() {

    val upiResponseLiveData = MutableLiveData<UpiUpdateResponse>()
    val upiErrorLiveData = MutableLiveData<String>()
    val addpointResponseLiveData = MutableLiveData<AddPointsResponse>()
    val addpointErrorLiveData = MutableLiveData<String>()


    fun updatedUpi(
        userId: Int,
        upiId: String,

    ) {
        viewModelScope.launch {
            upiRepositories.updateUpi(userId,upiId, object : NetworkCallback<UpiUpdateResponse> {
                override fun onResponse(
                    call: Call<UpiUpdateResponse>,
                    response: Response<UpiUpdateResponse>
                ) {
                    upiResponseLiveData.postValue(response.body())
                }

                override fun onFailure(call: Call<UpiUpdateResponse>, t: Throwable) {
                    upiErrorLiveData.postValue(DConstants.LOGIN_ERROR)
                }

                override fun onNoNetwork() {
                    upiErrorLiveData.postValue(DConstants.NO_NETWORK)
                }
            })
        }
    }


    fun addPoints(
        buyerName: String, amount: String,
        email: String, phone: String, purpose: String
    ) {
        viewModelScope.launch {
            upiRepositories.addPoints(buyerName, amount, email, phone, purpose, object : NetworkCallback<AddPointsResponse> {
                override fun onResponse(
                    call: Call<AddPointsResponse>,
                    response: Response<AddPointsResponse>
                ) {
                    addpointResponseLiveData.postValue(response.body())
                }

                override fun onFailure(call: Call<AddPointsResponse>, t: Throwable) {
                    addpointErrorLiveData.postValue(DConstants.LOGIN_ERROR)
                }

                override fun onNoNetwork() {
                    addpointErrorLiveData.postValue(DConstants.NO_NETWORK)
                }
            })
        }
    }


//    fun addPoint(
//        buyerName: String, amount: String,
//        email: String, phone: String, purpose: String
//    ) {
//        viewModelScope.launch {
//            isLoading.postValue(true)
//            chatRepositories.addPoints(buyerName, amount, email, phone, purpose).let {
//                if (it.body() != null) {
//                    addPointsLiveData.postValue(it.body() as AddPointsResponse)
//                    isLoading.postValue(false)
//                } else {
//                    isLoading.postValue(false)
//                }
//            }
//
//        }
//    }



}

