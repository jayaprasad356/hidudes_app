package com.gmwapp.hi_dude.widgets

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.WindowManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.activities.BaseActivity
import com.gmwapp.hi_dude.activities.RandomUserActivity
import com.gmwapp.hi_dude.activities.WalletActivity
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.dagger.GetRemainingTimeEvent
import com.gmwapp.hi_dude.dagger.UpdateRemainingTimeEvent
import com.gmwapp.hi_dude.utils.Helper
import com.zegocloud.uikit.components.audiovideo.ZegoBaseAudioVideoForegroundView
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.invite.internal.CallInviteActivity
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class CustomCallView : ZegoBaseAudioVideoForegroundView, LifecycleObserver {
    private var customCallrootView: View? = null
    private var tvRemainingTime: TextView? = null
    private var balanceTime: String? = null
    private var activity: RandomUserActivity? = null
    private var WALLET_ACTIVITY_REQUEST_CODE = 1;

    constructor(context: Context, userID: String?) : super(context, userID){
        registerLifecycleObserver()
    }

    constructor(
        context: Context, attrs: AttributeSet?, userID: String?
    ) : super(context, attrs, userID){
        registerLifecycleObserver()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        EventBus.getDefault().post(GetRemainingTimeEvent());
    }

    fun getDisplayContentHeight(): Int {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    override fun onForegroundViewCreated(uiKitUser: ZegoUIKitUser) {
        // Make the window full-screen
        // Make the window full-screen without hiding the navigation bar
        val activity = context as? Activity

        activity?.window?.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE  // Ensures layout doesn't change
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) // Ensures status bar respects layout
            clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)  // Ensure status bar is visible
        }
//        activity?.window?.apply {
//            decorView.systemUiVisibility = (
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    )
//            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//        }
//
        val root = activity?.findViewById<View>(android.R.id.content)

        if (root != null) {
            root?.setBackgroundColor(Color.parseColor("#ff0d4a"))
            // Set fitsSystemWindows to true to ensure layout respects insets
            root?.fitsSystemWindows = true
            ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // Initialize your custom view
        val prefs = BaseApplication.getInstance()?.getPrefs()
        val userData = prefs?.getUserData()

        val inflate = inflate(
            context,
            if (userData?.gender == DConstants.MALE)
                R.layout.widget_custom_call
            else
                R.layout.widget_custom_call_female,
            this
        )
        var height = getDisplayContentHeight();
        inflate.addOnLayoutChangeListener(OnLayoutChangeListener { v, left, top, right, bottom, leftWas, topWas, rightWas, bottomWas ->
            if(v.height < height/2){
                root?.setBackgroundColor(context.getColor(android.R.color.transparent))
                customCallrootView?.visibility = View.INVISIBLE
            } else{
                root?.setBackgroundColor(Color.parseColor("#ff0d4a"))
                customCallrootView?.visibility = View.VISIBLE
            }
        })

        customCallrootView = inflate
        tvRemainingTime = inflate.findViewById<View>(R.id.tv_remaining_time) as TextView?
        if (userData?.gender == DConstants.MALE) {
            var clCoins = inflate.findViewById<View>(R.id.cl_coins) as ConstraintLayout?
            clCoins?.setOnClickListener({
                try {
                    val aux: Fragment = FragmentForResult(RandomUserActivity())
                    val fm: FragmentManager? =
                        (context as CallInviteActivity).supportFragmentManager
                    fm?.beginTransaction()?.add(aux, "FRAGMENT_TAG")?.commit()
                    fm?.executePendingTransactions()
                    val intent = Intent(activity, WalletActivity::class.java)
                    intent.putExtra(DConstants.NEED_TO_FINISH, true)
                    ZegoUIKitPrebuiltCallService.minimizeCall()
                    aux.startActivityForResult(intent, WALLET_ACTIVITY_REQUEST_CODE)
                } catch (e: Exception) {
                }
            })

            }
            EventBus.getDefault().register(this)
        }

    private fun registerLifecycleObserver() {
        val lifecycleOwner = context as? androidx.lifecycle.LifecycleOwner
        lifecycleOwner?.lifecycle?.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Log.d("CustomCallView", "onResume called")

        restoreUIState()
    }

    private fun restoreUIState() {
        val activity = context as? Activity
        val root = activity?.findViewById<View>(android.R.id.content)

        activity?.window?.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE  // Ensures layout doesn't change
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) // Ensures status bar respects layout
            clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)  // Ensure status bar is visible
        }

        root?.apply {
            setBackgroundColor(Color.parseColor("#ff0d4a"))
            fitsSystemWindows = true
            ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        customCallrootView?.visibility = View.VISIBLE
    }



        @Subscribe(threadMode = ThreadMode.MAIN)
        fun onGetRemainingTimeEvent(event: UpdateRemainingTimeEvent?) {
            this.balanceTime = event?.remainingTime
        }

        fun setBalanceTime(balanceTime: String?) {
            this.balanceTime = balanceTime;
        }

        fun setContext(activity: BaseActivity) {
            this.activity = activity as RandomUserActivity;
        }


        fun updateTime(seconds: Int) {
            var balanceTimeInsecs: Int = 0
            try {
                if (balanceTime != null) {
                    val split = balanceTime!!.split(":")
                    balanceTimeInsecs += split[0].toInt() * 60 + split[1].toInt()
                }
            } catch (e: Exception) {
            }
            var remainingTime: Int = balanceTimeInsecs - seconds.toInt()
            tvRemainingTime?.visibility = View.VISIBLE
            val hours = remainingTime / 3600
            val minutes = (remainingTime % 3600) / 60;
            val secs = remainingTime % 60;
            tvRemainingTime?.text = String.format("%02d:%02d:%02d", hours, minutes, secs)
            if (BaseApplication.getInstance()?.getRoomId() != null && remainingTime <= 0) {
                ZegoUIKitPrebuiltCallService.endCall()
                if (!Helper.checkNetworkConnection()) {
                    BaseApplication.getInstance()?.setEndCallUpdatePending(true)
                }
                EventBus.getDefault().post(UpdateRemainingTimeEvent(balanceTime));

            }
        }

        override fun onCameraStateChanged(isCameraOn: Boolean) {
            // will be called when camera changed
        }

        override fun onMicrophoneStateChanged(isMicrophoneOn: Boolean) {
            // will be called when microphone changed
        }
    }

    class FragmentForResult(private val mActivity: RandomUserActivity?) : Fragment() {
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == Activity.RESULT_OK && requestCode == 1) {
                mActivity?.onButtonClick()
            }
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }


    }
