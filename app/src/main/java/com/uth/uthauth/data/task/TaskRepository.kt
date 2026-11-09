package com.uth.uthauth.data.task

import com.uth.uthauth.domain.model.DayStat
import com.uth.uthauth.domain.model.LoadState
import com.uth.uthauth.domain.model.TaskCategory
import com.uth.uthauth.domain.model.TaskChange
import com.uth.uthauth.domain.model.TodoTask
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    val tasks: Flow<List<TodoTask>>
    /** Số việc hoàn thành mỗi ngày trong tuần hiện tại (T2..CN). */
    val weeklyStats: Flow<List<DayStat>>
    /** Trạng thái của lần tải danh sách gần nhất. */
    val loadState: Flow<LoadState>

    /** Theo dõi một việc (null nếu không tồn tại / đã xóa). */
    fun observeTask(id: String): Flow<TodoTask?>

    /** GET /tasks – tải mọi việc. */
    suspend fun refresh()

    /** GET /tasks/{id} – tải chi tiết một việc khi mở màn Chi tiết. */
    suspend fun loadDetail(id: String): Result<Unit>

    /** PATCH /tasks/{id} – cập nhật ngay trên UI (optimistic), lỗi thì hoàn tác. */
    suspend fun update(id: String, change: TaskChange): Result<Unit>

    suspend fun setDone(id: String, done: Boolean): Result<Unit> = update(id, TaskChange(done = done))

    /** DELETE /tasks/{id}. */
    suspend fun delete(id: String): Result<Unit>

    suspend fun add(title: String, category: TaskCategory, time: String)
}
