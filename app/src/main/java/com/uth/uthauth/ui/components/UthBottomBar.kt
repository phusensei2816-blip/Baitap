package com.uth.uthauth.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthauth.R

enum class BottomTab(@StringRes val labelRes: Int, val icon: ImageVector) {
    HOME(R.string.tab_home, Icons.Outlined.Home),
    LIST(R.string.tab_list, Icons.AutoMirrored.Outlined.List),
    NOTIFICATIONS(R.string.tab_notifications, Icons.Outlined.Notifications),
    SETTINGS(R.string.tab_settings, Icons.Outlined.Tune),
}

@Composable
fun UthBottomBar(
    selected: BottomTab,
    onTabClick: (BottomTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .padding(top = 28.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(64.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabItem(BottomTab.HOME, selected, onTabClick)
                TabItem(BottomTab.LIST, selected, onTabClick)
                Spacer(Modifier.weight(1f)) // chỗ cho nút +
                TabItem(BottomTab.NOTIFICATIONS, selected, onTabClick)
                TabItem(BottomTab.SETTINGS, selected, onTabClick)
            }
        }
        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier.align(Alignment.TopCenter),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_task_desc))
        }
    }
}

@Composable
private fun RowScope.TabItem(
    tab: BottomTab,
    selected: BottomTab,
    onClick: (BottomTab) -> Unit,
) {
    val color = if (tab == selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant
    val label = stringResource(tab.labelRes)
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable { onClick(tab) }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(tab.icon, contentDescription = label, tint = color, modifier = Modifier.size(22.dp))
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = if (tab == selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
