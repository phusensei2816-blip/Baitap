package com.uth.uthauth.data.remote

import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object NetworkModule {

    fun createTaskApi(baseUrl: String, token: String, debug: Boolean): TaskApi {
        val json = Json {
            ignoreUnknownKeys = true   // bỏ qua trường chưa khai báo
            coerceInputValues = true   // null -> giá trị mặc định
        }

        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder().apply {
                if (token.isNotBlank()) header("Authorization", "Bearer $token")
            }.build()
            chain.proceed(request)
        }

        val logging = HttpLoggingInterceptor().apply {
            level = if (debug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            redactHeader("Authorization")   // không in token ra Logcat
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TaskApi::class.java)
    }
}
