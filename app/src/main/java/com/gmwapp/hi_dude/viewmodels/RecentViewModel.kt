package com.gmwapp.hi_dude.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.repositories.ProfileRepositories
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.CallsListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class RecentViewModel @Inject constructor(private val profileRepositories: ProfileRepositories) :
    ViewModel() {

    val callsListErrorLiveData = MutableLiveData<String>()
    val callsListLiveData = MutableLiveData<CallsListResponse>()
    fun getCallsList(userId: Int, gender: String) {
        viewModelScope.launch {
            profileRepositories.getCallsList(
                userId,
                gender,
                object : NetworkCallback<CallsListResponse> {
                    override fun onResponse(
                        call: Call<CallsListResponse>, response: Response<CallsListResponse>
                    ) {
                        callsListLiveData.postValue(response.body())
                    }

                    override fun onFailure(call: Call<CallsListResponse>, t: Throwable) {
                        callsListErrorLiveData.postValue(t.message)
                    }

                    override fun onNoNetwork() {
                        callsListErrorLiveData.postValue(DConstants.NO_NETWORK)
                    }
                })
        }
    }


}

