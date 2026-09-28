package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titleAmharic: String,
    val titleOriginal: String,
    val translator: String,
    val genre: String,
    val durationMinutes: Int,
    val releaseYear: Int,
    val rating: Float = 4.8f,
    val posterUrl: String = "",
    val videoUrl: String = "",
    val descriptionAmharic: String,
    val descriptionEnglish: String = "",
    val coinPrice: Int = 0, // 0 means Free to watch
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val viewsCount: Int = 1200,
    val likesCount: Int = 450,
    val uploaderName: String = "K3 Movie",
    val isUserUploaded: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis()
) {
    val isFree: Boolean get() = coinPrice <= 0
}
