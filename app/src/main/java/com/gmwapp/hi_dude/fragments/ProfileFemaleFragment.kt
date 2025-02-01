package com.gmwapp.hi_dude.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.activities.AccountPrivacyActivity
import com.gmwapp.hi_dude.activities.EarningsActivity
import com.gmwapp.hi_dude.activities.EditProfileActivity
import com.gmwapp.hi_dude.databinding.FragmentProfileFemaleBinding
import com.gmwapp.hi_dude.dialogs.BottomSheetLogout
import com.gmwapp.hi_dude.utils.setOnSingleClickListener
import com.gmwapp.hi_dude.viewmodels.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFemaleFragment : BaseFragment() {
    lateinit var binding: FragmentProfileFemaleBinding
    private val EDIT_PROFILE_REQUEST_CODE = 1
    private val accountViewModel: AccountViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileFemaleBinding.inflate(layoutInflater)
        initUI()
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == EDIT_PROFILE_REQUEST_CODE){
            updateValues()
        }
    }

    private fun updateValues(){
        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
        val prefs = BaseApplication.getInstance()?.getPrefs()
        val supportMail = prefs?.getSettingsData()?.support_mail
        val subject = getString(R.string.delete_account_mail_subject, userData?.mobile, userData?.language)

        val body = ""
        binding.tvSupportMail.setOnSingleClickListener {
            val intent = Intent(Intent.ACTION_VIEW)

            val data = Uri.parse(("mailto:$supportMail?subject=$subject").toString() + "&body=$body")
            intent.setData(data)

            startActivity(intent)
        }
        binding.tvSupportMail.paintFlags =
            binding.tvSupportMail.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.tvSupportMail.text =
            supportMail
        binding.tvName.text = userData?.name
        Glide.with(this).load(userData?.image).
        apply(RequestOptions.circleCropTransform()).into(binding.ivProfile)
    }

    private fun initUI(){
        updateValues()
        val prefs = BaseApplication.getInstance()?.getPrefs()

        binding.clEarnings.setOnSingleClickListener( {
            val intent = Intent(context, EarningsActivity::class.java)
            startActivity(intent)
        })
        binding.ivEditProfile.setOnSingleClickListener( {
            val intent = Intent(context, EditProfileActivity::class.java)
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE)
        })
        binding.clAccountPrivacy.setOnSingleClickListener( {
            val intent = Intent(context, AccountPrivacyActivity::class.java)
            startActivity(intent)
        })
        binding.cvLogout.setOnSingleClickListener( {
            val bottomSheet: BottomSheetLogout =
                BottomSheetLogout()
            fragmentManager?.let { it1 ->
                bottomSheet.show(
                    it1,
                    "ProfileFragment"
                )
            }
        })
        accountViewModel.getSettings()
        accountViewModel.settingsLiveData.observe(viewLifecycleOwner, Observer {
            if (it!=null && it.success) {
                if (it.data != null) {
                    if (it.data.size > 0) {
                        prefs?.setSettingsData(it.data.get(0))
                        val supportMail = prefs?.getSettingsData()?.support_mail
                        binding.tvSupportMail.text =
                            supportMail
                        val userData = prefs?.getUserData()
                        val subject = getString(R.string.delete_account_mail_subject, userData?.mobile,  userData?.language)

                        val body = ""
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
    }
}