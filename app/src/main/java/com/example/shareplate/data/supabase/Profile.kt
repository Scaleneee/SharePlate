package com.example.shareplate.data.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One row in the `users` table, linked to a Supabase auth user by user_id.
 */
@Serializable
data class Profile(
    @SerialName("user_id") val userId: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    @SerialName("organisation_name") val organisationName: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("profile_image_url") val profileImageUrl: String? = null,
    @SerialName("is_verified") val isVerified: Boolean = false,
    @SerialName("closing_time") val closingTime: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)
