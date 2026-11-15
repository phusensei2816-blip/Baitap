package com.uth.uthauth.ui.add

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uth.uthauth.R
import com.uth.uthauth.domain.model.TaskCategory
import com.uth.uthauth.domain.model.TaskPriority
import com.uth.uthauth.ui.AppViewModelProvider
import com.uth.uthauth.ui.add.components.ChoiceCard
import com.uth.uthauth.ui.add.components.SectionLabel
import com.uth.uthauth.ui.add.components.SegmentedChoice
import com.uth.uthauth.ui.add.components.TimePickerDialog
import com.uth.uthauth.ui.add.components.TimeRemindCard
import com.uth.uthauth.ui.add.components.TitleNoteCard
import com.uth.uthauth.ui.common.labelRes

@Composable
fun AddTaskRoute(
    onBack: () -> Unit,
    viewModel: AddTaskViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentOnBack by rememberUpdatedState(onBack)
    val createdText = stringResource(R.string.add_created)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AddTaskEvent.Saved -> {
                    Toast.makeText(context, createdText, Toast.LENGTH_SHORT).show()
                    currentOnBack()
                }
                is AddTaskEvent.Error ->
                    Toast.makeText(context, event.message.asString(context), Toast.LENGTH_LONG).show()
            }
        }
    }

    AddTaskScreen(
        state = state,
        onCancel = onBack,
        onTitleChange = viewModel::onTitleChange,
        onNoteChange = viewModel::onNoteChange,
        onCategoryChange = viewModel::onCategoryChange,
        onDayChange = viewModel::onDayChange,
        onTimeChange = viewModel::onTimeChange,
        onRemindChange = viewModel::onRemindChange,
        onPriorityChange = viewModel::onPriorityChange,
        onSave = viewModel::save,
    )
}

@Composable
fun AddTaskScreen(
    state: AddTaskUiState,
    onCancel: () -> Unit,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onCategoryChange: (TaskCategory) -> Unit,
    onDayChange: (DayOption) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onRemindChange: (Boolean) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
    onSave: () -> Unit,
) {
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    val titleFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { titleFocus.requestFocus() }   // con trỏ nằm sẵn ở ô tên như slide

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .imePadding()
                .padding(horizontal = 20.dp)
        ) {
            // Thanh trên: Hủy | Việc mới | Lưu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onCancel) { Text(stringResource(R.string.add_cancel)) }
                Text(
                    stringResource(R.string.add_screen_title),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                TextButton(onClick = onSave, enabled = state.canSave) {
                    Text(stringResource(R.string.add_save), fontWeight = FontWeight.SemiBold)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TitleNoteCard(
                    title = state.title,
                    note = state.note,
                    onTitleChange = onTitleChange,
                    onNoteChange = onNoteChange,
                    titleFocus = titleFocus
                )

                SectionLabel(stringResource(R.string.add_group), Modifier.padding(top = 6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TaskCategory.entries.forEach { category ->
                        ChoiceCard(
                            text = stringResource(category.labelRes()),
                            selected = state.category == category,
                            onClick = { onCategoryChange(category) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                SectionLabel(stringResource(R.string.add_day), Modifier.padding(top = 6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(
                        DayOption.TODAY to R.string.due_today,
                        DayOption.TOMORROW to R.string.due_tomorrow,
                        DayOption.THIS_WEEK to R.string.due_this_week,
                    ).forEach { (option, labelRes) ->
                        ChoiceCard(
                            text = stringResource(labelRes),
                            selected = state.day == option,
                            onClick = { onDayChange(option) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                TimeRemindCard(
                    timeText = state.timeText,
                    onTimeClick = { showTimePicker = true },
                    remind = state.remind,
                    onRemindChange = onRemindChange,
                    modifier = Modifier.padding(top = 4.dp)
                )

                SectionLabel(stringResource(R.string.add_priority), Modifier.padding(top = 6.dp))
                SegmentedChoice(
                    options = listOf(
                        TaskPriority.LOW to stringResource(R.string.priority_short_low),
                        TaskPriority.MEDIUM to stringResource(R.string.priority_short_medium),
                        TaskPriority.HIGH to stringResource(R.string.priority_short_high),
                    ),
                    selected = state.priority,
                    onSelect = onPriorityChange
                )
                Spacer(Modifier.height(8.dp))
            }

            // Nút "Lưu công việc": xám khi chưa nhập tên, có vòng xoay khi đang gọi API
            Button(
                onClick = onSave,
                enabled = state.canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(stringResource(R.string.add_submit), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            hour = state.hour,
            minute = state.minute,
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                onTimeChange(hour, minute)
                showTimePicker = false
            }
        )
    }
}
