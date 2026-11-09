package com.uth.uthauth.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthauth.R
import com.uth.uthauth.domain.model.TaskCategory
import com.uth.uthauth.ui.common.labelRes
import com.uth.uthauth.ui.theme.UthBlue
import com.uth.uthauth.ui.theme.UthBlueSoft
import com.uth.uthauth.ui.theme.WarningOrange
import com.uth.uthauth.ui.theme.WarningSoft

fun TaskCategory.accent(): Color = if (this == TaskCategory.STUDY) UthBlue else WarningOrange
fun TaskCategory.accentSoft(): Color = if (this == TaskCategory.STUDY) UthBlueSoft else WarningSoft

@Composable
fun CategoryCard(
    category: TaskCategory,
    remaining: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(category.accentSoft()),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (category == TaskCategory.STUDY) Icons.Outlined.School else Icons.Outlined.Home,
                contentDescription = null,
                tint = category.accent(),
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(stringResource(category.labelRes()), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Text(
            pluralStringResource(R.plurals.tasks_left, remaining, remaining),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
