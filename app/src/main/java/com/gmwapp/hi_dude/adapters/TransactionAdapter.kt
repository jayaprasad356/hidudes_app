package com.gmwapp.hi_dude.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.databinding.AdapterTransactionBinding
import com.gmwapp.hi_dude.retrofit.responses.TransactionsResponseData


class TransactionAdapter(
    val activity: Activity,
    private val transactions: List<TransactionsResponseData>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemHolder = ItemHolder(
            AdapterTransactionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        return itemHolder
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val transaction: TransactionsResponseData = transactions[position]

        if(transaction.payment_type == "Credit"){
            holder.binding.tvCoins.text = "+"+transaction.coins
            holder.binding.tvCoins.setTextColor(activity.getColor(android.R.color.holo_green_dark))
        } else{
            holder.binding.tvCoins.text = "-"+transaction.coins
            holder.binding.tvCoins.setTextColor(activity.getColor(android.R.color.holo_red_dark))
        }
        holder.binding.tvTransactionTitle.text = transaction.type
        holder.binding.tvTransactionDate.text = transaction.datetime
        holder.binding.tvTransactionHint.text = activity.getString(R.string.session_id)+transaction.id
    }

     override fun getItemCount(): Int {
        return transactions.size
    }

    internal class ItemHolder(val binding: AdapterTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}
