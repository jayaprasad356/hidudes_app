package com.gmwapp.hi_dude.repositories

import android.util.Log
import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.CallFemaleUserResponse
import com.gmwapp.hi_dude.retrofit.responses.FemaleCallAttendResponse
import com.gmwapp.hi_dude.retrofit.responses.FemaleUsersResponse
import com.gmwapp.hi_dude.retrofit.responses.RandomUsersResponse
import com.gmwapp.hi_dude.retrofit.responses.ReportsResponse
import com.gmwapp.hi_dude.retrofit.responses.UpdateCallStatusResponse
import com.gmwapp.hi_dude.retrofit.responses.UpdateConnectedCallResponse
import retrofit2.Response
import javax.inject.Inject

class FemaleUsersRepositories @Inject constructor(private val apiManager: ApiManager) {
    fun getFemaleUsers(userId: Int, callback: NetworkCallback<FemaleUsersResponse>) {
        apiManager.getFemaleUsers(userId, callback)
    }

    fun getRandomUser(userId: Int,callType: String, callback: NetworkCallback<RandomUsersResponse>) {
        apiManager.getRandomUser(userId,callType, callback)
    }

  fun getCalldetailsUser(userId: Int, callback: NetworkCallback<ReportsResponse>) {
        apiManager.getCalldetails(userId,callback)
    }

    fun updateCallStatus(
        userId: Int,
        callType: String,
        status: Int,
        callback: NetworkCallback<UpdateCallStatusResponse>
    ) {
        apiManager.updateCallStatus(
            userId, callType, status, callback
        )
    }

    fun femaleCallAttend(
        userId: Int,
        callId: Int,
        startTime: String,
        callback: NetworkCallback<FemaleCallAttendResponse>
    ) {
        apiManager.femaleCallAttend(
            userId, callId, startTime, callback
        )
    }

    fun callFemaleUser(
        userId: Int,
        callUserId: Int,
        callType: String,
        callback: NetworkCallback<CallFemaleUserResponse>
    ) {
        Log.d("callFemaleUser", "callFemaleUser: $userId $callUserId $callType")
        apiManager.callFemaleUser(
            userId, callUserId, callType, callback
        )
        Log.d("callFemaleUser", "callFemaleUser: $userId $callUserId $callType")
    }

    suspend fun updateConnectedCall(
        userId: Int,
        callId: Int,
        startedTime: String,
        endedTime: String,
    ) : Response<UpdateConnectedCallResponse> {
        return apiManager.updateConnectedCall(
            userId, callId, startedTime,endedTime
        )
    }

    suspend fun individualUpdateConnectedCall(
        userId: Int,
        callId: Int,
        startedTime: String,
        endedTime: String,
    ) : Response<UpdateConnectedCallResponse> {
        return apiManager.individualUpdateConnectedCall(
            userId, callId, startedTime,endedTime
        )
    }
}