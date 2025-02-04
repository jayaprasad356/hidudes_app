package com.gmwapp.hi_dude.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.activities.RandomUserActivity
import com.gmwapp.hi_dude.adapters.CoinAdapter
import com.gmwapp.hi_dude.adapters.FemaleUserAdapter
import com.gmwapp.hi_dude.adapters.RecentCallsAdapter
import com.gmwapp.hi_dude.callbacks.OnItemSelectionListener
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.databinding.FragmentRecentBinding
import com.gmwapp.hi_dude.retrofit.responses.CallsListResponseData
import com.gmwapp.hi_dude.retrofit.responses.CoinsResponseData
import com.gmwapp.hi_dude.retrofit.responses.FemaleUsersResponseData
import com.gmwapp.hi_dude.viewmodels.AccountViewModel
import com.gmwapp.hi_dude.viewmodels.RecentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecentFragment : BaseFragment() {
    lateinit var binding: FragmentRecentBinding
    private val recentViewModel: RecentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentBinding.inflate(layoutInflater)

        initUI()
        return binding.root
    }

    private fun initUI() {
        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
        userData?.let { recentViewModel.getCallsList(userData.id, userData.gender) }

        binding.swipeRefreshLayout.setOnRefreshListener {
            // Start refreshing the data
            userData?.let { recentViewModel.getCallsList(userData.id, userData.gender) }
        }




        recentViewModel.callsListLiveData.observe(viewLifecycleOwner, Observer{
            if(it!=null && it.success && it.data!=null) {
                binding.tlTitle.visibility = View.GONE
            }
            else {
                binding.tlTitle.visibility = View.VISIBLE
            }
        })

        recentViewModel.callsListLiveData.observe(viewLifecycleOwner, Observer {

            binding.swipeRefreshLayout.isRefreshing = false

            if(it!=null && it.success && it.data!=null) {
                binding.rvCalls.setLayoutManager(
                    LinearLayoutManager(
                        requireActivity(), LinearLayoutManager.VERTICAL, false
                    )
                )
                //  val recentCallsAdapter = RecentCallsAdapter(requireActivity(),it.data)



                var recentCallsAdapter = activity?.let { it1 ->
                    RecentCallsAdapter(it1,
                        it.data,
                        object : OnItemSelectionListener<CallsListResponseData> {
                            override fun onItemSelected(data: CallsListResponseData) {
                                val intent = Intent(context, RandomUserActivity::class.java)
                                intent.putExtra(DConstants.CALL_TYPE, "audio")
                                intent.putExtra(DConstants.RECEIVER_ID, data.id)
                                intent.putExtra(DConstants.RECEIVER_NAME, data.name)
                                intent.putExtra(DConstants.CALL_ID, 0)
                                intent.putExtra(DConstants.IMAGE, data.image)
                                intent.putExtra(DConstants.IS_RECEIVER_DETAILS_AVAILABLE, true)
                                intent.putExtra(DConstants.TEXT, getString(R.string.wait_user_hint, data.name))
                                startActivity(intent)
                            }
                        },
                        object : OnItemSelectionListener<CallsListResponseData> {
                            override fun onItemSelected(data: CallsListResponseData) {
                                val intent = Intent(context, RandomUserActivity::class.java)
                                intent.putExtra(DConstants.CALL_TYPE, "video")
                                intent.putExtra(DConstants.RECEIVER_ID, data.id)
                                intent.putExtra(DConstants.RECEIVER_NAME, data.name)
                                intent.putExtra(DConstants.CALL_ID, 0)
                                intent.putExtra(DConstants.IMAGE, data.image)
                                intent.putExtra(DConstants.IS_RECEIVER_DETAILS_AVAILABLE, true)
                                intent.putExtra(DConstants.TEXT, getString(R.string.wait_user_hint, data.name))
                                startActivity(intent)
                            }
                        })
                }


                binding.rvCalls.setAdapter(recentCallsAdapter)
                binding.rvCalls.visibility = View.VISIBLE
                binding.tlTitle.visibility = View.GONE

            }
        })
    }
}