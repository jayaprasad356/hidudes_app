package com.gmwapp.hi_dude.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.hi_dude.R

class OnboardingPagerAdapter(
    private val pages: List<Int>,
    private val images: List<Int>,
    private val titles: List<String>,
    private val subtitles: List<String>
) : RecyclerView.Adapter<OnboardingPagerAdapter.OnboardingViewHolder>() {

    class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivBottomImage: ImageView = view.findViewById(R.id.ivBottomImage)
        val tlTitle: TextView = view.findViewById(R.id.tl_title)
        val tlSubTitle: TextView = view.findViewById(R.id.tl_sub_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.onboarding_page1, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        // Set the image for ivBottomImage based on the position
        holder.ivBottomImage.setImageResource(images[position])

        // Set the title and subtitle text for this page
        holder.tlTitle.text = titles[position]
        holder.tlSubTitle.text = subtitles[position]
    }

    override fun getItemCount(): Int = pages.size
}


