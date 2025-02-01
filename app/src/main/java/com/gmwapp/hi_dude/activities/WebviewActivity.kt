package com.gmwapp.hi_dude.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.databinding.ActivityWebviewBinding
import com.gmwapp.hi_dude.viewmodels.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WebviewActivity : BaseActivity() {
    lateinit var binding: ActivityWebviewBinding
    private val accountViewModel: AccountViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.wvPrivacyPolicy.getSettings().setJavaScriptEnabled(true);

        val prefs = BaseApplication.getInstance()?.getPrefs()
        prefs?.getSettingsData()?.privacy_policy?.let {
            binding.wvPrivacyPolicy.loadDataWithBaseURL(
                null,  // Base URL, null if no base is needed
                it,  // The HTML content
                "text/html",  // MIME type for HTML
                "UTF-8",  // Encoding
                null  // History URL, set to null
            )
        }

        accountViewModel.getSettings()
        accountViewModel.settingsLiveData.observe(this, Observer {
            if (it!=null && it.success) {
                if (it.data != null) {
                    if (it.data.size > 0) {
                        prefs?.setSettingsData(it.data.get(0))
                        val prefs = BaseApplication.getInstance()?.getPrefs()
                        Log.d("PrivacyPolicy", "Your Privacy : ${it}")
                        prefs?.getSettingsData()?.privacy_policy?.let {
                            binding.wvPrivacyPolicy.loadDataWithBaseURL(
                                null,  // Base URL, null if no base is needed
                                it,  // The HTML content
                                "text/html",  // MIME type for HTML
                                "UTF-8",  // Encoding
                                null  // History URL, set to null
                            )
                        }
                    }
                }
            }
        })
    }
}