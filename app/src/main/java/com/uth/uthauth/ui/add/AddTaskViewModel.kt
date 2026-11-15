package com.uth.uthauth.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.uthauth.data.remote.toLoadError
import com.uth.uthauth.data.task.TaskRepository
import com.uth.uthauth.data.telemetry.AppTelemetry
import com.uth.uthauth.domain.model.NewTask
import com.uth.uthauth.domain.model.TaskCategory
import com.uth.uthauth.domain.model.TaskPriority
import com.uth.uthauth.ui.common.UiText
import com.uth.uthauth.ui.common.toUiText
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Ba lựa chọn "Ngày" trên màn hình. */
enum class DayOption {
    TODAY, TOMORROW, THIS_WEEK;

    fun toDate(today: LocalDate = LocalDate.now()): LocalDate = when (this) {
        TODAY -> today
        TOMORROW -> today.plusDays(1)
        THIS_WEEK -> {
            // Chủ nhật của tuần này; nếu quá gần (hôm nay/ngày mai) thì lấy hôm nay + 2 ngày
            val sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            if (sunday.isAfter(today.plusDays(1))) sunday else today.plusDays(2)
        }
    }
}

/** Toàn bộ trạng thái của form: View chỉ việc vẽ lại theo state này. */
data class AddTaskUiState(
    val title: String = "",
    val note: String = "",
    val category: TaskCategory = TaskCategory.STUDY,
    val day: DayOption = DayOption.TODAY,
    val hour: Int = 19,
    val minute: Int = 0,
    val remind: Boolean = true,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isSaving: Boolean = false,
) {
    val timeText: String get() = String.format(Locale.US, "%02d:%02d", hour, minute)
    /** Nút Lưu chỉ bật khi đã nhập tên và không đang gọi API. */
    val canSave: Boolean get() = title.isNotBlank() && !isSaving
}

sealed interface AddTaskEvent {
    data object Saved : AddTaskEvent
    data class Error(val message: UiText) : AddTaskEvent
}

class AddTaskViewModel(
    private val repository: TaskRepository,
    private val telemetry: AppTelemetry,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()

    private val _events = Channel<AddTaskEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }
    fun onNoteChange(value: String) = _uiState.update { it.copy(note = value) }
    fun onCategoryChange(value: TaskCategory) = _uiState.update { it.copy(category = value) }
    fun onDayChange(value: DayOption) = _uiState.update { it.copy(day = value) }
    fun onTimeChange(hour: Int, minute: Int) = _uiState.update { it.copy(hour = hour, minute = minute) }
    fun onRemindChange(value: Boolean) = _uiState.update { it.copy(remind = value) }
    fun onPriorityChange(value: TaskPriority) = _uiState.update { it.copy(priority = value) }

    /** Gọi POST /mock/teach/tasks. Thành công -> báo View đóng màn hình; lỗi -> giữ nguyên form. */
    fun save() {
        val s = _uiState.value
        if (!s.canSave) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = repository.create(
                NewTask(
                    title = s.title.trim(),
                    note = s.note.trim(),
                    category = s.category,
                    date = s.day.toDate().toString(),
                    time = s.timeText,
                    priority = s.priority,
                    remind = s.remind,
                )
            )
            _uiState.update { it.copy(isSaving = false) }

            result
                .onSuccess {
                    telemetry.logAddTask(s.category)
                    _events.send(AddTaskEvent.Saved)
                }
                .onFailure { _events.send(AddTaskEvent.Error(it.toLoadError().toUiText())) }
        }
    }
}
