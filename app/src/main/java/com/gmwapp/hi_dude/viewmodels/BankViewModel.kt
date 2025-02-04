package com.gmwapp.hi_dude.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.repositories.BankRepositories
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.BankUpdateResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject




@HiltViewModel
class BankViewModel @Inject constructor(private val bankRepositories: BankRepositories) : ViewModel() {

    val bankResponseLiveData = MutableLiveData<BankUpdateResponse>()
    val bankErrorLiveData = MutableLiveData<String>()

    fun updatedBank(
        userId: Int,
        holderName: String,
        accountNum: String,
        ifsc: String,
        bank: String,
        branch: String,


        ) {
        viewModelScope.launch {
            bankRepositories.updatebank(userId, bank, accountNum, branch, ifsc, holderName, object : NetworkCallback<BankUpdateResponse> {
                override fun onResponse(
                    call: Call<BankUpdateResponse>,
                    response: Response<BankUpdateResponse>
                ) {
                    bankResponseLiveData.postValue(response.body())
                    bankErrorLiveData.postValue(response.body()?.message)


                    Log.d("bankresposne","${response.body()}")



                }

                override fun onFailure(call: Call<BankUpdateResponse>, t: Throwable) {
                    Log.e("bankresposneError", "Error occurred", t)

                    bankErrorLiveData.postValue(DConstants.LOGIN_ERROR)
                    Log.d("bankresposneError","${t.message}")
                }

                override fun onNoNetwork() {
                    bankErrorLiveData.postValue(DConstants.NO_NETWORK)
                }
            })
        }
    }
}
