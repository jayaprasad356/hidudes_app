package com.gmwapp.hi_dude.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.facebook.appevents.AppEventsLogger
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.BillingManager.BillingManager
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.databinding.ActivityMainBinding
import com.gmwapp.hi_dude.dialogs.BottomSheetWelcomeBonus
import com.gmwapp.hi_dude.fragments.FemaleHomeFragment
import com.gmwapp.hi_dude.fragments.HomeFragment
import com.gmwapp.hi_dude.fragments.ProfileFemaleFragment
import com.gmwapp.hi_dude.fragments.ProfileFragment
import com.gmwapp.hi_dude.fragments.RecentFragment
import com.gmwapp.hi_dude.utils.DPreferences
import com.gmwapp.hi_dude.viewmodels.AccountViewModel
import com.gmwapp.hi_dude.viewmodels.FcmTokenViewModel
import com.gmwapp.hi_dude.viewmodels.OfferViewModel
import com.gmwapp.hi_dude.viewmodels.ProfileViewModel
import com.gmwapp.hi_dude.viewmodels.UpiPaymentViewModel
import com.gmwapp.hi_dude.viewmodels.WalletViewModel
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import kotlin.math.round


@AndroidEntryPoint
class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    BottomSheetWelcomeBonus.OnAddCoinsListener {
    lateinit var binding: ActivityMainBinding
    var isBackPressedAlready = false
    var userName: String? = null
    var userID: String? = null
    private var billingManager: BillingManager? = null

    val offerViewModel: OfferViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val accountViewModel: AccountViewModel by viewModels()
    private val fcmTokenViewModel: FcmTokenViewModel by viewModels()
    private val upiPaymentViewModel: UpiPaymentViewModel by viewModels()
    private val WalletViewModel: WalletViewModel by viewModels()

    lateinit var coinId: String
    private lateinit var logger: AppEventsLogger

    var PERMISSIONS: Array<String> = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_MEDIA_AUDIO
    )


    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.MANAGE_OWN_CALLS] == true) {
            // Permission granted, proceed with call service
        } else {
            // Show an error or disable call-related functionality
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }





        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
        userID = userData?.id.toString()

        billingManager = BillingManager(this)
        accountViewModel.getSettings()

        initUI()
        addObservers()

        updateFcmToken(userData?.id)

        userName = userData?.name

        onBackPressedDispatcher.addCallback(this) {
            if (isBackPressedAlready) {
                finish()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.press_back_again_to_exit),
                    Toast.LENGTH_SHORT
                ).show()
                isBackPressedAlready = true
                Handler().postDelayed({
                    isBackPressedAlready = false
                }, 3000)
            }
        }

        logger = AppEventsLogger.newLogger(this) // Initialize Logger

        logSentFriendRequestEvent()

        checkAndRequestUserConsent()
    }

    private fun showConsentDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_media_permission, null)
        val builder = AlertDialog.Builder(this, R.style.MaterialAlertDialogTheme)
            .setView(dialogView)

        val dialog = builder.create()

        // Disable closing on outside touch
        dialog.setCanceledOnTouchOutside(false)

        // Disable closing with back button
        dialog.setOnCancelListener { /* Prevent dialog from closing */ }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        // Set up button click listeners
        dialogView.findViewById<MaterialButton>(R.id.btn_accept).setOnClickListener {
            saveUserConsent(true)
            dialog.dismiss()
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_decline).setOnClickListener {
            saveUserConsent(false)
            dialog.dismiss()
        }
    }

    private fun saveUserConsent(consent: Boolean) {
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("image_consent", consent)
        editor.apply()
    }

    private fun getUserConsent(): Boolean? {
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        return if (sharedPreferences.contains("image_consent")) {
            sharedPreferences.getBoolean("image_consent", false) // Default to false if not found
        } else {
            null // Return null if the key does not exist
        }
    }

    private fun checkAndRequestUserConsent() {
        val consent = getUserConsent()

        if (consent == null || !consent) {  // If consent is not set or denied
            showConsentDialog()
        }
    }


    fun logSentFriendRequestEvent() {
        logger.logEvent("sentFriendRequest")
    }

