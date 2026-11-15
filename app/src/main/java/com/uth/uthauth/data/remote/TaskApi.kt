package com.uth.uthauth.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface TaskApi {
    /** Tải mọi việc khi mở màn hình Trang chủ / Danh sách. */
    @GET("mock/teach/tasks")
    suspend fun getTasks(): List<TaskDto>

    /** Màn "Việc mới": tạo công việc. */
    @POST("mock/teach/tasks")
    suspend fun createTask(@Body body: TaskCreateDto)

    /** Mở khi bấm một việc (màn Chi tiết). */
    @GET("mock/teach/tasks/{id}")
    suspend fun getTask(@Path("id") id: Long): TaskDto

    /** Cập nhật một phần: tick xong, sửa tiêu đề/ghi chú, việc nhỏ... */
    @PATCH("mock/teach/tasks/{id}")
    suspend fun updateTask(@Path("id") id: Long, @Body body: TaskPatchDto)

    @DELETE("mock/teach/tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Long)
}
