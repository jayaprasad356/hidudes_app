package com.gmwapp.hi_dude.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.databinding.ActivityMainBinding
import com.gmwapp.hi_dude.dialogs.BottomSheetWelcomeBonus
import com.gmwapp.hi_dude.fragments.FemaleHomeFragment
import com.gmwapp.hi_dude.fragments.HomeFragment
import com.gmwapp.hi_dude.fragments.ProfileFemaleFragment
import com.gmwapp.hi_dude.fragments.ProfileFragment
import com.gmwapp.hi_dude.fragments.RecentFragment
import com.gmwapp.hi_dude.viewmodels.OfferViewModel
import com.gmwapp.hi_dude.viewmodels.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.androidbrowserhelper.trusted.LauncherActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round


@AndroidEntryPoint
class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    BottomSheetWelcomeBonus.OnAddCoinsListener {
    lateinit var binding: ActivityMainBinding
    var isBackPressedAlready = false
    var userName: String? = null
    var userID: String? = null

    val offerViewModel: OfferViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()



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
        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
        userID = userData?.id.toString()



        initUI()
        addObservers()

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
    }
    override fun resumeZegoCloud(){
        addRoomStateChangedListener()
        moveTaskToBack(true)
    }

    private fun initUI() {
        val prefs = BaseApplication.getInstance()?.getPrefs()
        prefs?.getUserData()?.id?.let { profileViewModel.getUsers(it) }

        profileViewModel.getUserLiveData.observe(this, Observer {
            prefs?.setUserData(it.data);
        });

        userID?.let { offerViewModel.getOffer(it.toInt()) }
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

        var amount = "$amount"
        var pointsId = "$id"
        Log.d("amount", "amount $amount")

        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()

        val userId = userData?.id
        val name = userData?.name ?: ""
        val email = "test@gmail.com"
        val mobile = userData?.mobile ?: ""

        if (userId != null && pointsId.isNotEmpty() && amount.isNotEmpty()) {
            val userIdWithPoints = "$userId-$pointsId"

            val apiService = RetrofitClient.instance
            val call = apiService.addCoins(name, amount, email, mobile, userIdWithPoints)

            call.enqueue(object : retrofit2.Callback<ApiResponse> {
                override fun onResponse(
                    call: retrofit2.Call<ApiResponse>,
                    response: retrofit2.Response<ApiResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@MainActivity,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // println("Long URL: ${it.longurl}") // Print to the terminal
                        //Toast.makeText(mContext, it.longurl, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, LauncherActivity::class.java)
                        intent.setData(Uri.parse(response.body()?.longurl))
                        startActivity(intent)
                        //  Toast.makeText(this@WalletActivity, response.body()?.message ?: "Error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } else {
            Toast.makeText(this, "Invalid input data", Toast.LENGTH_SHORT).show()
        }
    }

    fun calculateOriginalPrice(price: Int, savePercentage: Int): Int {
        val originalPrice = price / (1 - (savePercentage / 100.0)) // Use Double for division
        return round(originalPrice).toInt() // Round to the nearest integer
    }
}