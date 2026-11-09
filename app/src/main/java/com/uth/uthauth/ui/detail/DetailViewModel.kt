package com.uth.uthauth.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.uthauth.data.remote.toLoadError
import com.uth.uthauth.data.task.TaskRepository
import com.uth.uthauth.data.telemetry.AppTelemetry
import com.uth.uthauth.domain.model.LoadState
import com.uth.uthauth.domain.model.Subtask
import com.uth.uthauth.domain.model.TaskChange
import com.uth.uthauth.domain.model.TodoTask
import com.uth.uthauth.ui.common.UiText
import com.uth.uthauth.ui.common.toUiText
import com.uth.uthauth.ui.navigation.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetailUiState(
    val task: TodoTask? = null,
    val loadState: LoadState = LoadState.Loading,
)

sealed interface DetailEvent {
    data class Message(val text: UiText) : DetailEvent
    data object Deleted : DetailEvent
}

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TaskRepository,
    private val telemetry: AppTelemetry,
) : ViewModel() {

    private val taskId: String = checkNotNull(savedStateHandle[Screen.Detail.ARG_TASK_ID])

    private val detailLoad = MutableStateFlow<LoadState>(LoadState.Loading)

    private val _events = Channel<DetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // Hiện ngay dữ liệu đã có trong bộ nhớ, rồi cập nhật khi GET /tasks/{id} trả về
    val uiState: StateFlow<DetailUiState> = combine(
        repository.observeTask(taskId),
        detailLoad
    ) { task, load -> DetailUiState(task, load) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailUiState())

    init {
        load()
    }

    /** GET /mock/teach/tasks/{id} – mở khi bấm một việc. */
    fun load() {
        viewModelScope.launch {
            detailLoad.value = LoadState.Loading
            detailLoad.value = repository.loadDetail(taskId).fold(
                onSuccess = { LoadState.Success },
                onFailure = { it.toLoadError() }
            )
        }
    }

    fun toggleDone() {
        val task = uiState.value.task ?: return
        if (!task.isDone) telemetry.logCompleteTask(task.category)
        patch(TaskChange(done = !task.isDone))
    }

    fun saveEdit(title: String, note: String) {
        patch(TaskChange(title = title.trim(), note = note.trim()))
    }

    fun toggleSubtask(index: Int) = changeSubtasks { list ->
        list.mapIndexed { i, s -> if (i == index) s.copy(done = !s.done) else s }
    }

    fun addSubtask(title: String) = changeSubtasks { it + Subtask(title.trim()) }

    fun renameSubtask(index: Int, title: String) = changeSubtasks { list ->
        list.mapIndexed { i, s -> if (i == index) s.copy(title = title.trim()) else s }
    }

    fun deleteSubtask(index: Int) = changeSubtasks { list ->
        list.filterIndexed { i, _ -> i != index }
    }

    /** DELETE /mock/teach/tasks/{id}. */
    fun delete() {
        viewModelScope.launch {
            repository.delete(taskId)
                .onSuccess {
                    telemetry.logDeleteTask()
                    _events.send(DetailEvent.Deleted)
                }
                .onFailure { _events.send(DetailEvent.Message(it.toLoadError().toUiText())) }
        }
    }

    private fun changeSubtasks(transform: (List<Subtask>) -> List<Subtask>) {
        val task = uiState.value.task ?: return
        patch(TaskChange(subtasks = transform(task.subtasks)))
    }

    /** PATCH /mock/teach/tasks/{id}: UI cập nhật ngay, lỗi thì hoàn tác và báo người dùng. */
    private fun patch(change: TaskChange) {
        viewModelScope.launch {
            repository.update(taskId, change)
                .onFailure { _events.send(DetailEvent.Message(it.toLoadError().toUiText())) }
        }
    }
}
