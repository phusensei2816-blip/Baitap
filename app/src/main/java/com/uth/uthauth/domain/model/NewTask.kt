package com.uth.uthauth.domain.model

/** Dữ liệu người dùng nhập ở màn "Việc mới". [date] dạng yyyy-MM-dd, [time] dạng HH:mm. */
data class NewTask(
    val title: String,
    val note: String,
    val category: TaskCategory,
    val date: String,
    val time: String,
    val priority: TaskPriority,
    val remind: Boolean,
)
