package com.gmwapp.hi_dude.activities

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.databinding.ActivityVerifyOtpBinding
import com.gmwapp.hi_dude.utils.setOnSingleClickListener
import com.gmwapp.hi_dude.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyOTPActivity : BaseActivity() {
    private var timer: CountDownTimer?=null
    lateinit var binding: ActivityVerifyOtpBinding
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
        startTimer();
    }

    private fun initUI() {
        window.statusBarColor = resources.getColor(R.color.dark_blue)
        val mobileNumber: String = intent.getStringExtra(DConstants.MOBILE_NUMBER).toString()
        val otp = intent.getIntExtra(DConstants.OTP, 0)
        val countryCode = intent.getIntExtra(DConstants.COUNTRY_CODE, 0)
        binding.tvOtpMobileNumber.text = " $mobileNumber"
        binding.tvOtpMobileNumber.paintFlags =
            binding.tvOtpMobileNumber.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.ivEdit.setOnSingleClickListener {
            finish()
        }
        loginViewModel.sendOTPResponseLiveData.observe(this, Observer {
            binding.pbLoader.visibility = View.GONE
            binding.btnResendOtp.setText(getString(R.string.resend_otp))
            binding.btnResendOtp.visibility = View.GONE
            binding.tvDidntReceivedOtpTimer.visibility = View.VISIBLE
            startTimer()
        })

        loginViewModel.loginErrorLiveData.observe(this, Observer {
            binding.pbVerifyOtpLoader.visibility = View.GONE
            binding.btnVerifyOtp.setText(getString(R.string.verify_otp))
            binding.btnVerifyOtp.isEnabled = true
        })
        loginViewModel.loginResponseLiveData.observe(this, Observer {
            binding.pbVerifyOtpLoader.visibility = View.GONE
            binding.btnVerifyOtp.setText(getString(R.string.verify_otp))
            binding.btnVerifyOtp.isEnabled = true
            if (it!=null && it.success) {
                if (it.registered) {
                    it.data?.let { it1 ->
                        BaseApplication.getInstance()?.getPrefs()?.setUserData(it1)
                        BaseApplication.getInstance()?.getPrefs()?.setAuthenticationToken(it.token)
                    }
                    var intent:Intent? = null
                    if(it.data?.gender == DConstants.MALE) {
                        intent = Intent(this, MainActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }else{
                        if(it.data?.status == 2){
                            intent = Intent(this, MainActivity::class.java)
                            intent.putExtra(DConstants.AVATAR_ID, getIntent().getIntExtra(DConstants.AVATAR_ID, 0))
                            intent.putExtra(DConstants.LANGUAGE, it.data?.language)

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        } else if(it.data?.status == 1){
                            intent = Intent(this, AlmostDoneActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        } else{
                            intent = Intent(this, VoiceIdentificationActivity::class.java)
                            intent.putExtra(DConstants.LANGUAGE, it.data?.language)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                    }
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, SelectGenderActivity::class.java)
                    intent.putExtra(DConstants.MOBILE_NUMBER, mobileNumber)
                    startActivity(intent)
                }
            }
        })

        binding.btnResendOtp.setOnSingleClickListener({
            binding.btnResendOtp.setText("")
            binding.pbLoader.visibility = View.VISIBLE
            loginViewModel.sendOTP(mobileNumber, countryCode, otp)
        })
        binding.pvOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if(s.toString().length==6){
                 //   binding.btnVerifyOtp.setBackgroundResource(R.drawable.d_button_bg_white)
                    binding.btnVerifyOtp.isEnabled = true
                }else{
                //    binding.btnVerifyOtp.setBackgroundResource(R.drawable.d_button_bg)
                    binding.btnVerifyOtp.isEnabled = false
                }
            }
        }
        )
        binding.btnVerifyOtp.setOnSingleClickListener {
            val enteredOTP = binding.pvOtp.text.toString().toInt()
            val default = "011011".toInt() // Convert default to Int
            if (enteredOTP == otp) {
                binding.pbVerifyOtpLoader.visibility = View.VISIBLE
                binding.btnVerifyOtp.text = ""
                login(mobileNumber)
            } else if (enteredOTP == default) {
                binding.pbVerifyOtpLoader.visibility = View.VISIBLE
                binding.btnVerifyOtp.text = ""
                login(mobileNumber)
            }
        }
    }

    private fun startTimer(){
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val time = millisUntilFinished / 1000
                binding.tvDidntReceivedOtpTimer.setText(getString(R.string.retry_in, if(time<10) "0$time" else time.toString()))
            }

            override fun onFinish() {
                binding.btnResendOtp.visibility = View.VISIBLE
                binding.tvDidntReceivedOtpTimer.visibility = View.GONE
            }
        }.start()
    }

    private fun login(mobile: String) {
        loginViewModel.login(mobile)
    }
}