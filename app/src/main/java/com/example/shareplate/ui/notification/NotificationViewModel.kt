package com.example.shareplate.ui.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareplate.data.model.Notification
import com.example.shareplate.data.remote.SupabaseProvider
import com.example.shareplate.data.repository.NotificationRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(

    private val repository:
    NotificationRepository =
        NotificationRepository()

) : ViewModel() {


    // ALL NOTIFICATIONS
    private val _notifications =
        MutableStateFlow<List<Notification>>(
            emptyList()
        )

    val notifications:
            StateFlow<List<Notification>> =
        _notifications.asStateFlow()


    // UNREAD COUNT
    private val _unreadCount =
        MutableStateFlow(0)

    val unreadCount:
            StateFlow<Int> =
        _unreadCount.asStateFlow()


    // LOADING
    private val _isLoading =
        MutableStateFlow(false)

    val isLoading:
            StateFlow<Boolean> =
        _isLoading.asStateFlow()


    // ERROR
    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage:
            StateFlow<String?> =
        _errorMessage.asStateFlow()


    // GET CURRENT LOGGED-IN USER ID
    private fun getCurrentUserId(): String? {

        return SupabaseProvider
            .client
            .auth
            .currentUserOrNull()
            ?.id
    }


    // LOAD NOTIFICATIONS DIRECTLY FROM SUPABASE
    fun loadNotifications() {

        viewModelScope.launch {

            _isLoading.value = true
            _errorMessage.value = null

            try {

                val userId =
                    getCurrentUserId()

                if (userId == null) {

                    _notifications.value =
                        emptyList()

                    _unreadCount.value =
                        0

                    _errorMessage.value =
                        "User is not logged in"

                    return@launch
                }


                val result =
                    repository
                        .getNotifications(
                            userId
                        )


                _notifications.value =
                    result


                updateUnreadCount()


            } catch (e: Exception) {

                Log.e(
                    "Notification",
                    "Failed to load notifications",
                    e
                )

                _errorMessage.value =
                    e.message
                        ?: "Failed to load notifications"

            } finally {

                _isLoading.value =
                    false
            }
        }
    }


    // REFRESH
    fun refreshNotifications() {

        loadNotifications()
    }


    // MARK ONE AS READ
    fun markNotificationAsRead(
        notificationId: Long
    ) {

        val currentNotification =
            _notifications
                .value
                .find {

                    it.notificationId ==
                            notificationId
                }


        // already read
        if (
            currentNotification?.isRead ==
            true
        ) {
            return
        }


        viewModelScope.launch {

            try {

                val userId =
                    getCurrentUserId()
                        ?: return@launch


                repository
                    .markNotificationAsRead(

                        notificationId =
                            notificationId,

                        userId =
                            userId
                    )


                // UPDATE LOCAL UI IMMEDIATELY
                _notifications.value =
                    _notifications
                        .value
                        .map { notification ->

                            if (
                                notification.notificationId ==
                                notificationId
                            ) {

                                notification.copy(
                                    isRead = true
                                )

                            } else {

                                notification
                            }
                        }


                updateUnreadCount()


            } catch (e: Exception) {

                Log.e(
                    "Notification",
                    "Failed to mark notification as read",
                    e
                )

                _errorMessage.value =
                    e.message
                        ?: "Failed to update notification"
            }
        }
    }


    // MARK ALL AS READ
    fun markAllNotificationsAsRead() {

        viewModelScope.launch {

            try {

                val userId =
                    getCurrentUserId()
                        ?: return@launch


                repository
                    .markAllNotificationsAsRead(
                        userId
                    )


                _notifications.value =
                    _notifications
                        .value
                        .map {

                            it.copy(
                                isRead = true
                            )
                        }


                _unreadCount.value =
                    0


            } catch (e: Exception) {

                Log.e(
                    "Notification",
                    "Failed to mark all as read",
                    e
                )

                _errorMessage.value =
                    e.message
                        ?: "Failed to update notifications"
            }
        }
    }


    // DELETE NOTIFICATION
    fun deleteNotification(
        notificationId: Long
    ) {

        viewModelScope.launch {

            try {

                val userId =
                    getCurrentUserId()
                        ?: return@launch


                repository
                    .deleteNotification(

                        notificationId =
                            notificationId,

                        userId =
                            userId
                    )


                _notifications.value =
                    _notifications
                        .value
                        .filter {

                            it.notificationId !=
                                    notificationId
                        }


                updateUnreadCount()


            } catch (e: Exception) {

                Log.e(
                    "Notification",
                    "Failed to delete notification",
                    e
                )

                _errorMessage.value =
                    e.message
                        ?: "Failed to delete notification"
            }
        }
    }


    private fun updateUnreadCount() {

        _unreadCount.value =
            _notifications
                .value
                .count {

                    !it.isRead
                }
    }


    fun clearError() {

        _errorMessage.value =
            null
    }
}