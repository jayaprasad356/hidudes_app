package com.gmwapp.hi_dude.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.adapters.TransactionAdapter
import com.gmwapp.hi_dude.databinding.ActivityTransactionsBinding
import com.gmwapp.hi_dude.utils.setOnSingleClickListener
import com.gmwapp.hi_dude.viewmodels.TransactionsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionsActivity : BaseActivity() {
    private lateinit var binding: ActivityTransactionsBinding
    private val transactionsViewModel: TransactionsViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    private var isLoading = false
    private var offset = 0
    private val limit = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
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
        binding.ivBack.setOnSingleClickListener { finish() }

        binding.btnAddCoins.setOnSingleClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
        }

        // Initialize RecyclerView and Adapter
        transactionAdapter = TransactionAdapter(this, mutableListOf())
        binding.rvTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvTransactions.adapter = transactionAdapter

        // Load Initial Transactions
        if (isInternetAvailable(this)) {
            loadTransactions()
        } else {
            binding.tvNointernet.visibility = View.VISIBLE
        }

        // Scroll Listener for Pagination
        binding.rvTransactions.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (!isLoading && layoutManager.findLastCompletelyVisibleItemPosition() == transactionAdapter.itemCount - 1) {
                    isLoading = true
                    offset += limit // Load next batch
                    loadTransactions()
                }
            }
        })

        // Observe Transactions Data
        transactionsViewModel.transactionsResponseLiveData.observe(this) { response ->
            isLoading = false
            if (response != null && response.success && response.data != null && response.data.isNotEmpty()) {
                transactionAdapter.addTransactions(response.data)
                binding.tvNoRecordFound.visibility = View.GONE
            } else if (transactionAdapter.itemCount == 0) {
                binding.tvNoRecordFound.visibility = View.VISIBLE
            }
        }
    }

    private fun loadTransactions() {
        BaseApplication.getInstance()?.getPrefs()?.getUserData()?.let {
            transactionsViewModel.getTransactions(it.id, offset, limit)
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
}
