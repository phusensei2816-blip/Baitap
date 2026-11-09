package com.uth.uthauth.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.uth.uthauth.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/** "Hôm nay, 14:00" / "Ngày mai, 21:00" / "Tuần này, 20:00" / "20/10/2026, 09:00". */
@Composable
fun dueText(date: String, time: String): String {
    val parsed = remember(date) { runCatching { LocalDate.parse(date) }.getOrNull() }
    val days = parsed?.let { ChronoUnit.DAYS.between(LocalDate.now(), it) }
    val label: String? = when {
        parsed == null || days == null -> null
        days == 0L -> stringResource(R.string.due_today)
        days == 1L -> stringResource(R.string.due_tomorrow)
        days in 2L..6L -> stringResource(R.string.due_this_week)
        else -> parsed.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }
    return if (label == null) time else stringResource(R.string.due_format, label, time)
}
