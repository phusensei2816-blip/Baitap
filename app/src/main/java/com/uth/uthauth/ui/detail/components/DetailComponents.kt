package com.uth.uthauth.ui.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthauth.R
import com.uth.uthauth.domain.model.Subtask
import com.uth.uthauth.domain.model.TaskPriority
import com.uth.uthauth.ui.common.labelRes
import com.uth.uthauth.ui.theme.DangerRed
import com.uth.uthauth.ui.theme.DangerSoft
import com.uth.uthauth.ui.theme.SuccessGreen
import com.uth.uthauth.ui.theme.SuccessSoft

@Composable
fun TagChip(text: String, container: Color, content: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(container)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        color = content,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun PriorityChip(priority: TaskPriority) {
    val (container, content) = when (priority) {
        TaskPriority.HIGH -> DangerSoft to DangerRed
        TaskPriority.MEDIUM -> MaterialTheme.colorScheme.outline.copy(alpha = 0.6f) to
            MaterialTheme.colorScheme.onSurfaceVariant
        TaskPriority.LOW -> SuccessSoft to SuccessGreen
    }
    TagChip(stringResource(priority.labelRes()), container, content)
}

/** Thẻ trắng: Hạn chót + Nhắc nhở. */
@Composable
fun InfoCard(due: String, remind: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        InfoRow(Icons.Outlined.CalendarToday, stringResource(R.string.detail_due), due)
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        InfoRow(
            Icons.Outlined.Notifications,
            stringResource(R.string.detail_remind),
            stringResource(if (remind) R.string.detail_remind_15 else R.string.detail_remind_off)
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun NoteCard(note: String, modifier: Modifier = Modifier) {
    Text(
        text = note.ifBlank { stringResource(R.string.detail_note_empty) },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
        fontSize = 13.sp,
        color = if (note.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
        else MaterialTheme.colorScheme.onSurface
    )
}

/** Danh sách việc nhỏ: tick, sửa, xóa, thêm mới. */
@Composable
fun SubtaskCard(
    subtasks: List<Subtask>,
    onToggle: (Int) -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onAdd: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        subtasks.forEachIndexed { index, subtask ->
            SubtaskRow(
                subtask = subtask,
                onToggle = { onToggle(index) },
                onEdit = { onEdit(index) },
                onDelete = { onDelete(index) }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        }
        AddSubtaskRow(onAdd)
    }
}

@Composable
private fun SubtaskRow(
    subtask: Subtask,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val primary = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onToggle)
                .then(
                    if (subtask.done) Modifier.background(primary)
                    else Modifier.border(BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline), RoundedCornerShape(6.dp))
                ),
            contentAlignment = Alignment.Center
        ) {
            if (subtask.done) {
                Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.done_desc), tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = subtask.title,
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color = if (subtask.done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            textDecoration = if (subtask.done) TextDecoration.LineThrough else TextDecoration.None
        )
        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
            Icon(
                Icons.Outlined.Edit,
                contentDescription = stringResource(R.string.subtask_edit_desc),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
            Icon(
                Icons.Outlined.Delete,
                contentDescription = stringResource(R.string.subtask_delete_desc),
                tint = DangerRed,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun AddSubtaskRow(onAdd: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(R.string.detail_add_subtask), fontSize = 13.sp) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
        TextButton(
            enabled = text.isNotBlank(),
            onClick = {
                onAdd(text)
                text = ""
            }
        ) { Text(stringResource(R.string.add_confirm), fontWeight = FontWeight.SemiBold) }
    }
}
