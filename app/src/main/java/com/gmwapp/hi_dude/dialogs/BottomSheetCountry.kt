package com.gmwapp.hi_dude.dialogs

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.adapters.CountryAdapter
import com.gmwapp.hi_dude.callbacks.OnItemSelectionListener
import com.gmwapp.hi_dude.databinding.BottomSheetCountryBinding
import com.gmwapp.hi_dude.retrofit.responses.Country
import com.gmwapp.hi_dude.utils.setOnSingleClickListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Locale


class BottomSheetCountry : BottomSheetDialogFragment() {
    private var onItemSelectionListener: OnItemSelectionListener<Country>? = null
    lateinit var binding: BottomSheetCountryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCountryBinding.inflate(layoutInflater)

        initUI()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onItemSelectionListener = activity as OnItemSelectionListener<Country>?
        } catch (e: ClassCastException) {
            Log.e("BottomSheetCountry", "onAttach: ClassCastException: " + e.message)
        }
    }

    private fun initUI() {
        binding.cvCountry.setBackgroundResource(R.drawable.card_view_border_country)

        binding.rvCountries.setLayoutManager(
            LinearLayoutManager(
                activity, LinearLayoutManager.VERTICAL, false
            )
        )
        val countries = arrayListOf(
            Country(getString(R.string.india), R.drawable.india, "+91"),
            Country(getString(R.string.saudi_arabia), R.drawable.saudi_arabia, "+966"),
            Country(getString(R.string.nepal), R.drawable.nepal, "+977"),
            Country(getString(R.string.bangladesh), R.drawable.bangladesh, "+880"),
            Country(getString(R.string.bahrain), R.drawable.bahrain, "+973"),
            Country(getString(R.string.qatar), R.drawable.qatar, "+974"),
            Country(getString(R.string.oman), R.drawable.oman, "+968"),
            Country(getString(R.string.uae), R.drawable.uae, "+971"),
            Country(getString(R.string.kuwait), R.drawable.kuwait, "+965"),
            Country(getString(R.string.srilanka), R.drawable.sri_lanka, "+94"),
            Country(getString(R.string.malaysia), R.drawable.malaysia, "+60"),
        )
        val countryAdapter = CountryAdapter(countries, object : OnItemSelectionListener<Country> {
            override fun onItemSelected(country: Country) {
                onItemSelectionListener?.onItemSelected(country)
                dismiss()
            }

            val number: Country?
                get() = TODO("Not yet implemented")
        })
        binding.rvCountries.setAdapter(countryAdapter)
        binding.ivClose.setOnSingleClickListener {
            binding.etCountry.setText("")
        }
        binding.etCountry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var filtered: List<Country> = countries.filter { country ->
                    s.toString().lowercase(Locale.getDefault()) in country.name.lowercase(Locale.getDefault())
                }
                countryAdapter.updateValues(filtered)
                binding.cvCountry.setBackgroundResource(R.drawable.card_view_border_country_selected)
                if (s.toString().length > 0) {
                    binding.ivClose.visibility = View.VISIBLE
                } else {
                    binding.ivClose.visibility = View.GONE
                }

            }

            override fun afterTextChanged(s: Editable) {
            }
        })

    }

}