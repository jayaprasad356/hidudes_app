package com.gmwapp.hi_dude.activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.BaseAdapter
import com.gmwapp.hi_dude.R

class StarRatingAdapter(private val context: Context, private val starCount: Int) : BaseAdapter() {

    private val stars = MutableList(starCount) { false } // Track star states (false: unfilled, true: filled)

    override fun getCount(): Int = stars.size

    override fun getItem(position: Int): Any = stars[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.star_item, parent, false)
        val starImageView = view.findViewById<ImageView>(R.id.iv_star)

        // Set initial star icon based on its state
        starImageView.setImageResource(if (stars[position]) R.drawable.ic_star_filled else R.drawable.ic_star_empty)

        // Toggle star state on click
        starImageView.setOnClickListener {
            for (i in 0..position) stars[i] = true  // Fill stars up to the clicked position
            for (i in position + 1 until stars.size) stars[i] = false  // Unfill stars beyond the clicked position
            notifyDataSetChanged()
        }

        return view
    }
}
