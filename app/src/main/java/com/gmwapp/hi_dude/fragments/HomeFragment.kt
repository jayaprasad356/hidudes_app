package com.gmwapp.hi_dude.fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.activities.RandomUserActivity
import com.gmwapp.hi_dude.activities.WalletActivity
import com.gmwapp.hi_dude.adapters.FemaleUserAdapter
import com.gmwapp.hi_dude.callbacks.OnItemSelectionListener
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.databinding.FragmentHomeBinding
import com.gmwapp.hi_dude.retrofit.responses.FemaleUsersResponseData
import com.gmwapp.hi_dude.utils.setOnSingleClickListener
import com.gmwapp.hi_dude.viewmodels.FemaleUsersViewModel
import com.onesignal.OneSignal
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var isAllFabVisible: Boolean = false
    lateinit var binding: FragmentHomeBinding
    private val femaleUsersViewModel: FemaleUsersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)



        initUI()
        setupSwipeToRefresh()
        return binding.root
    }

    private fun initUI() {
        binding.clCoins.setOnSingleClickListener {
            val intent = Intent(context, WalletActivity::class.java)
            startActivity(intent)
        }

        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
        val language = userData?.language

        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val isTagSet = sharedPreferences.getBoolean("isOneSignalTagSet", false)

        OneSignal.User.addTag("gender", "male")
        language?.let {
            OneSignal.User.addTag("language", it)
            Log.d("OneSignalTag", "Language tag added: $it")
        }

        language?.let {
            OneSignal.User.addTag("gender_language", "male_$it")
            Log.d("OneSignalTag", "male_$it")

        }






//        // Send the tag only if it hasn't been set before
//        if (!isTagSet) {
//            OneSignal.User.addTag("gender", "male")
//            language?.let {
//                OneSignal.User.addTag("language", it)
//                Log.d("OneSignal", "Language tag added: $it")
//            }
//
//            // Mark the flag so this doesn't happen again
//            sharedPreferences.edit().putBoolean("isOneSignalTagSet", true).apply()
//        } else {
//            Log.d("OneSignaltag", "Tag already set, skipping... ")
//        }
//



//
//        language?.let { OneSignal.User.addTag("language", it)
//            Log.d("OneSignaltag", "Language tag added: $it")
//        }
//
//        OneSignal.User.addTag("gender", "male")
//        Log.d("Gender","Male")



        userData?.id?.let {
            if (context?.let { it1 -> isInternetAvailable(it1) } == true) {
                loadFemaleUsers(it)
            } else {
                binding.tvNointernet.visibility = View.VISIBLE
            }
        }
        userData?.id?.let { profileViewModel.getUsers(it) }

        profileViewModel.getUserLiveData.observe(viewLifecycleOwner, Observer {
            it.data?.let { it1 ->
                BaseApplication.getInstance()?.getPrefs()?.setUserData(it1)
            }
            binding.tvCoins.text = it.data?.coins.toString()

        })



        binding.fabAudio.setOnSingleClickListener {
            val intent = Intent(context, RandomUserActivity::class.java)
            intent.putExtra(DConstants.CALL_TYPE, "audio")
            startActivity(intent)
        }

        binding.fabVideo.setOnSingleClickListener {
            val intent = Intent(context, RandomUserActivity::class.java)
            intent.putExtra(DConstants.CALL_TYPE, "video")
            startActivity(intent)
        }

        femaleUsersViewModel.femaleUsersResponseLiveData.observe(viewLifecycleOwner, Observer {


//            if (it?.data != null) {
//                Toast.makeText(activity, it?.message, Toast.LENGTH_SHORT).show()
//            }
//            else if (it.data?.isEmpty() == true) {
//                Toast.makeText(activity, "No Data Found", Toast.LENGTH_SHORT).show()
//            }

            it.data?.firstOrNull()?.audio_status?.let { audioStatus ->
                Log.d("responsecheck", "Audio Status: $audioStatus")
            }

            if (it?.data != null) {
                binding.rvProfiles.layoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

                val transactionAdapter = activity?.let { context ->
                    FemaleUserAdapter(
                        context,
                        it.data,
                        object : OnItemSelectionListener<FemaleUsersResponseData> {
                            override fun onItemSelected(data: FemaleUsersResponseData) {
                                val intent = Intent(context, RandomUserActivity::class.java)
                                intent.putExtra(DConstants.CALL_TYPE, "audio")
                                intent.putExtra(DConstants.RECEIVER_ID, data.id)
                                intent.putExtra(DConstants.RECEIVER_NAME, data.name)
                                intent.putExtra(DConstants.CALL_ID, 0)
                                intent.putExtra(DConstants.IMAGE, data.image)
                                intent.putExtra(DConstants.IS_RECEIVER_DETAILS_AVAILABLE, true)
                                intent.putExtra(
                                    DConstants.TEXT,
                                    getString(R.string.wait_user_hint, data.name)
                                )
                                startActivity(intent)
                            }
                        },
                        object : OnItemSelectionListener<FemaleUsersResponseData> {
                            override fun onItemSelected(data: FemaleUsersResponseData) {
                                val intent = Intent(context, RandomUserActivity::class.java)
                                intent.putExtra(DConstants.CALL_TYPE, "video")
                                intent.putExtra(DConstants.RECEIVER_ID, data.id)
                                intent.putExtra(DConstants.RECEIVER_NAME, data.name)
                                intent.putExtra(DConstants.CALL_ID, 0)
                                intent.putExtra(DConstants.IMAGE, data.image)
                                intent.putExtra(DConstants.IS_RECEIVER_DETAILS_AVAILABLE, true)
                                intent.putExtra(
                                    DConstants.TEXT,
                                    getString(R.string.wait_user_hint, data.name)
                                )
                                startActivity(intent)
                            }
                        })
                }
                binding.rvProfiles.adapter = transactionAdapter
            }

            // Stop the swipe-to-refresh loading animation
            binding.swipeRefreshLayout.isRefreshing = false
        })

        initFab()
    }

