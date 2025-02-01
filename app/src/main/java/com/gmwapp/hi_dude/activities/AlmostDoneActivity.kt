package com.gmwapp.hi_dude.activities

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.BuildConfig
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.databinding.ActivityAlmostDoneBinding
import com.gmwapp.hi_dude.retrofit.responses.UserData
import com.gmwapp.hi_dude.utils.setOnSingleClickListener
import com.gmwapp.hi_dude.viewmodels.AccountViewModel
import com.gmwapp.hi_dude.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AlmostDoneActivity : BaseActivity() {
    lateinit var binding: ActivityAlmostDoneBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private val accountViewModel: AccountViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlmostDoneBinding.inflate(layoutInflater)
        val prefs = BaseApplication.getInstance()?.getPrefs()
        val supportMail = prefs?.getSettingsData()?.support_mail
        binding.tvSupportMail.text =
            supportMail
        accountViewModel.getSettings()
        accountViewModel.settingsLiveData.observe(this, Observer {
            if (it!=null && it.success) {
                if (it.data != null) {
                    if (it.data.size > 0) {
                        prefs?.setSettingsData(it.data.get(0))
                        val supportMail = prefs?.getSettingsData()?.support_mail
                        binding.tvSupportMail.text =
                            supportMail
                        val userData = prefs?.getUserData()
                        val subject = getString(R.string.delete_account_mail_subject, userData?.mobile,  userData?.language)

                        val body = getString(R.string.mail_body, userData?.mobile,android.os.Build.MODEL,userData?.language,
                            BuildConfig.VERSION_CODE
                        )
                        binding.tvSupportMail.setOnSingleClickListener {
                            val intent = Intent(Intent.ACTION_VIEW)

                            val data = Uri.parse(("mailto:$supportMail?subject=$subject").toString() + "&body=$body")
                            intent.setData(data)

                            startActivity(intent)
                        }
                        binding.tvSupportMail.paintFlags =
                            binding.tvSupportMail.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                    }
                }
            }
        })
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        var intent: Intent? = null
        val prefs = BaseApplication.getInstance()?.getPrefs()
        var userData: UserData?
        prefs?.getUserData()?.id?.let { profileViewModel.getUsers(it) }
        profileViewModel.getUserLiveData.observe(this, Observer {
            prefs?.setUserData(it.data);
            userData = it.data;
            Log.d("statusCheck","$")
            if(userData?.status == 2){
                intent = Intent(this, MainActivity::class.java)
                intent?.putExtra(
                    DConstants.AVATAR_ID, getIntent().getIntExtra(DConstants.AVATAR_ID, 0)
                )
                intent?.putExtra(DConstants.LANGUAGE, userData?.language)
                intent?.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            } else if(userData?.status == 0){
                intent = Intent(this, VoiceIdentificationActivity::class.java)
                intent?.putExtra(DConstants.LANGUAGE, userData?.language)
                intent?.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            if(intent!=null) {
                startActivity(intent)
                finish()
            }
        });
    }
}