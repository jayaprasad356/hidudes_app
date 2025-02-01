package com.gmwapp.hi_dude.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.callbacks.OnItemSelectionListener
import com.gmwapp.hi_dude.databinding.AdapterInterestBinding
import com.gmwapp.hi_dude.retrofit.responses.Interests


class InterestsListAdapter(
    val activity: Activity,
    private val interests: ArrayList<Interests>,
    private var isLimitreached: Boolean,
    val onItemSelectionListener: OnItemSelectionListener<Interests>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemHolder = ItemHolder(
            AdapterInterestBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        return itemHolder
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val interest: Interests = interests[position]

        // Count the number of selected items
        selectedCount = interests.count { it.isSelected == true }

        holder.binding.main.setOnClickListener {
            if (selectedCount < 4 || interest.isSelected == true) {  // Allow selection if less than 4 or already selected
                onItemSelectionListener.onItemSelected(interest)
                interest.isSelected = interest.isSelected == null || interest.isSelected == false
                interests[position] = interest
                notifyDataSetChanged()
            }
        }

        // Update UI based on selection and limit reached
        if (interest.isSelected == true) {
            holder.binding.main.isEnabled = true
            holder.binding.main.setBackgroundResource(R.drawable.d_button_bg_interest_selected)
            holder.binding.tvInterest.setTextColor(activity.getColor(R.color.primary_blue))
        } else if (isLimitreached) {
            holder.binding.main.isEnabled = false
            holder.binding.main.setBackgroundResource(R.drawable.d_button_bg_interest_disabled)
            holder.binding.tvInterest.setTextColor(activity.getColor(R.color.interest_disabled_text_color))
        } else {
            holder.binding.main.isEnabled = true
            holder.binding.main.setBackgroundResource(R.drawable.d_button_bg_interest)
            holder.binding.tvInterest.setTextColor(activity.getColor(R.color.interest_text_color))
        }

        holder.binding.tvInterest.text = interest.name
        holder.binding.ivInterest.setImageResource(interest.image)
    }

    fun updateLimitReached(isLimitreached: Boolean) {
        this.isLimitreached = isLimitreached
    }

    override fun getItemCount(): Int {
        return interests.size
    }

    internal class ItemHolder(val binding: AdapterInterestBinding) :
        RecyclerView.ViewHolder(binding.root)
}
