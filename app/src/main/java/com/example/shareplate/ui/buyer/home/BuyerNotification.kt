package com.example.shareplate.ui.buyer.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shareplate.data.model.Notification
import com.example.shareplate.ui.buyer.BuyerViewModel


@Composable
fun BuyerNotificationScreen(

    buyerViewModel: BuyerViewModel? = null,

    onBackClick: () -> Unit = {}

) {

    val isPreview = LocalInspectionMode.current


    val actualViewModel: BuyerViewModel? =

        if (isPreview) {

            null

        } else {

            buyerViewModel ?: viewModel()
        }


    val notificationState = actualViewModel?.notifications?.collectAsState()


    val unreadCountState = actualViewModel?.unreadNotificationCount?.collectAsState()


    val loadingState = actualViewModel?.isLoading?.collectAsState()


    val errorState = actualViewModel?.errorMessage?.collectAsState()


    val notifications =

        if (isPreview) {

            listOf(

                Notification(
                    notificationId = 1,
                    userId = "preview",
                    title = "Order Confirmed",
                    message = "Your order has been confirmed.",
                    type = "ORDER",
                    createdAt = "2026-09-04T10:30:00+00:00",
                    isRead = false
                ),

                Notification(
                    notificationId = 2,
                    userId = "preview",
                    title = "Pickup Completed",
                    message = "Your food pickup has been completed.",
                    type = "ORDER",
                    createdAt = "2026-09-04T09:20:00+00:00",
                    isRead = true
                )
            )

        } else {

            notificationState?.value ?: emptyList()
        }


    val unreadCount =

        if (isPreview) {

            1

        } else {

            unreadCountState?.value ?: 0
        }


    val isLoading =

        if (isPreview) {

            false

        } else {

            loadingState?.value ?: false
        }


    val errorMessage = errorState?.value


    // LOAD NOTIFICATIONS
    LaunchedEffect(
        actualViewModel
    ) {

        if (!isPreview) {

            actualViewModel?.loadNotifications()
        }
    }


    Scaffold(

        containerColor = Color(
            0xFFF8F8F8
        )

    ) { innerPadding ->


        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(
                    innerPadding
                )
        ) {


            // HEADER
            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White
                    )
                    .padding(
                        horizontal = 12.dp, vertical = 12.dp
                    ),

                verticalAlignment = Alignment.CenterVertically

            ) {


                IconButton(

                    onClick = onBackClick

                ) {

                    Icon(

                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,

                        contentDescription = "Back"
                    )
                }


                Text(

                    text = "Notifications",

                    fontSize = 20.sp,

                    fontWeight = FontWeight.Bold,

                    modifier = Modifier.weight(
                        1f
                    )
                )


                if (unreadCount > 0) {

                    TextButton(

                        onClick = {

                            actualViewModel?.markAllNotificationsAsRead()
                        }

                    ) {

                        Text(
                            text = "Mark all read"
                        )
                    }
                }
            }


            // CONTENT
            when {


                isLoading && notifications.isEmpty() -> {

                    Box(

                        modifier = Modifier.fillMaxSize(),

                        contentAlignment = Alignment.Center

                    ) {

                        CircularProgressIndicator()
                    }
                }


                errorMessage != null && notifications.isEmpty() -> {

                    Box(

                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                24.dp
                            ),

                        contentAlignment = Alignment.Center

                    ) {

                        Text(

                            text = errorMessage,

                            color = Color.Red
                        )
                    }
                }


                notifications.isEmpty() -> {

                    Box(

                        modifier = Modifier.fillMaxSize(),

                        contentAlignment = Alignment.Center

                    ) {

                        Column(

                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {

                            Text(

                                text = "No notifications yet",

                                fontSize = 17.sp,

                                fontWeight = FontWeight.SemiBold
                            )


                            Spacer(

                                modifier = Modifier.height(
                                    6.dp
                                )
                            )


                            Text(

                                text = "Your notifications will appear here.",

                                fontSize = 13.sp,

                                color = Color.Gray
                            )
                        }
                    }
                }


                else -> {

                    LazyColumn(

                        modifier = Modifier.fillMaxSize(),

                        contentPadding = PaddingValues(
                            16.dp
                        ),

                        verticalArrangement = Arrangement.spacedBy(
                            10.dp
                        )

                    ) {


                        item {

                            Text(

                                text =

                                    if (unreadCount > 0) {

                                        "$unreadCount unread notification" + if (unreadCount > 1) {
                                            "s"
                                        } else {
                                            ""
                                        }

                                    } else {

                                        "You're all caught up"
                                    },

                                fontSize = 13.sp,

                                color = Color.Gray,

                                modifier = Modifier.padding(
                                    bottom = 4.dp
                                )
                            )
                        }


                        items(

                            items = notifications,

                            key = {
                                it.notificationId
                            }

                        ) { notification ->


                            BuyerNotificationCard(

                                notification = notification,

                                onClick = {

                                    if (!notification.isRead) {

                                        actualViewModel?.markNotificationAsRead(
                                                notification.notificationId
                                            )
                                    }
                                })
                        }
                    }
                }
            }
        }
    }
}

// NOTIFICATION CARD
@Composable
private fun BuyerNotificationCard(

    notification: Notification,

    onClick: () -> Unit

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(
            14.dp
        ),

        colors = CardDefaults.cardColors(

            containerColor =

                if (notification.isRead) {

                    Color.White

                } else {

                    Color(
                        0xFFFFF8E7
                    )
                }
        )

    ) {


        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    16.dp
                ),

            verticalAlignment = Alignment.Top

        ) {


            // UNREAD / READ INDICATOR
            Box(

                modifier = Modifier
                    .padding(
                        top = 7.dp
                    )
                    .size(
                        10.dp
                    )
                    .background(

                        color =

                            if (notification.isRead) {

                                Color.LightGray

                            } else {

                                Color(
                                    0xFFFFB300
                                )
                            },

                        shape = CircleShape
                    )
            )


            Spacer(

                modifier = Modifier.size(
                    12.dp
                )
            )


            Column(

                modifier = Modifier.weight(
                    1f
                )

            ) {


                Text(

                    text = notification.title,

                    fontSize = 16.sp,

                    fontWeight =

                        if (notification.isRead) {

                            FontWeight.Medium

                        } else {

                            FontWeight.Bold
                        }
                )


                Spacer(

                    modifier = Modifier.height(
                        5.dp
                    )
                )


                Text(

                    text = notification.message,

                    style = MaterialTheme.typography.bodyMedium,

                    color = Color.DarkGray
                )


                if (!notification.createdAt.isNullOrBlank()) {

                    Spacer(

                        modifier = Modifier.height(
                            8.dp
                        )
                    )


                    Text(

                        text = formatNotificationDate(
                            notification.createdAt
                        ),

                        fontSize = 11.sp,

                        color = Color.Gray
                    )
                }
            }
        }
    }
}

// FORMAT DATE
private fun formatNotificationDate(
    date: String?
): String {

    if (date.isNullOrBlank()) {

        return ""
    }


    return try {

        date.replace(
                "T", " "
            ).take(
                16
            )

    } catch (
        e: Exception
    ) {

        date
    }
}