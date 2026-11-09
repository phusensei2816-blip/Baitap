package com.uth.uthauth.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthauth.R
import com.uth.uthauth.domain.model.TaskSummary

@Composable
fun ProgressCard(summary: TaskSummary, modifier: Modifier = Modifier) {
    val messageRes = when {
        summary.total > 0 && summary.done == summary.total -> R.string.progress_msg_all
        summary.percent >= 50 -> R.string.progress_msg_half
        else -> R.string.progress_msg_start
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.progress_today),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
            Text(
                text = pluralStringResource(R.plurals.tasks_progress, summary.total, summary.done, summary.total),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(stringResource(messageRes), color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
        }
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { summary.progress },
                modifier = Modifier.size(60.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.25f),
                strokeWidth = 6.dp
            )
            Text(
                "${summary.percent}%",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
