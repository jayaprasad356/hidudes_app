package com.gmwapp.hi_dude.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.hi_dude.R


class UpiListAdapter(
    private val textList: List<String>,
    private val itemClickListener: (String) -> Unit
) : RecyclerView.Adapter<UpiListAdapter.UpiListHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION // Tracks the selected item position

    class UpiListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.upi_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpiListHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_upi, parent, false)
        return UpiListHolder(itemView)
    }

    override fun onBindViewHolder(holder: UpiListHolder, position: Int) {
        val itemText = textList[position]
        holder.textView.text = itemText

        // Highlight the selected item
        if (holder.adapterPosition == selectedPosition) {
            holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black_light))
            holder.textView.setTypeface(holder.textView.typeface, Typeface.BOLD)
        } else {
            holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.text_light_grey))
        }

        holder.itemView.setOnClickListener {
            // Get the updated position dynamically
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                val previousPosition = selectedPosition
                selectedPosition = currentPosition

                // Notify changes for both old and new selected positions
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                // Pass the selected item to the listener
                itemClickListener(textList[currentPosition])
            }
        }
    }

    override fun getItemCount(): Int = textList.size
}
