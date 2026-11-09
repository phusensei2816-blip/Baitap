package com.uth.uthauth.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthauth.R
import com.uth.uthauth.domain.model.DayStat

private val MaxBarHeight = 80.dp

@Composable
fun WeeklyChart(stats: List<DayStat>, modifier: Modifier = Modifier) {
    val max = (stats.maxOfOrNull { it.doneCount } ?: 0).coerceAtLeast(1)
    val primary = MaterialTheme.colorScheme.primary
    val labels = stringArrayResource(R.array.weekday_short)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(R.string.chart_title), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(
                stringResource(R.string.chart_subtitle),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            stats.forEach { day ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val barHeight = if (day.doneCount == 0) 3.dp else MaxBarHeight * (day.doneCount / max.toFloat())
                    val color = when {
                        day.isToday -> primary
                        day.doneCount > 0 -> primary.copy(alpha = 0.35f)
                        else -> MaterialTheme.colorScheme.outline
                    }
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .height(barHeight)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        labels.getOrElse(day.dayIndex) { "" },
                        fontSize = 10.sp,
                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
