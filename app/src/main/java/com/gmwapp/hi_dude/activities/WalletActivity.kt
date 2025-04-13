package com.gmwapp.hi_dude.activities

import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetails
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.BillingManager.BillingManager
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.adapters.CoinAdapter
import com.gmwapp.hi_dude.callbacks.OnItemSelectionListener
import com.gmwapp.hi_dude.databinding.ActivityWalletBinding
import com.gmwapp.hi_dude.retrofit.responses.CoinsResponseData
import com.gmwapp.hi_dude.retrofit.responses.RazorPayApiResponse
import com.gmwapp.hi_dude.utils.DPreferences
import com.gmwapp.hi_dude.utils.setOnSingleClickListener
import com.gmwapp.hi_dude.viewmodels.AccountViewModel
import com.gmwapp.hi_dude.viewmodels.ProfileViewModel
import com.gmwapp.hi_dude.viewmodels.UpiPaymentViewModel
import com.gmwapp.hi_dude.viewmodels.UpiViewModel
import com.gmwapp.hi_dude.viewmodels.WalletViewModel
import com.google.android.material.button.MaterialButton
//import com.google.androidbrowserhelper.trusted.LauncherActivity
//import com.zegocloud.uikit.prebuilt.call.core.utils.Storage.context
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WalletActivity : BaseActivity()  {
    lateinit var binding: ActivityWalletBinding
    private val WalletViewModel: WalletViewModel by viewModels()
    private val accountViewModel: AccountViewModel by viewModels()
    private val upiPaymentViewModel: UpiPaymentViewModel by viewModels()
    val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var billingClient: BillingClient
    private var skuDetailsList: List<SkuDetails> = emptyList()
    private var billingManager: BillingManager? = null
    private var btnBuyCoins: Button? = null
    private var btnBuySubscription: android.widget.Button? = null


    private val viewModel: UpiViewModel by viewModels()
    var amount = ""
    var pointsId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
        addObservers()

    }

    private fun initUI() {

        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
        binding.tvCoins.text = userData?.coins.toString()


        val layoutManager = GridLayoutManager(this, 3)
        binding.ivBack.setOnSingleClickListener {
            finish()
        }
//        binding.rvPlans.addItemDecoration(SpacesItemDecoration(20))
        binding.rvPlans.setLayoutManager(layoutManager)
//        binding.rvPlans.addItemDecoration(SpacesItemDecoration(10))
        BaseApplication.getInstance()?.getPrefs()?.getUserData()
            ?.let { WalletViewModel.getCoins(it.id) }
        WalletViewModel.coinsLiveData.observe(this, Observer {

            if (it.success) {
                //  Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

            if (it != null && it.success && it.data != null) {

                // Dynamically build SKU list
                val skuList = it.data.map { coin -> coin.id.toString() }

                // Initialize BillingManager with dynamic SKUs
                billingManager = BillingManager(this, skuList)

                // Create the adapter
                val coinAdapter =
                    CoinAdapter(this, it.data, object : OnItemSelectionListener<CoinsResponseData> {
                        override fun onItemSelected(coin: CoinsResponseData) {
                            // Update button text and make it visible when an item is selected
                            binding.btnAddCoins.text =
                                getString(R.string.add_quantity_coins, coin.coins)
                            binding.btnAddCoins.visibility = View.VISIBLE
                            amount = coin.price.toString()
                            pointsId = coin.id.toString()

                        }

                        val number: CoinsResponseData?
                            get() = null
                    })

                // Set the adapter
                binding.rvPlans.adapter = coinAdapter

                // Set default button text and visibility for the first item
                if (it.data.isNotEmpty()) {
                    val firstCoin = it.data[0]
                    binding.btnAddCoins.text =
                        getString(R.string.add_quantity_coins, firstCoin.coins)
                    binding.btnAddCoins.visibility = View.VISIBLE
                    amount = firstCoin.price.toString()
                    pointsId = firstCoin.id.toString()

                }
            }

        })

//        billingManager = BillingManager(this)

        binding.btnAddCoins.setOnClickListener(View.OnClickListener { view: View? ->

            val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
            val userId = userData?.id
            val pointsIdInt: Int = pointsId.toIntOrNull() ?: 0

//            showEditProfileDialog(pointsIdInt)

            if (userId != null && pointsId.isNotEmpty()) {
                if (pointsIdInt != null) {

                    // Generate 4-digit random number
                    val random4Digit = (1000..9999).random()

                    // ✅ Save userId and pointsIdInt BEFORE launching billing
                    val preferences = DPreferences(this)
                    preferences.clearSelectedOrderId()
                    preferences.setSelectedUserId(userId.toString())
                    preferences.setSelectedPlanId(java.lang.String.valueOf(pointsIdInt))
                    preferences.setSelectedOrderId(java.lang.String.valueOf(random4Digit))
                    WalletViewModel.tryCoins(userId, pointsIdInt, 0, random4Digit, "try")
                    billingManager!!.purchaseProduct(
//                        "coins_12",
                        pointsId,
                    )
                    WalletViewModel.navigateToMain.observe(this, Observer { shouldNavigate ->

                        if (shouldNavigate) {
                            Toast.makeText(this, "Coin updated successfully", Toast.LENGTH_SHORT)
                                .show()
                            userData?.id?.let { profileViewModel.getUsers(it) }

                            profileViewModel.getUserLiveData.observe(this, Observer {
                                it.data?.let { it1 ->
                                    BaseApplication.getInstance()?.getPrefs()?.setUserData(it1)
                                }
                                binding.tvCoins.text = it.data?.coins.toString()
                                WalletViewModel._navigateToMain.postValue(false)
                            })
                        } else {

                            profileViewModel.getUserLiveData.observe(this, Observer {
                                it.data?.let { it1 ->
                                    BaseApplication.getInstance()?.getPrefs()?.setUserData(it1)
                                }
                                binding.tvCoins.text = it.data?.coins.toString()

                            })
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
        })
    }

//        binding.btnAddCoins.setOnSingleClickListener {
//            val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
//
//            val userId = userData?.id
//            val name = userData?.name ?: ""
//            val email = "test@gmail.com"
//            val mobile = userData?.mobile ?: ""
//
//            // get 2 percentage of amount
//            val twoPercentage = amount.toDouble() * 0.02
//            val roundedAmount = Math.round(twoPercentage)
//            val total_amount = (amount.toDouble() + roundedAmount).toString()
//
//
//
//            Log.d("Amount", "amount $amount")
//            Log.d("Amount", "Totalamount $total_amount")
//            Log.d("Amount", "Roundamount $roundedAmount")
//            if (userId != null && pointsId.isNotEmpty() && total_amount.isNotEmpty()) {
//                val userIdWithPoints = "$userId-$pointsId"
//
//                val apiService = RetrofitClient.instance
//                val call = apiService.addCoins(name, total_amount, email, mobile, userIdWithPoints)
//                val callRazor = apiService.addCoinsRazorPay(userIdWithPoints,name,total_amount,email,mobile)
//
//                accountViewModel.getSettings()
//
//                accountViewModel.settingsLiveData.observe(this, Observer { response ->
//                    if (response != null && response.success) {
//                        response.data?.let { settingsList ->
//                            if (settingsList.isNotEmpty()) {
//                                val settingsData = settingsList[0]
//
//                                when ("upigateway") {
//                                    "razorpay" -> {
//
//
//                                        callRazor.enqueue(object : retrofit2.Callback<RazorPayApiResponse> {
//                                            override fun onResponse(call: retrofit2.Call<RazorPayApiResponse>, response: retrofit2.Response<RazorPayApiResponse>) {
//                                                if (response.isSuccessful && response.body() != null) {
//                                                    val apiResponse = response.body()
//
//                                                    // Extract the Razorpay payment link
//                                                    val paymentUrl = apiResponse?.short_url
//
//                                                    if (!paymentUrl.isNullOrEmpty()) {
//
//                                                        val intent =Intent(this@WalletActivity, LauncherActivity::class.java)
//                                                        intent.setData(Uri.parse(response.body()?.short_url))
//                                                        Log.d("WalletResponse","${response.body()?.short_url}")
//                                                        startActivity(intent)
//
////                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl))
////                                startActivity(intent)
//                                                    } else {
//                                                        Toast.makeText(this@WalletActivity, "Failed to get payment link", Toast.LENGTH_SHORT).show()
//                                                    }
//                                                } else {
//                                                    Toast.makeText(this@WalletActivity, "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
//                                                }
//                                            }
//
//                                            override fun onFailure(call: retrofit2.Call<RazorPayApiResponse>, t: Throwable) {
//                                                Toast.makeText(this@WalletActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
//                                            }
//                                        })
//                                    }
//
//                                    "upigateway" ->{
//                                        Log.d("useing", "upigateway")
//                                        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
//                                        var userid = userData?.id
//                                        Log.d("useing", "upigateway")
//                                        userid?.let {
//                                            val clientTxnId = generateRandomTxnId(it, pointsId) // Generate a new transaction ID
//                                            upiPaymentViewModel.createUpiPayment(it, clientTxnId, total_amount)
//                                            Log.d("upigateway", "upigateway: " + it + " + " + clientTxnId + " + " + total_amount)
//                                        }
//
//                                    }
//
//                                    "instamojo" -> {
//
//
//                                        call.enqueue(object : retrofit2.Callback<ApiResponse> {
//                                            override fun onResponse(call: retrofit2.Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
//                                                if (response.isSuccessful && response.body()?.success == true) {
//                                                    Toast.makeText(this@WalletActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
//                                                } else {
//                                                    // println("Long URL: ${it.longurl}") // Print to the terminal
//                                                    //Toast.makeText(mContext, it.longurl, Toast.LENGTH_SHORT).show()
//
//                                                    val intent =
//                                                        Intent(this@WalletActivity, LauncherActivity::class.java)
//                                                    intent.setData(Uri.parse(response.body()?.longurl))
//                                                    Log.d("WalletResponse","${response.body()?.longurl}")
//                                                    startActivity(intent)
//                                                    finish()// Directly starting the intent without launcher
//                                                    //  Toast.makeText(this@WalletActivity, response.body()?.message ?: "Error", Toast.LENGTH_SHORT).show()
//                                                }
//                                            }
//
//                                            override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
//                                                Toast.makeText(this@WalletActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
//                                            }
//                                        })
//                                    }
//                                    else -> {
//                                        Toast.makeText(this, "Invalid Payment Gateway", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                            }
//                        }
//                    }
//                })
//            } else {
//                Toast.makeText(this, "Invalid input data", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    fun generateRandomTxnId(userId: Int, coinId: String): String {
        return "$userId-$coinId-${System.currentTimeMillis()}"
    }

    private fun addObservers() {
        WalletViewModel.afterAddCoinsLiveData.observe(this, Observer { afterAddCoins ->
            if (afterAddCoins != null) {
                Log.d("UI Update", "Coins updated successfully. Refreshing UI.")

                // ✅ Update UI
                binding.tvCoins.text = afterAddCoins.toString()

                // ✅ Fetch updated user profile
                BaseApplication.getInstance()?.getPrefs()?.getUserData()?.id?.let {
                    profileViewModel.getUsers(it)
                }
            }
        })

        viewModel.addpointResponseLiveData.observe(this, Observer {
            if (it != null && it.success) {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        })

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
    }

    private fun showEditProfileDialog(pointsIdInt: Int) {
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

                    // Generate 4-digit random number
                    val random4Digit = (1000..9999).random()

                    // ✅ Save userId and pointsIdInt BEFORE launching billing
                    val preferences = DPreferences(this)
                    preferences.clearSelectedOrderId()
                    preferences.setSelectedUserId(userId.toString())
                    preferences.setSelectedPlanId(java.lang.String.valueOf(pointsIdInt))
                    preferences.setSelectedOrderId(java.lang.String.valueOf(random4Digit))
                    WalletViewModel.tryCoins(userId, pointsIdInt, 0, random4Digit, "try")
                    billingManager!!.purchaseProduct(
                        //"coins_12",
                        pointsId,
                    )
                    WalletViewModel.navigateToMain.observe(this, Observer { shouldNavigate ->

                        if (shouldNavigate) {
                            Toast.makeText(this, "Coin updated successfully", Toast.LENGTH_SHORT)
                                .show()
                            userData.id.let { profileViewModel.getUsers(it) }

                            profileViewModel.getUserLiveData.observe(this, Observer {
                                it.data?.let { it1 ->
                                    BaseApplication.getInstance()?.getPrefs()?.setUserData(it1)
                                }
                                binding.tvCoins.text = it.data?.coins.toString()
                                WalletViewModel._navigateToMain.postValue(false)
                            })
                        }else{

                            profileViewModel.getUserLiveData.observe(this, Observer {
                                it.data?.let { it1 ->
                                    BaseApplication.getInstance()?.getPrefs()?.setUserData(it1)
                                }
                                binding.tvCoins.text = it.data?.coins.toString()

                            })
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

    private fun refreshUI() {
        val intent = intent
        finish() // Close the activity
        startActivity(intent) // Restart the same activity
    }

}