//    override fun resumeZegoCloud(){
//        addRoomStateChangedListener()
//        moveTaskToBack(true)
//    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, notifications will work
        } else {
            // Permission denied, notify the user
        }
    }

    private fun initUI() {

        upiPaymentViewModel.upiPaymentLiveData.observe(this, Observer { response ->
            if (response != null && response.status) {
                val paymentUrl = response.data.firstOrNull()?.payment_url

                if (!paymentUrl.isNullOrEmpty()) {
                    Log.d("UPI Payment", "Payment URL: $paymentUrl")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl))
                    startActivity(intent)
                } else {
                    Log.e("UPI Payment Error", "Payment URL is null or empty")
                    Toast.makeText(this, "Payment URL not found. Please try again later.", Toast.LENGTH_LONG).show()
                }
            } else {
                Log.e("UPI Payment Error", "Invalid response: ${response?.data}")
                Toast.makeText(this, "Payment failed. Please check your internet or payment details.", Toast.LENGTH_LONG).show()
            }
        })




//        accountViewModel.settingsLiveData.observe(this, Observer { response ->
//            if (response?.success == true) {
//                response.data?.let { settingsList ->
//                    if (settingsList.isNotEmpty()) {
//                        val settingsData = settingsList[0]
//                        settingsData.payment_gateway_type?.let { paymentGatewayType ->
//                            Log.d("settingsData", "settingsData $paymentGatewayType")
//                            //handlePaymentGateway(paymentGatewayType)
////                            paymentGateway = paymentGatewayType
////                            Log.d("paymentGateway","$paymentGateway")
//                        } ?: run {
//                            // Show Toast if payment_gateway_type is null
//                            Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//            }
//        })


        val prefs = BaseApplication.getInstance()?.getPrefs()
        prefs?.getUserData()?.id?.let { profileViewModel.getUsers(it) }

        profileViewModel.getUserLiveData.observe(this, Observer { response ->
            response?.data?.let { userData ->
                prefs?.setUserData(userData)
            } ?: run {
                Log.e("Observer", "RegisterResponse is null")
            }
        })


        Log.d("DEBUG", "Received userID: $userID")

        userID?.toIntOrNull()?.let { offerViewModel.getOffer(it) }
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
        removeShiftMode()
    }


    private fun addObservers() {
        offerViewModel.offerResponseLiveData.observe(this) { response ->
            if (response.success) {
                val coin = response.data[0].coins
                val discountedPrice = response.data[0].price
                val save = response.data[0].save
                val coinId = response.data[0].id

                val originalPrice = calculateOriginalPrice(discountedPrice, save)


                Log.d("OrinalPrice","OriginalPrice $originalPrice")
                Log.d("OrinalPrice","discountPrice $discountedPrice")
                Log.d("OrinalPrice","savePercent $save")
                if (BaseApplication.getInstance()?.getPrefs()
                        ?.getUserData()?.gender == DConstants.MALE
                ) {
                    val bottomSheet = BottomSheetWelcomeBonus(coin, originalPrice, discountedPrice,coinId)
                    bottomSheet.show(supportFragmentManager, "BottomSheetWelcomeBonus")
                }
            }
        }
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
        binding.bottomNavigationView.selectedItemId = R.id.home
        removeShiftMode()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                val homeFragment = if (BaseApplication.getInstance()?.getPrefs()
                        ?.getUserData()?.gender == DConstants.FEMALE
                ) FemaleHomeFragment() else HomeFragment()
                supportFragmentManager.beginTransaction().replace(R.id.flFragment, homeFragment)
                    .commit()
                return true
            }

            R.id.recent -> {
                supportFragmentManager.beginTransaction().replace(R.id.flFragment, RecentFragment())
                    .commit()
                return true
            }

            R.id.profile -> {
                if (BaseApplication.getInstance()?.getPrefs()
                        ?.getUserData()?.gender == DConstants.MALE
                ) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, ProfileFragment()).commit()
                } else {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, ProfileFemaleFragment()).commit()
                }
                return true
            }
        }
        return false
    }

    @SuppressLint("RestrictedApi")
    fun removeShiftMode() {
        binding.bottomNavigationView.labelVisibilityMode =
            NavigationBarView.LABEL_VISIBILITY_LABELED
        val menuView = binding.bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount) {
            val item = menuView.getChildAt(i) as BottomNavigationItemView
            item.setShifting(false)
            item.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED)

            // set once again checked value, so view will be updated
            item.setChecked(item.itemData!!.isChecked)
        }
    }


    override fun onAddCoins(amount: String, id: Int) {
        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
        val userId = userData?.id
        var pointsId = "$id"
        val pointsIdInt: Int = pointsId.toIntOrNull() ?: 0

//        showEditProfileDialog(amount, id, pointsIdInt)

        if (userId != null && pointsId.isNotEmpty()) {
            if (pointsIdInt != null) {

                // ✅ Save userId and pointsIdInt BEFORE launching billing
                val preferences = DPreferences(this)
                preferences.setSelectedUserId(userId.toString())
                preferences.setSelectedPlanId(java.lang.String.valueOf(pointsIdInt))
                WalletViewModel.tryCoins(userId, pointsIdInt)
                billingManager!!.purchaseProduct(
//                    "coins_12",
                        pointsId,
                    userId,
                    pointsIdInt
                )
                WalletViewModel.navigateToMain.observe(this, Observer { shouldNavigate ->
//                    val intent = Intent(this, MainActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(intent)
//                    finish() // ✅ Now this works because we are in an Activity
                })
            }
        } else {
            Toast.makeText(this, "Invalid input data", Toast.LENGTH_SHORT).show()
        }
    }


