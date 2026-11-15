package com.uth.uthauth.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.uthauth.R
import com.uth.uthauth.ui.common.UiText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NotificationType { DUE_SOON, OVERDUE, DONE, REMINDER }

/** Một thông báo trên giao diện. Văn bản là [UiText] để dịch theo ngôn ngữ hiện tại. */
data class NotificationUi(
    val id: Int,
    val type: NotificationType,
    val title: UiText,
    val message: UiText,
    val time: UiText,
    val isRead: Boolean = false,
)

data class NotificationsUiState(
    val items: List<NotificationUi> = emptyList(),
) {
    val unreadCount: Int get() = items.count { !it.isRead }
    /** Danh sách rỗng -> View hiển thị emptyView. */
    val isEmpty: Boolean get() = items.isEmpty()
}

/**
 * Thực hành MVVM (slide 51):
 *  1. ViewModel tạo một danh sách thông báo.
 *  2. Sau 10 giây, cho danh sách về rỗng -> màn hình hiển thị emptyView.
 */
class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState(items = sampleNotifications()))
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(CLEAR_AFTER_MS)
            _uiState.update { it.copy(items = emptyList()) }
        }
    }

    /** Nút "Đánh dấu đã đọc": bỏ chấm xanh của mọi thông báo. */
    fun markAllRead() {
        _uiState.update { state -> state.copy(items = state.items.map { it.copy(isRead = true) }) }
    }

    private fun sampleNotifications() = listOf(
        NotificationUi(
            id = 1,
            type = NotificationType.DUE_SOON,
            title = UiText.Res(R.string.notif_due_soon_title),
            message = UiText.Res(R.string.notif_due_soon_msg, "Ôn chương 3 – Cấu trúc dữ liệu", "14:00"),
            time = UiText.Plain("13:45"),
            isRead = false,
        ),
        NotificationUi(
            id = 2,
            type = NotificationType.OVERDUE,
            title = UiText.Res(R.string.notif_overdue_title),
            message = UiText.Res(R.string.notif_overdue_msg),
            time = UiText.Plain("10:10"),
            isRead = false,
        ),
        NotificationUi(
            id = 3,
            type = NotificationType.DONE,
            title = UiText.Res(R.string.notif_done_title),
            message = UiText.Res(R.string.notif_done_msg, 2),
            time = UiText.Plain("11:30"),
            isRead = true,
        ),
        NotificationUi(
            id = 4,
            type = NotificationType.REMINDER,
            title = UiText.Res(R.string.notif_reminder_title),
            message = UiText.Res(R.string.notif_reminder_msg),
            time = UiText.Res(R.string.notif_yesterday),
            isRead = true,
        ),
    )

    private companion object {
        const val CLEAR_AFTER_MS = 10_000L
    }
}
