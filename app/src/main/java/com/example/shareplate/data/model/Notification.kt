package com.example.shareplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(

    @SerialName("notification_id")
    val notificationId: Long,

    @SerialName("user_id")
    val userId: String,

    val title: String,

    val message: String,

    val type: String,

    @SerialName("related_id")
    val relatedId: Long? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("is_read")
    val isRead: Boolean = false
)