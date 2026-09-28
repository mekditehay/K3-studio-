package com.example.data.repository

import com.example.data.dao.MovieDao
import com.example.data.model.Movie
import com.example.data.model.PurchasedMovie
import com.example.data.model.WatchHistory
import kotlinx.coroutines.flow.Flow

class MovieRepository(private val movieDao: MovieDao) {
    val allMovies: Flow<List<Movie>> = movieDao.getAllMovies()
    val featuredMovies: Flow<List<Movie>> = movieDao.getFeaturedMovies()
    val trendingMovies: Flow<List<Movie>> = movieDao.getTrendingMovies()
    val userUploadedMovies: Flow<List<Movie>> = movieDao.getUserUploadedMovies()
    val purchasedMovies: Flow<List<PurchasedMovie>> = movieDao.getAllPurchasedMovies()
    val watchHistory: Flow<List<WatchHistory>> = movieDao.getAllWatchHistory()

    fun getMovieById(id: Long): Flow<Movie?> = movieDao.getMovieById(id)
    suspend fun getMovieByIdOnce(id: Long): Movie? = movieDao.getMovieByIdOnce(id)

    fun getMoviesByGenre(genre: String): Flow<List<Movie>> = movieDao.getMoviesByGenre(genre)

    fun searchMovies(query: String): Flow<List<Movie>> = movieDao.searchMovies(query)

    fun isMoviePurchasedFlow(movieId: Long): Flow<Boolean> = movieDao.isMoviePurchasedFlow(movieId)
    suspend fun isMoviePurchased(movieId: Long): Boolean = movieDao.isMoviePurchased(movieId)

    suspend fun publishMovie(movie: Movie): Long = movieDao.insertMovie(movie)

    suspend fun updateMovie(movie: Movie) = movieDao.updateMovie(movie)

    suspend fun deleteMovie(movieId: Long) = movieDao.deleteMovieById(movieId)

    suspend fun recordPurchase(movieId: Long, coinsSpent: Int) {
        movieDao.insertPurchasedMovie(
            PurchasedMovie(movieId = movieId, coinsSpent = coinsSpent)
        )
    }

    suspend fun updateWatchProgress(movieId: Long, progressSec: Long, durationSec: Long) {
        movieDao.saveWatchHistory(
            WatchHistory(
                movieId = movieId,
                progressSeconds = progressSec,
                durationSeconds = durationSec,
                lastWatchedTimestamp = System.currentTimeMillis()
            )
        )
    }
}
