package com.gmwapp.hi_dude.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.repositories.OfferRepositories
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.OfferResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject




@HiltViewModel
class OfferViewModel @Inject constructor(private val offerRepositories: OfferRepositories) : ViewModel() {

    val offerResponseLiveData = MutableLiveData<OfferResponse>()
    val offerrrorLiveData = MutableLiveData<String>()

    fun getOffer(
        userId: Int,
    ) {
        viewModelScope.launch {
            offerRepositories.getoffer(userId,  object : NetworkCallback<OfferResponse> {
                override fun onResponse(
                    call: Call<OfferResponse>,
                    response: Response<OfferResponse>
                ) {
                    offerResponseLiveData.postValue(response.body())
                }

                override fun onFailure(call: Call<OfferResponse>, t: Throwable) {
                    offerrrorLiveData.postValue(DConstants.LOGIN_ERROR)
                }

                override fun onNoNetwork() {
                    offerrrorLiveData.postValue(DConstants.NO_NETWORK)
                }
            })
        }
    }
}

