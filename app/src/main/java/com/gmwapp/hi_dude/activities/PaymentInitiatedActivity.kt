package com.gmwapp.hi_dude.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gmwapp.hi_dude.databinding.ActivityPaymentInitiatedBinding

class PaymentInitiatedActivity : AppCompatActivity() {
    lateinit var binding: ActivityPaymentInitiatedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentInitiatedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        binding.btnGoBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

    }
}