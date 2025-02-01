package com.gmwapp.hi_dude.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.repositories.WithdrawRepositories
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.WithdrawResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject




@HiltViewModel
class WithdrawViewModel @Inject constructor(private val withdrawRepository: WithdrawRepositories) : ViewModel() {

    val withdrawResponseLiveData = MutableLiveData<WithdrawResponse>()
    val withdrawErrorLiveData = MutableLiveData<String>()

    fun addWithdrawal(
        userId: Int,
        amount: Int,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            withdrawRepository.addWithdrawal(userId, amount, paymentMethod, object : NetworkCallback<WithdrawResponse> {
                override fun onResponse(
                    call: Call<WithdrawResponse>,
                    response: Response<WithdrawResponse>
                ) {
                    withdrawResponseLiveData.postValue(response.body())
                }

                override fun onFailure(call: Call<WithdrawResponse>, t: Throwable) {
                    withdrawErrorLiveData.postValue(DConstants.LOGIN_ERROR)
                }

                override fun onNoNetwork() {
                    withdrawErrorLiveData.postValue(DConstants.NO_NETWORK)
                }
            })
        }
    }
}


