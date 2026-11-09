package com.uth.uthauth.domain.model

/** Các thay đổi một phần của một việc (null = giữ nguyên). Tương ứng PATCH /tasks/{id}. */
data class TaskChange(
    val title: String? = null,
    val note: String? = null,
    val done: Boolean? = null,
    val subtasks: List<Subtask>? = null,
)
