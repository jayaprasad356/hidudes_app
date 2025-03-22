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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.*
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//class SplashScreenActivity : AppCompatActivity() {
//
//    private lateinit var appUpdateManager: AppUpdateManager
//    private val REQUEST_CODE_UPDATE = 100
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_splash_screen)
//
//        checkForAppUpdate()
//    }
//
//    private fun fakeUpdateCheck() {
//        // Simulate an update being available
//        val fakeUpdateAvailable = true
//        if (fakeUpdateAvailable) {
//            showUpdateDialog("https://your-app-update-link.com", "New version available. Update now.")
//        }
//    }
//
//    private fun checkForAppUpdate() {
//        appUpdateManager = AppUpdateManagerFactory.create(this)
//        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
//
//        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
//            when {
//                // Immediate update (forces update before using the app)
//                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
//                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> {
//                    startImmediateUpdate(appUpdateInfo)
//                }
//
//                // Flexible update (allows user to continue using the app)
//                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
//                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
//                    startFlexibleUpdate(appUpdateInfo)
//                }
//
//                // If an update was already in progress
//                appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
//                    startImmediateUpdate(appUpdateInfo)
//                }
//            }
//        }
//    }
//
//    private fun startImmediateUpdate(appUpdateInfo: AppUpdateInfo) {
//        appUpdateManager.startUpdateFlowForResult(
//            appUpdateInfo,
//            AppUpdateType.IMMEDIATE,
//            this,
//            REQUEST_CODE_UPDATE
//        )
//    }
//
//    private fun startFlexibleUpdate(appUpdateInfo: AppUpdateInfo) {
//        appUpdateManager.startUpdateFlowForResult(
//            appUpdateInfo,
//            AppUpdateType.FLEXIBLE,
//            this,
//            REQUEST_CODE_UPDATE
//        )
//
//        appUpdateManager.registerListener(installStateUpdatedListener)
//    }
//
//    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
//        if (state.installStatus() == InstallStatus.DOWNLOADED) {
//            showUpdateCompleteSnackbar()
//        }
//    }
//
//    private fun showUpdateCompleteSnackbar() {
//        Snackbar.make(findViewById(android.R.id.content), "Update downloaded. Restart app to apply changes.", Snackbar.LENGTH_INDEFINITE)
//            .setAction("Restart") {
//                appUpdateManager.completeUpdate()
//            }.show()
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE_UPDATE) {
//            if (resultCode != Activity.RESULT_OK) {
//                Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show()
//                checkForAppUpdate()
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        appUpdateManager.unregisterListener(installStateUpdatedListener)
//    }
//
//        private fun showUpdateDialog(link: String, description: String) {
//        val bottomSheetDialog = BottomSheetDialog(this)
//        val view = layoutInflater.inflate(R.layout.bottom_dialog_update, null)
//        bottomSheetDialog.setContentView(view)
//        bottomSheetDialog.setCancelable(false);
//
//        val btnUpdate = view.findViewById<View>(R.id.btnUpdate)
//        val dialogMessage = view.findViewById<TextView>(R.id.dialog_message)
//        dialogMessage.text = description
//        btnUpdate.setOnClickListener(View.OnClickListener {
//            val url = link;
//            val i = Intent(Intent.ACTION_VIEW)
//            i.data = Uri.parse(url)
//            startActivity(i)
//        })
//
//
//        // Customize your bottom dialog here
//        // For example, you can set text, buttons, etc.
//
//        bottomSheetDialog.show()
//    }
//}


@AndroidEntryPoint
class SplashScreenActivity : BaseActivity() {
    lateinit var binding: ActivitySplashScreenBinding
    val profileViewModel: ProfileViewModel by viewModels()
    val viewModel: LoginViewModel by viewModels()
    var currentVersion = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
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







        profileViewModel.getUserLiveData.observe(this, Observer {
//            Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()
            prefs?.setUserData(it.data)
            userData = it.data
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

                    val link = it.data[0].link
                    val description = it.data[0].description

                    GotoActivity(userData, latestVersion, link, description)
                }
            }
        })


    }

    fun GotoActivity(
        userData: UserData?,
        latestVersion: String,
        link: String,
        description: String
    ) {

        if (currentVersion.toInt() >= latestVersion.toInt()) {
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

}

