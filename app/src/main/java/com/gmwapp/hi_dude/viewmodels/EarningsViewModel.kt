package com.gmwapp.hi_dude.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.repositories.EarningsRepositories
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.EarningsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class EarningsViewModel @Inject constructor(private val earningsRepositories: EarningsRepositories) : ViewModel() {

    val earningsResponseLiveData = MutableLiveData<EarningsResponse>()
    val earningsErrorLiveData = MutableLiveData<String>()

    fun getEarnings(userId: Int) {
        viewModelScope.launch {
            earningsRepositories.getEarnings(userId, object:NetworkCallback<EarningsResponse> {
                override fun onResponse(
                    call: Call<EarningsResponse>,
                    response: Response<EarningsResponse>
                ) {
                    earningsResponseLiveData.postValue(response.body());
                }

                override fun onFailure(call: Call<EarningsResponse>, t: Throwable) {
                    earningsErrorLiveData.postValue(DConstants.LOGIN_ERROR);
                }

                override fun onNoNetwork() {
                    earningsErrorLiveData.postValue(DConstants.NO_NETWORK);
                }
            })
        }
    }

}

