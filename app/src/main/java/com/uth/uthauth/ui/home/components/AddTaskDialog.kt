package com.uth.uthauth.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.uth.uthauth.R
import com.uth.uthauth.domain.model.TaskCategory
import com.uth.uthauth.ui.common.labelRes

private val TimeRegex = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: TaskCategory, time: String) -> Unit,
) {
    var title by rememberSaveable { mutableStateOf("") }
    var time by rememberSaveable { mutableStateOf("08:00") }
    var category by rememberSaveable { mutableStateOf(TaskCategory.STUDY) }

    val timeValid = TimeRegex.matches(time)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.add_name)) },
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskCategory.entries.forEach { c ->
                        FilterChip(
                            selected = category == c,
                            onClick = { category = c },
                            label = { Text(stringResource(c.labelRes())) }
                        )
                    }
                }
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it.take(5) },
                    label = { Text(stringResource(R.string.add_time)) },
                    singleLine = true,
                    isError = !timeValid,
                    supportingText = { if (!timeValid) Text(stringResource(R.string.add_time_error)) }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank() && timeValid,
                onClick = { onConfirm(title, category, time) }
            ) { Text(stringResource(R.string.add_confirm)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}
