package com.gmwapp.hi_dude.utils

import com.gmwapp.hi_dude.retrofit.responses.GiftData

object GiftManager {
    private val giftMap = mutableMapOf<String, GiftData>() // Store full GiftData instead of just coins

    fun updateGifts(giftData: List<GiftData>) {
        giftMap.clear()
        for (gift in giftData) {
            giftMap[gift.gift_icon] = gift
        }
    }

    fun getGiftIconsWithCoins(): Map<String, GiftData> {
        return giftMap
    }
}