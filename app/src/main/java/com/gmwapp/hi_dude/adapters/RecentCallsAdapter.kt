package com.gmwapp.hi_dude.adapters

import android.app.Activity
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
        holder.binding.tvName.text = call.name
        val userData = BaseApplication.getInstance()?.getPrefs()?.getUserData()
        if (userData?.gender == DConstants.MALE) {
            holder.binding.ivAudioCircle.visibility = View.VISIBLE
            holder.binding.ivVideoCircle.visibility = View.VISIBLE
            holder.binding.ivAudio.visibility = View.VISIBLE
            holder.binding.ivVideo.visibility = View.VISIBLE
            holder.binding.tvAmount.visibility = View.GONE

            if (call.audio_status == 0) {
                holder.binding.ivAudio.setColorFilter(ContextCompat.getColor(activity, R.color.white))
                holder.binding.ivAudioCircle.setColorFilter(ContextCompat.getColor(activity, R.color.text_light_grey))
                holder.binding.ivAudio.isEnabled = false
            }else{
                holder.binding.ivAudioCircle.setOnSingleClickListener{
                    onAudioListener.onItemSelected(call)
                }
                holder.binding.ivAudio.setColorFilter(ContextCompat.getColor(activity, R.color.white))
                holder.binding.ivAudio.isEnabled = true

            }
            if (call.video_status == 0) {
                holder.binding.ivVideo.setColorFilter(ContextCompat.getColor(activity, R.color.white))
                holder.binding.ivVideoCircle.setColorFilter(ContextCompat.getColor(activity, R.color.text_light_grey))
                holder.binding.ivVideo.isEnabled = false

            }else{
                holder.binding.ivVideoCircle.setOnSingleClickListener{ onVideoListener.onItemSelected(call) }
                holder.binding.ivVideo.setColorFilter(ContextCompat.getColor(activity, R.color.white))
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




    }

    override fun getItemCount(): Int {
        return callList.size
    }

    internal class ItemHolder(val binding: AdapterRecentCallsBinding) :
        RecyclerView.ViewHolder(binding.root)
}