//    override fun onAddCoins(amount: String, id: Int) {
//
//        var amount = "$amount"
//        var pointsId = "$id"
//        coinId = id.toString()
//        Log.d("amount", "amount $amount")
//
//        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
//
//        val userId = userData?.id
//        val name = userData?.name ?: ""
//        val email = "test@gmail.com"
//        val mobile = userData?.mobile ?: ""
//
//        if (userId != null && pointsId.isNotEmpty() && amount.isNotEmpty()) {
//            val userIdWithPoints = "$userId-$pointsId"
//
//            val apiService = RetrofitClient.instance
//            val call = apiService.addCoins(name, amount, email, mobile, userIdWithPoints)
//
//            val callRazor = apiService.addCoinsRazorPay(userIdWithPoints,name,amount,email,mobile)
//
//            accountViewModel.getSettings()
//
//
//            accountViewModel.settingsLiveData.observe(this, Observer { response ->
//                if (response != null && response.success) {
//                    response.data?.let { settingsList ->
//                        if (settingsList.isNotEmpty()) {
//                            val settingsData = settingsList[0]
//
//                            when ("upigateway") {
//                                "razorpay" -> {
//
//
//                                    callRazor.enqueue(object : retrofit2.Callback<RazorPayApiResponse> {
//                                        override fun onResponse(call: retrofit2.Call<RazorPayApiResponse>, response: retrofit2.Response<RazorPayApiResponse>) {
//                                            if (response.isSuccessful && response.body() != null) {
//                                                val apiResponse = response.body()
//
//                                                // Extract the Razorpay payment link
//                                                val paymentUrl = apiResponse?.short_url
//
//                                                if (!paymentUrl.isNullOrEmpty()) {
//
//                                                    val intent =Intent(this@MainActivity, LauncherActivity::class.java)
//                                                    intent.setData(Uri.parse(response.body()?.short_url))
//                                                    Log.d("WalletResponse","${response.body()?.short_url}")
//                                                    startActivity(intent)
//
////                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl))
////                                startActivity(intent)
//                                                } else {
//                                                    Toast.makeText(this@MainActivity, "Failed to get payment link", Toast.LENGTH_SHORT).show()
//                                                }
//                                            } else {
//                                                Toast.makeText(this@MainActivity, "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
//                                            }
//                                        }
//
//                                        override fun onFailure(call: retrofit2.Call<RazorPayApiResponse>, t: Throwable) {
//                                            Toast.makeText(this@MainActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
//                                        }
//                                    })
//
//
//
//
//
//                                }
//
//                                "upigateway" ->{
//
//                                    val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
//                                    var userid = userData?.id
//
//
//                                    userid?.let {
//                                        val clientTxnId = generateRandomTxnId(it,coinId)  // Generate a new transaction ID
//                                        upiPaymentViewModel.createUpiPayment(it, clientTxnId, amount)
//                                        Log.d("upigateway", "upigateway: " + it + " + " + clientTxnId + " + " + amount)
//                                    }
//
//                                }
//
//                                "instamojo" -> {
//
//
//                                    call.enqueue(object : retrofit2.Callback<ApiResponse> {
//                                        override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
//                                            if (response.isSuccessful && response.body()?.success == true) {
//                                                Toast.makeText(this@MainActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
//                                            } else {
//                                                // println("Long URL: ${it.longurl}") // Print to the terminal
//                                                //Toast.makeText(mContext, it.longurl, Toast.LENGTH_SHORT).show()
//
//                                                val intent =
//                                                    Intent(this@MainActivity, LauncherActivity::class.java)
//                                                intent.setData(Uri.parse(response.body()?.longurl))
//                                                Log.d("WalletResponse","${response.body()?.longurl}")
//                                                startActivity(intent)
//                                                finish()// Directly starting the intent without launcher
//                                                //  Toast.makeText(this@WalletActivity, response.body()?.message ?: "Error", Toast.LENGTH_SHORT).show()
//                                            }
//                                        }
//
//                                        override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
//                                            Toast.makeText(this@MainActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
//                                        }
//                                    })
//
//
//
//
//                                }
//                                else -> {
//                                    Toast.makeText(this, "Invalid Payment Gateway", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                        }
//                    }
//                }
//            })
//
//
////            call.enqueue(object : retrofit2.Callback<ApiResponse> {
////                override fun onResponse(
////                    call: retrofit2.Call<ApiResponse>,
////                    response: retrofit2.Response<ApiResponse>
////                ) {
////                    if (response.isSuccessful && response.body()?.success == true) {
////                        Toast.makeText(
////                            this@MainActivity,
////                            response.body()?.message,
////                            Toast.LENGTH_SHORT
////                        ).show()
////                    } else {
////                        // println("Long URL: ${it.longurl}") // Print to the terminal
////                        //Toast.makeText(mContext, it.longurl, Toast.LENGTH_SHORT).show()
////                        val intent = Intent(this@MainActivity, LauncherActivity::class.java)
////                        intent.setData(Uri.parse(response.body()?.longurl))
////                        startActivity(intent)
////                        //  Toast.makeText(this@WalletActivity, response.body()?.message ?: "Error", Toast.LENGTH_SHORT).show()
////                    }
////                }
////
////                override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
////                    Toast.makeText(this@MainActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT)
////                        .show()
////                }
////            })
//        } else {
//            Toast.makeText(this, "Invalid input data", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun showEditProfileDialog(amount: String, id: Int, pointsIdInt: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)
        val builder = AlertDialog.Builder(this, R.style.MaterialAlertDialogTheme)
            .setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()


        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()

        val userId = userData?.id
        val name = userData?.name ?: ""
        val email = "test@gmail.com"
        val mobile = userData?.mobile ?: ""
        var pointsId = "$id"

        // get 2 percentage of amount
        val twoPercentage = amount.toDouble() * 0.02
        val roundedAmount = Math.round(twoPercentage)
        val total_amount = (amount.toDouble() + roundedAmount).toString()

        dialogView.findViewById<MaterialButton>(R.id.btn_upload_photo).setText("Default Pay")

        dialogView.findViewById<MaterialButton>(R.id.btn_upload_photo).apply {
            setText("QR Scanner") // Set button text
            setIconResource(0) // Removes the icon
            // or
            // icon = null
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_edit_profile).apply {
            setText("Direct Pay") // Set button text
            setIconResource(0) // Removes the icon
        }

        // Set up button click listeners
        dialogView.findViewById<MaterialButton>(R.id.btn_edit_profile).setOnClickListener {
            if (userId != null && pointsId.isNotEmpty()) {
                if (pointsIdInt != null) {

                    // ✅ Save userId and pointsIdInt BEFORE launching billing
                    val preferences = DPreferences(this)
                    preferences.setSelectedUserId(userId.toString())
                    preferences.setSelectedPlanId(java.lang.String.valueOf(pointsIdInt))
                    WalletViewModel.tryCoins(userId, pointsIdInt)
                    billingManager!!.purchaseProduct(
                        //"coins_12",
                        pointsId,
                        userId,
                        pointsIdInt
                    )
                    WalletViewModel.navigateToMain.observe(this, Observer { shouldNavigate ->

                        if (shouldNavigate) {
                            Toast.makeText(this, "Coin updated successfully", Toast.LENGTH_SHORT)
                                .show()
                            userData.id.let { profileViewModel.getUsers(it) }


                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish() // ✅ Now this works because we are in an Activity
                        }
//                        val intent = Intent(this, MainActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        startActivity(intent)
//                        finish() // ✅ Now this works because we are in an Activity
                    })
                }
            } else {
                Toast.makeText(this, "Invalid input data", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_upload_photo).setOnClickListener {
            if (userId != null && pointsId.isNotEmpty() && total_amount.isNotEmpty()) {
                val userIdWithPoints = "$userId-$pointsId"

                val apiService = RetrofitClient.instance
                val call = apiService.addCoins(name, total_amount, email, mobile, userIdWithPoints)
                val callRazor = apiService.addCoinsRazorPay(userIdWithPoints,name,total_amount,email,mobile)

                accountViewModel.getSettings()

                accountViewModel.settingsLiveData.observe(this, Observer { response ->
                    if (response != null && response.success) {
                        response.data?.let { settingsList ->
                            if (settingsList.isNotEmpty()) {
                                val settingsData = settingsList[0]

                                when ("upigateway") {

                                    "upigateway" ->{
                                        Log.d("useing", "upigateway")
                                        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
                                        var userid = userData?.id
                                        Log.d("useing", "upigateway")
                                        userid?.let {
                                            val clientTxnId = generateRandomTxnId(it, pointsId) // Generate a new transaction ID
                                            upiPaymentViewModel.createUpiPayment(it, clientTxnId, total_amount)
                                            Log.d("upigateway", "upigateway: " + it + " + " + clientTxnId + " + " + total_amount)
                                        }
                                    }
                                    else -> {
                                        Toast.makeText(this, "Invalid Payment Gateway", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                })
            } else {
                Toast.makeText(this, "Invalid input data", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        dialogView.findViewById<ImageView>(R.id.iv_close).setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Removes default dialog background
        dialog.show()
    }

    fun calculateOriginalPrice(price: Int, savePercentage: Int): Int {
        val originalPrice = price / (1 - (savePercentage / 100.0)) // Use Double for division
        return round(originalPrice).toInt() // Round to the nearest integer
    }

    fun generateRandomTxnId(userId: Int, coinId: String): String {
        return "$userId-$coinId-${System.currentTimeMillis()}"
    }


    fun updateFcmToken(userId: Int?) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            Log.d("FCM", "Device token: $token")

            userId?.let { fcmTokenViewModel.sendToken(it, token) }
            observeTokenResponse()
        }
    }

    fun observeTokenResponse() {
        fcmTokenViewModel.tokenResponseLiveData.observe(this) { response ->
            Log.d("FCMToken", "$response")

            response?.let {
                if (it.success) {
                    Log.d("FCMToken", "Token saved successfully!")
                } else {
                    Log.e("FCMToken", "Failed to save token")
                }
            }
        }

    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+
            requestPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.MANAGE_OWN_CALLS
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        requestPermissions()
    }

}