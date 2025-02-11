package com.gmwapp.hi_dude.utils

import com.gmwapp.hi_dude.viewmodels.GiftViewModel

object GiftViewModelProvider {
    lateinit var giftViewModel: GiftViewModel

    fun init(viewModel: GiftViewModel) {
        giftViewModel = viewModel
    }
}