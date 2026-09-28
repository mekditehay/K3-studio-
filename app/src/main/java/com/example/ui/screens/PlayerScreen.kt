package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Movie
import com.example.ui.components.CoinBalanceChip
import com.example.ui.components.CustomVideoPlayer
import com.example.ui.components.MoviePosterCard
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CoinGold
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBackground
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MovieViewModel
import com.example.ui.viewmodel.WalletViewModel

@Composable
fun PlayerScreen(
    movie: Movie,
    movieViewModel: MovieViewModel,
    walletViewModel: WalletViewModel,
    onBack: () -> Unit,
    onSelectMovie: (Movie) -> Unit,
    onRequestUnlock: (Movie) -> Unit,
    onWatchAdClick: () -> Unit,
    onOpenCoinsHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    val allMovies by movieViewModel.allMovies.collectAsStateWithLifecycle()
    val wallet by walletViewModel.wallet.collectAsStateWithLifecycle()
    val isUnlocked = movieViewModel.isMovieUnlocked(movie)

    var isLiked by remember { mutableStateOf(false) }
    var isBookmarked by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var isDownloaded by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }

    val relatedMovies = remember(movie, allMovies) {
        allMovies.filter { it.id != movie.id && (it.genre == movie.genre || it.translator == movie.translator) }
            .ifEmpty { allMovies.filter { it.id != movie.id } }
    }

    if (isFullscreen) {
        // Fullscreen player mode
        CustomVideoPlayer(
            movie = movie,
            isUnlocked = isUnlocked,
            onUnlockClick = { onRequestUnlock(movie) },
            isFullscreen = true,
            onToggleFullscreen = { isFullscreen = false },
            onProgressUpdate = { pos, dur ->
                movieViewModel.saveWatchProgress(movie.id, pos, dur)
            }
        )
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("player_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Top Navigation Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp).testTag("player_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "ተመለስ",
                        tint = TextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoinBalanceChip(
                        balance = wallet?.balance ?: 0,
                        onClick = onOpenCoinsHub
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "በ K3 Movie ላይ '${movie.titleAmharic}' የተተረጎመ ድንቅ ፊልም ተመልከቱ! #K3Movie #የትርጉምፊልም"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "ፊልሙን አጋራ"))
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "አጋራ",
                            tint = TextPrimary
                        )
                    }
                }
            }
        }

        // Custom Video Player Component
        item {
            CustomVideoPlayer(
                movie = movie,
                isUnlocked = isUnlocked,
                onUnlockClick = { onRequestUnlock(movie) },
                isFullscreen = false,
                onToggleFullscreen = { isFullscreen = true },
                onProgressUpdate = { pos, dur ->
                    movieViewModel.saveWatchProgress(movie.id, pos, dur)
                }
            )
        }

        // Movie Title & Dubbing Translator Info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = movie.titleAmharic,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = movie.titleOriginal,
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }

                    // Translator Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CinemaRed)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = movie.translator,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Metadata Row (Rating, Year, Duration, Genre)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkSurfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = CoinGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${movie.rating}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "${movie.releaseYear}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "•",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "${movie.durationMinutes} ደቂቃ",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "•",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Text(
                        text = movie.genre,
                        color = AccentCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lock / Unlock Callout Card
                if (!isUnlocked && !movie.isFree) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CoinGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = CoinGold,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "የተቆለፈ ፊልም",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "ዋጋ፡ ${movie.coinPrice} ኮይኖች",
                                        color = CoinGold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Button(
                                onClick = { onRequestUnlock(movie) },
                                colors = ButtonDefaults.buttonColors(containerColor = CoinGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("player_unlock_cta_button")
                            ) {
                                Text(
                                    text = "በኮይን ክፈት",
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Interactive Action Buttons (Like, Bookmark, Download, Watch Ad)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Like Action
                    ActionButton(
                        icon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        label = if (isLiked) "${movie.likesCount + 1}" else "${movie.likesCount}",
                        tint = if (isLiked) CinemaRed else TextSecondary,
                        onClick = { isLiked = !isLiked }
                    )

                    // Bookmark Action
                    ActionButton(
                        icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        label = "ዝርዝር",
                        tint = if (isBookmarked) CoinGold else TextSecondary,
                        onClick = {
                            isBookmarked = !isBookmarked
                            Toast.makeText(
                                context,
                                if (isBookmarked) "ወደ የእኔ ዝርዝር ተጨምሯል" else "ከዝርዝር ተወግዷል",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    // Download Action
                    ActionButton(
                        icon = if (isDownloaded) Icons.Default.Check else Icons.Default.Download,
                        label = if (isDownloaded) "ወርዷል" else if (isDownloading) "${(downloadProgress * 100).toInt()}%" else "አውርድ",
                        tint = if (isDownloaded) AccentGreen else if (isDownloading) AccentCyan else TextSecondary,
                        onClick = {
                            if (!isUnlocked) {
                                onRequestUnlock(movie)
                            } else if (!isDownloaded && !isDownloading) {
                                isDownloading = true
                                Toast.makeText(context, "ፊልሙ በመውረድ ላይ ነው...", Toast.LENGTH_SHORT).show()
                                // Simulate download
                                downloadProgress = 0.35f
                                isDownloaded = true
                                isDownloading = false
                                Toast.makeText(context, "ፊልሙ ለኦፍላይን እይታ በተሳካ ሁኔታ ወርዷል!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    // Watch Ad to Earn Coins Shortcut
                    ActionButton(
                        icon = Icons.Default.Tv,
                        label = "+25 🪙",
                        tint = CoinGold,
                        onClick = onWatchAdClick
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "የፊልሙ አጭር ታሪክ",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = movie.descriptionAmharic,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "የተጫነው በ: ${movie.uploaderName}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Related Dubbed Movies
        if (relatedMovies.isNotEmpty()) {
            item {
                Text(
                    text = "ተመሳሳይ የትርጉም ፊልሞች",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(relatedMovies) { relMovie ->
                        MoviePosterCard(
                            movie = relMovie,
                            isUnlocked = movieViewModel.isMovieUnlocked(relMovie),
                            onClick = { onSelectMovie(relMovie) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = tint,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
