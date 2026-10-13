package com.pratik.podcastplayer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val AppFont = FontFamily.SansSerif

val PodcastTypography = Typography(
    headlineLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    titleLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = AppFont,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = AppFont,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)