package com.actsol.thekee

import com.google.gson.annotations.SerializedName

data class Episode (
        @SerializedName("id")
        val id: Long? = null,
        @SerializedName("title")
        val title: String? = null,
        @SerializedName("poster_photo")
        val posterPhoto: String? = null,
        @SerializedName("duration")
        val duration: String? = null,
        @SerializedName("download_url")
        val downloadURL: String? = null,
        @SerializedName("hd_url")
        val hdURL: String? = null,
        @SerializedName("trailer_url")
        val trailerURL: Any? = null,
        @SerializedName("media_url")
        val mediaURL: String? = null,
        @SerializedName("created_at")
        val createdAt: String? = null,
        @SerializedName("release_date")
        val releaseDate: String? = null,
        @SerializedName("user_id")
        val userID: String? = null,
        @SerializedName("profile_id")
        val profileID: String? = null,
        @SerializedName("watching")
        val userMediaWatching: UserMediaWatching? = null,
        @SerializedName("subtitle")
        val subtitle: List<Subtitle>? = null
)

data class Subtitle (
        @SerializedName("file_url")
        val fileURL: String? = null,
        @SerializedName("language")
        val language: String? = null
)

data class UserMediaWatching (
        @SerializedName("current_time")
        val currentTime: String? = null,
        @SerializedName("duration")
        val duration: String? = null
)