package com.gmwapp.hi_dude.retrofit

import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.AddPointsResponse
import com.gmwapp.hi_dude.retrofit.responses.AppUpdateResponse
import com.gmwapp.hi_dude.retrofit.responses.AvatarsListResponse
import com.gmwapp.hi_dude.retrofit.responses.BankUpdateResponse
import com.gmwapp.hi_dude.retrofit.responses.CallFemaleUserResponse
import com.gmwapp.hi_dude.retrofit.responses.CallsListResponse
import com.gmwapp.hi_dude.retrofit.responses.CoinsResponse
import com.gmwapp.hi_dude.retrofit.responses.DeleteUserResponse
import com.gmwapp.hi_dude.retrofit.responses.EarningsResponse
import com.gmwapp.hi_dude.retrofit.responses.ExplanationVideoResponse
import com.gmwapp.hi_dude.retrofit.responses.FemaleCallAttendResponse
import com.gmwapp.hi_dude.retrofit.responses.FemaleUsersResponse
import com.gmwapp.hi_dude.retrofit.responses.GetRemainingTimeResponse
import com.gmwapp.hi_dude.retrofit.responses.LoginResponse
import com.gmwapp.hi_dude.retrofit.responses.OfferResponse
import com.gmwapp.hi_dude.retrofit.responses.RandomUsersResponse
import com.gmwapp.hi_dude.retrofit.responses.RatingResponse
import com.gmwapp.hi_dude.retrofit.responses.RegisterResponse
import com.gmwapp.hi_dude.retrofit.responses.ReportsResponse
import com.gmwapp.hi_dude.retrofit.responses.SendOTPResponse
import com.gmwapp.hi_dude.retrofit.responses.SettingsResponse
import com.gmwapp.hi_dude.retrofit.responses.SpeechTextResponse
import com.gmwapp.hi_dude.retrofit.responses.TransactionsResponse
import com.gmwapp.hi_dude.retrofit.responses.UpdateCallStatusResponse
import com.gmwapp.hi_dude.retrofit.responses.UpdateConnectedCallResponse
import com.gmwapp.hi_dude.retrofit.responses.UpdateProfileResponse
import com.gmwapp.hi_dude.retrofit.responses.UpiUpdateResponse
import com.gmwapp.hi_dude.retrofit.responses.UserValidationResponse
import com.gmwapp.hi_dude.retrofit.responses.VoiceUpdateResponse
import com.gmwapp.hi_dude.retrofit.responses.WithdrawResponse
import com.gmwapp.hi_dude.utils.Helper
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import javax.inject.Inject

