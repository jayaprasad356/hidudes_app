package com.gmwapp.hi_dude.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.callbacks.OnButtonClickListener
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.databinding.ActivityRandomUserBinding
import com.gmwapp.hi_dude.services.CallingService
import com.gmwapp.hi_dude.viewmodels.FemaleUsersViewModel
import com.gmwapp.hi_dude.viewmodels.ProfileViewModel
import com.gmwapp.hi_dude.workers.CallUpdateWorker
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.ExplainReasonCallback
import com.permissionx.guolindev.callback.RequestCallback
import com.zegocloud.uikit.ZegoUIKit
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.core.CallInvitationServiceImpl
import com.zegocloud.uikit.prebuilt.call.invite.internal.OutgoingCallButtonListener
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallType
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallUser
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoInvitationCallListener
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import dagger.hilt.android.AndroidEntryPoint
import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


@AndroidEntryPoint
class RandomUserActivity : BaseActivity(), OnButtonClickListener {
    private var timer: CountDownTimer? = null;
    private var mediaPlayer: MediaPlayer? = null
    private var isReceiverDetailsAvailable: Boolean = false
    private val CALL_PERMISSIONS_REQUEST_CODE = 1
    lateinit var binding: ActivityRandomUserBinding
    private val femaleUsersViewModel: FemaleUsersViewModel by viewModels()
    private var usersCount: Int = 0
    private val profileViewModel: ProfileViewModel by viewModels()

    private var userId: String = ""
    private var callUserId: String = ""
    private var callUserName: String = ""
    private var startTime: String = ""
    private var endTime: String = ""
    var targetUserId: String? = null

    private val dateFormat = SimpleDateFormat("HH:mm:ss").apply {
        timeZone = TimeZone.getTimeZone("Asia/Kolkata") // Set to IST time zone
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRandomUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
        askPermissions()
        onBackPressedDispatcher.addCallback(this) {
            stopCall()
            finish()
        }
    }

    private fun checkOverlayPermission() {
        try {
            PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .onExplainRequestReason(ExplainReasonCallback { scope, deniedList ->
                    try {
                        val message =
                            "We need your consent for the following permissions in order to use the offline call function properly"
                        scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
                    } catch (e: Exception) {
                    }
                }).request(RequestCallback { allGranted, grantedList, deniedList ->
                    try {
                        if (allGranted) {
                            initializeCall(false)
                        } else {
                            checkOverlayPermission()
                        }
                    } catch (e: Exception) {
                    }
                })
        } catch (e: Exception) {
        }

    }

