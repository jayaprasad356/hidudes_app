package com.gmwapp.hi_dude.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.hi_dude.repositories.WalletRepositories
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.CoinsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class WalletViewModel @Inject constructor(private val walletRepositories: WalletRepositories) : ViewModel() {

    val coinsLiveData = MutableLiveData<CoinsResponse>()
    fun getCoins(userId: Int) {
        viewModelScope.launch {
            walletRepositories.getCoins(userId, object:NetworkCallback<CoinsResponse> {
                override fun onResponse(
                    call: Call<CoinsResponse>,
                    response: Response<CoinsResponse>
                ) {
                    coinsLiveData.postValue(response.body());
                }

                override fun onFailure(call: Call<CoinsResponse>, t: Throwable) {
                }

                override fun onNoNetwork() {
                }
            })
        }
    }
}

