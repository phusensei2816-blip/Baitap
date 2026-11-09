package com.uth.uthauth.data.task

import com.uth.uthauth.data.remote.TaskApi
import com.uth.uthauth.data.remote.toDomain
import com.uth.uthauth.data.remote.toDto
import com.uth.uthauth.data.remote.toLoadError
import com.uth.uthauth.data.telemetry.AppTelemetry
import com.uth.uthauth.domain.model.DayStat
import com.uth.uthauth.domain.model.LoadState
import com.uth.uthauth.domain.model.TaskCategory
import com.uth.uthauth.domain.model.TaskChange
import com.uth.uthauth.domain.model.TodoTask
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex

/**
 * Nguồn dữ liệu: REST API /mock/teach/tasks.
 *
 * API mock luôn trả cùng một dữ liệu mẫu, kể cả sau PATCH/DELETE. Để app hoạt động như một
 * server thật trong phiên chạy, repository giữ "lớp phủ" (overlay) các thay đổi đã gửi thành công
 * và áp lại mỗi khi tải danh sách mới.
 */
class ApiTaskRepository(
    private val api: TaskApi,
    private val telemetry: AppTelemetry,
) : TaskRepository {

    private val state = MutableStateFlow<List<TodoTask>>(emptyList())
    private val _loadState = MutableStateFlow<LoadState>(LoadState.Loading)
    private val refreshLock = Mutex()
    private var localId = 0L

    private val overlay = mutableMapOf<String, TodoTask>()   // việc đã sửa thành công
    private val removed = mutableSetOf<String>()             // việc đã xóa thành công

    override val loadState: Flow<LoadState> = _loadState.asStateFlow()

    override val tasks: Flow<List<TodoTask>> = state.map { list ->
        list.sortedWith(compareBy<TodoTask>({ it.date }, { it.time }))
    }

    override val weeklyStats: Flow<List<DayStat>> = state.map { list ->
        val today = LocalDate.now()
        val monday = today.with(DayOfWeek.MONDAY)
        val counts = IntArray(7)
        list.filter { it.isDone }.forEach { task ->
            val date = runCatching { LocalDate.parse(task.date) }.getOrNull() ?: return@forEach
            val index = (date.toEpochDay() - monday.toEpochDay()).toInt()
            if (index in 0..6) counts[index]++
        }
        (0..6).map { DayStat(it, counts[it], isToday = it == today.dayOfWeek.value - 1) }
    }

    override fun observeTask(id: String): Flow<TodoTask?> =
        state.map { list -> list.firstOrNull { it.id == id } }.distinctUntilChanged()

    override suspend fun refresh() {
        if (!refreshLock.tryLock()) return          // đang tải thì bỏ qua
        try {
            _loadState.value = LoadState.Loading
            _loadState.value = try {
                val server = api.getTasks().map { it.toDomain() }
                val locals = state.value.filter { it.id.startsWith(LOCAL_PREFIX) }
                state.value = applyOverlay(server) + locals
                LoadState.Success
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                report(e)
                e.toLoadError()
            }
        } finally {
            refreshLock.unlock()
        }
    }

    override suspend fun loadDetail(id: String): Result<Unit> {
        if (id.startsWith(LOCAL_PREFIX)) return Result.success(Unit)
        return try {
            val dto = api.getTask(id.toLong())
            // Mock trả cùng một mẫu cho mọi id -> chỉ nhận kết quả khi đúng id đã yêu cầu
            if (dto.id.toString() == id && id !in removed) {
                val fresh = overlay[id] ?: dto.toDomain()
                state.update { list ->
                    if (list.any { it.id == id }) list.map { if (it.id == id) fresh else it } else list + fresh
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            report(e)
            Result.failure(e)
        }
    }

    override suspend fun update(id: String, change: TaskChange): Result<Unit> {
        val before = state.value.firstOrNull { it.id == id }
            ?: return Result.failure(NoSuchElementException("Task $id not found"))

        replace(id, before.withChange(change))              // cập nhật UI ngay
        if (id.startsWith(LOCAL_PREFIX)) return Result.success(Unit)

        return try {
            api.updateTask(id.toLong(), change.toDto())
            state.value.firstOrNull { it.id == id }?.let { overlay[id] = it }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            replace(id, before)                             // lỗi -> hoàn tác
            report(e)
            Result.failure(e)
        }
    }

    override suspend fun delete(id: String): Result<Unit> {
        if (!id.startsWith(LOCAL_PREFIX)) {
            try {
                api.deleteTask(id.toLong())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                report(e)
                return Result.failure(e)
            }
        }
        removed += id
        overlay.remove(id)
        state.update { list -> list.filterNot { it.id == id } }
        return Result.success(Unit)
    }

    override suspend fun add(title: String, category: TaskCategory, time: String) {
        val task = TodoTask(
            id = "$LOCAL_PREFIX${++localId}",
            title = title.trim(),
            category = category,
            time = time,
            date = LocalDate.now().toString(),
        )
        state.update { it + task }
    }

    private fun applyOverlay(server: List<TodoTask>): List<TodoTask> =
        server.filter { it.id !in removed }.map { overlay[it.id] ?: it }

    private fun replace(id: String, task: TodoTask) {
        state.update { list -> list.map { if (it.id == id) task else it } }
    }

    private fun TodoTask.withChange(c: TaskChange) = copy(
        title = c.title ?: title,
        note = c.note ?: note,
        isDone = c.done ?: isDone,
        subtasks = c.subtasks ?: subtasks,
    )

    private fun report(e: Exception) {
        if (e !is IOException) telemetry.recordError(e)   // lỗi mạng thông thường không cần báo
    }

    private companion object {
        const val LOCAL_PREFIX = "local-"
    }
}
