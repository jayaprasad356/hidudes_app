package com.gmwapp.hi_dude.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.callbacks.OnItemSelectionListener
import com.gmwapp.hi_dude.databinding.AdapterCoinBinding
import com.gmwapp.hi_dude.retrofit.responses.CoinsResponseData
import com.gmwapp.hi_dude.utils.setOnSingleClickListener


class CoinAdapter(
    val activity: Activity,
    private val coins: ArrayList<CoinsResponseData>,
    val onItemSelectionListener: OnItemSelectionListener<CoinsResponseData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemHolder = ItemHolder(
            AdapterCoinBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        return itemHolder
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val coin: CoinsResponseData = coins[position]

        // Set the default selected item to position 0
        if (position == 0 && coins.none { it.isSelected == true }) {
            coin.isSelected = true
        }

        if (coin.popular == 1){
            holder.binding.tvPopular.visibility = View.VISIBLE
        }else{
            holder.binding.tvPopular.visibility = View.GONE
        }

        // Update the UI based on selection
        if (coin.isSelected == true) {
            holder.binding.cvCoin.strokeWidth = 4
            holder.binding.cvCoin.strokeColor = activity.resources.getColor(R.color.primary_blue)
            holder.binding.llPrice.setBackgroundColor(activity.resources.getColor(R.color.primary_blue))
            holder.binding.tvCoins.setTextColor(activity.resources.getColor(R.color.white))
            holder.binding.tvCoinstxt.setTextColor(activity.resources.getColor(R.color.white))
            holder.binding.tvPrice.setTextColor(activity.resources.getColor(R.color.white))
        } else {
            holder.binding.cvCoin.strokeWidth = 0
            holder.binding.cvCoin.strokeColor = activity.resources.getColor(R.color.white)
            holder.binding.llPrice.setBackground(activity.resources.getDrawable(R.drawable.border_coin_bg))
            holder.binding.tvCoins.setTextColor(activity.resources.getColor(R.color.black_light))
            holder.binding.tvCoinstxt.setTextColor(activity.resources.getColor(R.color.black_light))
            holder.binding.tvPrice.setTextColor(activity.resources.getColor(R.color.primary_blue))
        }

        // Handle item click
        holder.binding.main.setOnSingleClickListener {
            onItemSelectionListener.onItemSelected(coin)
            coins.onEach { it.isSelected = false }
            coin.isSelected = true
            coins[position] = coin
            notifyDataSetChanged()
        }

        // Set coin details
        holder.binding.tvCoins.text = coin.coins.toString()
        if (coin.save == null) {
            holder.binding.tvDiscountPrice.visibility = View.GONE
        } else {
            holder.binding.tvDiscountPrice.visibility = View.VISIBLE
            holder.binding.tvDiscountPrice.text = "Save ${coin.save} %"
        }
        holder.binding.tvPrice.text = activity.getString(R.string.rupee_text, coin.price)
    }

    override fun getItemCount(): Int {
        return coins.size
    }

    internal class ItemHolder(val binding: AdapterCoinBinding) :
        RecyclerView.ViewHolder(binding.root)
}
