package com.alexgi.kando.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class TodoDto(
    val userId: Int,
    val id: Long,
    val title: String,
    val completed: Boolean
)

interface TodoApiService {
    @GET("todos")
    suspend fun getTodos(): List<TodoDto>

    companion object {
        private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

        fun create(): TodoApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TodoApiService::class.java)
        }
    }
}
