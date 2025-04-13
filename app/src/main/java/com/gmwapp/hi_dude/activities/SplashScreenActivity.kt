package com.gmwapp.hi_dude.activities

import android.app.blob.BlobStoreManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.databinding.ActivitySplashScreenBinding
import com.gmwapp.hi_dude.retrofit.responses.UserData
import com.gmwapp.hi_dude.viewmodels.LoginViewModel
import com.gmwapp.hi_dude.viewmodels.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gmwapp.hi_dude.agora.female.FemaleCallAcceptActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.*
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class SplashScreenActivity : BaseActivity() {
    lateinit var binding: ActivitySplashScreenBinding
    val profileViewModel: ProfileViewModel by viewModels()
    val viewModel: LoginViewModel by viewModels()
    var currentVersion = ""
    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                Log.e("Update", "Update flow failed! Result code: ${result.resultCode}")
            }
        }

        initUI()
        playVideo()
    }

    private fun playVideo() {
        val videoView = findViewById<VideoView>(R.id.videoView)
        val videoPath = "android.resource://" + packageName + "/" + R.raw.logo_gif

        videoView.setVideoURI(Uri.parse(videoPath))
        videoView.setOnPreparedListener { mediaPlayer ->
            val videoWidth = mediaPlayer.videoWidth
            val videoHeight = mediaPlayer.videoHeight

            val videoViewWidth = videoView.width
            val videoViewHeight = videoView.height

            val videoRatio = videoWidth.toFloat() / videoHeight.toFloat()
            val screenRatio = videoViewWidth.toFloat() / videoViewHeight.toFloat()

            if (videoRatio > screenRatio) {
                videoView.layoutParams.height = (videoViewWidth / videoRatio).toInt()
            } else {
                videoView.layoutParams.width = (videoViewHeight * videoRatio).toInt()
            }

            videoView.requestLayout()
            mediaPlayer.isLooping = false
            videoView.start()
        }
    }

    private fun initUI() {
        // Check for network connectivity
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }


        viewModel.appUpdate()

        var intent: Intent? = null
        val prefs = BaseApplication.getInstance()?.getPrefs()
        var userData = prefs?.getUserData()



        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            currentVersion = pInfo.versionCode.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val isIncomingCall = BaseApplication.getInstance()?.isIncomingCall() ?: false
        val senderId = BaseApplication.getInstance()?.getSenderIdForSplashActivity() ?: -1
        val callType = BaseApplication.getInstance()?.getCallTypeForSplashActivity()
        val channelName = BaseApplication.getInstance()?.getChannelName() ?: "default_channel"
        val callId = BaseApplication.getInstance()?.getCallIdForSplashActivity()

        if (isIncomingCall) {
            Log.d("SplashActivity", "Incoming call detected! Redirecting to Call Accept Screen.")

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, FemaleCallAcceptActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("CALL_TYPE", callType)
                    putExtra("SENDER_ID", senderId)
                    putExtra("CHANNEL_NAME", channelName)
                    putExtra("CALL_ID", callId)
                    Log.d("CALL_TYPE_Data", "$callType")

                }
                startActivity(intent)
                finish()
            }, 2000)  // Delay ONLY if there's an incoming call
        } else {
            Log.d("SplashActivity", "No incoming call. Redirecting to MainActivity.")
        }







        profileViewModel.getUserLiveData.observe(this, Observer {
            prefs?.setUserData(it?.data)
            userData = it?.data

            intent = when {
                userData?.status == 2 -> {
//                    Toast.makeText(this, "4", Toast.LENGTH_SHORT).show()
                    Intent(this, MainActivity::class.java).apply {
                        putExtra(
                            DConstants.AVATAR_ID,
                            getIntent().getIntExtra(DConstants.AVATAR_ID, 0)
                        )
                        putExtra(DConstants.LANGUAGE, userData?.language)
                        flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                }

                userData?.status == 1 -> {
//                    Toast.makeText(this, "5", Toast.LENGTH_SHORT).show()
                    Intent(this, AlmostDoneActivity::class.java).apply {
                        flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                }

                else -> {
//                    Toast.makeText(this, "6", Toast.LENGTH_SHORT).show()
                    Intent(this, VoiceIdentificationActivity::class.java).apply {
                        putExtra(DConstants.LANGUAGE, userData?.language)
                        flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                }
            }
            startActivity(intent)
            finish()
        })



        viewModel.appUpdateResponseLiveData.observe(this, Observer {
            lifecycleScope.launch {
                delay(2000) // Wait for 10 seconds before navigating
                if (it != null && it.success) {

                    val latestVersion = it.data[0].app_version.toString()
                    val minimum_required_version = it.data[0].minimum_required_version.toString()

                    val link = it.data[0].link
                    val description = it.data[0].description

                    GotoActivity(
                        userData,
                        latestVersion,
                        minimum_required_version,
                        link,
                        description
                    )
                }
            }
        })


    }

    fun GotoActivity(
        userData: UserData?,
        latestVersion: String,
        minimum_required_version: String,
        link: String,
        description: String
    ) {



        if (currentVersion.toInt() >= minimum_required_version.toInt()) {
//            Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()
            if (userData == null) {
//                Toast.makeText(this, "2", Toast.LENGTH_SHORT).show()

//                intent = Intent(this@SplashScreenActivity, NewLoginActivity::class.java)
                val intent = Intent(this@SplashScreenActivity, NewLoginActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                if (userData?.gender == DConstants.MALE) {
//                    Toast.makeText(this, "3", Toast.LENGTH_SHORT).show()
                  //  intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
//                    Toast.makeText(this, "4", Toast.LENGTH_SHORT).show()
                    BaseApplication.getInstance()?.getPrefs()?.getUserData()?.id?.let {
                        profileViewModel.getUsers(it)
                    }

                }

                intent?.let {
                    Handler().postDelayed({
                        startActivity(it)
                        finish()
                    }, 2000)
                }
            }
        } else {
            showUpdateDialog(link, description)
        }

    }

    // Function to check network availability
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun showUpdateDialog(link: String, description: String) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_dialog_update, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.setCancelable(false);

        val btnUpdate = view.findViewById<View>(R.id.btnUpdate)
        val dialogMessage = view.findViewById<TextView>(R.id.dialog_message)
        dialogMessage.text = description
        btnUpdate.setOnClickListener(View.OnClickListener {
            val url = link;
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        })


        // Customize your bottom dialog here
        // For example, you can set text, buttons, etc.

        bottomSheetDialog.show()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().unregister(this) // Prevent unwanted registration
    }
}


//@AndroidEntryPoint
//class SplashScreenActivity : BaseActivity() {
//    lateinit var binding: ActivitySplashScreenBinding
//    val profileViewModel: ProfileViewModel by viewModels()
//    val viewModel: LoginViewModel by viewModels()
//    var currentVersion = ""
//    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        enableEdgeToEdge()
//        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
//            if (result.resultCode == RESULT_OK) {
//                Log.i("UpdateSuccess", "Update successful! Restarting app...")
//                restartApp()
//            } else {
//                Log.e("UpdateFailed", "Update flow failed! Result code: ${result.resultCode}")
//            }
//        }
//        checkForInAppUpdate()
//        initUI()
//        playVideo()
//    }
//
//    private fun playVideo() {
//        val videoView = findViewById<VideoView>(R.id.videoView)
//        val videoPath = "android.resource://" + packageName + "/" + R.raw.logo_gif
//
//        videoView.setVideoURI(Uri.parse(videoPath))
//        videoView.setOnPreparedListener { mediaPlayer ->
//            val videoWidth = mediaPlayer.videoWidth
//            val videoHeight = mediaPlayer.videoHeight
//
//            val videoViewWidth = videoView.width
//            val videoViewHeight = videoView.height
//
//            val videoRatio = videoWidth.toFloat() / videoHeight.toFloat()
//            val screenRatio = videoViewWidth.toFloat() / videoViewHeight.toFloat()
//
//            if (videoRatio > screenRatio) {
//                videoView.layoutParams.height = (videoViewWidth / videoRatio).toInt()
//            } else {
//                videoView.layoutParams.width = (videoViewHeight * videoRatio).toInt()
//            }
//
//            videoView.requestLayout()
//            mediaPlayer.isLooping = false
//            videoView.start()
//        }
//    }
//
//    private fun initUI() {
//        // Check for network connectivity
//        if (!isNetworkAvailable()) {
//            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//
//        viewModel.appUpdate()
//
//        var intent: Intent? = null
//        val prefs = BaseApplication.getInstance()?.getPrefs()
//        var userData = prefs?.getUserData()
//
//
//
//        try {
//            val pInfo = packageManager.getPackageInfo(packageName, 0)
//            currentVersion = pInfo.versionCode.toString()
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//        }
//
//        val isIncomingCall = BaseApplication.getInstance()?.isIncomingCall() ?: false
//        val senderId = BaseApplication.getInstance()?.getSenderIdForSplashActivity() ?: -1
//        val callType = BaseApplication.getInstance()?.getCallTypeForSplashActivity()
//        val channelName = BaseApplication.getInstance()?.getChannelName() ?: "default_channel"
//        val callId = BaseApplication.getInstance()?.getCallIdForSplashActivity()
//
//        if (isIncomingCall) {
//            Log.d("SplashActivity", "Incoming call detected! Redirecting to Call Accept Screen.")
//
//            Handler(Looper.getMainLooper()).postDelayed({
//                val intent = Intent(this, FemaleCallAcceptActivity::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    putExtra("CALL_TYPE", callType)
//                    putExtra("SENDER_ID", senderId)
//                    putExtra("CHANNEL_NAME", channelName)
//                    putExtra("CALL_ID", callId)
//                    Log.d("CALL_TYPE_Data", "$callType")
//
//                }
//                startActivity(intent)
//                finish()
//            }, 2000)  // Delay ONLY if there's an incoming call
//        } else {
//            Log.d("SplashActivity", "No incoming call. Redirecting to MainActivity.")
//        }
//
//
//
//
//
//
//
//        profileViewModel.getUserLiveData.observe(this, Observer {
////            Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()
//            prefs?.setUserData(it.data)
//            userData = it.data
//            intent = when {
//                userData?.status == 2 -> {
////                    Toast.makeText(this, "4", Toast.LENGTH_SHORT).show()
//                    Intent(this, MainActivity::class.java).apply {
//                        putExtra(
//                            DConstants.AVATAR_ID,
//                            getIntent().getIntExtra(DConstants.AVATAR_ID, 0)
//                        )
//                        putExtra(DConstants.LANGUAGE, userData?.language)
//                        flags =
//                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    }
//                }
//
//                userData?.status == 1 -> {
////                    Toast.makeText(this, "5", Toast.LENGTH_SHORT).show()
//                    Intent(this, AlmostDoneActivity::class.java).apply {
//                        flags =
//                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    }
//                }
//
//                else -> {
////                    Toast.makeText(this, "6", Toast.LENGTH_SHORT).show()
//                    Intent(this, VoiceIdentificationActivity::class.java).apply {
//                        putExtra(DConstants.LANGUAGE, userData?.language)
//                        flags =
//                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    }
//                }
//            }
//            startActivity(intent)
//            finish()
//        })
//
//
//
//        viewModel.appUpdateResponseLiveData.observe(this, Observer {
//            lifecycleScope.launch {
//                delay(2000) // Wait for 10 seconds before navigating
//                if (it != null && it.success) {
//
//                    val latestVersion = it.data[0].app_version.toString()
//
//                    val link = it.data[0].link
//                    val description = it.data[0].description
//
//                    GotoActivity(userData, latestVersion, link, description)
//                }
//            }
//        })
//
//
//    }
//
//    fun GotoActivity(
//        userData: UserData?,
//        latestVersion: String,
//        link: String,
//        description: String
//    ) {
//
//
//
//        if (currentVersion.toInt() >= latestVersion.toInt()) {
////            Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()
//            if (userData == null) {
////                Toast.makeText(this, "2", Toast.LENGTH_SHORT).show()
//
////                intent = Intent(this@SplashScreenActivity, NewLoginActivity::class.java)
//                val intent = Intent(this@SplashScreenActivity, NewLoginActivity::class.java)
//                startActivity(intent)
//                finish()
//
//            } else {
//                if (userData?.gender == DConstants.MALE) {
////                    Toast.makeText(this, "3", Toast.LENGTH_SHORT).show()
//                    //  intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
//                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                } else {
////                    Toast.makeText(this, "4", Toast.LENGTH_SHORT).show()
//                    BaseApplication.getInstance()?.getPrefs()?.getUserData()?.id?.let {
//                        profileViewModel.getUsers(it)
//                    }
//
//                }
//
//                intent?.let {
//                    Handler().postDelayed({
//                        startActivity(it)
//                        finish()
//                    }, 2000)
//                }
//            }
//        } else {
//            showUpdateDialog(link, description)
//        }
//
//    }
//
//    // Function to check network availability
//    private fun isNetworkAvailable(): Boolean {
//        val connectivityManager =
//            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val network = connectivityManager.activeNetwork
//        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
//        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
//    }
//
//    private fun showUpdateDialog(link: String, description: String) {
//        val bottomSheetDialog = BottomSheetDialog(this)
//        val view = layoutInflater.inflate(R.layout.bottom_dialog_update, null)
//        bottomSheetDialog.setContentView(view)
//        bottomSheetDialog.setCancelable(false);
//
//        val btnUpdate = view.findViewById<View>(R.id.btnUpdate)
//        val dialogMessage = view.findViewById<TextView>(R.id.dialog_message)
//        dialogMessage.text = description
//        btnUpdate.setOnClickListener(View.OnClickListener {
////            val url = link;
////            val i = Intent(Intent.ACTION_VIEW)
////            i.data = Uri.parse(url)
////            startActivity(i)
//            checkForInAppUpdate()
//        })
//
//
//        // Customize your bottom dialog here
//        // For example, you can set text, buttons, etc.
//
//        bottomSheetDialog.show()
//    }
//
//    override fun onStart() {
//        super.onStart()
//        EventBus.getDefault().unregister(this) // Prevent unwanted registration
//    }
//
//
//    private fun checkForInAppUpdate(){
//        val appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
//
//        // Returns an intent object that you use to check for an update.
//        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
//
//        // Checks that the platform will allow the specified type of update.
//        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                // This example applies an immediate update. To apply a flexible update
//                // instead, pass in AppUpdateType.FLEXIBLE
//                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//                // Request the update.
//
//                appUpdateManager.startUpdateFlowForResult(
//                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
//                    appUpdateInfo,
//                    // an activity result launcher registered via registerForActivityResult
//                    activityResultLauncher,
//                    // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
//                    // flexible updates.
//                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
//
//
//            }
//        }
////
////        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
////            // handle callback
////            if (result.resultCode != RESULT_OK) {
////               // log("Update flow failed! Result code: " + result.resultCode);
////                // If the update is canceled or fails,
////                // you can request to start the update again.
////            }
////        }
//
//
//    }
//
//    private fun restartApp() {
//        val intent = packageManager.getLaunchIntentForPackage(packageName)
//        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
//        finishAffinity() // Close all activities
//    }
//
//
//
//}