package com.gmwapp.hi_dude.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gmwapp.hi_dude.R
import com.gmwapp.hi_dude.activities.WalletActivity
import com.gmwapp.hi_dude.databinding.BottomSheetWelcomeBonusBinding
import com.gmwapp.hi_dude.utils.setOnSingleClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetWelcomeBonus(
    private val coins: Int,
    private val orinalPrice: Int,
    private val discountedPrice: Int
) : BottomSheetDialogFragment() {

    interface OnAddCoinsListener {
        fun onAddCoins(coins: String, id: Int)
    }

    private var _binding: BottomSheetWelcomeBonusBinding? = null
    private val binding get() = _binding!!
    private var addCoinsListener: OnAddCoinsListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnAddCoinsListener) {
            addCoinsListener = context
        } else {
            throw RuntimeException("$context must implement OnAddCoinsListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetWelcomeBonusBinding.inflate(inflater, container, false)

        // Setting the strikethrough effect
        binding.tvBonusOriginal.paintFlags =
            binding.tvBonusOriginal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        // Set text views with the provided data
        binding.tvBonusText.text = "$coins Coins"
        binding.tvBonusOriginal.text = "₹$orinalPrice"
        binding.tvBonusDiscount.text = "₹$discountedPrice"

        val twoPercentage = discountedPrice.toDouble() * 0.02
        val roundedAmount = Math.round(twoPercentage)
        val total_amount = (discountedPrice.toDouble() + roundedAmount).toString()

        // Button click listeners
        binding.tvViewMorePlans.setOnSingleClickListener {
            startActivity(Intent(context, WalletActivity::class.java))
        }

        binding.btnAddCoins.setOnSingleClickListener {
            addCoinsListener?.onAddCoins(total_amount, id)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme)
    }
}