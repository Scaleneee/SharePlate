package com.example.shareplate.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Theme Colour
 */
val SharePlateBlack = Color(0xFF111111)
val SharePlateWhite = Color(0xFFFFFFFF)
val SharePlateBackground = Color(0xFFF7F7F7)
val SharePlateSurface = Color(0xFFFFFFFF)
val SharePlateGrey = Color(0xFF6B6B6B)
val SharePlateLightGrey = Color(0xFFEAEAEA)
val SharePlateBorder = Color(0xFFD8D8D8)

/**
 * Status Colour
 */
val StatusGreen = Color(0xFF2E7D32)
val StatusOrange = Color(0xFFF57C00)
val StatusRed = Color(0xFFD32F2F)
val StatusBlue = Color(0xFF1976D2)

fun getListingStatusColor(
    status: String,
    discountPercent: Int
): Color {
    return when {
        status == "TRANSFERRED_TO_NGO" -> StatusBlue
        status == "SOLD_OUT" -> SharePlateGrey
        status == "COMPLETED" -> SharePlateGrey
        discountPercent >= 80 -> StatusRed
        discountPercent >= 70 -> StatusOrange
        else -> StatusGreen
    }
}