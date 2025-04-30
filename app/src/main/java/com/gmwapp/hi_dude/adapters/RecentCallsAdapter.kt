package com.gmwapp.hi_dude.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gmwapp.hi_dude.BaseApplication
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.callbacks.OnItemSelectionListener
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.databinding.AdapterRecentCallsBinding
import com.gmwapp.hi_dude.retrofit.responses.CallsListResponseData
import com.gmwapp.hi_dude.utils.setOnSingleClickListener


class RecentCallsAdapter(
    val activity: Activity,
    private val callList: ArrayList<CallsListResponseData>,
    val onAudioListener: OnItemSelectionListener<CallsListResponseData>,
    val onVideoListener: OnItemSelectionListener<CallsListResponseData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemHolder = ItemHolder(
            AdapterRecentCallsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        return itemHolder
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val call: CallsListResponseData = callList[position]
        Glide.with(activity).load(call.image).apply(
            RequestOptions().circleCrop()
        ).into(holder.binding.ivImage)

        holder.binding.ivAudioCircle.setOnClickListener(null)
        holder.binding.ivVideoCircle.setOnClickListener(null)
        // Reset Views before applying new data
        holder.binding.ivAudioCircle.visibility = View.GONE
        holder.binding.ivVideoCircle.visibility = View.GONE
        holder.binding.ivAudio.visibility = View.GONE
        holder.binding.ivVideo.visibility = View.GONE
        holder.binding.tvAmount.visibility = View.GONE

        holder.binding.ivAudio.setColorFilter(ContextCompat.getColor(activity, R.color.white))
        holder.binding.ivVideo.setColorFilter(ContextCompat.getColor(activity, R.color.white))

        holder.binding.ivAudioCircle.clearColorFilter()
        holder.binding.ivVideoCircle.clearColorFilter()

        holder.binding.ivAudio.isEnabled = false
        holder.binding.ivVideo.isEnabled = false



        holder.binding.tvName.text = call.name
        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
        if (userData?.gender == DConstants.MALE) {
            holder.binding.ivAudioCircle.visibility = View.VISIBLE
            holder.binding.ivVideoCircle.visibility = View.VISIBLE
            holder.binding.ivAudio.visibility = View.VISIBLE
            holder.binding.ivVideo.visibility = View.VISIBLE
            holder.binding.tvAmount.visibility = View.GONE

            if (call.audio_status == 0) {
                holder.binding.ivAudioCircle.setBackgroundResource(R.drawable.d_button_disable)
                holder.binding.ivAudio.setColorFilter(ContextCompat.getColor(activity, R.color.unselect_grey))
                holder.binding.ivAudio.isEnabled = false
            }else{
                holder.binding.ivAudioCircle.setOnSingleClickListener{
                    onAudioListener.onItemSelected(call)
                }
                holder.binding.ivAudioCircle.setBackgroundResource(R.drawable.ic_permissions_circle)
                holder.binding.ivAudio.setColorFilter(ContextCompat.getColor(activity, R.color.primary_blue))
                holder.binding.ivAudio.isEnabled = true

            }
            if (call.video_status == 0) {
                holder.binding.ivVideoCircle.setBackgroundResource(R.drawable.d_button_disable)
                holder.binding.ivVideo.setColorFilter(ContextCompat.getColor(activity, R.color.unselect_grey))
                holder.binding.ivVideo.isEnabled = false

            }else{
                holder.binding.ivVideoCircle.setOnSingleClickListener{ onVideoListener.onItemSelected(call) }
                holder.binding.ivVideoCircle.setBackgroundResource(R.drawable.ic_permissions_circle)
                holder.binding.ivVideo.setColorFilter(ContextCompat.getColor(activity, R.color.primary_blue))
                holder.binding.ivVideo.isEnabled = true
            }
        } else {
            holder.binding.ivAudioCircle.visibility = View.GONE
            holder.binding.ivVideoCircle.visibility = View.GONE
            holder.binding.ivAudio.visibility = View.GONE
            holder.binding.ivVideo.visibility = View.GONE
            holder.binding.tvAmount.visibility = View.VISIBLE
            holder.binding.tvAmount.text = activity.getString(R.string.rupee_text, call.income)
        }
        holder.binding.tvTime.text = call.started_time + " \u2022" + call.duration

        Log.d("RecentCallUserName","${call.name}")



    }

    override fun getItemCount(): Int {
        return callList.size
    }



    fun addData(newData: List<CallsListResponseData>) {
        val start = callList.size
        callList.addAll(newData)
        notifyItemRangeInserted(start, newData.size)
    }

    fun clearData() {
        callList.clear()
        notifyDataSetChanged()
    }


    internal class ItemHolder(val binding: AdapterRecentCallsBinding) :
        RecyclerView.ViewHolder(binding.root)


}
