package com.uth.uthauth.ui.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.uth.uthauth.R
import com.uth.uthauth.ui.theme.UthBlue
import com.uth.uthauth.ui.theme.UthBlueSoft

/**
 * Hiện lần lượt hộp thoại giải thích -> xin quyền hệ thống cho từng bước.
 * Bước nào đã được cấp sẵn thì tự bỏ qua. Khi hết bước gọi [onFinished].
 */
@Composable
fun PermissionFlow(onFinished: () -> Unit) {
    val context = LocalContext.current
    // Lưu "thứ tự bước kế tiếp" để xoay màn hình / đổi ngôn ngữ không bị mất tiến trình
    var cursor by rememberSaveable { mutableIntStateOf(0) }

    val step = PermissionStep.entries.firstOrNull {
        it.ordinal >= cursor && it.permissions.isNotEmpty() && !it.isGranted(context)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // Dù được cấp hay từ chối đều sang bước tiếp theo
        step?.let { cursor = it.ordinal + 1 }
    }

    LaunchedEffect(step) {
        if (step == null) onFinished()
    }

    step?.let {
        PermissionDialog(
            step = it,
            onAllow = { launcher.launch(it.permissions.toTypedArray()) },
            onSkip = { cursor = it.ordinal + 1 },
        )
    }
}

@Composable
private fun PermissionDialog(
    step: PermissionStep,
    onAllow: () -> Unit,
    onSkip: () -> Unit,
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(UthBlueSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(step.icon, contentDescription = null, tint = UthBlue, modifier = Modifier.size(44.dp))
            }
            Spacer(Modifier.height(20.dp))
            Text(
                stringResource(step.titleRes),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(step.messageRes),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onAllow,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50)
            ) { Text(stringResource(step.allowRes), fontWeight = FontWeight.SemiBold) }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50)
            ) { Text(stringResource(R.string.perm_skip), fontWeight = FontWeight.SemiBold) }
        }
    }
}
