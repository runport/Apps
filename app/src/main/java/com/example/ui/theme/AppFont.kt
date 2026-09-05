package com.example.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.R

enum class PersianFont(
  val code: String,
  val displayName: String,
  val description: String
) {
  YEKAN("yekan", "یکان (Yekan)", "قلم مدرن، شیک و خوانا متناسب با گوشی"),
  VAZIR("vazir", "وزیرمتن (Vazirmatn)", "قلم رسمی و استاندارد اپلیکیشن‌های برتر"),
  SHABNAM("shabnam", "شبنم (Shabnam)", "قلم فانتزی با انحناهای نرم و جذاب"),
  SYSTEM("system", "پیش‌فرض سیستم (System)", "فونت پیش‌فرض کارخانه سازنده گوشی");

  fun getFontFamily(): FontFamily {
    return when (this) {
      YEKAN -> FontFamily(
        Font(R.font.yekan, FontWeight.Normal),
        Font(R.font.yekan, FontWeight.Medium),
        Font(R.font.yekan, FontWeight.Bold),
        Font(R.font.yekan, FontWeight.ExtraBold),
        Font(R.font.yekan, FontWeight.Black)
      )
      VAZIR -> FontFamily(
        Font(R.font.vazirmatn, FontWeight.Normal),
        Font(R.font.vazirmatn, FontWeight.Medium),
        Font(R.font.vazirmatn, FontWeight.Bold)
      )
      SHABNAM -> FontFamily(
        Font(R.font.shabnam, FontWeight.Normal),
        Font(R.font.shabnam, FontWeight.Medium),
        Font(R.font.shabnam, FontWeight.Bold)
      )
      SYSTEM -> FontFamily.Default
    }
  }

  companion object {
    fun fromCode(code: String): PersianFont {
      return values().find { it.code.equals(code, ignoreCase = true) } ?: YEKAN
    }
  }
}
