package com.uth.uthauth.ui.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uth.uthauth.R
import com.uth.uthauth.domain.model.LoadState
import com.uth.uthauth.domain.model.TaskCategory
import com.uth.uthauth.domain.model.TodoTask
import com.uth.uthauth.ui.AppViewModelProvider
import com.uth.uthauth.ui.components.AdBanner
import com.uth.uthauth.ui.components.BottomTab
import com.uth.uthauth.ui.components.UthBottomBar
import com.uth.uthauth.ui.home.components.AddTaskDialog
import com.uth.uthauth.ui.home.components.CategoryCard
import com.uth.uthauth.ui.home.components.HomeHeader
import com.uth.uthauth.ui.home.components.LoadErrorCard
import com.uth.uthauth.ui.home.components.ProgressCard
import com.uth.uthauth.ui.home.components.PromoBanner
import com.uth.uthauth.ui.home.components.TaskItem
import com.uth.uthauth.ui.home.components.TaskSkeleton

@Composable
fun HomeRoute(
    onOpenProfile: () -> Unit,
    onOpenList: () -> Unit,
    onOpenTask: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    val comingSoonText = stringResource(R.string.coming_soon)
    val comingSoon = { Toast.makeText(context, comingSoonText, Toast.LENGTH_SHORT).show() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { Toast.makeText(context, it.asString(context), Toast.LENGTH_SHORT).show() }
    }

    HomeScreen(
        state = state,
        onToggleTask = viewModel::toggleTask,
        onAddClick = { showAddDialog = true },
        onAvatarClick = onOpenProfile,
        onTabClick = { tab ->
            when (tab) {
                BottomTab.HOME -> Unit
                BottomTab.LIST -> onOpenList()
                BottomTab.SETTINGS -> onOpenProfile()
                BottomTab.NOTIFICATIONS -> comingSoon()
            }
        },
        onComingSoon = comingSoon,
        onRetry = viewModel::refresh,
        onOpenTask = onOpenTask,
    )

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, category: TaskCategory, time ->
                viewModel.addTask(title, category, time)
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onToggleTask: (TodoTask) -> Unit,
    onAddClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onTabClick: (BottomTab) -> Unit,
    onComingSoon: () -> Unit,
    onRetry: () -> Unit,
    onOpenTask: (String) -> Unit,
) {
    val loadState = state.loadState

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            UthBottomBar(
                selected = BottomTab.HOME,
                onTabClick = onTabClick,
                onAddClick = onAddClick
            )
        }
    ) { inner ->
        // Kéo xuống để gọi lại API
        PullToRefreshBox(
            isRefreshing = loadState is LoadState.Loading,
            onRefresh = onRetry,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = inner.calculateTopPadding())
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 12.dp,
                    bottom = inner.calculateBottomPadding() + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    HomeHeader(
                        user = state.user,
                        onAvatarClick = onAvatarClick,
                        onBellClick = onComingSoon
                    )
                }
                item { ProgressCard(state.summary, Modifier.padding(top = 6.dp)) }
                state.config.welcomeMessage?.let { message ->
                    item { PromoBanner(message) }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CategoryCard(TaskCategory.STUDY, state.studyRemaining, Modifier.weight(1f))
                        CategoryCard(TaskCategory.LIFE, state.lifeRemaining, Modifier.weight(1f))
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.section_today),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            stringResource(R.string.see_all),
                            modifier = Modifier.clickable(onClick = onComingSoon),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Lỗi API: hiện thẻ báo lỗi + nút Thử lại
                if (loadState is LoadState.Error) {
                    item { LoadErrorCard(loadState, onRetry) }
                }
                // Đang tải lần đầu: khung xám nhấp nháy
                if (loadState is LoadState.Loading && state.tasks.isEmpty()) {
                    items(3) { TaskSkeleton() }
                }
                // Tải xong nhưng không có việc nào
                if (loadState is LoadState.Success && state.tasks.isEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.empty_tasks),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                items(state.tasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onToggle = { onToggleTask(task) },
                        onClick = { onOpenTask(task.id) }
                    )
                }
                if (state.config.showAds) {
                    item { AdBanner(Modifier.padding(top = 8.dp)) }
                }
            }
        }
    }
}
