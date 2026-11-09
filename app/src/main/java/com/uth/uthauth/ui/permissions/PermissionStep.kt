package com.uth.uthauth.ui.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import com.uth.uthauth.R

/** Thứ tự khai báo = thứ tự hiển thị: Vị trí -> Thông báo -> Máy ảnh. */
enum class PermissionStep(
    val permissions: List<String>,
    @StringRes val titleRes: Int,
    @StringRes val messageRes: Int,
    @StringRes val allowRes: Int,
    val icon: ImageVector,
) {
    LOCATION(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        titleRes = R.string.perm_location_title,
        messageRes = R.string.perm_location_msg,
        allowRes = R.string.perm_allow,
        icon = Icons.Filled.LocationOn,
    ),
    NOTIFICATION(
        // Android 12 trở xuống không cần xin quyền này -> danh sách rỗng, bước sẽ bị bỏ qua
        permissions = if (Build.VERSION.SDK_INT >= 33) listOf(Manifest.permission.POST_NOTIFICATIONS) else emptyList(),
        titleRes = R.string.perm_notification_title,
        messageRes = R.string.perm_notification_msg,
        allowRes = R.string.perm_turn_on,
        icon = Icons.Filled.Notifications,
    ),
    CAMERA(
        permissions = listOf(Manifest.permission.CAMERA),
        titleRes = R.string.perm_camera_title,
        messageRes = R.string.perm_camera_msg,
        allowRes = R.string.perm_turn_on,
        icon = Icons.Filled.CameraAlt,
    );

    /** Chỉ cần được cấp một trong các quyền (vd. vị trí chính xác hoặc xấp xỉ). */
    fun isGranted(context: Context): Boolean = permissions.any {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}
