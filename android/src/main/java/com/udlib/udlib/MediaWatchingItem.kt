package com.udlib.udlib

import com.google.gson.annotations.SerializedName

data class MediaWatchingItem(
        val status: Boolean,
        val message: String,
        val data: Data
) {
    data class Data (
            @SerializedName("id")
            val id: Long,
            @SerializedName("user_id")
            val userID: Long,
            @SerializedName("media_id")
            val mediaID: Long,
            @SerializedName("duration")
            val duration: String,
            @SerializedName("current_time")
            val currentTime: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("deleted_at")
            val deletedAt: Any? = null
    )
}