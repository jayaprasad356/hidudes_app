package com.gmwapp.hi_dude.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.callbacks.OnItemSelectionListener
import com.gmwapp.hi_dude.databinding.AdapterFemaleUserBinding
import com.gmwapp.hi_dude.retrofit.responses.FemaleUsersResponse
import com.gmwapp.hi_dude.retrofit.responses.FemaleUsersResponseData
import com.gmwapp.hi_dude.retrofit.responses.Interests
import com.gmwapp.hi_dude.utils.Helper
import com.gmwapp.hi_dude.utils.setOnSingleClickListener
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class FemaleUserAdapter(
    val activity: Activity,
    private var femaleUsers: List<FemaleUsersResponseData>,
    val onAudioListener: OnItemSelectionListener<FemaleUsersResponseData>,
    val onVideoListener: OnItemSelectionListener<FemaleUsersResponseData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemHolder = ItemHolder(
            AdapterFemaleUserBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        return itemHolder
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val femaleUser: FemaleUsersResponseData = femaleUsers[position]
        Glide.with(activity)
            .load(femaleUser.image)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(28)))
            .into(holder.binding.ivProfile)





        val  audioStatus = femaleUser.audio_status
        val  videoStatus = femaleUser.video_status

        if (audioStatus == 1) {
            holder.binding.cvAudio.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.button_background))
            holder.binding.cvAudio.setOnSingleClickListener {
                val position = holder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedUser = femaleUsers[position]
                    onAudioListener.onItemSelected(clickedUser)
                    Log.d("FemaleName", "${clickedUser.name}")
                }
            }


        }
        if (videoStatus == 1) {
            holder.binding.cvVideo.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.button_background))
            holder.binding.cvVideo.setOnSingleClickListener{
                onVideoListener.onItemSelected(femaleUser)
            }
        }

        if (audioStatus == 0 && videoStatus == 0) {
            holder.binding.cvAudio.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.inactive_call))
            holder.binding.cvVideo.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.inactive_call))
        }

        holder.binding.tvName.text = femaleUser.name
        holder.binding.tvLanguage.text = femaleUser.language

        val interestsAsString = femaleUser.interests.trim('[', ']').split(", ")

        val staggeredGridLayoutManager = FlexboxLayoutManager(activity).apply {
            flexWrap = FlexWrap.WRAP
            alignItems = AlignItems.FLEX_START
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }

        val itemDecoration = FlexboxItemDecoration(activity).apply {
            setDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_divider))
            setOrientation(FlexboxItemDecoration.VERTICAL)
        }
        //   holder.binding.rvInterests.addItemDecoration(itemDecoration)
        holder.binding.rvInterests.layoutManager = staggeredGridLayoutManager


        val interests = arrayListOf<Interests>()
        interestsAsString.forEach {
            interests.add(Helper.getInterestObject(activity, it))
        }


        val interestsListAdapter = InterestsFemaleListAdapter(activity, interests, false, object : OnItemSelectionListener<Interests> {
            override fun onItemSelected(interest: Interests) {
                // Handle interest item selection
            }
        })

        holder.binding.rvInterests.adapter = interestsListAdapter
        holder.binding.tvSummary.text = femaleUser.describe_yourself
    }

    override fun getItemCount(): Int {
        return femaleUsers.size
    }

    internal class ItemHolder(val binding: AdapterFemaleUserBinding) : RecyclerView.ViewHolder(binding.root)
}