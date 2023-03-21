package com.udlib.udlib

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * REST API access points.
 */
interface ApiService {
    @GET
    fun updateCurrentTime(@Url url: String): Call<MediaWatchingItem>

}