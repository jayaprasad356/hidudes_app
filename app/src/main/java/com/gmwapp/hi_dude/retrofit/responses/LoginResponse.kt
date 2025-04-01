package com.gmwapp.hi_dude.retrofit.responses

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean,
    val registered: Boolean,
    val message: String,
    val token: String?,
    val data: UserData?,
)



data class UserData (
    val id: Int,
    val name: String,
    @SerializedName("user_gender")
    val gender: String,
    val image: String,
    val language: String,
    val interests: String?,
    val mobile: String,
    val avatar_id: Int,
    val datetime: String,
    val updated_at: String,
    val created_at: String,
    val age:Int?,
    val describe_yourself:String?,
    val voice:String?,
    val status:Int?,
    val audio_status:Int?,
    val video_status:Int?,
    val balance:Float?,
    val coins:Int?,
    val bank:String?,
    val account_num:String?,
    val branch:String?,
    val ifsc:String?,
    val holder_name:String?,
    val upi_id:String?,
    val profile_status:String?,

)