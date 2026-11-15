package com.uth.uthauth.ui.notifications

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uth.uthauth.R
import com.uth.uthauth.ui.AppViewModelProvider
import com.uth.uthauth.ui.components.BottomTab
import com.uth.uthauth.ui.components.UthBottomBar
import com.uth.uthauth.ui.notifications.components.NotificationList
import com.uth.uthauth.ui.notifications.components.NotificationsEmptyView

@Composable
fun NotificationsRoute(
    onOpenHome: () -> Unit,
    onOpenList: () -> Unit,
    onOpenAdd: () -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: NotificationsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    NotificationsScreen(
        state = state,
        onMarkAllRead = viewModel::markAllRead,
        onAddClick = onOpenAdd,
        onReminderSettingsClick = onOpenProfile,
        onTabClick = { tab ->
            when (tab) {
                BottomTab.HOME -> onOpenHome()
                BottomTab.LIST -> onOpenList()
                BottomTab.NOTIFICATIONS -> Unit
                BottomTab.SETTINGS -> onOpenProfile()
            }
        },
    )
}

@Composable
fun NotificationsScreen(
    state: NotificationsUiState,
    onMarkAllRead: () -> Unit,
    onAddClick: () -> Unit,
    onReminderSettingsClick: () -> Unit,
    onTabClick: (BottomTab) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            UthBottomBar(
                selected = BottomTab.NOTIFICATIONS,
                onTabClick = onTabClick,
                onAddClick = onAddClick
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = inner.calculateTopPadding())
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.notif_title),
                    style = MaterialTheme.typography.headlineMedium
                )
                if (!state.isEmpty) {
                    TextButton(onClick = onMarkAllRead, enabled = state.unreadCount > 0) {
                        Text(
                            stringResource(R.string.notif_mark_read),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Danh sách rỗng -> emptyView (có hiệu ứng chuyển mờ)
            Crossfade(
                targetState = state.isEmpty,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                label = "notifications-content"
            ) { empty ->
                if (empty) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = inner.calculateBottomPadding()),
                        contentAlignment = Alignment.Center
                    ) {
                        NotificationsEmptyView(
                            onAddClick = onAddClick,
                            onSettingsClick = onReminderSettingsClick
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = inner.calculateBottomPadding() + 16.dp)
                    ) {
                        NotificationList(state.items)
                    }
                }
            }
        }
    }
}
