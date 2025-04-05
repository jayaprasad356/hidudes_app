package com.gmwapp.hi_dude

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
//import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService

class ShutdownReceiver(): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_SHUTDOWN == intent?.action) {
            // ZegoUIKitPrebuiltCallService.endCall()
        }
    }

}