class ApiManager @Inject constructor(private val retrofit: Retrofit) {
    private fun getApiInterface(): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }




    fun login(
        mobile: String, callback: NetworkCallback<LoginResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<LoginResponse> = getApiInterface().login(mobile)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun appUpdate(
        callback: NetworkCallback<AppUpdateResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<AppUpdateResponse> = getApiInterface().appUpdate(1)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }



    fun sendOTP(
        mobile: String, countryCode: Int, otp: Int, callback: NetworkCallback<SendOTPResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<SendOTPResponse> = getApiInterface().sendOTP(mobile, countryCode, otp)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun getAvatarsList(
        gender: String, callback: NetworkCallback<AvatarsListResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<AvatarsListResponse> = getApiInterface().getAvatarsList(gender)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun getCallsList(
        userId: Int, gender: String, callback: NetworkCallback<CallsListResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<CallsListResponse> = getApiInterface().getCallsList(userId, gender)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun register(
        mobile: String,
        language: String,
        avatarId: Int,
        gender: String,
        callback: NetworkCallback<RegisterResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<RegisterResponse> =
                getApiInterface().register(mobile, language, avatarId, gender)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun getUser(
        userId: Int, callback: NetworkCallback<RegisterResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<RegisterResponse> = getApiInterface().getUser(userId)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun getUserSync(
        userId: Int
    ): Response<RegisterResponse> {
        val apiCall: Call<RegisterResponse> = getApiInterface().getUserSync(userId)
        return apiCall.execute()
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
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<RegisterResponse> = getApiInterface().registerFemale(
                mobile, language, avatarId, gender, age, interests, describe_yourself
            )
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun getTransactions(
        userId: Int, callback: NetworkCallback<TransactionsResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<TransactionsResponse> = getApiInterface().getTransactions(userId)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun getFemaleUsers(
        userId: Int, callback: NetworkCallback<FemaleUsersResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<FemaleUsersResponse> = getApiInterface().getFemaleUsers(userId)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun getRandomUser(
        userId: Int,callType: String, callback: NetworkCallback<RandomUsersResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<RandomUsersResponse> = getApiInterface().getRandomUser(userId, callType)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }


    fun getCalldetails(
        userId: Int, callback: NetworkCallback<ReportsResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<ReportsResponse> = getApiInterface().getReports(userId)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun updateCallStatus(
        userId: Int,
        callType: String,
        status: Int,
        callback: NetworkCallback<UpdateCallStatusResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<UpdateCallStatusResponse> =
                getApiInterface().updateCallStatus(userId, callType, status)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun femaleCallAttend(
        userId: Int,
        callId: Int,
        startedTime: String,
        callback: NetworkCallback<FemaleCallAttendResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<FemaleCallAttendResponse> =
                getApiInterface().femaleCallAttend(userId, callId, startedTime)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun callFemaleUser(
        userId: Int,
        callUserId: Int,
        callType: String,
        callback: NetworkCallback<CallFemaleUserResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<CallFemaleUserResponse> =
                getApiInterface().callFemaleUser(userId, callUserId, callType)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    suspend fun updateConnectedCall(
        userId: Int,
        callId: Int,
        startedTime: String,
        endedTime: String,
    ) : Response<UpdateConnectedCallResponse>{
        return getApiInterface().updateConnectedCall(userId, callId, startedTime, endedTime)
    }

    suspend fun individualUpdateConnectedCall(
        userId: Int,
        callId: Int,
        startedTime: String,
        endedTime: String,
    ) : Response<UpdateConnectedCallResponse>{
        return getApiInterface().individualUpdateConnectedCall(userId, callId, startedTime, endedTime)
    }

    fun getEarnings(
        userId: Int, callback: NetworkCallback<EarningsResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<EarningsResponse> = getApiInterface().getEarnings(userId)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun updateBank(
        userId: Int,
        bank: String,
        accountNum: String,
        branch: String,
        ifsc: String,
        holderName: String,
        callback: NetworkCallback<BankUpdateResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<BankUpdateResponse> = getApiInterface().updateBank(userId, bank, accountNum, branch, ifsc, holderName)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }


    fun updateRating(
        userId: Int,
        call_user_id: Int,
        ratings: String,
        title: String,
        description: String,
        callback: NetworkCallback<RatingResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<RatingResponse> = getApiInterface().updateRatings(userId,call_user_id,ratings,title,description)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun updateUpi(
        userId: Int,
        upiId: String,
        callback: NetworkCallback<UpiUpdateResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<UpiUpdateResponse> = getApiInterface().updateUpi(userId,upiId)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun addPoints(
        buyerName: String,
        amount: String, email: String, phone: String,
        purpose: String,
        callback: NetworkCallback<AddPointsResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<AddPointsResponse> = getApiInterface().addPoints(buyerName, amount, email, phone, purpose)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }


    fun addWithdrawal(
        userId: Int,
        amount: Int,
        paymentMethod: String,
        callback: NetworkCallback<WithdrawResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<WithdrawResponse> = getApiInterface().addWithdrawal(userId,amount,paymentMethod)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }


    fun getOffer(
        userId: Int,
        callback: NetworkCallback<OfferResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<OfferResponse> = getApiInterface().getOffer(userId)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun getExplanationvideos(
        language: String,
        callback: NetworkCallback<ExplanationVideoResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<ExplanationVideoResponse> = getApiInterface().getExplanationVideos(language)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }




    fun updateProfile(
        userId: Int,
        avatarId: Int,
        name: String,
        interests: String?,
        callback: NetworkCallback<UpdateProfileResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<UpdateProfileResponse> =
                getApiInterface().updateProfile(userId, avatarId, name, interests)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun getCoins(
        userId: Int, callback: NetworkCallback<CoinsResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<CoinsResponse> = getApiInterface().getCoins(userId)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun deleteUsers(
        userId: Int, deleteReason: String, callback: NetworkCallback<DeleteUserResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<DeleteUserResponse> =
                getApiInterface().deleteUsers(userId, deleteReason)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun userValidation(
        userId: Int, name: String, callback: NetworkCallback<UserValidationResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<UserValidationResponse> =
                getApiInterface().userValidation(userId, name)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun getSettings(
        callback: NetworkCallback<SettingsResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<SettingsResponse> = getApiInterface().getSettings()
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun getSpeechText(
        userId: Int, language: String, callback: NetworkCallback<SpeechTextResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<SpeechTextResponse> =
                getApiInterface().getSpeechText(userId, language)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun updateVoice(
        userId: Int, voice: MultipartBody.Part, callback: NetworkCallback<VoiceUpdateResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<VoiceUpdateResponse> = getApiInterface().updateVoice(userId, voice)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }

    fun getRemainingTime(
        userId: Int, callType: String, callback: NetworkCallback<GetRemainingTimeResponse>
    ) {
        if (Helper.checkNetworkConnection()) {
            val apiCall: Call<GetRemainingTimeResponse> = getApiInterface().getRemainingTime(userId, callType)
            apiCall.enqueue(callback)
        } else {
            callback.onNoNetwork()
        }
    }
}

interface ApiInterface {
    @FormUrlEncoded
    @POST("login")
    fun login(@Field("mobile") mobile: String): Call<LoginResponse>

    @FormUrlEncoded
    @POST("send_otp")
    fun sendOTP(
        @Field("mobile") mobile: String,
        @Field("country_code") countryCode: Int,
        @Field("otp") otp: Int
    ): Call<SendOTPResponse>


    @FormUrlEncoded
    @POST("appsettings_list")
    fun appUpdate(@Field("user_id") userId: Int):Call<AppUpdateResponse>

    @FormUrlEncoded
    @POST("avatar_list")
    fun getAvatarsList(@Field("gender") gender: String): Call<AvatarsListResponse>

    @FormUrlEncoded
    @POST("calls_list")
    fun getCallsList(@Field("user_id") userId: Int,@Field("gender") gender: String): Call<CallsListResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("mobile") mobile: String,
        @Field("language") language: String,
        @Field("avatar_id") avatarId: Int,
        @Field("gender") gender: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("userdetails")
    fun getUser(
        @Field("user_id") user_id: Int,
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("userdetails")
    fun getUserSync(
        @Field("user_id") user_id: Int,
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("register")
    fun registerFemale(
        @Field("mobile") mobile: String,
        @Field("language") language: String,
        @Field("avatar_id") avatarId: Int,
        @Field("gender") gender: String,
        @Field("age") age: String,
        @Field("interests") interests: String,
        @Field("describe_yourself") describe_yourself: String,

        ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("transaction_list")
    fun getTransactions(@Field("user_id") userId: Int): Call<TransactionsResponse>

    @FormUrlEncoded
    @POST("female_users_list")
    fun getFemaleUsers(@Field("user_id") userId: Int): Call<FemaleUsersResponse>

    @FormUrlEncoded
    @POST("random_user")
    fun getRandomUser(@Field("user_id") userId: Int,@Field("call_type") callType: String): Call<RandomUsersResponse>


    @FormUrlEncoded
    @POST("reports")
    fun getReports(@Field("user_id") userId: Int): Call<ReportsResponse>

    @FormUrlEncoded
    @POST("calls_status_update")
    fun updateCallStatus(
        @Field("user_id") userId: Int,
        @Field("call_type") call_type: String,
        @Field("status") status: Int
    ): Call<UpdateCallStatusResponse>

    @FormUrlEncoded
    @POST("female_call_attend")
    fun femaleCallAttend(
        @Field("user_id") userId: Int,
        @Field("call_id") callId: Int,
        @Field("started_time") startedTime: String
    ): Call<FemaleCallAttendResponse>

    @FormUrlEncoded
    @POST("call_female_user")
    fun callFemaleUser(
        @Field("user_id") userId: Int,
        @Field("call_user_id") callUserId: Int,
        @Field("call_type") callType: String
    ): Call<CallFemaleUserResponse>

    @FormUrlEncoded
    @POST("update_connected_call")
    suspend fun updateConnectedCall(
        @Field("user_id") userId: Int,
        @Field("call_id") callId: Int,
        @Field("started_time") startedTime: String,
        @Field("ended_time") endedTime: String,
    ): Response<UpdateConnectedCallResponse>

    @FormUrlEncoded
    @POST("individual_update_connected_call")
    suspend fun individualUpdateConnectedCall(
        @Field("user_id") userId: Int,
        @Field("call_id") callId: Int,
        @Field("started_time") startedTime: String,
        @Field("ended_time") endedTime: String,
    ): Response<UpdateConnectedCallResponse>

    @FormUrlEncoded
    @POST("withdrawals_list")
    fun getEarnings(@Field("user_id") userId: Int): Call<EarningsResponse>


    @FormUrlEncoded
    @POST("update_bank")
    fun updateBank(
        @Field("user_id") userId: Int,
        @Field("bank") bank: String,
        @Field("account_num") accountNum: String,
        @Field("branch") branch: String,
        @Field("ifsc") ifsc: String,
        @Field("holder_name") holderName: String
    ): Call<BankUpdateResponse>

    @FormUrlEncoded
    @POST("ratings")
    fun updateRatings(
        @Field("user_id") userId: Int,
        @Field("call_user_id") call_user_id: Int,
        @Field("ratings") ratings: String,
        @Field("title") title: String,
        @Field("description") description: String
    ): Call<RatingResponse>

    @FormUrlEncoded
    @POST("update_upi")
    fun updateUpi(
        @Field("user_id") userId: Int,
        @Field("upi_id") upiId: String,
    ): Call<UpiUpdateResponse>

    @FormUrlEncoded
    @POST("dwpay/add_coins_requests.php")
    fun addPoints(
        @Field("buyer_name") buyer_name: String,
        @Field("amount") amount: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("purpose") purpose: String,
    ): Call<AddPointsResponse>

    @FormUrlEncoded
    @POST("withdrawals")
    fun addWithdrawal(
        @Field("user_id") userId: Int,
        @Field("amount") amount: Int,
        @Field("type") paymentMethod: String
    ): Call<WithdrawResponse>


    @FormUrlEncoded
    @POST("best_offers")
    fun getOffer(
        @Field("user_id") userId: Int,
    ): Call<OfferResponse>


    @FormUrlEncoded
    @POST("update_profile")
    fun updateProfile(
        @Field("user_id") userId: Int,
        @Field("avatar_id") avatarId: Int,
        @Field("name") name: String,
        @Field("interests") interests: String?
    ): Call<UpdateProfileResponse>

    @FormUrlEncoded
    @POST("coins_list")
    fun getCoins(@Field("user_id") userId: Int): Call<CoinsResponse>

    @FormUrlEncoded
    @POST("delete_users")
    fun deleteUsers(
        @Field("user_id") userId: Int, @Field("delete_reason") deleteReason: String
    ): Call<DeleteUserResponse>

    @FormUrlEncoded
    @POST("user_validations")
    fun userValidation(
        @Field("user_id") userId: Int, @Field("name") name: String
    ): Call<UserValidationResponse>

    @FormUrlEncoded
    @POST("speech_text")
    fun getSpeechText(
        @Field("user_id") userId: Int, @Field("language") language: String
    ): Call<SpeechTextResponse>

    @Multipart
    @POST("update_voice")
    fun updateVoice(
        @Part("user_id") userId: Int, @Part voice: MultipartBody.Part
    ): Call<VoiceUpdateResponse>

    @FormUrlEncoded
    @POST("get_remaining_time")
    fun getRemainingTime(
        @Field("user_id") userId: Int, @Field("call_type") callType:String
    ): Call<GetRemainingTimeResponse>

    @POST("settings_list")
    fun getSettings(): Call<SettingsResponse>

    @FormUrlEncoded
    @POST("explaination_video_list")
    fun getExplanationVideos(
        @Field("language") language: String
    ): Call<ExplanationVideoResponse>
}