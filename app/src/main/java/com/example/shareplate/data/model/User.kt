package com.example.shareplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(

    @SerialName("user_id")
    val userId: String,

    val name: String,

    val email: String,

    val phone: String? = null,

    val role: UserRole,

    @SerialName("organisation_name")
    val organisationName: String? = null,

    val address: String? = null,

    val latitude: Double? = null,

    val longitude: Double? = null,

    @SerialName("profile_image_url")
    val profileImageUrl: String? = null,

    @SerialName("is_verified")
    val isVerified: Boolean = false,

    @SerialName("closing_time")
    val closingTime: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)