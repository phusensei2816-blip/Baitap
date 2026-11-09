package com.uth.uthauth.ui.list

import android.widget.Toast
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
import androidx.compose.ui.res.pluralStringResource
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
import com.uth.uthauth.ui.components.BottomTab
import com.uth.uthauth.ui.components.UthBottomBar
import com.uth.uthauth.ui.home.components.AddTaskDialog
import com.uth.uthauth.ui.home.components.LoadErrorCard
import com.uth.uthauth.ui.home.components.TaskItem
import com.uth.uthauth.ui.home.components.TaskSkeleton
import com.uth.uthauth.ui.list.components.FilterRow
import com.uth.uthauth.ui.list.components.SearchField

@Composable
fun ListRoute(
    onOpenTask: (String) -> Unit,
    onOpenHome: () -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    val comingSoonText = stringResource(R.string.coming_soon)

    LaunchedEffect(Unit) {
        viewModel.events.collect { Toast.makeText(context, it.asString(context), Toast.LENGTH_SHORT).show() }
    }

    ListScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onFilterChange = viewModel::onFilterChange,
        onToggleTask = viewModel::toggleTask,
        onOpenTask = onOpenTask,
        onAddClick = { showAddDialog = true },
        onTabClick = { tab ->
            when (tab) {
                BottomTab.HOME -> onOpenHome()
                BottomTab.LIST -> Unit
                BottomTab.SETTINGS -> onOpenProfile()
                BottomTab.NOTIFICATIONS ->
                    Toast.makeText(context, comingSoonText, Toast.LENGTH_SHORT).show()
            }
        },
        onRetry = viewModel::refresh,
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
fun ListScreen(
    state: ListUiState,
    onQueryChange: (String) -> Unit,
    onFilterChange: (ListFilter) -> Unit,
    onToggleTask: (TodoTask) -> Unit,
    onOpenTask: (String) -> Unit,
    onAddClick: () -> Unit,
    onTabClick: (BottomTab) -> Unit,
    onRetry: () -> Unit,
) {
    val loadState = state.loadState
    val noVisible = state.pending.isEmpty() && state.done.isEmpty()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            UthBottomBar(
                selected = BottomTab.LIST,
                onTabClick = onTabClick,
                onAddClick = onAddClick
            )
        }
    ) { inner ->
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.list_title),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            pluralStringResource(R.plurals.tasks_pending, state.pendingCount, state.pendingCount),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                item { SearchField(state.query, onQueryChange) }
                item { FilterRow(state.filter, onFilterChange) }

                if (loadState is LoadState.Error) {
                    item { LoadErrorCard(loadState, onRetry) }
                }
                if (loadState is LoadState.Loading && state.totalCount == 0) {
                    items(4) { TaskSkeleton() }
                }

                if (state.pending.isNotEmpty()) {
                    item { SectionLabel(stringResource(R.string.section_todo)) }
                    items(state.pending, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            detailed = true,
                            onToggle = { onToggleTask(task) },
                            onClick = { onOpenTask(task.id) }
                        )
                    }
                }
                if (state.done.isNotEmpty()) {
                    item { SectionLabel(stringResource(R.string.section_done)) }
                    items(state.done, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            detailed = true,
                            onToggle = { onToggleTask(task) },
                            onClick = { onOpenTask(task.id) }
                        )
                    }
                }

                if (noVisible && loadState is LoadState.Success) {
                    item {
                        Text(
                            stringResource(
                                if (state.totalCount == 0) R.string.empty_tasks else R.string.list_no_results
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(top = 6.dp),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
