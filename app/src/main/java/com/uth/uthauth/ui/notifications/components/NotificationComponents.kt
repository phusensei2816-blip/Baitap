package com.uth.uthauth.ui.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthauth.R
import com.uth.uthauth.ui.notifications.NotificationType
import com.uth.uthauth.ui.notifications.NotificationUi
import com.uth.uthauth.ui.theme.DangerRed
import com.uth.uthauth.ui.theme.DangerSoft
import com.uth.uthauth.ui.theme.SuccessGreen
import com.uth.uthauth.ui.theme.SuccessSoft
import com.uth.uthauth.ui.theme.UthBlue
import com.uth.uthauth.ui.theme.UthBlueSoft

private data class Visual(val icon: ImageVector, val container: Color, val tint: Color)

private fun NotificationType.visual() = when (this) {
    NotificationType.DUE_SOON, NotificationType.REMINDER -> Visual(Icons.Outlined.Alarm, UthBlueSoft, UthBlue)
    NotificationType.OVERDUE -> Visual(Icons.Outlined.Warning, DangerSoft, DangerRed)
    NotificationType.DONE -> Visual(Icons.Filled.Check, SuccessSoft, SuccessGreen)
}

/** Một thẻ trắng chứa toàn bộ danh sách, các dòng ngăn cách bằng đường kẻ mảnh. */
@Composable
fun NotificationList(items: List<NotificationUi>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        items.forEachIndexed { index, item ->
            if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            NotificationRow(item)
        }
    }
}

@Composable
private fun NotificationRow(item: NotificationUi) {
    val context = LocalContext.current
    val visual = item.type.visual()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(visual.container),
            contentAlignment = Alignment.Center
        ) {
            Icon(visual.icon, contentDescription = null, tint = visual.tint, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.title.asString(context),
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.time.asString(context),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!item.isRead) {
                    Spacer(Modifier.size(8.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
            }
            Text(
                text = item.message.asString(context),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** emptyView: hiện khi danh sách thông báo rỗng. */
@Composable
fun NotificationsEmptyView(
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(UthBlueSoft.copy(alpha = 0.6f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = UthBlue, modifier = Modifier.size(44.dp))
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 26.dp, bottom = 26.dp)
                    .size(28.dp)
                    .background(SuccessGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            stringResource(R.string.notif_empty_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.notif_empty_msg),
            modifier = Modifier.padding(horizontal = 32.dp),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onAddClick, shape = RoundedCornerShape(14.dp)) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(6.dp))
            Text(stringResource(R.string.notif_empty_add), fontWeight = FontWeight.SemiBold)
        }
        TextButton(onClick = onSettingsClick) {
            Text(stringResource(R.string.notif_empty_settings), fontWeight = FontWeight.SemiBold)
        }
    }
}
