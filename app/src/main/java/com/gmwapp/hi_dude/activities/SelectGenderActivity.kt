package com.gmwapp.hi_dude.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.PagerSnapHelper
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.adapters.AvatarsListAdapter
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.databinding.ActivitySelectGenderBinding
import com.gmwapp.hi_dude.utils.setOnSingleClickListener
import com.gmwapp.hi_dude.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SelectGenderActivity : BaseActivity() {
    lateinit var binding: ActivitySelectGenderBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private var selectedGender = "male"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectGenderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
    }

    private fun initUI() {
        binding.cvGender.setBackgroundResource(R.drawable.d_button_bg_gender_outline)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvAvatars)
        setCenterLayoutManager(binding.rvAvatars)
        binding.ivBack.setOnSingleClickListener {
            finish()
        }
        binding.btnContinue.setOnSingleClickListener {
            var intent:Intent? = null
            if (selectedGender == DConstants.MALE) {
                intent = Intent(this, SelectLanguageActivity::class.java)
              //  OneSignal.User.addTag("gender", "male")
            } else {
                intent = Intent(this, FemaleAboutActivity::class.java)
             //   OneSignal.User.addTag("gender", "female")
            }
            val layoutManager = binding.rvAvatars.layoutManager as CenterLayoutManager
            val avatarId =
                profileViewModel.avatarsListLiveData.value?.data?.get(layoutManager.findFirstCompletelyVisibleItemPosition())?.id
            intent.putExtra(DConstants.AVATAR_ID, avatarId)
            intent.putExtra(
                DConstants.MOBILE_NUMBER, getIntent().getStringExtra(DConstants.MOBILE_NUMBER)
            )
            intent.putExtra(DConstants.GENDER, selectedGender)
            startActivity(intent)

        }
        binding.btnMale.setOnSingleClickListener {
            selectedGender = DConstants.MALE
            profileViewModel.getAvatarsList(selectedGender)
            binding.btnMale.setBackgroundResource(R.drawable.d_button_bg_gender_selected)
            binding.btnFemale.setBackgroundColor(getColor(android.R.color.transparent))
            binding.btnMale.setTextColor(getColor(R.color.primary_blue))
            binding.btnFemale.setTextColor(getColor(R.color.unselect_grey))
            binding.btnMale.isEnabled = false
            binding.btnFemale.isEnabled = true
        }
        binding.btnFemale.setOnSingleClickListener {
            selectedGender = DConstants.FEMALE
            profileViewModel.getAvatarsList(selectedGender)
            binding.btnMale.setBackgroundColor(getColor(android.R.color.transparent))
            binding.btnFemale.setBackgroundResource(R.drawable.d_button_bg_gender_selected)
            binding.btnMale.setTextColor(getColor(R.color.unselect_grey))
            binding.btnFemale.setTextColor(getColor(R.color.primary_blue))
            binding.btnMale.isEnabled = true
            binding.btnFemale.isEnabled = false
        }
        profileViewModel.getAvatarsList("male")
        profileViewModel.avatarsListLiveData.observe(this, Observer {
            if (it?.data != null) {
                val avatarsListAdapter = AvatarsListAdapter(
                    this, it.data
                )
                binding.rvAvatars.setAdapter(avatarsListAdapter)
                binding.rvAvatars.smoothScrollToPosition(0)
            }
        })

    }

}