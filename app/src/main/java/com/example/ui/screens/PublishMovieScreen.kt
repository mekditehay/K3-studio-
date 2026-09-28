package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.Movie
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

@Composable
fun PublishMovieScreen(
    movieViewModel: MovieViewModel,
    onMoviePublished: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userUploadedMovies by movieViewModel.userUploadedMovies.collectAsStateWithLifecycle()

    var titleAmharic by remember { mutableStateOf("") }
    var titleOriginal by remember { mutableStateOf("") }
    var translator by remember { mutableStateOf("ሄኖክ ትርጉም") }
    var genre by remember { mutableStateOf("የተግባር (Action)") }
    var durationText by remember { mutableStateOf("120") }
    var yearText by remember { mutableStateOf("2024") }
    var coinPrice by remember { mutableIntStateOf(50) }
    var videoUrl by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4") }
    var posterUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=600&auto=format&fit=crop&q=80") }
    var descriptionAmharic by remember { mutableStateOf("") }
    var uploaderName by remember { mutableStateOf("እኔ (ፈጣሪ)") }
    var isPublishing by remember { mutableStateOf(false) }

    val translatorPresets = listOf("ሄኖክ ትርጉም", "ዳኒ ስቱዲዮ", "ዮኒ ሲኒማ", "ቴዲ ሙቪ", "አቤል ትርጉም", "ራስ ትርጉም")
    val genrePresets = listOf("የተግባር (Action)", "ቀልድና ተግባር", "ፍቅርና ድራማ", "የህንድ ትርጉም", "የኮሪያ ትርጉም", "አስፈሪ (Horror)", "አኒሜሽን")
    val pricePresets = listOf(0, 30, 50, 75, 100)

    val posterPresets = listOf(
        Pair("የተግባር ፖስተር 1", "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=600&auto=format&fit=crop&q=80"),
        Pair("ሳይንስና ፍልሚያ", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80"),
        Pair("አስደናቂ አኒሜሽን", "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop&q=80"),
        Pair("ፍቅርና ድራማ", "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=600&auto=format&fit=crop&q=80")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("publish_movie_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp)
    ) {
        // Screen Title Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CinemaRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "አዲስ የትርጉም ፊልም ልቀቅ",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "የተተረጎመ ፊልምዎን ለተመልካቾች በኮይን ያጋሩ",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Movie Amharic Title
                    Text(
                        text = "የፊልሙ ስም (በአማርኛ) *",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = titleAmharic,
                        onValueChange = { titleAmharic = it },
                        placeholder = { Text("ለምሳሌ፡ ግላዲያተር 2 (የትርጉም ፊልም)", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("publish_title_input"),
                        singleLine = true,
                        colors = outlinedTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Original English Title
                    Text(
                        text = "ኦሪጅናል ርዕስ (Original Title)",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = titleOriginal,
                        onValueChange = { titleOriginal = it },
                        placeholder = { Text("e.g. Gladiator II", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("publish_original_title_input"),
                        singleLine = true,
                        colors = outlinedTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Translator Selector
                    Text(
                        text = "አስተርጓሚ (Translator) *",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(translatorPresets) { trans ->
                            val isSelected = trans == translator
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CinemaRed else DarkSurfaceVariant)
                                    .clickable { translator = trans }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = trans,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Genre Selector
                    Text(
                        text = "የፊልሙ ዘውግ / ምድብ *",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(genrePresets) { g ->
                            val isSelected = g == genre
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AccentCyan else DarkSurfaceVariant)
                                    .clickable { genre = g }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = g,
                                    color = if (isSelected) Color.Black else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Duration & Year
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ርዝመት (ደቂቃ)",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = durationText,
                                onValueChange = { durationText = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = outlinedTextFieldColors()
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "የተለቀቀበት ዓመት",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = yearText,
                                onValueChange = { yearText = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = outlinedTextFieldColors()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Coin Price Selector
                    Text(
                        text = "የመክፈቻ የኮይን ዋጋ (Coin Price) *",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pricePresets.forEach { price ->
                            val isSelected = price == coinPrice
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CoinGold else DarkSurfaceVariant)
                                    .clickable { coinPrice = price }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = if (price == 0) "ነፃ (Free)" else "$price 🪙",
                                    color = if (isSelected) Color.Black else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Poster Preset Selector
                    Text(
                        text = "የፖስተር ፎቶ ምረጥ *",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(posterPresets) { (name, url) ->
                            val isSelected = url == posterUrl
                            Box(
                                modifier = Modifier
                                    .size(width = 70.dp, height = 95.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) CinemaRed else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { posterUrl = url }
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(CinemaRed)
                                            .align(Alignment.TopEnd)
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Description
                    Text(
                        text = "የፊልሙ አጭር መግለጫ / ታሪክ",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = descriptionAmharic,
                        onValueChange = { descriptionAmharic = it },
                        placeholder = { Text("ስለ ፊልሙ ታሪክ በአጭሩ ይግለጹ...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().height(90.dp).testTag("publish_description_input"),
                        maxLines = 4,
                        colors = outlinedTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Publish Button
                    Button(
                        onClick = {
                            if (titleAmharic.isBlank()) {
                                Toast.makeText(context, "እባክዎ የፊልሙን ርዕስ ያስገቡ", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isPublishing = true
                            movieViewModel.publishNewMovie(
                                titleAmharic = titleAmharic,
                                titleOriginal = titleOriginal,
                                translator = translator,
                                genre = genre,
                                durationMinutes = durationText.toIntOrNull() ?: 120,
                                releaseYear = yearText.toIntOrNull() ?: 2024,
                                coinPrice = coinPrice,
                                videoUrl = videoUrl,
                                posterUrl = posterUrl,
                                descriptionAmharic = descriptionAmharic,
                                uploaderName = uploaderName,
                                onSuccess = { id ->
                                    isPublishing = false
                                    Toast.makeText(context, "🎉 '${titleAmharic}' በተሳካ ሁኔታ ተለቋል!", Toast.LENGTH_LONG).show()
                                    // Reset fields
                                    titleAmharic = ""
                                    titleOriginal = ""
                                    descriptionAmharic = ""
                                },
                                onError = { err ->
                                    isPublishing = false
                                    Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("publish_movie_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Publish,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "አሁን ፊልሙን ልቀቅ (Publish Movie)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section: Already Published Movies by user
        if (userUploadedMovies.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "እርስዎ የለቀቋቸው ፊልሞች (${userUploadedMovies.size})",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(userUploadedMovies) { uploadedMovie ->
                MovieWideCard(
                    movie = uploadedMovie,
                    isUnlocked = true,
                    onClick = { onMoviePublished(uploadedMovie) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedBorderColor = CinemaRed,
    unfocusedBorderColor = DarkSurfaceVariant,
    focusedContainerColor = DarkSurfaceVariant,
    unfocusedContainerColor = DarkSurfaceVariant
)
