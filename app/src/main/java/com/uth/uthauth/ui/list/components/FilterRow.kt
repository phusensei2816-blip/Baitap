package com.uth.uthauth.ui.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.uth.uthauth.ui.list.ListFilter
import com.uth.uthauth.ui.theme.TextPrimary

/** Chip lọc: Tất cả | Học tập | Cuộc sống. */
@Composable
fun FilterRow(
    selected: ListFilter,
    onSelect: (ListFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ListFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == selected,
                onClick = { onSelect(filter) },
                label = { Text(stringResource(filter.labelRes)) },
                shape = RoundedCornerShape(50),
                border = null,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    selectedContainerColor = TextPrimary,
                    selectedLabelColor = Color.White,
                )
            )
        }
    }
}
