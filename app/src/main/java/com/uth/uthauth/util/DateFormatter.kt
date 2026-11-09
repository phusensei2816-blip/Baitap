package com.uth.uthauth.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatter {
    /** vi: "Thứ Ba, 20 tháng 10" – en: "Tuesday, October 20". */
    fun today(locale: Locale, date: LocalDate = LocalDate.now()): String =
        if (locale.language == "vi") vietnamese(date)
        else date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", locale))

    private fun vietnamese(date: LocalDate): String {
        val day = when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "Thứ Hai"
            DayOfWeek.TUESDAY -> "Thứ Ba"
            DayOfWeek.WEDNESDAY -> "Thứ Tư"
            DayOfWeek.THURSDAY -> "Thứ Năm"
            DayOfWeek.FRIDAY -> "Thứ Sáu"
            DayOfWeek.SATURDAY -> "Thứ Bảy"
            DayOfWeek.SUNDAY -> "Chủ nhật"
        }
        return "$day, ${date.dayOfMonth} tháng ${date.monthValue}"
    }
}
