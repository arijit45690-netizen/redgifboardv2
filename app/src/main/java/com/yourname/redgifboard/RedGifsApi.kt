package com.yourname.redgifboard

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RedGifsApiService {

    @GET("v2/auth/temporary")
    suspend fun getToken(): TokenResponse

    @GET("v2/gifs/search")
    suspend fun searchGifs(
        @Header("Authorization") auth: String,
        @Query("search_text") query: String,
        @Query("count") count: Int = 20,
        @Query("page") page: Int = 1,
        @Query("order") order: String = "trending",
        @Query("type") type: String = "g"
    ): SearchResponse
}

object RedGifsClient {
    val api: RedGifsApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("User-Agent", "RedGifBoard/1.0 Android")
                    .build()
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.redgifs.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RedGifsApiService::class.java)
    }
}
