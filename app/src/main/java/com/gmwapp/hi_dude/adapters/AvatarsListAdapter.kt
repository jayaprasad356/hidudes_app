package com.gmwapp.hi_dude.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.gmwapp.hi_dude.databinding.AdapterAvatarBinding
import com.gmwapp.hi_dude.retrofit.responses.AvatarsListData


class AvatarsListAdapter(
    private val activity: Activity,
    private val avatarsListData: ArrayList<AvatarsListData?>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemHolder = ItemHolder(
            AdapterAvatarBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        return itemHolder
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val avatarsListData: AvatarsListData? = avatarsListData[position]

        Glide.with(activity).load(avatarsListData?.image)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(14))).into(holder.binding.ivAvatar)

    }

    override fun getItemCount(): Int {
        return avatarsListData.size
    }

    internal class ItemHolder(val binding: AdapterAvatarBinding) :
        RecyclerView.ViewHolder(binding.root)
}
