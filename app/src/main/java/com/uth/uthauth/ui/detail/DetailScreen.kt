package com.uth.uthauth.ui.detail

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.uth.uthauth.domain.model.TodoTask
import com.uth.uthauth.ui.AppViewModelProvider
import com.uth.uthauth.ui.common.dueText
import com.uth.uthauth.ui.common.labelRes
import com.uth.uthauth.ui.detail.components.EditTaskDialog
import com.uth.uthauth.ui.detail.components.InfoCard
import com.uth.uthauth.ui.detail.components.NoteCard
import com.uth.uthauth.ui.detail.components.PriorityChip
import com.uth.uthauth.ui.detail.components.SectionTitle
import com.uth.uthauth.ui.detail.components.SubtaskCard
import com.uth.uthauth.ui.detail.components.SubtaskDialog
import com.uth.uthauth.ui.detail.components.TagChip
import com.uth.uthauth.ui.home.components.LoadErrorCard
import com.uth.uthauth.ui.home.components.accent
import com.uth.uthauth.ui.home.components.accentSoft
import com.uth.uthauth.ui.theme.DangerRed

@Composable
fun DetailRoute(
    onBack: () -> Unit,
    viewModel: DetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentOnBack by rememberUpdatedState(onBack)

    var showEdit by rememberSaveable { mutableStateOf(false) }
    var showDelete by rememberSaveable { mutableStateOf(false) }
    var editingSubtask by rememberSaveable { mutableStateOf<Int?>(null) }
    val deletedText = stringResource(R.string.detail_deleted)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DetailEvent.Message ->
                    Toast.makeText(context, event.text.asString(context), Toast.LENGTH_SHORT).show()
                DetailEvent.Deleted -> {
                    Toast.makeText(context, deletedText, Toast.LENGTH_SHORT).show()
                    currentOnBack()
                }
            }
        }
    }

    DetailScreen(
        state = state,
        onBack = onBack,
        onEditClick = { showEdit = true },
        onRetry = viewModel::load,
        onToggleDone = viewModel::toggleDone,
        onDeleteClick = { showDelete = true },
        onToggleSubtask = viewModel::toggleSubtask,
        onEditSubtask = { editingSubtask = it },
        onDeleteSubtask = viewModel::deleteSubtask,
        onAddSubtask = viewModel::addSubtask,
    )

    val task = state.task
    if (showEdit && task != null) {
        EditTaskDialog(
            initialTitle = task.title,
            initialNote = task.note,
            onDismiss = { showEdit = false },
            onSave = { title, note ->
                viewModel.saveEdit(title, note)
                showEdit = false
            }
        )
    }
    editingSubtask?.let { index ->
        task?.subtasks?.getOrNull(index)?.let { subtask ->
            SubtaskDialog(
                initialTitle = subtask.title,
                onDismiss = { editingSubtask = null },
                onSave = {
                    viewModel.renameSubtask(index, it)
                    editingSubtask = null
                }
            )
        }
    }
    if (showDelete && task != null) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text(stringResource(R.string.detail_delete_title)) },
            text = { Text(stringResource(R.string.detail_delete_msg, task.title)) },
            confirmButton = {
                TextButton(onClick = {
                    showDelete = false
                    viewModel.delete()
                }) { Text(stringResource(R.string.detail_delete), color = DangerRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
fun DetailScreen(
    state: DetailUiState,
    onBack: () -> Unit,
    onEditClick: () -> Unit,
    onRetry: () -> Unit,
    onToggleDone: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleSubtask: (Int) -> Unit,
    onEditSubtask: (Int) -> Unit,
    onDeleteSubtask: (Int) -> Unit,
    onAddSubtask: (String) -> Unit,
) {
    val task = state.task
    val loadState = state.loadState

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 20.dp)
        ) {
            DetailTopBar(onBack = onBack, onEditClick = onEditClick, editEnabled = task != null)

            // Thanh tiến trình mảnh khi đang gọi GET /tasks/{id}
            if (loadState is LoadState.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            when {
                task != null -> TaskContent(
                    task = task,
                    loadState = loadState,
                    onRetry = onRetry,
                    onToggleDone = onToggleDone,
                    onDeleteClick = onDeleteClick,
                    onToggleSubtask = onToggleSubtask,
                    onEditSubtask = onEditSubtask,
                    onDeleteSubtask = onDeleteSubtask,
                    onAddSubtask = onAddSubtask,
                    modifier = Modifier.weight(1f)
                )
                loadState is LoadState.Error -> Box(Modifier.weight(1f).padding(top = 16.dp)) {
                    LoadErrorCard(loadState, onRetry)
                }
                loadState is LoadState.Loading -> Box(Modifier.weight(1f))
                else -> Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(R.string.detail_not_found),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailTopBar(onBack: () -> Unit, onEditClick: () -> Unit, editEnabled: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onBack)
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_desc),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.size(4.dp))
            Text(stringResource(R.string.back_desc), color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
        }
        TextButton(onClick = onEditClick, enabled = editEnabled) {
            Text(stringResource(R.string.detail_edit), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun TaskContent(
    task: TodoTask,
    loadState: LoadState,
    onRetry: () -> Unit,
    onToggleDone: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleSubtask: (Int) -> Unit,
    onEditSubtask: (Int) -> Unit,
    onDeleteSubtask: (Int) -> Unit,
    onAddSubtask: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (loadState is LoadState.Error) {
                LoadErrorCard(loadState, onRetry)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagChip(
                    text = stringResource(task.category.labelRes()),
                    container = task.category.accentSoft(),
                    content = task.category.accent()
                )
                PriorityChip(task.priority)
            }
            Text(
                text = task.title,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Bold
            )

            InfoCard(due = dueText(task.date, task.time), remind = task.remind)

            SectionTitle(stringResource(R.string.detail_note), Modifier.padding(top = 6.dp))
            NoteCard(task.note)

            val doneCount = task.subtasks.count { it.done }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SectionTitle(stringResource(R.string.detail_subtasks))
                SectionTitle("$doneCount/${task.subtasks.size}")
            }
            LinearProgressIndicator(
                progress = {
                    if (task.subtasks.isEmpty()) 0f else doneCount.toFloat() / task.subtasks.size
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(50)),
            )
            SubtaskCard(
                subtasks = task.subtasks,
                onToggle = onToggleSubtask,
                onEdit = onEditSubtask,
                onDelete = onDeleteSubtask,
                onAdd = onAddSubtask
            )
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = onToggleDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                stringResource(if (task.isDone) R.string.detail_mark_undone else R.string.detail_mark_done),
                fontWeight = FontWeight.SemiBold
            )
        }
        TextButton(
            onClick = onDeleteClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(stringResource(R.string.detail_delete), color = DangerRed, fontWeight = FontWeight.SemiBold)
        }
    }
}
