package com.gmwapp.hi_dude.repositories

import android.util.Log
import com.gmwapp.hi_dude.retrofit.responses.FcmNotificationResponse
import com.gmwapp.hi_dude.retrofit.ApiManager
import javax.inject.Inject
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback


class FcmNotificationRepository @Inject constructor(private val apiManager: ApiManager) {
    fun sendFcmNotification(
        senderId: Int,
        receiverId: Int,
        callType: String,
        channelName: String,
        message: String,
        callback: NetworkCallback<FcmNotificationResponse>
    ) {
        Log.d("FcmNotificationRepo", "Sending FCM notification...")
        Log.d("FcmNotificationRepo", "Sending FCM notification..." + senderId + " " + receiverId + " "  + callType + " "  + channelName + " "  + message)
        apiManager.sendFcmNotification(senderId, receiverId, callType, channelName, message, callback)
        Log.d("FcmNotificationRepo", "FCM notification sent.")
    }
}

