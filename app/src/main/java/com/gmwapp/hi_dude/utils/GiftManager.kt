package com.gmwapp.hi_dude.utils

import com.gmwapp.hi_dude.retrofit.responses.GiftData

object GiftManager {
    private val giftMap = mutableMapOf<String, Int>() // URL -> Coins

    fun updateGifts(giftData: List<GiftData>) {
        giftMap.clear()
        for (gift in giftData) {
            giftMap[gift.gift_icon] = gift.coins
        }
    }

    fun getGiftIconsWithCoins(): Map<String, Int> {
        return giftMap
    }
}