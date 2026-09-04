package com.example.shareplate.util

object PriceCalculator {

    fun calculateDiscountedPrice(
        originalPriceCent: Int,
        discountPercent: Int
    ): Int {
        return originalPriceCent * (100 - discountPercent) / 100
    }
}