//    private fun setupSwipeToRefresh() {
//        binding.swipeRefreshLayout.setOnRefreshListener {
//            val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
//            userData?.id?.let {
//                if (context?.let { context -> isInternetAvailable(context) } == true) {
//                    loadFemaleUsers(it)
//                    Log.d("refreshing","refreshing")
//                } else {
//                    binding.tvNointernet.visibility = View.VISIBLE
//                    binding.swipeRefreshLayout.isRefreshing = false
//                }
//            }
//        }
//    }


    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
            userData?.id?.let {
                if (context?.let { context -> isInternetAvailable(context) } == true) {
                    // Clear the existing data
                    femaleUsersViewModel.femaleUsersResponseLiveData.value?.data?.clear()

                    // Notify the adapter that data has been cleared
                    (binding.rvProfiles.adapter as? FemaleUserAdapter)?.notifyDataSetChanged()

                    // Reload the data
                    loadFemaleUsers(it)
                    Log.d("refreshing", "refreshing")
                } else {
                    binding.tvNointernet.visibility = View.VISIBLE
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }


    private fun loadFemaleUsers(userId: Int) {
        femaleUsersViewModel.getFemaleUsers(userId)
    }

    fun initFab() {
        binding.fabRandom.extend()
        binding.fabAudio.hide()
        binding.fabVideo.hide()
        binding.fabRandom.setOnSingleClickListener {
            if (!isAllFabVisible) {
                showDimBackground()
                binding.fabAudio.show()
                binding.fabVideo.show()
                binding.tvAudio1.visibility = View.VISIBLE
                binding.tvAudio2.visibility = View.VISIBLE
                binding.tvVideo1.visibility = View.VISIBLE
                binding.tvVideo2.visibility = View.VISIBLE
                binding.ivCoinAudio.visibility = View.VISIBLE
                binding.ivCoinVideo.visibility = View.VISIBLE

                // Change the bg color to white when expanded
                binding.fabRandom.backgroundTintList = resources.getColorStateList(R.color.white)

                // Change the icon tint to black
                binding.fabRandom.setIconTintResource(R.color.black)

                // Change the icon to close when expanded
                binding.fabRandom.setIconResource(R.drawable.ic_close)

                binding.fabRandom.shrink()
                isAllFabVisible = true
            } else {
                binding.fabAudio.hide()
                binding.fabVideo.hide()
                binding.tvAudio1.visibility = View.GONE
                binding.tvAudio2.visibility = View.GONE
                binding.tvVideo1.visibility = View.GONE
                binding.tvVideo2.visibility = View.GONE
                binding.ivCoinAudio.visibility = View.GONE
                binding.ivCoinVideo.visibility = View.GONE

                hideDimBackground()

                // Reset the bg color to blue when collapsed
                binding.fabRandom.backgroundTintList = resources.getColorStateList(R.color.blue)

                // Reset the icon tint to white
                binding.fabRandom.setIconTintResource(R.color.white)

                // Change the icon to random when collapsed
                binding.fabRandom.setIconResource(R.drawable.random)
                binding.fabRandom.extend()

                isAllFabVisible = false
            }
        }
    }

    private fun showDimBackground() {
        binding.dimBackground.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(400).start()
        }
    }

    private fun hideDimBackground() {
        binding.dimBackground.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                binding.dimBackground.visibility = View.GONE
            }.start()
    }

    // Check for Internet Connection
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

}