package com.uth.uthauth.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.uthauth.data.auth.AuthRepository
import com.uth.uthauth.data.config.AppConfig
import com.uth.uthauth.data.config.RemoteConfigRepository
import com.uth.uthauth.data.presence.UserPresenceRepository
import com.uth.uthauth.data.remote.toLoadError
import com.uth.uthauth.data.task.TaskRepository
import com.uth.uthauth.data.telemetry.AppTelemetry
import com.uth.uthauth.domain.model.AuthUser
import com.uth.uthauth.domain.model.LoadState
import com.uth.uthauth.domain.model.TaskCategory
import com.uth.uthauth.domain.model.TaskSummary
import com.uth.uthauth.domain.model.TodoTask
import com.uth.uthauth.push.PushRegistrar
import com.uth.uthauth.ui.common.UiText
import com.uth.uthauth.ui.common.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val user: AuthUser? = null,
    val tasks: List<TodoTask> = emptyList(),
    val summary: TaskSummary = TaskSummary(0, 0),
    val studyRemaining: Int = 0,
    val lifeRemaining: Int = 0,
    val config: AppConfig = AppConfig(),
    val loadState: LoadState = LoadState.Loading,
)

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository,
    private val telemetry: AppTelemetry,
    private val remoteConfig: RemoteConfigRepository,
    private val presence: UserPresenceRepository,
    private val push: PushRegistrar,
) : ViewModel() {

    private val config = MutableStateFlow(remoteConfig.current())

    private val _events = Channel<UiText>(Channel.BUFFERED)
    /** Thông báo lỗi một lần (vd. PATCH thất bại). */
    val events = _events.receiveAsFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        authRepository.observeUser(),
        taskRepository.tasks,
        config,
        taskRepository.loadState
    ) { user, tasks, cfg, loadState ->
        HomeUiState(
            user = user,
            tasks = tasks,
            summary = TaskSummary(total = tasks.size, done = tasks.count { it.isDone }),
            studyRemaining = tasks.count { it.category == TaskCategory.STUDY && !it.isDone },
            lifeRemaining = tasks.count { it.category == TaskCategory.LIFE && !it.isDone },
            config = cfg,
            loadState = loadState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(user = authRepository.currentUser, config = config.value)
    )

    init {
        // Gọi API tải danh sách việc ngay khi mở màn hình
        refresh()

        viewModelScope.launch {
            remoteConfig.refresh()
            config.value = remoteConfig.current()
        }
        authRepository.currentUser?.let { user ->
            telemetry.setUser(user.uid)
            presence.start(user)
            viewModelScope.launch { push.register(user.uid) }
        }
    }

    /** Dùng cho kéo-để-làm-mới và nút "Thử lại". */
    fun refresh() {
        viewModelScope.launch { taskRepository.refresh() }
    }

    fun toggleTask(task: TodoTask) {
        if (!task.isDone) telemetry.logCompleteTask(task.category)
        viewModelScope.launch {
            taskRepository.setDone(task.id, !task.isDone)
                .onFailure { _events.send(it.toLoadError().toUiText()) }
        }
    }

    fun addTask(title: String, category: TaskCategory, time: String) {
        telemetry.logAddTask(category)
        viewModelScope.launch { taskRepository.add(title, category, time) }
    }
}
