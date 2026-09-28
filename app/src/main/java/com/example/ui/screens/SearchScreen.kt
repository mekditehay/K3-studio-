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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Movie
import com.example.ui.components.MovieWideCard
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MovieViewModel

@Composable
fun SearchScreen(
    movieViewModel: MovieViewModel,
    onMovieClick: (Movie) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by movieViewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by movieViewModel.searchResults.collectAsStateWithLifecycle()
    val allMovies by movieViewModel.allMovies.collectAsStateWithLifecycle()

    val suggestions = listOf("ሄኖክ", "ዳኒ", "ዮኒ", "ተግባር", "ጆን ዊክ", "ነፃ", "ባሁባሊ")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("search_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp)
    ) {
        // Search Input Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "ተመለስ",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { movieViewModel.updateSearchQuery(it) },
                    placeholder = { Text("በፊልም ስም፣ አስተርጓሚ ወይም ዘውግ ፈልግ...", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextMuted)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { movieViewModel.updateSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "አፅዳ", tint = TextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_text_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CinemaRed,
                        unfocusedBorderColor = DarkSurfaceVariant,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Quick Suggestion Chips
        item {
            Text(
                text = "ፈጣን መፈለጊያዎች:",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(suggestions) { sugg ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurfaceVariant)
                            .clickable { movieViewModel.updateSearchQuery(sugg) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = sugg,
                            color = TextPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Results Section
        val displayList = if (searchQuery.isBlank()) allMovies else searchResults
        item {
            Text(
                text = if (searchQuery.isBlank()) "ሁሉንም የትርጉም ፊልሞች ያስሱ (${allMovies.size})" else "የፍለጋ ውጤቶች (${searchResults.size})",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (displayList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ለ '${searchQuery}' ምንም ፊልም አልተገኘም። እባክዎ በሌላ ቃል ይሞክሩ።",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        }

        items(displayList) { movie ->
            MovieWideCard(
                movie = movie,
                isUnlocked = movieViewModel.isMovieUnlocked(movie),
                onClick = { onMovieClick(movie) },
                modifier = Modifier.padding(vertical = 5.dp)
            )
        }
    }
}
