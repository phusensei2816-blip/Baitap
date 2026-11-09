package com.uth.uthauth.data.remote

import com.uth.uthauth.domain.model.Subtask
import com.uth.uthauth.domain.model.TaskCategory
import com.uth.uthauth.domain.model.TaskChange
import com.uth.uthauth.domain.model.TaskPriority
import com.uth.uthauth.domain.model.TodoTask
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/** Việc nhỏ: JSON có 2 trường. Chấp nhận thêm vài tên gọi khác phòng khi API đặt tên khác. */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class SubtaskDto(
    @JsonNames("text", "name") val title: String = "",
    @JsonNames("completed", "isDone") val done: Boolean = false,
)

/** Khớp JSON của GET /mock/teach/tasks và GET /mock/teach/tasks/{id}. */
@Serializable
data class TaskDto(
    val id: Long,
    val title: String,
    val note: String = "",
    val category: String = "life",
    val date: String = "",
    val time: String = "00:00",
    val priority: String = "medium",
    val remind: Boolean = false,
    val done: Boolean = false,
    val subtasks: List<SubtaskDto> = emptyList(),
)

/** Body của PATCH: chỉ gửi trường nào khác null (encodeDefaults = false nên trường null bị bỏ qua). */
@Serializable
data class TaskPatchDto(
    val title: String? = null,
    val note: String? = null,
    val done: Boolean? = null,
    val subtasks: List<SubtaskDto>? = null,
)

fun TaskDto.toDomain() = TodoTask(
    id = id.toString(),
    title = title,
    category = TaskCategory.fromApi(category),
    time = time,
    isDone = done,
    note = note,
    date = date,
    priority = TaskPriority.fromApi(priority),
    remind = remind,
    subtasks = subtasks.map { Subtask(it.title, it.done) },
)

fun TaskChange.toDto() = TaskPatchDto(
    title = title,
    note = note,
    done = done,
    subtasks = subtasks?.map { SubtaskDto(it.title, it.done) },
)
