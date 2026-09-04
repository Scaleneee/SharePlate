package com.example.shareplate.data.repository

import com.example.shareplate.data.model.Notification
import com.example.shareplate.data.remote.SupabaseProvider
import io.github.jan.supabase.postgrest.from

class NotificationRepository {

    private val supabase =
        SupabaseProvider.client


    // GET ALL NOTIFICATIONS FOR CURRENT USER
    suspend fun getNotifications(
        userId: String
    ): List<Notification> {

        return supabase
            .from("notifications")
            .select {

                filter {

                    eq(
                        "user_id",
                        userId
                    )
                }
            }
            .decodeList<Notification>()
            .sortedByDescending {

                it.createdAt ?: ""
            }
    }


    // MARK ONE NOTIFICATION AS READ
    suspend fun markNotificationAsRead(
        notificationId: Long,
        userId: String
    ) {

        supabase
            .from("notifications")
            .update(
                {

                    set(
                        "is_read",
                        true
                    )
                }
            ) {

                filter {

                    eq(
                        "notification_id",
                        notificationId
                    )

                    eq(
                        "user_id",
                        userId
                    )
                }
            }
    }


    // MARK ALL NOTIFICATIONS AS READ
    suspend fun markAllNotificationsAsRead(
        userId: String
    ) {

        supabase
            .from("notifications")
            .update(
                {

                    set(
                        "is_read",
                        true
                    )
                }
            ) {

                filter {

                    eq(
                        "user_id",
                        userId
                    )

                    eq(
                        "is_read",
                        false
                    )
                }
            }
    }


    // DELETE ONE NOTIFICATION
    suspend fun deleteNotification(
        notificationId: Long,
        userId: String
    ) {

        supabase
            .from("notifications")
            .delete {

                filter {

                    eq(
                        "notification_id",
                        notificationId
                    )

                    eq(
                        "user_id",
                        userId
                    )
                }
            }
    }
}