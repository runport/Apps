package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun getTypographyWithFont(fontFamily: FontFamily): Typography {
  return Typography(
    displayLarge = TextStyle(
      fontFamily = fontFamily,
      fontWeight = FontWeight.ExtraBold,
      fontSize = 30.sp,
      lineHeight = 38.sp,
      letterSpacing = (-0.5).sp,
    ),
    displayMedium = TextStyle(
      fontFamily = fontFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 24.sp,
      lineHeight = 32.sp,
      letterSpacing = (-0.25).sp,
    ),
    titleLarge = TextStyle(
      fontFamily = fontFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 19.sp,
      lineHeight = 26.sp,
      letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
      fontFamily = fontFamily,
      fontWeight = FontWeight.SemiBold,
      fontSize = 16.sp,
      lineHeight = 23.sp,
      letterSpacing = 0.15.sp,
    ),
    titleSmall = TextStyle(
      fontFamily = fontFamily,
      fontWeight = FontWeight.Medium,
      fontSize = 14.sp,
      lineHeight = 20.sp,
      letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
      fontFamily = fontFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 14.sp,
      lineHeight = 21.sp,
      letterSpacing = 0.15.sp,
    ),
    bodyMedium = TextStyle(
      fontFamily = fontFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 13.sp,
      lineHeight = 19.sp,
      letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
      fontFamily = fontFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 11.sp,
      lineHeight = 16.sp,
      letterSpacing = 0.3.sp,
    ),
    labelLarge = TextStyle(
      fontFamily = fontFamily,
      fontWeight = FontWeight.SemiBold,
      fontSize = 13.sp,
      lineHeight = 18.sp,
      letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
      fontFamily = fontFamily,
      fontWeight = FontWeight.Medium,
      fontSize = 11.sp,
      lineHeight = 15.sp,
      letterSpacing = 0.4.sp,
    ),
    labelSmall = TextStyle(
      fontFamily = fontFamily,
      fontWeight = FontWeight.Medium,
      fontSize = 10.sp,
      lineHeight = 14.sp,
      letterSpacing = 0.4.sp,
    )
  )
}

val DefaultPersianFontFamily: FontFamily get() = PersianFont.YEKAN.getFontFamily()
val Typography = getTypographyWithFont(DefaultPersianFontFamily)
