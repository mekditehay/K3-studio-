package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Movie
import com.example.ui.components.RewardedAdDialog
import com.example.ui.components.UnlockMovieDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyLibraryScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.PublishMovieScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.WalletAdsScreen
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CoinGold
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MovieViewModel
import com.example.ui.viewmodel.PurchaseResult
import com.example.ui.viewmodel.WalletViewModel

enum class MainTab(val title: String, val icon: ImageVector, val tag: String) {
    HOME("ዋና ገጽ", Icons.Default.Movie, "tab_home"),
    PUBLISH("ፊልም ልቀቅ", Icons.Default.CloudUpload, "tab_publish"),
    COINS("ኮይኖች", Icons.Default.MonetizationOn, "tab_coins"),
    LIBRARY("የእኔ ፊልም", Icons.Default.VideoLibrary, "tab_library")
}

class MainActivity : ComponentActivity() {
    private val movieViewModel: MovieViewModel by viewModels()
    private val walletViewModel: WalletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                K3MovieApp(
                    movieViewModel = movieViewModel,
                    walletViewModel = walletViewModel
                )
            }
        }
    }
}

@Composable
fun K3MovieApp(
    movieViewModel: MovieViewModel,
    walletViewModel: WalletViewModel
) {
    val context = LocalContext.current

    var currentTab by remember { mutableStateOf(MainTab.HOME) }
    var selectedPlayingMovie by remember { mutableStateOf<Movie?>(null) }
    var isSearchOpen by remember { mutableStateOf(false) }

    // Dialog States
    var movieToUnlock by remember { mutableStateOf<Movie?>(null) }
    val adState by walletViewModel.adState.collectAsStateWithLifecycle()
    val wallet by walletViewModel.wallet.collectAsStateWithLifecycle()

    // Handle back button on top-level overlays
    if (selectedPlayingMovie != null) {
        BackHandler {
            selectedPlayingMovie = null
        }
    } else if (isSearchOpen) {
        BackHandler {
            isSearchOpen = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        bottomBar = {
            // Hide bottom bar when watching movie in detail player
            if (selectedPlayingMovie == null) {
                NavigationBar(
                    containerColor = Color(0xFF10131C),
                    contentColor = TextPrimary,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    MainTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab && !isSearchOpen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                isSearchOpen = false
                                currentTab = tab
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = if (tab == MainTab.COINS) CoinGold else CinemaRed,
                                selectedTextColor = if (tab == MainTab.COINS) CoinGold else Color.White,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = if (tab == MainTab.COINS) Color(0xFF332408) else Color(0xFF3B0D11)
                            ),
                            modifier = Modifier.testTag(tab.tag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                selectedPlayingMovie != null -> {
                    PlayerScreen(
                        movie = selectedPlayingMovie!!,
                        movieViewModel = movieViewModel,
                        walletViewModel = walletViewModel,
                        onBack = { selectedPlayingMovie = null },
                        onSelectMovie = { newMovie ->
                            if (movieViewModel.isMovieUnlocked(newMovie)) {
                                selectedPlayingMovie = newMovie
                            } else {
                                movieToUnlock = newMovie
                            }
                        },
                        onRequestUnlock = { movieToUnlock = it },
                        onWatchAdClick = { walletViewModel.startWatchingAd() },
                        onOpenCoinsHub = {
                            selectedPlayingMovie = null
                            currentTab = MainTab.COINS
                        }
                    )
                }

                isSearchOpen -> {
                    SearchScreen(
                        movieViewModel = movieViewModel,
                        onMovieClick = { movie ->
                            if (movieViewModel.isMovieUnlocked(movie)) {
                                isSearchOpen = false
                                selectedPlayingMovie = movie
                            } else {
                                movieToUnlock = movie
                            }
                        },
                        onBack = { isSearchOpen = false }
                    )
                }

                else -> {
                    when (currentTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                movieViewModel = movieViewModel,
                                walletViewModel = walletViewModel,
                                onMovieClick = { movie ->
                                    if (movieViewModel.isMovieUnlocked(movie)) {
                                        selectedPlayingMovie = movie
                                    } else {
                                        movieToUnlock = movie
                                    }
                                },
                                onOpenSearch = { isSearchOpen = true },
                                onOpenCoinsHub = { currentTab = MainTab.COINS },
                                onWatchAdClick = { walletViewModel.startWatchingAd() }
                            )
                        }

                        MainTab.PUBLISH -> {
                            PublishMovieScreen(
                                movieViewModel = movieViewModel,
                                onMoviePublished = { publishedMovie ->
                                    selectedPlayingMovie = publishedMovie
                                }
                            )
                        }

                        MainTab.COINS -> {
                            WalletAdsScreen(
                                walletViewModel = walletViewModel,
                                onWatchAdClick = { walletViewModel.startWatchingAd() }
                            )
                        }

                        MainTab.LIBRARY -> {
                            MyLibraryScreen(
                                movieViewModel = movieViewModel,
                                walletViewModel = walletViewModel,
                                onMovieClick = { movie ->
                                    if (movieViewModel.isMovieUnlocked(movie)) {
                                        selectedPlayingMovie = movie
                                    } else {
                                        movieToUnlock = movie
                                    }
                                },
                                onWatchAdClick = { walletViewModel.startWatchingAd() },
                                onOpenCoinsHub = { currentTab = MainTab.COINS }
                            )
                        }
                    }
                }
            }

            // Global Rewarded Ad Modal
            RewardedAdDialog(
                adState = adState,
                onClaimReward = { walletViewModel.claimAdReward() },
                onDismiss = { walletViewModel.dismissAd() }
            )

            // Global Unlock Movie Dialog
            movieToUnlock?.let { targetMovie ->
                UnlockMovieDialog(
                    movie = targetMovie,
                    currentCoinBalance = wallet?.balance ?: 0,
                    onConfirmUnlock = {
                        movieViewModel.purchaseMovie(targetMovie) { result ->
                            when (result) {
                                is PurchaseResult.Success -> {
                                    Toast.makeText(
                                        context,
                                        "🎉 '${targetMovie.titleAmharic}' በተሳካ ሁኔታ ተከፍቷል!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    movieToUnlock = null
                                    selectedPlayingMovie = targetMovie
                                }
                                is PurchaseResult.InsufficientCoins -> {
                                    Toast.makeText(
                                        context,
                                        "ኮይንዎ አልበቃም! ማስታወቂያ በማየት ተጨማሪ ኮይን ያግኙ።",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                is PurchaseResult.Error -> {
                                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    onWatchAdToEarnCoins = {
                        movieToUnlock = null
                        walletViewModel.startWatchingAd()
                    },
                    onDismiss = { movieToUnlock = null }
                )
            }
        }
    }
}
