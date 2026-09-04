package com.example.shareplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateSurplusPrice(

    @SerialName("current_discount_percent")
    val currentDiscountPercent: Int,

    @SerialName("current_price_cent")
    val currentPriceCent: Int
)