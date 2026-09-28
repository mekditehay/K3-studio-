package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.Movie
import com.example.ui.components.CoinBalanceChip
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
fun HomeScreen(
    movieViewModel: MovieViewModel,
    walletViewModel: WalletViewModel,
    onMovieClick: (Movie) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenCoinsHub: () -> Unit,
    onWatchAdClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allMovies by movieViewModel.allMovies.collectAsStateWithLifecycle()
    val featuredMovies by movieViewModel.featuredMovies.collectAsStateWithLifecycle()
    val trendingMovies by movieViewModel.trendingMovies.collectAsStateWithLifecycle()
    val userUploadedMovies by movieViewModel.userUploadedMovies.collectAsStateWithLifecycle()
    val filteredMovies by movieViewModel.filteredMovies.collectAsStateWithLifecycle()
    val selectedGenre by movieViewModel.selectedGenre.collectAsStateWithLifecycle()
    val wallet by walletViewModel.wallet.collectAsStateWithLifecycle()
    val purchasedMovies by movieViewModel.purchasedMovies.collectAsStateWithLifecycle()

    val categories = listOf("ሁሉም", "በነፃ", "ፕሪሚየም", "የተግባር", "ቀልድና ተግባር", "ፍቅርና ድራማ", "የህንድ ትርጉም", "የኮሪያ ትርጉም")

    val topFeatured = featuredMovies.firstOrNull() ?: allMovies.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // App Bar / Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo & Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CinemaRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "K3",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "K3 MOVIE",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "ምርጥ የትርጉም ፊልሞች",
                            color = CinemaRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Coin Balance Chip & Search
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoinBalanceChip(
                        balance = wallet?.balance ?: 0,
                        onClick = onOpenCoinsHub
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onOpenSearch,
                        modifier = Modifier.size(38.dp).testTag("search_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "ፈልግ",
                            tint = TextPrimary
                        )
                    }
                }
            }
        }

        // Hero Showcase Banner (Featured Movie)
        if (topFeatured != null) {
            item {
                val isUnlocked = movieViewModel.isMovieUnlocked(topFeatured)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .aspectRatio(1.6f)
                        .clickable { onMovieClick(topFeatured) }
                        .testTag("featured_hero_banner")
                ) {
                    AsyncImage(
                        model = topFeatured.posterUrl,
                        contentDescription = topFeatured.titleAmharic,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Cinematic Gradient Overlays
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.95f)
                                    )
                                )
                            )
                    )

                    // Hero Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CinemaRed)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = topFeatured.translator,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "★ ${topFeatured.rating}  •  ${topFeatured.releaseYear}",
                                color = CoinGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = topFeatured.titleAmharic,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = topFeatured.descriptionAmharic,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { onMovieClick(topFeatured) },
                                colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isUnlocked) "ተመልከት" else "በ ${topFeatured.coinPrice} 🪙 ክፈት",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Watch Ad Quick Button
                            Button(
                                onClick = onWatchAdClick,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2108)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CoinGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp).testTag("quick_watch_ad_hero")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tv,
                                    contentDescription = null,
                                    tint = CoinGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "+25 🪙 ማስታወቂያ እይ",
                                    color = CoinGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Category / Genre Filter Chips
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedGenre
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) CinemaRed else DarkSurfaceVariant
                            )
                            .clickable { movieViewModel.selectGenre(cat) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("filter_chip_$cat")
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Trending Dubbed Movies Carousel
        if (selectedGenre == "ሁሉም") {
            item {
                SectionHeader(
                    title = "ተወዳጅ የትርጉም ፊልሞች 🔥",
                    subtitle = "በአድናቂዎች ዘንድ ከፍተኛ እይታ ያገኙ"
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(trendingMovies) { movie ->
                        MoviePosterCard(
                            movie = movie,
                            isUnlocked = movieViewModel.isMovieUnlocked(movie),
                            onClick = { onMovieClick(movie) }
                        )
                    }
                }
            }

            // User Uploaded / Newly Released Dubbed Movies
            if (userUploadedMovies.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    SectionHeader(
                        title = "በቅርቡ የተለቀቁ ፊልሞች 🎬",
                        subtitle = "በተርጓሚዎች እና ፈጣሪዎች አዲስ የተጨመሩ"
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(userUploadedMovies) { movie ->
                            MoviePosterCard(
                                movie = movie,
                                isUnlocked = movieViewModel.isMovieUnlocked(movie),
                                onClick = { onMovieClick(movie) }
                            )
                        }
                    }
                }
            }

            // Free Dubbed Movies Section (No coins required)
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SectionHeader(
                    title = "ነፃ የትርጉም ፊልሞች 🎁",
                    subtitle = "ያለ ምንም ኮይን በቀጥታ ይመልከቱ"
                )

                val freeMovies = allMovies.filter { it.isFree }
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(freeMovies) { movie ->
                        MoviePosterCard(
                            movie = movie,
                            isUnlocked = true,
                            onClick = { onMovieClick(movie) }
                        )
                    }
                }
            }
        }

        // Filtered Grid / Full List
        item {
            Spacer(modifier = Modifier.height(14.dp))
            SectionHeader(
                title = if (selectedGenre == "ሁሉም") "ሁሉንም የትርጉም ፊልሞች ያስሱ" else "$selectedGenre ፊልሞች",
                subtitle = "${filteredMovies.size} ፊልሞች ተገኝተዋል"
            )
        }

        // 2-column or list items of filtered movies
        items(filteredMovies.chunked(2)) { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (movie in pair) {
                    MoviePosterCard(
                        movie = movie,
                        isUnlocked = movieViewModel.isMovieUnlocked(movie),
                        onClick = { onMovieClick(movie) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}
