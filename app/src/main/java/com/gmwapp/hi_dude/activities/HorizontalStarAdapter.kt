package com.gmwapp.hi_dude.activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.hi_dude.R

class HorizontalStarAdapter(
    private val context: Context,
    private val starCount: Int,
    private val onRatingChanged: (Int) -> Unit // Callback for rating changes
) : RecyclerView.Adapter<HorizontalStarAdapter.StarViewHolder>() {

    private val stars = MutableList(starCount) { false }

    inner class StarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val starImageView: ImageView = view.findViewById(R.id.iv_star)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StarViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.star_item, parent, false)
        return StarViewHolder(view)
    }

    override fun onBindViewHolder(holder: StarViewHolder, position: Int) {
        holder.starImageView.setImageResource(
            if (stars[position]) R.drawable.ic_star_filled else R.drawable.ic_star_empty
        )

        // Apply fade-in animation to the star image view
        val fadeInAnimation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.fade_in)
        holder.starImageView.startAnimation(fadeInAnimation)

        holder.starImageView.setOnClickListener {
            for (i in 0..position) stars[i] = true
            for (i in position + 1 until stars.size) stars[i] = false
            notifyDataSetChanged()
            onRatingChanged(position + 1) // Notify the new rating
        }
    }

    override fun getItemCount(): Int = stars.size
}


