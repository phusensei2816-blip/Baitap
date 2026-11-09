package com.uth.uthauth.domain.model

enum class TaskCategory {
    STUDY, LIFE;

    companion object {
        /** API trả về "study" / "life". */
        fun fromApi(value: String?): TaskCategory =
            if (value.equals("study", ignoreCase = true)) STUDY else LIFE
    }
}

enum class TaskPriority {
    LOW, MEDIUM, HIGH;

    companion object {
        fun fromApi(value: String?): TaskPriority =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
    }
}

data class TodoTask(
    val id: String,
    val title: String,
    val category: TaskCategory,
    val time: String,                       // "HH:mm"
    val isDone: Boolean = false,
    val note: String = "",
    val date: String = "",                  // "yyyy-MM-dd"
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val remind: Boolean = false,
    val subtasks: List<Subtask> = emptyList(),
)

data class Subtask(
    val title: String,
    val done: Boolean = false,
)

data class TaskSummary(val total: Int, val done: Int) {
    val pending: Int get() = total - done
    val progress: Float get() = if (total == 0) 0f else done.toFloat() / total
    val percent: Int get() = (progress * 100).toInt()
}

/** [dayIndex]: 0 = Thứ Hai ... 6 = Chủ nhật. */
data class DayStat(
    val dayIndex: Int,
    val doneCount: Int,
    val isToday: Boolean,
)
