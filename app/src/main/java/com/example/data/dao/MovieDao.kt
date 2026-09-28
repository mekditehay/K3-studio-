package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Movie
import com.example.data.model.PurchasedMovie
import com.example.data.model.WatchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY createdTimestamp DESC")
    fun getAllMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isFeatured = 1 ORDER BY rating DESC")
    fun getFeaturedMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isTrending = 1 ORDER BY viewsCount DESC")
    fun getTrendingMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE isUserUploaded = 1 ORDER BY createdTimestamp DESC")
    fun getUserUploadedMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE genre = :genre ORDER BY createdTimestamp DESC")
    fun getMoviesByGenre(genre: String): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    fun getMovieById(id: Long): Flow<Movie?>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    suspend fun getMovieByIdOnce(id: Long): Movie?

    @Query("SELECT * FROM movies WHERE titleAmharic LIKE '%' || :query || '%' OR titleOriginal LIKE '%' || :query || '%' OR translator LIKE '%' || :query || '%' OR genre LIKE '%' || :query || '%'")
    fun searchMovies(query: String): Flow<List<Movie>>

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    @Update
    suspend fun updateMovie(movie: Movie)

    @Query("DELETE FROM movies WHERE id = :id")
    suspend fun deleteMovieById(id: Long)

    // Purchases
    @Query("SELECT * FROM purchased_movies ORDER BY purchasedAt DESC")
    fun getAllPurchasedMovies(): Flow<List<PurchasedMovie>>

    @Query("SELECT EXISTS(SELECT 1 FROM purchased_movies WHERE movieId = :movieId)")
    fun isMoviePurchasedFlow(movieId: Long): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM purchased_movies WHERE movieId = :movieId)")
    suspend fun isMoviePurchased(movieId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchasedMovie(purchasedMovie: PurchasedMovie)

    // Watch History
    @Query("SELECT * FROM watch_history ORDER BY lastWatchedTimestamp DESC")
    fun getAllWatchHistory(): Flow<List<WatchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWatchHistory(watchHistory: WatchHistory)
}
