package com.gmwapp.hi_dude.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.activities.RandomUserActivity
import com.gmwapp.hi_dude.adapters.CoinAdapter
import com.gmwapp.hi_dude.adapters.FemaleUserAdapter
import com.gmwapp.hi_dude.adapters.RecentCallsAdapter
import com.gmwapp.hi_dude.agora.FcmUtils
import com.gmwapp.hi_dude.agora.male.MaleCallConnectingActivity
import com.gmwapp.hi_dude.callbacks.OnItemSelectionListener
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.databinding.FragmentRecentBinding
import com.gmwapp.hi_dude.retrofit.responses.CallsListResponseData
import com.gmwapp.hi_dude.retrofit.responses.CoinsResponseData
import com.gmwapp.hi_dude.retrofit.responses.FemaleUsersResponseData
import com.gmwapp.hi_dude.viewmodels.AccountViewModel
import com.gmwapp.hi_dude.viewmodels.RecentViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.gmwapp.hi_dude.agora.male.MaleAudioCallingActivity
import com.gmwapp.hi_dude.agora.male.MaleVideoCallingActivity



@AndroidEntryPoint
class RecentFragment : BaseFragment() {
    lateinit var binding: FragmentRecentBinding
    private val recentViewModel: RecentViewModel by viewModels()
    private lateinit var recentCallsAdapter: RecentCallsAdapter
    private var isLoading = false
    private var offset = 0
    private val limit = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentBinding.inflate(inflater, container, false)
        initUI()
        observeViewModel()
        return binding.root
    }

    private fun initUI() {
        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
        if (userData == null) {
            binding.tlTitle.visibility = View.VISIBLE
            return
        }

        // Initial call
        recentViewModel.getCallsList(userData.id, userData.gender, limit, offset)

        // Swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            isLoading = true
            recentCallsAdapter.clearData()
            recentViewModel.getCallsList(userData.id, userData.gender, limit, offset)
        }

        // Set up RecyclerView
        binding.rvCalls.layoutManager = LinearLayoutManager(requireContext())
        recentCallsAdapter = RecentCallsAdapter(
            requireActivity(),
            ArrayList(),
            object : OnItemSelectionListener<CallsListResponseData> {
                override fun onItemSelected(data: CallsListResponseData) {
                    startMaleCallConnectingActivity(data, "audio")
                }
            },
            object : OnItemSelectionListener<CallsListResponseData> {
                override fun onItemSelected(data: CallsListResponseData) {
                    startMaleCallConnectingActivity(data, "video")
                }
            }
        )
        binding.rvCalls.adapter = recentCallsAdapter

        // Pagination
        binding.rvCalls.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (!isLoading &&
                    layoutManager.findLastCompletelyVisibleItemPosition() == recentCallsAdapter.itemCount - 1
                ) {
                    isLoading = true
                    offset += limit
                    userData.let {
                        recentViewModel.getCallsList(it.id, it.gender, limit, offset)
                    }
                }
            }
        })
    }

    private fun observeViewModel() {
        recentViewModel.callsListLiveData.observe(viewLifecycleOwner, Observer {
            binding.swipeRefreshLayout.isRefreshing = false
            isLoading = false

            if (it != null && it.success && it.data != null && it.data.isNotEmpty()) {
                binding.tlTitle.visibility = View.GONE
                binding.rvCalls.visibility = View.VISIBLE
                recentCallsAdapter.addData(it.data)
            } else if (recentCallsAdapter.itemCount == 0) {
                binding.tlTitle.visibility = View.VISIBLE
                binding.rvCalls.visibility = View.GONE
            }
        })
    }

    private fun startMaleCallConnectingActivity(data: CallsListResponseData, callType: String) {
        val intent = Intent(requireContext(), MaleCallConnectingActivity::class.java).apply {
            putExtra(DConstants.CALL_TYPE, callType)
            putExtra(DConstants.RECEIVER_ID, data.id)
            putExtra(DConstants.RECEIVER_NAME, data.name)
            putExtra(DConstants.CALL_ID, 0)
            putExtra(DConstants.IMAGE, data.image)
            putExtra(DConstants.IS_RECEIVER_DETAILS_AVAILABLE, true)
            putExtra(DConstants.TEXT, getString(R.string.wait_user_hint, data.name))
        }
        FcmUtils.isUserAvailable=1
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        if (FcmUtils.isUserAvailable==0){
            val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
            offset = 0
            isLoading = true
            recentCallsAdapter.clearData()
            userData?.let { recentViewModel.getCallsList(it.id, userData.gender, limit, offset) }        }

    }
}
