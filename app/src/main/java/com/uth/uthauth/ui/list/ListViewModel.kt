package com.uth.uthauth.ui.list

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.uthauth.R
import com.uth.uthauth.data.remote.toLoadError
import com.uth.uthauth.data.task.TaskRepository
import com.uth.uthauth.data.telemetry.AppTelemetry
import com.uth.uthauth.domain.model.LoadState
import com.uth.uthauth.domain.model.TaskCategory
import com.uth.uthauth.domain.model.TodoTask
import com.uth.uthauth.ui.common.UiText
import com.uth.uthauth.ui.common.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ListFilter(@StringRes val labelRes: Int, val category: TaskCategory?) {
    ALL(R.string.filter_all, null),
    STUDY(R.string.category_study, TaskCategory.STUDY),
    LIFE(R.string.category_life, TaskCategory.LIFE),
}

data class ListUiState(
    val query: String = "",
    val filter: ListFilter = ListFilter.ALL,
    val pending: List<TodoTask> = emptyList(),   // CẦN LÀM (đã lọc + tìm kiếm)
    val done: List<TodoTask> = emptyList(),      // ĐÃ XONG (đã lọc + tìm kiếm)
    val pendingCount: Int = 0,                   // tổng việc chưa xong (không phụ thuộc bộ lọc)
    val totalCount: Int = 0,
    val loadState: LoadState = LoadState.Loading,
)

class ListViewModel(
    private val repository: TaskRepository,
    private val telemetry: AppTelemetry,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(ListFilter.ALL)

    private val _events = Channel<UiText>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val uiState: StateFlow<ListUiState> = combine(
        query, filter, repository.tasks, repository.loadState
    ) { q, f, tasks, load ->
        val keyword = q.trim()
        val visible = tasks.filter { task ->
            (f.category == null || task.category == f.category) &&
                (keyword.isEmpty() ||
                    task.title.contains(keyword, ignoreCase = true) ||
                    task.note.contains(keyword, ignoreCase = true))
        }
        ListUiState(
            query = q,
            filter = f,
            pending = visible.filter { !it.isDone },
            done = visible.filter { it.isDone },
            pendingCount = tasks.count { !it.isDone },
            totalCount = tasks.size,
            loadState = load,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ListUiState()
    )

    init {
        refresh() // "Tải mọi việc khi mở màn hình"
    }

    fun refresh() {
        viewModelScope.launch { repository.refresh() }
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onFilterChange(value: ListFilter) {
        filter.value = value
    }

    fun toggleTask(task: TodoTask) {
        if (!task.isDone) telemetry.logCompleteTask(task.category)
        viewModelScope.launch {
            repository.setDone(task.id, !task.isDone)
                .onFailure { _events.send(it.toLoadError().toUiText()) }
        }
    }

    fun addTask(title: String, category: TaskCategory, time: String) {
        telemetry.logAddTask(category)
        viewModelScope.launch { repository.add(title, category, time) }
    }
}
