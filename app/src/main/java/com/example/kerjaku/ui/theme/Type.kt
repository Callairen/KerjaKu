package com.example.kerjaku.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.kerjaku.R

val Poppins = FontFamily(
    Font(R.font.poppins_thin, FontWeight.Thin),
    Font(R.font.poppins_extralight, FontWeight.ExtraLight),
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_extrabold, FontWeight.ExtraBold),
    Font(R.font.poppins_black, FontWeight.Black)
)

// Set Poppins as the default for all typography styles
val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = Poppins),
    displayMedium = TextStyle(fontFamily = Poppins),
    displaySmall = TextStyle(fontFamily = Poppins),
    headlineLarge = TextStyle(fontFamily = Poppins),
    headlineMedium = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 24.sp),
    headlineSmall = TextStyle(fontFamily = Poppins),
    titleLarge = TextStyle(fontFamily = Poppins),
    titleMedium = TextStyle(fontFamily = Poppins),
    titleSmall = TextStyle(fontFamily = Poppins),
    bodyLarge = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = Poppins),
    labelLarge = TextStyle(fontFamily = Poppins),
    labelMedium = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = Poppins)
)
