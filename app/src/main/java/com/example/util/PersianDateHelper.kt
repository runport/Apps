package com.example.util

import java.util.Calendar

/**
 * Persian (Jalali) date helper for automated date generation and period filtering
 */
object PersianDateHelper {

  private val persianMonths = listOf(
    "فروردین", "اردیبهشت", "خرداد",
    "تیر", "مرداد", "شهریور",
    "مهر", "آبان", "آذر",
    "دی", "بهمن", "اسفند"
  )

  /**
   * Returns current Persian date formatted like "۱۵ اسفند ۱۴۰۴"
   */
  fun getTodayPersianDate(): String {
    val cal = Calendar.getInstance()
    val gy = cal.get(Calendar.YEAR)
    val gm = cal.get(Calendar.MONTH) + 1
    val gd = cal.get(Calendar.DAY_OF_MONTH)

    val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
    val monthName = persianMonths.getOrElse(jm - 1) { "اسفند" }
    return "${toPersianDigits(jd)} $monthName ${toPersianDigits(jy)}"
  }

  /**
   * Returns a short Persian date like "۱۵ اسفند"
   */
  fun getTodayShortPersianDate(): String {
    val cal = Calendar.getInstance()
    val gy = cal.get(Calendar.YEAR)
    val gm = cal.get(Calendar.MONTH) + 1
    val gd = cal.get(Calendar.DAY_OF_MONTH)

    val (_, jm, jd) = gregorianToJalali(gy, gm, gd)
    val monthName = persianMonths.getOrElse(jm - 1) { "اسفند" }
    return "${toPersianDigits(jd)} $monthName"
  }

  fun toPersianDigits(number: Int): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val str = number.toString()
    val sb = StringBuilder()
    for (ch in str) {
      if (ch in '0'..'9') {
        sb.append(persianDigits[ch - '0'])
      } else {
        sb.append(ch)
      }
    }
    return sb.toString()
  }

  fun toPersianDigits(str: String): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val sb = StringBuilder()
    for (ch in str) {
      if (ch in '0'..'9') {
        sb.append(persianDigits[ch - '0'])
      } else {
        sb.append(ch)
      }
    }
    return sb.toString()
  }

  private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
    val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

    var gy2 = gy - 1600
    var gm2 = gm - 1
    var gd2 = gd - 1

    var gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400

    for (i in 0 until gm2) {
      gDayNo += gDaysInMonth[i]
    }
    if (gm2 > 1 && ((gy2 % 4 == 0 && gy2 % 100 != 0) || (gy2 % 400 == 0))) {
      gDayNo++
    }
    gDayNo += gd2

    var jDayNo = gDayNo - 79

    val jNp = jDayNo / 12053
    jDayNo %= 12053

    var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
    jDayNo %= 1461

    if (jDayNo >= 366) {
      jy += (jDayNo - 1) / 365
      jDayNo = (jDayNo - 1) % 365
    }

    var jm = 0
    for (i in 0..11) {
      if (jDayNo < jDaysInMonth[i]) {
        jm = i + 1
        break
      }
      jDayNo -= jDaysInMonth[i]
    }
    val jd = jDayNo + 1

    return Triple(jy, jm, jd)
  }
}
