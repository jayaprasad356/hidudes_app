package com.gmwapp.hi_dude.dialogs

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.activities.LoginActivity
import com.gmwapp.hi_dude.activities.NewLoginActivity
import com.gmwapp.hi_dude.databinding.BottomSheetLogoutBinding
import com.gmwapp.hi_dude.utils.setOnSingleClickListener
import com.gmwapp.hi_dude.viewmodels.FcmTokenViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.messaging.FirebaseMessaging
import com.onesignal.OneSignal
//import com.tencent.mmkv.MMKV
//import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService


class BottomSheetLogout : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetLogoutBinding
    private val fcmTokenViewModel: FcmTokenViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetLogoutBinding.inflate(layoutInflater)

        initUI()
        return binding.root
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    private fun initUI() {

        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()


        binding.btnLogout.setOnSingleClickListener {

            updateFcmToken(userData?.id)
            Log.d("LogoutBtn", "Clicked")

        }


    }


    fun updateFcmToken(userId: Int?) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->

            userId?.let { fcmTokenViewModel.sendToken(it, "0") }
            observeTokenResponse()
        }
    }

    fun observeTokenResponse() {
        fcmTokenViewModel.tokenResponseLiveData.observe(this) { response ->
            Log.d("FCMToken", "$response")

            if (response==null){
                val prefs = BaseApplication.getInstance()?.getPrefs()
                prefs?.clearUserData()
                val intent = Intent(context, NewLoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)

            }
            response?.let {
                if (it.success) {
                    Log.d("FCMToken", "Token updated successfully!")


//                    MMKV.defaultMMKV().remove("user_id");
//                    MMKV.defaultMMKV().remove("user_name");
                    OneSignal.User.removeTag("gender_language") // Clears the tag on logout
                    OneSignal.User.removeTag("gender") // Clears the tag on logout
                    OneSignal.User.removeTag("language") // Clears the tag on logout

                    // ZegoUIKitPrebuiltCallService.unInit()
                    val prefs = BaseApplication.getInstance()?.getPrefs()
                    prefs?.clearUserData()
                    val intent = Intent(context, NewLoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)


                } else {
                    Log.e("FCMToken", "Failed to save token")


//                    MMKV.defaultMMKV().remove("user_id");
//                    MMKV.defaultMMKV().remove("user_name");
                    OneSignal.User.removeTag("gender_language") // Clears the tag on logout
                    OneSignal.User.removeTag("gender") // Clears the tag on logout
                    OneSignal.User.removeTag("language") // Clears the tag on logout

                    //ZegoUIKitPrebuiltCallService.unInit()
                    val prefs = BaseApplication.getInstance()?.getPrefs()
                    prefs?.clearUserData()
                    val intent = Intent(context, NewLoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)

                }
            }
        }

    }
}