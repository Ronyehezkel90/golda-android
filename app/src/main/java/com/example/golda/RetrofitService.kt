package com.example.golda

import com.example.golda.model.ReviewItem
import retrofit2.Response
import retrofit2.http.GET

interface RetrofitService {
    @GET("/posts")
    suspend fun getPosts(): Response<List<ReviewItem>>
}