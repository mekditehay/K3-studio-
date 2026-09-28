package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Movie
import com.example.ui.components.CoinBalanceChip
import com.example.ui.components.MovieWideCard
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
fun MyLibraryScreen(
    movieViewModel: MovieViewModel,
    walletViewModel: WalletViewModel,
    onMovieClick: (Movie) -> Unit,
    onWatchAdClick: () -> Unit,
    onOpenCoinsHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allMovies by movieViewModel.allMovies.collectAsStateWithLifecycle()
    val purchasedMovies by movieViewModel.purchasedMovies.collectAsStateWithLifecycle()
    val watchHistory by movieViewModel.watchHistory.collectAsStateWithLifecycle()
    val wallet by walletViewModel.wallet.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("የተገዙ / የተከፈቱ", "የእይታ ታሪክ")

    val unlockedMovies = remember(allMovies, purchasedMovies) {
        val purchasedIds = purchasedMovies.map { it.movieId }.toSet()
        allMovies.filter { purchasedIds.contains(it.id) || it.isFree }
    }

    val historyMovies = remember(allMovies, watchHistory) {
        val historyMap = watchHistory.associateBy { it.movieId }
        allMovies.filter { historyMap.containsKey(it.id) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("my_library_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "የእኔ ፊልሞች (Library)",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "የተከፈቱ ፊልሞች እና የተመለከቷቸው",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                CoinBalanceChip(
                    balance = wallet?.balance ?: 0,
                    onClick = onOpenCoinsHub
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurfaceVariant,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CinemaRed
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color.White else TextSecondary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        val currentList = if (selectedTab == 0) unlockedMovies else historyMovies

        if (currentList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.LockOpen else Icons.Default.History,
                            contentDescription = null,
                            tint = CoinGold,
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (selectedTab == 0) "እስካሁን ምንም ፊልም አልገዙም" else "እስካሁን ምንም ፊልም አልተመለከቱም",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (selectedTab == 0)
                                "ማስታወቂያዎችን በማየት ኮይን ሰብስበው የሚወዱትን የትርጉም ፊልም ይክፈቱ!"
                            else "የሚወዱትን የትርጉም ፊልም ከዋናው ገጽ መርጠው ይመልከቱ።",
                            color = TextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onWatchAdClick,
                            colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ማስታወቂያ እይ (+25 🪙)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        items(currentList) { movie ->
            MovieWideCard(
                movie = movie,
                isUnlocked = true,
                onClick = { onMovieClick(movie) },
                modifier = Modifier.padding(vertical = 5.dp)
            )
        }
    }
}
