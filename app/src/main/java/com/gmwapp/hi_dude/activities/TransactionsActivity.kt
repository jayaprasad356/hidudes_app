package com.gmwapp.hi_dude.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.adapters.TransactionAdapter
import com.gmwapp.hi_dude.databinding.ActivityTransactionsBinding
import com.gmwapp.hi_dude.utils.setOnSingleClickListener
import com.gmwapp.hi_dude.viewmodels.TransactionsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionsActivity : BaseActivity() {
    lateinit var binding: ActivityTransactionsBinding
    private val transactionsViewModel: TransactionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        binding.ivBack.setOnSingleClickListener {
            finish()
        }

        binding.btnAddCoins.setOnSingleClickListener({
            val intent = Intent(this, WalletActivity::class.java)
            startActivity(intent)
        })
        BaseApplication.getInstance()?.getPrefs()?.getUserData()
            ?.let {

                if (this.let { it1 -> isInternetAvailable(it1) } == true) {
                    transactionsViewModel.getTransactions(it.id)
                } else {

                    binding.tvNointernet.visibility = View.VISIBLE

                }

            }
        transactionsViewModel.transactionsResponseLiveData.observe(this, Observer {

            if (it.success) {
                // Toast or other success logic can be added here
            } else {
             //   Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

            if (it.data != null && it.data.isNotEmpty()) {
                // Hide "No Record Found" message
                binding.tvNoRecordFound.visibility = View.GONE

                // Populate RecyclerView
                binding.rvTransactions.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                val transactionAdapter = TransactionAdapter(this, it.data)
                binding.rvTransactions.adapter = transactionAdapter
            } else {
                // Show "No Record Found" message
                binding.tvNoRecordFound.visibility = View.VISIBLE

                // Optionally clear the RecyclerView
                binding.rvTransactions.adapter = null
            }
        })


    }



        // Check for Internet Connection
        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        }
}