    fun askPermissions() {
        val permissionNeeded =
            arrayOf("android.permission.RECORD_AUDIO", "android.permission.CAMERA")

        if (ContextCompat.checkSelfPermission(
                this, "android.permission.CAMERA"
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this, "android.permission.RECORD_AUDIO"
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(permissionNeeded, CALL_PERMISSIONS_REQUEST_CODE)
        } else {
            checkOverlayPermission()
        }
    }

    private fun initializeCall(cancelled: Boolean) {
        if (mediaPlayer == null) {
            val resID = resources.getIdentifier("rhythm", "raw", packageName)
            mediaPlayer = MediaPlayer.create(this, resID)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }

        val instance = BaseApplication.getInstance()
        if (isReceiverDetailsAvailable) {
            instance?.setReceiverDetailsAvailable(true)
            if (cancelled) {
                mediaPlayer?.pause()
                mediaPlayer?.stop()
                finish()
            } else {
                val receiverId = intent.getIntExtra(DConstants.RECEIVER_ID, 0)
                val receiverName = intent.getStringExtra(DConstants.RECEIVER_NAME)
                callType = intent.getStringExtra(DConstants.CALL_TYPE)
                instance?.setCallType(callType)
                val userData = instance?.getPrefs()?.getUserData()
                userData?.id?.let {
                    femaleUsersViewModel.callFemaleUser(
                        it, receiverId, callType.toString()
                    )
                }

                femaleUsersViewModel.callFemaleUserResponseLiveData.observe(this, Observer {
                    if (it != null && it.success) {
                        val callId = it.data?.call_id
                        val balanceTime = it.data?.balance_time
                        if (callId != null) {
                            setupCall(
                                receiverId.toString(),
                                receiverName.toString(),
                                callType.toString(),
                                balanceTime,
                                callId
                            )
                            addRoomStateChangedListener(callId)
                        }
                    } else {
                        Toast.makeText(
                            this@RandomUserActivity, it?.message, Toast.LENGTH_LONG
                        ).show()
                        stopCall()
                        finish()
                    }
                })

            }
        } else {
            instance?.setReceiverDetailsAvailable(false)

            if (usersCount < 4) {
                usersCount++
                val userData = instance?.getPrefs()?.getUserData()
                val callType = intent.getStringExtra(DConstants.CALL_TYPE)
                userData?.let {
                    callType?.let { it1 -> femaleUsersViewModel.getRandomUser(it.id, it1) }
                }
            } else {
                stopCall()
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (BaseApplication.getInstance()?.getRoomId() != null) {
            moveTaskToBack(true)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CALL_PERMISSIONS_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                val permissionToCamera = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val permissionToRecord = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (permissionToCamera && permissionToRecord) {
                    checkOverlayPermission()
                } else {
                    finish()
                    val intent = Intent(this, GrantPermissionsActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onButtonClick() {
        getRemainingTime()
    }

    private fun initUI() {

        isReceiverDetailsAvailable =
            intent.getBooleanExtra(DConstants.IS_RECEIVER_DETAILS_AVAILABLE, false)
        progress()

        val callType = intent.getStringExtra(DConstants.CALL_TYPE)
        val image = intent.getStringExtra(DConstants.IMAGE)
        val text = intent.getStringExtra(DConstants.TEXT)



        if (callType == "audio") {
            binding.tvTitle.text = "Audio call"
        } else {
            binding.tvTitle.text = "Video call"
        }

        val prefs = BaseApplication.getInstance()?.getPrefs()
        val userData = prefs?.getUserData()
        val profileImage = userData?.image

        val requestOptions = RequestOptions().circleCrop()
        Glide.with(this).load(profileImage).apply(requestOptions).into(binding.ivCallerProfile)

        if (image != null) {
            val requestOptions = RequestOptions().circleCrop()
            Glide.with(this).load(image).apply(requestOptions).into(binding.ivLogo)
        }
        if (text != null) {
            // binding.tvWaitHint.text = text
        }

//        binding.btnCancel.setOnClickListener({
//            stopCall()
//            finish()
//        })

        femaleUsersViewModel.randomUsersResponseLiveData.observe(this, Observer {
            if (it != null && it.success) {
                val data = it.data
                data?.call_id?.let { it1 ->

                    setupCall(
                        data.call_user_id.toString(),
                        data.call_user_name.toString(),
                        callType.toString(),
                        data.balance_time,
                        it1,
                    )
                }
                data?.call_id?.let { it1 -> addRoomStateChangedListener(it1) }
                Log.d("balanceTime", "${data?.balance_time}")
            } else {
                Toast.makeText(
                    this@RandomUserActivity, it?.message, Toast.LENGTH_LONG
                ).show()
                mediaPlayer?.pause()
                mediaPlayer?.stop()
                finish()
            }
        })
        femaleUsersViewModel.randomUsersErrorLiveData.observe(this, Observer {
            showErrorMessage(it)
            mediaPlayer?.pause()
            mediaPlayer?.stop()
            finish()
        })
    }

    private fun progress() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Set the ProgressBar max to 100
        val max = (if (isReceiverDetailsAvailable) 30 else 28) * 1000L
        progressBar.max = max.toInt()

        // Timer for 30 seconds
        timer = null;
        timer = object : CountDownTimer(max, 500) { // 30000ms = 30s, 300ms interval
            override fun onTick(millisUntilFinished: Long) {
                // Calculate the progress
                val progress = (max - millisUntilFinished).toInt()
                progressBar.progress = progress
            }

            override fun onFinish() {
                // Complete progress when the timer finishes
                progressBar.progress = max.toInt()
                timer?.cancel()
                try {
                    CallInvitationServiceImpl.getInstance().cancelInvitation { result -> }
                } catch (e: Exception) {
                }
                initializeCall(true)
            }
        }
        timer?.start()
    }

    private fun addRoomStateChangedListener(callId: Int) {
        BaseApplication.getInstance()?.setCallId(callId)
        ZegoUIKitPrebuiltCallService.events.invitationEvents.outgoingCallButtonListener =
            object : OutgoingCallButtonListener {
                override fun onOutgoingCallCancelButtonPressed() {
                    stopCall()
                    finish()
                }
            }
        ZegoUIKitPrebuiltCallService.events.invitationEvents.invitationListener =
            object : ZegoInvitationCallListener {
                override fun onIncomingCallReceived(
                    callID: String?,
                    caller: ZegoCallUser?,
                    callType: ZegoCallType?,
                    callees: MutableList<ZegoCallUser>?
                ) {
                }


                override fun onIncomingCallCanceled(callID: String?, caller: ZegoCallUser?) {
                    Log.d("InvitationEvent", "Incoming call canceled. CallID: $callID, Caller: ${caller?.name}")

                    stopCall()
                    finish()
                }

                override fun onIncomingCallTimeout(callID: String?, caller: ZegoCallUser?) {
                    Log.d("InvitationEvent", "Incoming call timeout. CallID: $callID")

                    stopCall()
                    finish()
                }

                override fun onOutgoingCallAccepted(callID: String?, callee: ZegoCallUser?) {
                    Log.d("InvitationEvent", "Call accepted $callID")

                }


                override fun onOutgoingCallRejectedCauseBusy(
                    callID: String?, callee: ZegoCallUser?
                ) {
                    ZegoUIKitPrebuiltCallService.endCall()
                    initializeCall(true)
                }

                override fun onOutgoingCallDeclined(callID: String?, callee: ZegoCallUser?) {
                    Log.d("CallEvent", "Outgoing call declined. CallID: $callID, Callee: ${callee?.name}")

                    ZegoUIKitPrebuiltCallService.endCall()
                    initializeCall(true)
                }

                override fun onOutgoingCallTimeout(
                    callID: String?, callees: MutableList<ZegoCallUser>?
                ) {
                    Log.d("CallEvent", "Outgoing call timeout. CallID: $callID")

                    ZegoUIKitPrebuiltCallService.endCall()
                    initializeCall(true)
                }
            }


        ZegoUIKit.addRoomStateChangedListener { room, reason, _, _ ->
            when (reason) {
                ZegoRoomStateChangedReason.LOGINED -> {
                    Log.d("RoomStateChanged", "Login successful")
                    if(CallInvitationServiceImpl.getInstance().callInvitationData.type == 1) {
                        activateWakeLock()
                    }
                    startService(Intent(this@RandomUserActivity, CallingService::class.java))
                    timer?.cancel();
                    lastActiveTime = System.currentTimeMillis()
                    mediaPlayer?.pause()
                    mediaPlayer?.stop()
                    roomID = room
                    BaseApplication.getInstance()?.setRoomId(roomID)
                    userId = BaseApplication.getInstance()?.getPrefs()
                        ?.getUserData()?.id.toString() // Set user_id
                    callUserId = targetUserId.toString() // Set call_user_id
                    startTime = dateFormat.format(Date()) // Set call start time in IST
                    BaseApplication.getInstance()?.setStartTime(startTime)
                }

                ZegoRoomStateChangedReason.LOGOUT -> {
                    Log.d("RoomStateChanged", "Logout successful")
                    Log.d("RoomStateChanged", "Logout reason: $reason")  // This will give you the reason

                    releaseWakeLock()
                    lifecycleScope.launch {
                        stopService(Intent(this@RandomUserActivity, CallingService::class.java))
                        lastActiveTime = null
                        delay(500)
                        if (roomID != null) {
                            roomID = null
                            BaseApplication.getInstance()?.setRoomId(null)
                            BaseApplication.getInstance()?.setCallId(null)
                            endTime = dateFormat.format(Date()) // Set call end time in IST

                            val constraints =
                                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build()
                            val data: Data = Data.Builder().putInt(
                                DConstants.USER_ID,
                                BaseApplication.getInstance()?.getPrefs()?.getUserData()?.id ?: 0
                            ).putInt(DConstants.CALL_ID, callId)
                                .putString(DConstants.STARTED_TIME, startTime)
                                .putBoolean(DConstants.IS_INDIVIDUAL, isReceiverDetailsAvailable)
                                .putString(DConstants.ENDED_TIME, endTime).build()

                            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(
                                CallUpdateWorker::class.java
                            ).setInputData(data).setConstraints(constraints).build()
                            WorkManager.getInstance(this@RandomUserActivity)
                                .enqueue(oneTimeWorkRequest)
                            mediaPlayer?.pause()
                            mediaPlayer?.stop()
                            finish()
                            val receiverId = intent.getIntExtra(DConstants.RECEIVER_ID, 0)
                            val intent = Intent(this@RandomUserActivity, RatingActivity::class.java)
                            intent.putExtra(DConstants.RECEIVER_NAME, callUserName)
                            intent.putExtra(DConstants.RECEIVER_ID, receiverId)
                            startActivity(intent)
                        }
                    }
                }

                else ->  {Log.d("RoomStateChanged", "Unhandled reason: $reason")
                }
            }
        }
    }

    private fun stopCall() {
        try {
            try {
                CallInvitationServiceImpl.getInstance().cancelInvitation { result -> }
            } catch (e: Exception) {
            }
            ZegoUIKitPrebuiltCallService.endCall()
            mediaPlayer?.pause()
            mediaPlayer?.stop()
        } catch (e: Exception) {
        }
    }

    private fun setupCall(
        receiverId: String?, receiverName: String, type: String, balanceTime: String?, callId: Int
    ) {
        val prefs = BaseApplication.getInstance()?.getPrefs()
        val userData = prefs?.getUserData()
        if (userData != null) {
            this.balanceTime = balanceTime
            setupZegoUIKit(userData.id, userData.name)
        }
        when (type) {
            "audio" -> receiverId?.let { StartVoiceCall(it, receiverName, callId) }
            "video" -> receiverId?.let { StartVideoCall(it, receiverName, callId) }
            else -> Toast.makeText(this, "Invalid call type", Toast.LENGTH_SHORT).show()
        }

    }

    private fun StartVoiceCall(targetUserId: String, targetName: String, callId: Int) {
        binding.voiceCallButton.setIsVideoCall(false)
        binding.voiceCallButton.resourceID = "zego_call"
        val user = ZegoUIKitUser(targetUserId, targetName)
        val instance = BaseApplication.getInstance()
        user.avatar = instance?.getPrefs()?.getUserData()?.image
        binding.voiceCallButton.setCustomData(callId.toString())
        binding.voiceCallButton.setInvitees(listOf(user))
        binding.voiceCallButton.setTimeout(if (isReceiverDetailsAvailable) 30 else 7)
        callUserName = targetName
        callUserId = targetUserId
        instance?.setCallUserId(callUserId)
        instance?.setCallUserName(callUserName)
        lifecycleScope.launch {
            if (instance?.isCalled() == false || instance?.isCalled() == null) {
                delay(4000)
                instance?.setCalled(true)
            }
            binding.voiceCallButton.performClick()
        }

    }

    private fun StartVideoCall(targetUserId: String, targetName: String, callId: Int) {
        callUserName = targetName
        callUserId = targetUserId
        binding.voiceCallButton.setIsVideoCall(true)
        binding.voiceCallButton.resourceID = "zego_call"
        val user = ZegoUIKitUser(targetUserId, targetName)
        val instance = BaseApplication.getInstance()
        user.avatar = instance?.getPrefs()?.getUserData()?.image
        binding.voiceCallButton.setCustomData(callId.toString())
        binding.voiceCallButton.setInvitees(listOf(user))
        binding.voiceCallButton.setTimeout(if (isReceiverDetailsAvailable) 30 else 7)
        lifecycleScope.launch {
            if (instance?.isCalled() == false || instance?.isCalled() == null) {
                delay(4000)
                instance?.setCalled(true)
            }
            binding.voiceCallButton.performClick()
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("End Call")
        builder.setMessage("Are you sure you want to end this call?")

        builder.setPositiveButton("End Call") { dialog, which ->
            stopCall()
            finish()
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()

        dialog.show()

        // Get the buttons
        val negativeButton: Button = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

        // Set text color
        negativeButton.setTextColor(resources.getColor(R.color.Red)) // Set Cancel button text color
        positiveButton.setTextColor(resources.getColor(R.color.white))
        positiveButton.setBackgroundColor(resources.getColor(R.color.teal_200))


        //Optional: Change dialog background
        //dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
