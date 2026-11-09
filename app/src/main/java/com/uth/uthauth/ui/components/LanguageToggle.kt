package com.uth.uthauth.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthauth.util.LanguageManager

/** Nút chuyển ngôn ngữ VI | EN. Đổi xong Activity tự tạo lại và toàn bộ chuỗi đổi theo. */
@Composable
fun LanguageToggle(modifier: Modifier = Modifier) {
    val isEnglish = LocalConfiguration.current.locales[0].language == LanguageManager.ENGLISH
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            .padding(3.dp)
    ) {
        LangOption("VI", selected = !isEnglish) { LanguageManager.set(LanguageManager.VIETNAMESE) }
        LangOption("EN", selected = isEnglish) { LanguageManager.set(LanguageManager.ENGLISH) }
    }
}

@Composable
private fun LangOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
    )
}
