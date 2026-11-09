package com.uth.uthauth.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthauth.R
import com.uth.uthauth.domain.model.LoadState
import com.uth.uthauth.ui.common.toUiText
import com.uth.uthauth.ui.theme.DangerRed
import com.uth.uthauth.ui.theme.DangerSoft

/** Hiện khi gọi API lỗi (mất mạng, 500, 401...) kèm nút Thử lại. */
@Composable
fun LoadErrorCard(
    error: LoadState.Error,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val message = error.toUiText().asString(LocalContext.current)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DangerSoft)
            .padding(start = 14.dp, top = 6.dp, bottom = 6.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(message, modifier = Modifier.weight(1f), color = DangerRed, fontSize = 12.sp)
        TextButton(onClick = onRetry) {
            Text(stringResource(R.string.retry), color = DangerRed, fontWeight = FontWeight.Bold)
        }
    }
}
