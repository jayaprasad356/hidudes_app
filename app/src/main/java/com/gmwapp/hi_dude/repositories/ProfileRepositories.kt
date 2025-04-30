package com.gmwapp.hi_dude.repositories

import com.gmwapp.hi_dude.retrofit.ApiManager
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.AvatarsListResponse
import com.gmwapp.hi_dude.retrofit.responses.CallsListResponse
import com.gmwapp.hi_dude.retrofit.responses.DeleteUserResponse
import com.gmwapp.hi_dude.retrofit.responses.GetRemainingTimeResponse
import com.gmwapp.hi_dude.retrofit.responses.RegisterResponse
import com.gmwapp.hi_dude.retrofit.responses.SpeechTextResponse
import com.gmwapp.hi_dude.retrofit.responses.UpdateProfileResponse
import com.gmwapp.hi_dude.retrofit.responses.UserValidationResponse
import com.gmwapp.hi_dude.retrofit.responses.VoiceUpdateResponse
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class ProfileRepositories @Inject constructor(private val apiManager: ApiManager) {
    fun getAvatarsList(gender: String, callback: NetworkCallback<AvatarsListResponse>) {
        apiManager.getAvatarsList(gender, callback)
    }

    fun getCallsList(userId: Int, gender: String,limit: Int, currentOffset: Int, callback: NetworkCallback<CallsListResponse>) {
        apiManager.getCallsList(userId, gender,limit,currentOffset, callback)
    }

    fun register(
        mobile: String,
        language: String,
        avatarId: Int,
        gender: String,
        callback: NetworkCallback<RegisterResponse>
    ) {
        apiManager.register(mobile, language, avatarId, gender, callback)
    }

    fun getUser(
        userId: Int,
        callback: NetworkCallback<RegisterResponse>
    ) {
        apiManager.getUser(userId, callback)
    }

    fun getUserSync(
        userId: Int
    ): Response<RegisterResponse> {
        return apiManager.getUserSync(userId)
    }

    fun registerFemale(
        mobile: String,
        language: String,
        avatarId: Int,
        gender: String,
        age: String,
        interests: String,
        describe_yourself: String,
        callback: NetworkCallback<RegisterResponse>
    ) {
        apiManager.registerFemale(
            mobile, language, avatarId, gender, age, interests, describe_yourself, callback
        )
    }

    fun updateProfile(
        userId: Int,
        avatarId: Int,
        name: String,
        interests: String?,
        callback: NetworkCallback<UpdateProfileResponse>
    ) {
        apiManager.updateProfile(userId, avatarId, name, interests, callback)
    }

    fun deleteUsers(
        userId: Int, deleteReason: String, callback: NetworkCallback<DeleteUserResponse>
    ) {
        apiManager.deleteUsers(userId, deleteReason, callback)
    }

    fun userValidation(
        userId: Int, name: String, callback: NetworkCallback<UserValidationResponse>
    ) {
        apiManager.userValidation(userId, name, callback)
    }

    fun getSpeechText(
        userId: Int, language: String, callback: NetworkCallback<SpeechTextResponse>
    ) {
        apiManager.getSpeechText(userId, language, callback)
    }

    fun updateVoice(
        userId: Int, voice: MultipartBody.Part, callback: NetworkCallback<VoiceUpdateResponse>
    ) {
        apiManager.updateVoice(userId, voice, callback)
    }

    fun getRemainingTime(
        userId: Int, callType: String, callback: NetworkCallback<GetRemainingTimeResponse>
    ) {
        apiManager.getRemainingTime(userId, callType, callback)
    }
}