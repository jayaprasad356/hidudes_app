package com.gmwapp.hi_dude.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.callbacks.OnItemSelectionListener
import com.gmwapp.hi_dude.databinding.AdapterDeleteReasonsBinding
import com.gmwapp.hi_dude.retrofit.responses.Reason
import com.gmwapp.hi_dude.utils.setOnSingleClickListener


class DeleteReasonAdapter(
    val activity: Activity,
    private val reasons: ArrayList<Reason>,
    private var isOther: Boolean,
    val onItemSelectionListener: OnItemSelectionListener<Reason>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemHolder = ItemHolder(
            AdapterDeleteReasonsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        return itemHolder
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val reason: Reason = reasons[position]

        holder.binding.main.setOnSingleClickListener {
            onItemSelectionListener.onItemSelected(reason)
            if (reason.name == "Other") {
                isOther = reason.isSelected != true
                reasons.filter { it.name != "Other" }.forEach{it.isSelected = false}
            }
            reason.isSelected = reason.isSelected == null || reason.isSelected == false
            reasons[position] = reason
            notifyDataSetChanged()
        }
        if (isOther) {
            if (reason.name == "Other") {
                if (reason.isSelected == true) {
                    holder.binding.main.isEnabled = true
                    holder.binding.main.setBackgroundResource(R.drawable.d_button_bg_interest_selected)
                    holder.binding.tvReason.setTextColor(activity.getColor(R.color.black))
                } else {
                    holder.binding.main.setBackgroundResource(R.drawable.d_button_bg_interest)
                    holder.binding.tvReason.setTextColor(activity.getColor(R.color.interest_text_color))
                }
            } else {
                holder.binding.main.isEnabled = false
                holder.binding.main.setBackgroundResource(R.drawable.d_button_bg_interest_disabled)
                holder.binding.tvReason.setTextColor(activity.getColor(R.color.interest_disabled_text_color))
            }
        } else if (reason.isSelected == true) {
            holder.binding.main.isEnabled = true
            holder.binding.main.setBackgroundResource(R.drawable.d_button_bg_interest_selected)
            holder.binding.tvReason.setTextColor(activity.getColor(R.color.black))
        } else {
            holder.binding.main.isEnabled = true
            holder.binding.main.setBackgroundResource(R.drawable.d_button_bg_interest)
            holder.binding.tvReason.setTextColor(activity.getColor(R.color.interest_text_color))
        }
        holder.binding.tvReason.text = reason.name
    }

    override fun getItemCount(): Int {
        return reasons.size
    }

    internal class ItemHolder(val binding: AdapterDeleteReasonsBinding) :
        RecyclerView.ViewHolder(binding.root)
}
