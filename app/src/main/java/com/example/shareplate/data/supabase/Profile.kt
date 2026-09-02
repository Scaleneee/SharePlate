package com.example.shareplate.data.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    @SerialName("user_id") val userId: String,
    @SerialName("full_name") val fullName: String,
    val email: String,
    @SerialName("phone_number") val phoneNumber: String,
    val role: String,
    @SerialName("business_name") val businessName: String? = null,
    @SerialName("business_address") val businessAddress: String? = null,
    @SerialName("delivery_address") val deliveryAddress: String? = null,
    @SerialName("organization_name") val organizationName: String? = null,
    @SerialName("organization_reg_no") val organizationRegNo: String? = null
)
