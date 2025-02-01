package com.gmwapp.hi_dude.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.hi_dude.callbacks.OnItemSelectionListener
import com.gmwapp.hi_dude.databinding.AdapterInterestFemaleListBinding
import com.gmwapp.hi_dude.retrofit.responses.Interests


class InterestsMaleListAdapter(
    val activity: Activity,
    private val interests: ArrayList<Interests>,
    private var isLimitreached: Boolean,
    val onItemSelectionListener: OnItemSelectionListener<Interests>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemHolder = ItemHolder(
            AdapterInterestFemaleListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        return itemHolder
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val interest: Interests = interests[position]

//        holder.binding.main.setOnSingleClickListener(View.OnClickListener {
//            onItemSelectionListener.onItemSelected(interest)
//            interest.isSelected = interest.isSelected == null || interest.isSelected == false
//            interests[position] = interest
//            notifyDataSetChanged()
//        })
//
//        if(interest.isSelected == true){
//            holder.binding.main.isEnabled  = true
//            holder.binding.main.setBackgroundResource(R.drawable.d_button_bg_interest_selected)
//            holder.binding.tvInterest.setTextColor(activity.getColor(R.color.black))
//        } else if(isLimitreached){
//            holder.binding.main.isEnabled  = false
//            holder.binding.main.setBackgroundResource(R.drawable.d_button_bg_interest_disabled)
//            holder.binding.tvInterest.setTextColor(activity.getColor(R.color.interest_disabled_text_color))
//        }else{
//            holder.binding.main.isEnabled  = true
//            holder.binding.main.setBackgroundResource(R.drawable.d_button_bg_interest)
//            holder.binding.tvInterest.setTextColor(activity.getColor(R.color.interest_text_color))
//        }


        holder.binding.tvInterest.text = interest.name
        holder.binding.ivInterest.setImageResource(interest.image)
    }

    override fun getItemCount(): Int {
        return interests.size
    }

    internal class ItemHolder(val binding: AdapterInterestFemaleListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}
