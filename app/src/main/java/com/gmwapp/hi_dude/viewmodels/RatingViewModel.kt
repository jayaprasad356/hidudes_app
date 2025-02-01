package com.gmwapp.hi_dude.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.repositories.RatingRepositories
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.RatingResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject




@HiltViewModel

class RatingViewModel @Inject constructor(private val ratingRepositories: RatingRepositories) : ViewModel() {

    val ratingResponseLiveData = MutableLiveData<RatingResponse>()
    val ratingErrorLiveData = MutableLiveData<String>()

    fun updatedrating(
        userId: Int,
        call_user_id: Int,
        ratings: String,
        title: String,
        description: String
    ) {
        viewModelScope.launch {
            ratingRepositories.updaterating(userId, call_user_id, ratings, title, description, object : NetworkCallback<RatingResponse> {
                override fun onResponse(
                    call: Call<RatingResponse>,
                    response: Response<RatingResponse>
                ) {
                    ratingResponseLiveData.postValue(response.body())
                }

                override fun onFailure(call: Call<RatingResponse>, t: Throwable) {
                    ratingErrorLiveData.postValue(DConstants.LOGIN_ERROR)
                }

                override fun onNoNetwork() {
                    ratingErrorLiveData.postValue(DConstants.NO_NETWORK)
                }
            })
        }
    }
}

