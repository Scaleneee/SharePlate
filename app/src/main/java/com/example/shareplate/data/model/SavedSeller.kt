package com.example.shareplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SavedSeller(

    @SerialName("buyer_id")
    val buyerId: String,

    @SerialName("seller_id")
    val sellerId: String,

    @SerialName("saved_at")
    val savedAt: String? = null
)