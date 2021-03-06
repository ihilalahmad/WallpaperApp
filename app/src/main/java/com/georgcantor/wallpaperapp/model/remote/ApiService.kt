package com.georgcantor.wallpaperapp.model.remote

import com.georgcantor.wallpaperapp.BuildConfig.PIXABAY_KEY
import com.georgcantor.wallpaperapp.model.data.pixabay.Pictures
import com.georgcantor.wallpaperapp.model.data.unsplash.UnsplashResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("?key=$PIXABAY_KEY")
    suspend fun getPixabayPictures(
        @Query("q") query: String,
        @Query("page") index: Int
    ): Pictures

    @GET
    suspend fun getUnsplashPictures(
        @Url url: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): UnsplashResponse
}
