package com.gmwapp.hi_dude.viewmodels



import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.hi_dude.repositories.UpiPaymentRepository
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.UpiPaymentResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UpiPaymentViewModel @Inject constructor(private val upiPaymentRepository: UpiPaymentRepository) : ViewModel() {

    val upiPaymentLiveData = MutableLiveData<UpiPaymentResponse>()

    fun createUpiPayment(userId: Int, clientTxnId: String, amount: String) {
        viewModelScope.launch {
            upiPaymentRepository.createUpiPayment(userId, clientTxnId, amount, object : NetworkCallback<UpiPaymentResponse> {
                override fun onResponse(call: Call<UpiPaymentResponse>, response: Response<UpiPaymentResponse>) {
                    if (response.isSuccessful) {
                        upiPaymentLiveData.postValue(response.body())
                        Log.d("UPI PaymentCheck", "Order ID: ${response.body()}")
                    } else {
                        Log.e("UPI Payment123", "Failed with response: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<UpiPaymentResponse>, t: Throwable) {
                    Log.e("UPI Payment897", "API call failed: ${t.localizedMessage}")
                }

                override fun onNoNetwork() {
                    Log.e("UPI Payment", "No internet connection")
                }
            })
        }
    }
}
