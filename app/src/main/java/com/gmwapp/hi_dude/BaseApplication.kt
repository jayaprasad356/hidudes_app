package com.gmwapp.hi_dude

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.repositories.FcmNotificationRepository
import com.gmwapp.hi_dude.utils.DPreferences
import com.google.firebase.FirebaseApp
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
//import com.zegocloud.uikit.prebuilt.call.core.CallInvitationServiceImpl
//import com.zegocloud.uikit.prebuilt.call.core.notification.RingtoneManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider {
    private var isReceiverDetailsAvailable: Boolean = false
    private var startTime: String? = null
    private var callUserId: String? = null
    private var callUserName: String? = null
    private var callId: Int? = null
    private var mPreferences: DPreferences? = null
    private var called: Boolean? = null
    private var callType: String? = null
    private var roomId: String? = null
    private var mediaPlayer: MediaPlayer? = null
    private var endCallUpdatePending: Boolean? = null
    val ONESIGNAL_APP_ID = "2878a3a7-8a9a-4902-b255-72e9af65af29"
    //    val ONESIGNAL_APP_ID = "2c7d72ae-8f09-48ea-a3c8-68d9c913c592"
    private lateinit var sharedPreferences: SharedPreferences

    private var currentActivity: Activity? = null

    private var senderId: Int? = null
    private var callTypeForSplashActivity: String? = null
    private var channelName: String? = null
    private var callIdForSplashActivity: Int? = null
    private var incomingCall: Boolean = false



    private val lifecycleCallbacks: ActivityLifecycleCallbacks =
        object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


            }

            override fun onActivityStarted(activity: Activity) {
                currentActivity = activity
                Log.d("myCurrentActivity","$currentActivity")
            }

            override fun onActivityResumed(activity: Activity) {

                currentActivity = activity

                if(getInstance()?.getPrefs()?.getUserData()?.gender == DConstants.MALE) {
//                    CallInvitationServiceImpl.getInstance().hideIncomingCallDialog()
//                    RingtoneManager.stopRingTone()
                }
            }

            override fun onActivityPaused(p0: Activity) {
            }

            override fun onActivityStopped(p0: Activity) {
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                if (currentActivity == activity) {
                    currentActivity = null
                }
            }

        }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var fcmNotificationRepository: FcmNotificationRepository

    companion object {
        private var mInstance: BaseApplication? = null


        fun getInstance(): BaseApplication? {
            return mInstance
        }





    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        mPreferences = DPreferences(this)
        FirebaseApp.initializeApp(this)
        registerReceiver(ShutdownReceiver(), IntentFilter(Intent.ACTION_SHUTDOWN));
        if(BuildConfig.DEBUG) {
            OneSignal.Debug.logLevel = LogLevel.VERBOSE
        }

        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)


        // requestPermission will show the native Android notification permission prompt.
        // NOTE: It's recommended to use a OneSignal In-App Message to prompt instead.
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(false)
        }
        var userId = getInstance()?.getPrefs()
            ?.getUserData()?.id.toString() // Set user_id
        Log.d("userIDCheck", "Logging in with userId: $userId")


        if (!userId.isNullOrEmpty()) {
            Log.d("OneSignal", "Logging in with userId: $userId")
            OneSignal.login(userId)
        } else {
            Log.e("OneSignal", "User ID is null or empty, cannot log in.")
        }

        registerActivityLifecycleCallbacks(lifecycleCallbacks)



    }

    fun getCurrentActivity(): Activity? {
        return currentActivity
    }


    fun playIncomingCallSound() {
        stopRingtone() // Stop any existing ringtone before playing a new one

        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.rhythm)
        mediaPlayer?.setOnCompletionListener {
            it.release()
            mediaPlayer = null // Set to null to avoid using a released player
        }
        mediaPlayer?.isLooping = true

        mediaPlayer?.start()
    }

    fun isRingtonePlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }


    fun stopRingtone() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()  // Release resources
        }
        mediaPlayer = null  // Ensure it's set to null after stopping
        Log.d("MediaPlayer", "Ringtone stopped and released.")
    }


    override fun registerActivityLifecycleCallbacks(callback: ActivityLifecycleCallbacks?) {
        super.registerActivityLifecycleCallbacks(callback)
    }

    fun getPrefs(): DPreferences? {
        return mPreferences
    }

    fun setCalled(called: Boolean) {
        this.called = called
    }

    fun isCalled(): Boolean? {
        return this.called
    }

    fun setRoomId(roomId: String?) {
        this.roomId = roomId
    }

    fun getRoomId(): String? {
        return this.roomId
    }

    fun setMediaPlayer(mediaPlayer: MediaPlayer?) {
        this.mediaPlayer = mediaPlayer
    }

    fun getMediaPlayer(): MediaPlayer? {
        return this.mediaPlayer
    }

    fun setReceiverDetailsAvailable(isReceiverDetailsAvailable: Boolean) {
        this.isReceiverDetailsAvailable = isReceiverDetailsAvailable
    }

    fun isReceiverDetailsAvailable(): Boolean {
        return this.isReceiverDetailsAvailable
    }

    fun setCallUserId(callUserId: String?) {
        this.callUserId = callUserId
    }

    fun getCallUserId(): String? {
        return this.callUserId
    }

    fun setCallUserName(callUserName: String?) {
        this.callUserName = callUserName
    }

    fun getCallUserName(): String? {
        return this.callUserName
    }

    fun setStartTime(startTime: String?) {
        this.startTime = startTime
    }

    fun getStartTime(): String? {
        return this.startTime
    }

    fun setCallId(callId: Int?) {
        this.callId = callId
    }

    fun getCallId(): Int? {
        return this.callId
    }

    fun setCallType(callType: String?) {
        this.callType = callType
    }

    fun getCallType(): String? {
        return this.callType
    }

    fun setEndCallUpdatePending(endCallUpdatePending: Boolean?) {
        this.endCallUpdatePending = endCallUpdatePending
    }

    fun isEndCallUpdatePending(): Boolean? {
        return this.endCallUpdatePending
    }

    fun saveSenderId(senderId: Int) {
        sharedPreferences.edit().putInt("SENDER_ID", senderId).apply()
    }

    fun getSenderId(): Int {
        return sharedPreferences.getInt("SENDER_ID", -1)
    }


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()


    fun setIncomingCall(senderId: Int, callType: String, channelName: String, callId: Int) {
        this.senderId = senderId
        this.callTypeForSplashActivity = callType
        this.channelName = channelName
        this.callIdForSplashActivity = callId
        this.incomingCall = true
    }

    fun clearIncomingCall() {
        this.incomingCall = false
    }

    fun isIncomingCall(): Boolean = incomingCall
    fun getSenderIdForSplashActivity(): Int = senderId ?: -1
    fun getCallTypeForSplashActivity(): String = callTypeForSplashActivity.toString()
    fun getChannelName(): String = channelName.toString()
    fun getCallIdForSplashActivity(): Int? = callIdForSplashActivity

    fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as android.app.ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = applicationContext.packageName

        for (appProcess in appProcesses) {
            if (appProcess.processName == packageName &&
                appProcess.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true  // App is in foreground
            }
        }
        return false  // App is in background
    }
}