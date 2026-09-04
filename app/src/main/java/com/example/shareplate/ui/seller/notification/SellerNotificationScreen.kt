package com.example.shareplate.ui.seller.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shareplate.data.model.Notification
import com.example.shareplate.ui.notification.NotificationViewModel
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun SellerNotificationScreen(

    notificationViewModel:
    NotificationViewModel =
        viewModel(),

    onBackClick: () -> Unit = {}

) {


    val notifications by
    notificationViewModel
        .notifications
        .collectAsStateWithLifecycle()


    val unreadCount by
    notificationViewModel
        .unreadCount
        .collectAsStateWithLifecycle()


    val isLoading by
    notificationViewModel
        .isLoading
        .collectAsStateWithLifecycle()


    val errorMessage by
    notificationViewModel
        .errorMessage
        .collectAsStateWithLifecycle()


    // LOAD REAL SUPABASE DATA
    LaunchedEffect(Unit) {

        notificationViewModel
            .loadNotifications()
    }


    Scaffold(

        topBar = {

            SellerNotificationTopBar(

                unreadCount =
                    unreadCount,

                onBackClick =
                    onBackClick,

                onMarkAllRead = {

                    notificationViewModel
                        .markAllNotificationsAsRead()
                }
            )
        }

    ) { innerPadding ->


        when {

            // FIRST LOAD
            isLoading &&
                    notifications.isEmpty() -> {

                Box(

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                innerPadding
                            ),

                    contentAlignment =
                        Alignment.Center

                ) {

                    CircularProgressIndicator()
                }
            }


            // ERROR
            errorMessage != null &&
                    notifications.isEmpty() -> {

                NotificationErrorScreen(

                    message =
                        errorMessage
                            ?: "Unable to load notifications",

                    onRetry = {

                        notificationViewModel
                            .loadNotifications()
                    },

                    modifier =
                        Modifier.padding(
                            innerPadding
                        )
                )
            }


            // NO DATABASE NOTIFICATIONS
            notifications.isEmpty() -> {

                EmptyNotificationScreen(

                    modifier =
                        Modifier.padding(
                            innerPadding
                        )
                )
            }


            // REAL NOTIFICATIONS
            else -> {

                LazyColumn(

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                innerPadding
                            ),

                    contentPadding =
                        PaddingValues(
                            horizontal = 16.dp,
                            vertical = 18.dp
                        ),

                    verticalArrangement =
                        Arrangement.spacedBy(
                            12.dp
                        )

                ) {


                    item {

                        NotificationSummary(
                            unreadCount =
                                unreadCount
                        )
                    }


                    items(

                        items =
                            notifications,

                        key = {
                            it.notificationId
                        }

                    ) { notification ->


                        SellerNotificationCard(

                            notification =
                                notification,

                            onClick = {

                                if (
                                    !notification.isRead
                                ) {

                                    notificationViewModel
                                        .markNotificationAsRead(
                                            notification.notificationId
                                        )
                                }
                            },

                            onDelete = {

                                notificationViewModel
                                    .deleteNotification(
                                        notification.notificationId
                                    )
                            }
                        )
                    }


                    if (
                        isLoading
                    ) {

                        item {

                            Box(

                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            16.dp
                                        ),

                                contentAlignment =
                                    Alignment.Center

                            ) {

                                CircularProgressIndicator(
                                    modifier =
                                        Modifier.size(
                                            24.dp
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun SellerNotificationTopBar(

    unreadCount: Int,

    onBackClick: () -> Unit,

    onMarkAllRead: () -> Unit

) {

    Column {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 8.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically

        ) {


            IconButton(

                onClick =
                    onBackClick

            ) {

                Icon(

                    imageVector =
                        Icons.AutoMirrored
                            .Filled
                            .ArrowBack,

                    contentDescription =
                        "Back"
                )
            }


            Column(

                modifier =
                    Modifier.weight(
                        1f
                    )

            ) {

                Text(

                    text =
                        "Notifications",

                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,

                    fontWeight =
                        FontWeight.Bold
                )


                if (
                    unreadCount > 0
                ) {

                    Text(

                        text =
                            "$unreadCount unread",

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }


            if (
                unreadCount > 0
            ) {

                TextButton(

                    onClick =
                        onMarkAllRead

                ) {

                    Text(
                        text =
                            "Mark all read"
                    )
                }
            }
        }


        HorizontalDivider()
    }
}


@Composable
private fun NotificationSummary(
    unreadCount: Int
) {

    Text(

        text =
            if (
                unreadCount == 0
            ) {

                "You're all caught up"

            } else if (
                unreadCount == 1
            ) {

                "1 unread notification"

            } else {

                "$unreadCount unread notifications"
            },

        style =
            MaterialTheme
                .typography
                .bodyMedium,

        color =
            MaterialTheme
                .colorScheme
                .onSurfaceVariant,

        modifier =
            Modifier.padding(
                bottom = 4.dp
            )
    )
}


@Composable
private fun SellerNotificationCard(

    notification:
    Notification,

    onClick: () -> Unit,

    onDelete: () -> Unit

) {


    val backgroundColor =

        if (
            notification.isRead
        ) {

            MaterialTheme
                .colorScheme
                .surface

        } else {

            MaterialTheme
                .colorScheme
                .surfaceVariant
        }


    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },

        shape =
            RoundedCornerShape(
                18.dp
            ),

        colors =
            CardDefaults
                .cardColors(

                    containerColor =
                        backgroundColor
                )

    ) {


        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        16.dp
                    ),

            verticalAlignment =
                Alignment.Top

        ) {


            // READ / UNREAD DOT
            Box(

                modifier =
                    Modifier
                        .padding(
                            top = 6.dp
                        )
                        .size(
                            10.dp
                        )
                        .background(

                            color =

                                if (
                                    notification.isRead
                                ) {

                                    MaterialTheme
                                        .colorScheme
                                        .outlineVariant

                                } else {

                                    MaterialTheme
                                        .colorScheme
                                        .primary
                                },

                            shape =
                                CircleShape
                        )
            )


            Spacer(

                modifier =
                    Modifier.width(
                        14.dp
                    )
            )


            Column(

                modifier =
                    Modifier.weight(
                        1f
                    )

            ) {


                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement
                            .SpaceBetween,

                    verticalAlignment =
                        Alignment.Top

                ) {


                    Text(

                        text =
                            notification.title,

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,

                        fontWeight =

                            if (
                                notification.isRead
                            ) {

                                FontWeight.Medium

                            } else {

                                FontWeight.Bold
                            },

                        modifier =
                            Modifier.weight(
                                1f
                            )
                    )


                    IconButton(

                        onClick =
                            onDelete,

                        modifier =
                            Modifier.size(
                                32.dp
                            )

                    ) {

                        Icon(

                            imageVector =
                                Icons.Default
                                    .Delete,

                            contentDescription =
                                "Delete notification",

                            modifier =
                                Modifier.size(
                                    18.dp
                                ),

                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
                }


                Spacer(

                    modifier =
                        Modifier.height(
                            4.dp
                        )
                )


                Text(

                    text =
                        notification.message,

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,

                    maxLines =
                        4,

                    overflow =
                        TextOverflow.Ellipsis
                )


                Spacer(

                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )


                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement
                            .SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {


                    NotificationTypeLabel(

                        type =
                            notification.type
                    )


                    Text(

                        text =
                            formatNotificationTime(
                                notification.createdAt
                            ),

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }
        }
    }
}


@Composable
private fun NotificationTypeLabel(
    type: String
) {


    val displayText =

        when (
            type.uppercase()
        ) {

            "ORDER_COLLECTED" ->
                "Order Collected"

            "PRICE_DROP" ->
                "Price Update"

            "ORDER_READY" ->
                "Order"

            "PICKUP_REMINDER" ->
                "Pickup"

            "NGO_PRE_ALERT" ->
                "NGO Alert"

            "DONATION_AVAILABLE" ->
                "Donation"

            else ->
                type
                    .replace(
                        "_",
                        " "
                    )
                    .lowercase()
                    .replaceFirstChar {

                        it.uppercase()
                    }
        }


    Box(

        modifier =
            Modifier
                .background(

                    color =
                        MaterialTheme
                            .colorScheme
                            .surfaceVariant,

                    shape =
                        RoundedCornerShape(
                            50.dp
                        )
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 5.dp
                )

    ) {

        Text(

            text =
                displayText,

            style =
                MaterialTheme
                    .typography
                    .labelMedium,

            fontWeight =
                FontWeight.SemiBold
        )
    }
}


@Composable
private fun EmptyNotificationScreen(

    modifier: Modifier =
        Modifier

) {

    Box(

        modifier =
            modifier
                .fillMaxSize(),

        contentAlignment =
            Alignment.Center

    ) {

        Column(

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {


            Text(

                text =
                    "No notifications",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(

                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            Text(

                text =
                    "New notifications will appear here.",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }
    }
}


@Composable
private fun NotificationErrorScreen(

    message: String,

    onRetry: () -> Unit,

    modifier: Modifier =
        Modifier

) {

    Box(

        modifier =
            modifier
                .fillMaxSize()
                .padding(
                    24.dp
                ),

        contentAlignment =
            Alignment.Center

    ) {

        Column(

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {


            Text(

                text =
                    "Unable to load notifications",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(

                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            Text(

                text =
                    message,

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )


            Spacer(

                modifier =
                    Modifier.height(
                        20.dp
                    )
            )


            Button(

                onClick =
                    onRetry

            ) {

                Text(
                    text =
                        "Retry"
                )
            }
        }
    }
}


private fun formatNotificationTime(
    timestamp: String?
): String {


    if (
        timestamp.isNullOrBlank()
    ) {

        return ""
    }


    return try {


        val dateTime =
            OffsetDateTime
                .parse(
                    timestamp
                )
                .atZoneSameInstant(
                    ZoneId.systemDefault()
                )


        dateTime.format(

            DateTimeFormatter
                .ofPattern(
                    "dd MMM, hh:mm a"
                )
        )


    } catch (
        e: Exception
    ) {

        timestamp
    }
}