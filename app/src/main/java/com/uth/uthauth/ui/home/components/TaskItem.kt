package com.uth.uthauth.ui.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthauth.R
import com.uth.uthauth.domain.model.TaskPriority
import com.uth.uthauth.domain.model.TodoTask
import com.uth.uthauth.ui.common.dueText
import com.uth.uthauth.ui.common.labelRes
import com.uth.uthauth.ui.theme.DangerRed
import com.uth.uthauth.ui.theme.DangerSoft

/**
 * @param onToggle bấm vào vòng tròn: đổi trạng thái xong (PATCH)
 * @param onClick bấm vào phần còn lại: mở màn Chi tiết
 * @param detailed true (màn Danh sách): hiện cả ngày hạn và nhãn "Gấp"; false (Trang chủ): chỉ hiện giờ
 */
@Composable
fun TaskItem(
    task: TodoTask,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    detailed: Boolean = false,
) {
    val primary = MaterialTheme.colorScheme.primary
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .clickable(onClick = onToggle)
                .then(
                    if (task.isDone) Modifier.background(primary)
                    else Modifier.border(BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline), CircleShape)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (task.isDone) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = stringResource(R.string.done_desc),
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = task.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (task.isDone) muted else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(task.category.labelRes()),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(task.category.accentSoft())
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = task.category.accent()
                )
                Text(
                    text = if (detailed) dueText(task.date, task.time) else task.time,
                    fontSize = 11.sp,
                    color = muted
                )
            }
        }

        if (detailed && task.priority == TaskPriority.HIGH && !task.isDone) {
            Text(
                text = stringResource(R.string.priority_badge_urgent),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DangerSoft)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = DangerRed
            )
        } else if (!detailed) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = muted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
