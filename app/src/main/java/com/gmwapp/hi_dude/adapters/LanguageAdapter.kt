package com.gmwapp.hi_dude.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.callbacks.OnItemSelectionListener
import com.gmwapp.hi_dude.databinding.AdapterLanguageBinding
import com.gmwapp.hi_dude.retrofit.responses.Language
import com.gmwapp.hi_dude.utils.setOnSingleClickListener


class LanguageAdapter(
    val activity: Activity,
    private val languages: ArrayList<Language>,
    val onItemSelectionListener: OnItemSelectionListener<Language>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemHolder = ItemHolder(
            AdapterLanguageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        return itemHolder
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val language: Language = languages[position]



        //  holder.binding.tvLanguage.text different color based on position
        if (position  == 0) {
            holder.binding.tvLanguage.setTextColor(activity.resources.getColor(R.color.Hindi))
        }
        else if (position  == 1) {
            holder.binding.tvLanguage.setTextColor(activity.resources.getColor(R.color.Telungu))
        }
        else if (position  == 2) {
            holder.binding.tvLanguage.setTextColor(activity.resources.getColor(R.color.Malayalam))
        }
        else if (position  == 3) {
            holder.binding.tvLanguage.setTextColor(activity.resources.getColor(R.color.Kanadam))
        }
        else if (position  == 4) {
            holder.binding.tvLanguage.setTextColor(activity.resources.getColor(R.color.Punjabi))
        }
        else if (position  == 5) {
            holder.binding.tvLanguage.setTextColor(activity.resources.getColor(R.color.Tamil))
        }


        holder.binding.tvLanguage.text = language.name
        holder.binding.ivLanguage.setImageResource(language.image)
        if (language.isSelected == true) {
            holder.binding.main.setBackgroundResource(R.drawable.d_button_bg_language_selected)
        } else {
            holder.binding.main.setBackgroundResource(R.drawable.d_button_bg_language)
        }
        holder.binding.main.setOnSingleClickListener {
            onItemSelectionListener.onItemSelected(language)
            languages.onEach { it.isSelected = false }
            language.isSelected = true
            languages[position] = language
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return languages.size
    }

    internal class ItemHolder(val binding: AdapterLanguageBinding) :
        RecyclerView.ViewHolder(binding.root)
}
