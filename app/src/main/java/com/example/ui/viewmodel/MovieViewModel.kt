package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.Movie
import com.example.data.model.PurchasedMovie
import com.example.data.model.WatchHistory
import com.example.data.repository.MovieRepository
import com.example.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class PurchaseResult {
    data object Success : PurchaseResult()
    data class InsufficientCoins(val required: Int, val current: Int) : PurchaseResult()
    data class Error(val message: String) : PurchaseResult()
}

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val movieRepository = MovieRepository(database.movieDao())
    val walletRepository = WalletRepository(database.walletDao())

    init {
        viewModelScope.launch {
            AppDatabase.populateInitialData(database.movieDao(), database.walletDao())
        }
    }

    val allMovies: StateFlow<List<Movie>> = movieRepository.allMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredMovies: StateFlow<List<Movie>> = movieRepository.featuredMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendingMovies: StateFlow<List<Movie>> = movieRepository.trendingMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userUploadedMovies: StateFlow<List<Movie>> = movieRepository.userUploadedMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val purchasedMovies: StateFlow<List<PurchasedMovie>> = movieRepository.purchasedMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchHistory: StateFlow<List<WatchHistory>> = movieRepository.watchHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter by genre/category
    private val _selectedGenre = MutableStateFlow("ሁሉም")
    val selectedGenre = _selectedGenre.asStateFlow()

    fun selectGenre(genre: String) {
        _selectedGenre.value = genre
    }

    // Filtered movies according to category
    val filteredMovies: StateFlow<List<Movie>> = combine(allMovies, _selectedGenre) { movies, genre ->
        when (genre) {
            "ሁሉም" -> movies
            "በነፃ" -> movies.filter { it.isFree }
            "ፕሪሚየም" -> movies.filter { !it.isFree }
            else -> movies.filter { it.genre.contains(genre, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<Movie>> = combine(allMovies, _searchQuery) { movies, query ->
        if (query.isBlank()) {
            emptyList()
        } else {
            val q = query.trim().lowercase()
            movies.filter {
                it.titleAmharic.lowercase().contains(q) ||
                        it.titleOriginal.lowercase().contains(q) ||
                        it.translator.lowercase().contains(q) ||
                        it.genre.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Check if a movie is unlocked/accessible
    fun isMovieUnlocked(movie: Movie): Boolean {
        if (movie.isFree) return true
        val purchasedList = purchasedMovies.value
        return purchasedList.any { it.movieId == movie.id }
    }

    // Purchase / Unlock movie with coins
    fun purchaseMovie(movie: Movie, onResult: (PurchaseResult) -> Unit) {
        viewModelScope.launch {
            if (movie.isFree) {
                onResult(PurchaseResult.Success)
                return@launch
            }
            if (isMovieUnlocked(movie)) {
                onResult(PurchaseResult.Success)
                return@launch
            }

            val wallet = database.walletDao().getWalletOnce()
            val currentBalance = wallet?.balance ?: 0
            if (currentBalance < movie.coinPrice) {
                onResult(PurchaseResult.InsufficientCoins(required = movie.coinPrice, current = currentBalance))
                return@launch
            }

            val spent = walletRepository.spendCoins(movie.coinPrice, movie.titleAmharic)
            if (spent) {
                movieRepository.recordPurchase(movie.id, movie.coinPrice)
                onResult(PurchaseResult.Success)
            } else {
                onResult(PurchaseResult.Error("ግዢው አልተሳካም። እባክዎ እንደገና ይሞክሩ።"))
            }
        }
    }

    // Release / Publish Movie
    fun publishNewMovie(
        titleAmharic: String,
        titleOriginal: String,
        translator: String,
        genre: String,
        durationMinutes: Int,
        releaseYear: Int,
        coinPrice: Int,
        videoUrl: String,
        posterUrl: String,
        descriptionAmharic: String,
        uploaderName: String,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        if (titleAmharic.isBlank()) {
            onError("እባክዎ የፊልሙን የአማርኛ ርዕስ ያስገቡ")
            return
        }
        if (translator.isBlank()) {
            onError("እባክዎ የተርጓሚውን ስም ያስገቡ (ለምሳሌ፡ ሄኖክ፣ ዳኒ)")
            return
        }
        val safeVideoUrl = if (videoUrl.isNotBlank()) videoUrl else "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
        val safePosterUrl = if (posterUrl.isNotBlank()) posterUrl else "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=600&auto=format&fit=crop&q=80"

        viewModelScope.launch {
            try {
                val newMovie = Movie(
                    titleAmharic = titleAmharic.trim(),
                    titleOriginal = if (titleOriginal.isNotBlank()) titleOriginal.trim() else titleAmharic.trim(),
                    translator = translator.trim(),
                    genre = genre.trim(),
                    durationMinutes = if (durationMinutes > 0) durationMinutes else 120,
                    releaseYear = if (releaseYear > 0) releaseYear else 2024,
                    rating = 5.0f,
                    posterUrl = safePosterUrl,
                    videoUrl = safeVideoUrl,
                    descriptionAmharic = if (descriptionAmharic.isNotBlank()) descriptionAmharic.trim() else "በተርጓሚ $translator የተተረጎመ አዲስ ፊልም",
                    descriptionEnglish = "Newly published translated movie by $translator",
                    coinPrice = coinPrice.coerceAtLeast(0),
                    isFeatured = false,
                    isTrending = true,
                    uploaderName = if (uploaderName.isNotBlank()) uploaderName.trim() else "አስተርጓሚ / Creator",
                    isUserUploaded = true,
                    createdTimestamp = System.currentTimeMillis()
                )
                val id = movieRepository.publishMovie(newMovie)
                onSuccess(id)
            } catch (e: Exception) {
                onError("ፊልሙን መልቀቅ አልተቻለም: ${e.localizedMessage ?: "ያልታወቀ ስህተት"}")
            }
        }
    }

    // Save watch position
    fun saveWatchProgress(movieId: Long, progressSec: Long, durationSec: Long) {
        viewModelScope.launch {
            movieRepository.updateWatchProgress(movieId, progressSec, durationSec)
        }
    }
}
