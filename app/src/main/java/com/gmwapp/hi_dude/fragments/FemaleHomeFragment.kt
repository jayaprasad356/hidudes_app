package com.gmwapp.hi_dude.fragments

import android.Manifest
import android.app.ActivityManager
import android.app.NotificationManager.IMPORTANCE_NONE
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsClient.getPackageName
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.activities.EarningsActivity
import com.gmwapp.hi_dude.activities.GrantPermissionsActivity
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.databinding.FragmentFemaleHomeBinding
import com.gmwapp.hi_dude.retrofit.callbacks.NetworkCallback
import com.gmwapp.hi_dude.retrofit.responses.FemaleCallAttendResponse
import com.gmwapp.hi_dude.services.CallingService
import com.gmwapp.hi_dude.utils.setOnSingleClickListener
import com.gmwapp.hi_dude.viewmodels.FemaleUsersViewModel
import com.gmwapp.hi_dude.workers.CallUpdateWorker
import com.onesignal.OneSignal
import com.tencent.mmkv.MMKV
import com.zegocloud.uikit.ZegoUIKit
import com.zegocloud.uikit.prebuilt.call.core.CallInvitationServiceImpl
import com.zegocloud.uikit.prebuilt.call.core.notification.PrebuiltCallNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


@AndroidEntryPoint
class FemaleHomeFragment : BaseFragment() {
    private val OVERLAY_REQUEST_CODE: Int = 2
    private var mContext: Context? = null
    private val CALL_PERMISSIONS_REQUEST_CODE = 1
    private val NOTIFICATIONS_ENABLED_REQUEST_CODE = 3
    lateinit var binding: FragmentFemaleHomeBinding
    private val femaleUsersViewModel: FemaleUsersViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private var isPermissionDenied: Boolean = false
    private val dateFormat = SimpleDateFormat("HH:mm:ss").apply {
        timeZone = TimeZone.getTimeZone("Asia/Kolkata") // Set to IST time zone
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            initializeCall()
        } else {
            try {
                askNotificationPermission()
            } catch (e: Exception) {
                val intent = Intent(context, GrantPermissionsActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private var startTime: String = ""
    private var endTime: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFemaleHomeBinding.inflate(layoutInflater)

        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        isPermissionDenied = sharedPreferences.getBoolean("isTagSet", false)
        initUI()
        askPermissions()
        askMediaPermissions()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    fun askPermissions() {
        val permissionNeeded =
            arrayOf("android.permission.RECORD_AUDIO", "android.permission.CAMERA")

        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it, "android.permission.CAMERA"
                )
            } != PackageManager.PERMISSION_GRANTED || context?.let {
                ContextCompat.checkSelfPermission(
                    it, "android.permission.RECORD_AUDIO"
                )
            } != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissionNeeded, CALL_PERMISSIONS_REQUEST_CODE)
        } else {
            checkOverlayPermission()
        }
    }

    private fun askNotificationsEnabled(){
        if(mContext!=null) {
            val invitationConfig = CallInvitationServiceImpl.getInstance()
                .callInvitationConfig
            var channelID = MMKV.defaultMMKV().getString("channelID", null)
            if (channelID == null) {
                channelID = if (invitationConfig?.notificationConfig != null) {
                    invitationConfig.notificationConfig.channelID
                } else {
                    PrebuiltCallNotificationManager.incoming_call_channel_id
                }
            }
            if (NotificationManagerCompat.from(mContext!!).areNotificationsEnabled()
                && NotificationManagerCompat.from(mContext!!)
                    .getNotificationChannel(channelID.toString())?.importance != IMPORTANCE_NONE
                && NotificationManagerCompat.from(mContext!!)
                    .getNotificationChannel(CallingService.callingChannelId)?.importance != IMPORTANCE_NONE
            ) {
                initializeCall()
            }else{
                try {
                    val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, mContext?.packageName)
                    Toast.makeText(context, getString(R.string.enable_notification), Toast.LENGTH_SHORT).show()
                    startActivityForResult(settingsIntent, NOTIFICATIONS_ENABLED_REQUEST_CODE)
                } catch (e: Exception) {
                    initializeCall()
                }
            }
        }else{
            initializeCall()
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        requireActivity(), Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    askNotificationsEnabled();
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    val intent = Intent(context, GrantPermissionsActivity::class.java)
                    startActivity(intent)
                } else {
                    try {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } catch (e: Exception) {
                        val intent = Intent(context, GrantPermissionsActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else {
                askNotificationsEnabled();
            }
        } catch (e: Exception) {
            val intent = Intent(context, GrantPermissionsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun askMediaPermissions() {
        val requiredPermissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> { // Android 14+ (API 34)
                Log.d("askMediaPermissions", "android version :" + Build.VERSION.SDK_INT.toString())
                Log.d("askMediaPermissions", "android version :" + Build.VERSION_CODES.UPSIDE_DOWN_CAKE.toString())
                try {
                    arrayOf(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)  // Access selected media
                } catch (e : Exception) {
                    Log.d("askMediaPermissions", "android version :" + e.message)
                    arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> { // Android 13 (API 33)
                arrayOf(
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }
            else -> { // Android 12 and below
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        // Check if permissions are already granted
        if (requiredPermissions.all { ContextCompat.checkSelfPermission(requireActivity(), it) == PackageManager.PERMISSION_GRANTED }) {
            onMediaPermissionsGranted()
        } else {

            // Show a message explaining why the permission is needed
//            Toast.makeText(requireActivity(), "This app needs access to your photos, videos, Microphone and media files.", Toast.LENGTH_LONG).show()

            // Request permissions if not granted
            requestMediaPermissionLauncher.launch(requiredPermissions)
        }
    }

    private val requestMediaPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            onMediaPermissionsGranted()
        } else {
            val intent = Intent(context, GrantPermissionsActivity::class.java)
            startActivity(intent)
//            Toast.makeText(requireActivity(), "Permissions denied. Unable to access media.", Toast.LENGTH_LONG).show()
        }
    }

    private fun onMediaPermissionsGranted() {
//        Toast.makeText(requireActivity(), "Media permissions granted!", Toast.LENGTH_LONG).show()
        // Proceed with accessing images/videos
    }

    private fun checkOverlayPermission() {

        if (isPermissionDenied) {
            // If permission was denied before, do not ask again
            askNotificationPermission()
            return
        }


        try {
            val result = mContext?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            if (!Settings.canDrawOverlays(mContext) && !result.isLowRamDevice) {
                try {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context?.packageName)
                    )
                    startActivityForResult(intent, OVERLAY_REQUEST_CODE)
                } catch (e: Exception) {
                    askNotificationPermission()
                }
            } else {
                askNotificationPermission()
            }
        } catch (e: Exception) {
            askNotificationPermission()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OVERLAY_REQUEST_CODE) {
            if (Settings.canDrawOverlays(mContext)) {
                askNotificationPermission()
            } else {
                sharedPreferences.edit().putBoolean("isTagSet", true).apply()
                askNotificationPermission()
            }
        } else if(requestCode == NOTIFICATIONS_ENABLED_REQUEST_CODE){
            askNotificationsEnabled()
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
                if (!(permissionToCamera && permissionToRecord)) {
                    val intent = Intent(context, GrantPermissionsActivity::class.java)
                    startActivity(intent)
                } else {
                    checkOverlayPermission()
                }
            }
        }
    }

    private fun initializeCall() {
        val prefs = BaseApplication.getInstance()?.getPrefs()
        val userData = prefs?.getUserData()
        if (userData != null) {
            registerBroadcastReceiver()
            setupZegoUIKit(userData.id, userData.name)
            addRoomStateChangedListener()
        }
    }

    private fun initUI() {

        val prefs = BaseApplication.getInstance()?.getPrefs()
        val userData = prefs?.getUserData()


        val language = userData?.language

        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val isTagSet = sharedPreferences.getBoolean("isOneSignalTagSet", false)



        OneSignal.User.addTag("gender", "female")
        language?.let {
            OneSignal.User.addTag("language", it)
            Log.d("OneSignalTag", "Language tag added: $it")
        }

        language?.let {
            OneSignal.User.addTag("gender_language", "female_$it")
            Log.d("OneSignalTag", "female_$it")

        }



//
//        // Send the tag only if it hasn't been set before
//        if (!isTagSet) {
//            OneSignal.User.addTag("gender", "female")
//            language?.let {
//                OneSignal.User.addTag("language", it)
//                Log.d("OneSignalTag", "Language tag added: $it")
//            }
//
//            // Mark the flag so this doesn't happen again
//            sharedPreferences.edit().putBoolean("isOneSignalTagSet", true).apply()
//        } else {
//            Log.d("OneSignalTag", "Tag already set, skipping... ")
//        }



        binding.clCoins.setOnSingleClickListener({
            val intent = Intent(context, EarningsActivity::class.java)
            startActivity(intent)
        })

        if (userData != null) {
            binding.sAudio.isChecked = userData.audio_status == 1
            binding.sVideo.isChecked = userData.video_status == 1
        }

        binding.tvCoins.text = "₹" + userData?.balance.toString()

        Log.d("femaleuserdata", "${userData?.name} , ${userData?.language}")

        femaleUsersViewModel.getReports(userData?.id!!)


        femaleUsersViewModel.reportResponseLiveData.observe(viewLifecycleOwner, Observer {
            if (it.success) {

                binding.tvApproxEarnings.text = it.data[0].today_earnings.toString()
                binding.tvTotalCalls.text = it.data[0].today_calls.toString()

            } else {
                //  Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        femaleUsersViewModel.updateCallStatusResponseLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null && it.success) {
                prefs.setUserData(it.data)
            } else {
                Toast.makeText(context, it?.message, Toast.LENGTH_SHORT).show()
                binding.sAudio.isChecked = prefs.getUserData()?.audio_status == 1
                binding.sVideo.isChecked = prefs.getUserData()?.video_status == 1
            }
        })
        femaleUsersViewModel.updateCallStatusErrorLiveData.observe(viewLifecycleOwner, Observer {
            showErrorMessage(it)
            binding.sAudio.isChecked = prefs.getUserData()?.audio_status == 1
            binding.sVideo.isChecked = prefs.getUserData()?.video_status == 1
        })
        binding.sAudio.setOnCheckedChangeListener({ buttonView, isChecked ->
            userData.id.let {
                femaleUsersViewModel.updateCallStatus(
                    it, DConstants.AUDIO, if (isChecked) 1 else 0
                )
            }
            if (isChecked) {

            }
        })
        binding.sVideo.setOnCheckedChangeListener({ buttonView, isChecked ->
            userData.id.let {
                femaleUsersViewModel.updateCallStatus(
                    it, DConstants.VIDEO, if (isChecked) 1 else 0
                )
            }
        })

        binding.btnFloatingAction.setOnClickListener {
            val groupLink = "https://whatsapp.com/channel/0029Vb53H0q8PgsDJKx6EN2i" // Replace with your actual WhatsApp group link
            openWhatsAppGroup(groupLink)
        }


    }

    private fun openWhatsAppGroup(groupLink: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(groupLink)
            intent.setPackage("com.whatsapp") // Ensures only WhatsApp handles the intent
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
//            Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
        }
    }


    private fun addRoomStateChangedListener() {

        ZegoUIKit.addRoomStateChangedListener { room, reason, _, _ ->
            when (reason) {
                ZegoRoomStateChangedReason.LOGINED -> {
                    if (CallInvitationServiceImpl.getInstance().callInvitationData.type == 1) {
                        activateWakeLock()
                    }
                    mContext?.startService(Intent(mContext, CallingService::class.java))
                    mContext?.let {
                        NotificationManagerCompat.from(it)
                            .cancel(PrebuiltCallNotificationManager.incoming_call_notification_id)
                    }
                    CallInvitationServiceImpl.getInstance().dismissCallNotification()
                    lastActiveTime = System.currentTimeMillis()
                    roomID = room
                    startTime = dateFormat.format(Date()) // Set call start time in IST
                    femaleUsersViewModel.femaleCallAttend(receivedId,
                        callId,
                        startTime,
                        object : NetworkCallback<FemaleCallAttendResponse> {
                            override fun onResponse(
                                call: Call<FemaleCallAttendResponse>,
                                response: Response<FemaleCallAttendResponse>
                            ) {
                                balanceTime = response.body()?.data?.remaining_time
                            }

                            override fun onFailure(
                                call: Call<FemaleCallAttendResponse>, t: Throwable
                            ) {
                            }

                            override fun onNoNetwork() {
                            }
                        })

                }

                ZegoRoomStateChangedReason.LOGOUT -> {
                    releaseWakeLock()
                    lifecycleScope.launch {
                        mContext?.stopService(Intent(mContext, CallingService::class.java))

                        lastActiveTime = 0
                        delay(500)
                        if (roomID != null) {
                            roomID = null
                            endTime = dateFormat.format(Date()) // Set call end time in IST

                            val constraints =
                                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build()
                            val data: Data = Data.Builder().putInt(DConstants.USER_ID, receivedId)
                                .putInt(DConstants.CALL_ID, callId)
                                .putString(DConstants.STARTED_TIME, startTime).putBoolean(
                                    DConstants.IS_INDIVIDUAL,
                                    BaseApplication.getInstance()
                                        ?.isReceiverDetailsAvailable() == true
                                ).putString(DConstants.ENDED_TIME, endTime).build()
                            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(
                                CallUpdateWorker::class.java
                            ).setInputData(data).setConstraints(constraints).build()
                            mContext?.let {
                                WorkManager.getInstance(it).enqueue(oneTimeWorkRequest)
                            }
                            val prefs = BaseApplication.getInstance()?.getPrefs()
                            val userData = prefs?.getUserData()
                            if (userData != null) {
                                setupZegoUIKit(userData.id, userData.name)
                            }
                        }
                    }
                }

                else -> {
                }
            }
        }
    }

}