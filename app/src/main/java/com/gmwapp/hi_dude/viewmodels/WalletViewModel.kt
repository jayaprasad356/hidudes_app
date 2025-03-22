package com.gmwapp.hi_dude.viewmodels

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.hi_dude.activities.MainActivity
import com.gmwapp.hi_dude.repositories.WalletRepositories
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.AddCoinsResponse
import com.gmwapp.hi_dude.retrofit.responses.CoinsResponse
import com.gmwapp.hi_dude.retrofit.responses.UpiPaymentResponse
import com.gmwapp.hi_dude.utils.DPreferences
import com.zegocloud.uikit.prebuilt.call.core.utils.Storage.context
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class WalletViewModel @Inject constructor(private val walletRepositories: WalletRepositories) : ViewModel() {

    val coinsLiveData = MutableLiveData<CoinsResponse>()
    val addCoinsResponse = MutableLiveData<AddCoinsResponse>()
    val afterAddCoinsLiveData = MutableLiveData<String>()
    val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: LiveData<Boolean> get() = _navigateToMain

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

    fun addCoins(userId: Int, coinId: Int) {
        _navigateToMain.postValue(false)
        viewModelScope.launch {
            walletRepositories.addCoins(userId, coinId, object : NetworkCallback<AddCoinsResponse> {
                override fun onResponse(call: Call<AddCoinsResponse>, response: Response<AddCoinsResponse>) {
                    Log.d("addCoins", "Raw Response: ${response.body()?.toString()}")

                    if (response.isSuccessful) {
                        Log.d("addCoins", "Response Successful: ${response.isSuccessful}")
                        Log.d("addCoins", "Response Message: ${response.message()}")

                        val responseBody = response.body()

                        responseBody?.let {
                            Log.d("UPI PaymentCheck", "Success: ${it.success}")
                            Log.d("UPI PaymentCheck", "Message: ${it.message}")
                            if (it.success) {
                                Log.d("addCoins", "Coins added successfully! Fetching updated balance...")

                                _navigateToMain.postValue(true) // ✅ Notify Activity to navigate

                                // ✅ Save new coin balance to SharedPreferences
                                DPreferences(context).setAfterAddCoins(it.data.coins)

                                // ✅ Notify UI via LiveData
                                afterAddCoinsLiveData.postValue(it.data.coins)

                                // ✅ Fetch updated coins
                                getCoins(userId)
                            }
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        Log.e("UPI Payment Error", "Failed with response: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<AddCoinsResponse>, t: Throwable) {
                    Log.e("UPI Payment API", "API call failed: ${t.localizedMessage}")

                    // ✅ Retry logic (Optional)
                    viewModelScope.launch {
                        Log.d("addCoins", "Retrying addCoins API call after failure...")
//                        addCoins(userId, coinId)  // Retry once if it failed
                    }

                    // ✅ Fetch latest coins to check if they were actually added
                    getCoins(userId)
                }

                override fun onNoNetwork() {
                    Log.e("UPI Payment", "No internet connection")
                }
            })
        }
    }


}

