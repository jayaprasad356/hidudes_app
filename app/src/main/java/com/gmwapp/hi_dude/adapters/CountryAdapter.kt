package com.gmwapp.hi_dude.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.hi_dude.callbacks.OnItemSelectionListener
import com.gmwapp.hi_dude.databinding.AdapterCountryBinding
import com.gmwapp.hi_dude.retrofit.responses.Country
import com.gmwapp.hi_dude.utils.setOnSingleClickListener


class CountryAdapter(
    private var countries: List<Country>,
    val onItemSelectionListener: OnItemSelectionListener<Country>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemHolder = ItemHolder(
            AdapterCountryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        return itemHolder
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val country: Country = countries[position]

        holder.binding.tvCountry.text = country.name
        holder.binding.ivCountry.setImageResource(country.image)
        holder.binding.main.setOnSingleClickListener {
            onItemSelectionListener.onItemSelected(country)
        }
    }

    public fun updateValues(countries: List<Country>){
        this.countries = countries;
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    internal class ItemHolder(val binding: AdapterCountryBinding) :
        RecyclerView.ViewHolder(binding.root)
}
