package com.example.ui.components

import android.net.Uri
import android.widget.FrameLayout
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.Movie
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CoinGold
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun CustomVideoPlayer(
    movie: Movie,
    isUnlocked: Boolean,
    onUnlockClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFullscreen: Boolean = false,
    onToggleFullscreen: () -> Unit = {},
    onProgressUpdate: (progressSec: Long, durationSec: Long) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }
    var showControls by remember { mutableStateOf(true) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var selectedAudioTrack by remember { mutableStateOf("አማርኛ ትርጉም (ዋና)") }
    var selectedQuality by remember { mutableStateOf("1080p Full HD") }
    var showAudioMenu by remember { mutableStateOf(false) }
    var showQualityMenu by remember { mutableStateOf(false) }

    // Auto-hide controls timer
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(4000)
            showControls = false
        }
    }

    // Periodic progress tracker
    LaunchedEffect(videoViewRef, isPlaying, isUnlocked) {
        while (true) {
            delay(1000)
            videoViewRef?.let { vv ->
                if (vv.isPlaying) {
                    val pos = vv.currentPosition.toLong()
                    val dur = vv.duration.toLong()
                    currentPositionMs = pos
                    if (dur > 0) {
                        durationMs = dur
                        onProgressUpdate(pos / 1000, dur / 1000)
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isFullscreen) Modifier.fillMaxSize() else Modifier.aspectRatio(16f / 9f)
            )
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
            .testTag("video_player_container"),
        contentAlignment = Alignment.Center
    ) {
        if (isUnlocked) {
            // Android VideoView for actual video streaming
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    VideoView(ctx).apply {
                        val layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                        this.layoutParams = layoutParams

                        val uri = Uri.parse(
                            if (movie.videoUrl.isNotBlank()) movie.videoUrl
                            else "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
                        )
                        setVideoURI(uri)

                        setOnPreparedListener { mp ->
                            isBuffering = false
                            durationMs = mp.duration.toLong()
                            mp.isLooping = true
                            start()
                            isPlaying = true
                        }

                        setOnInfoListener { _, what, _ ->
                            if (what == android.media.MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                                isBuffering = true
                            } else if (what == android.media.MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                                isBuffering = false
                            }
                            false
                        }

                        setOnErrorListener { _, _, _ ->
                            isBuffering = false
                            true
                        }

                        videoViewRef = this
                    }
                },
                update = { view ->
                    videoViewRef = view
                }
            )

            DisposableEffect(Unit) {
                onDispose {
                    videoViewRef?.stopPlayback()
                    videoViewRef = null
                }
            }
        } else {
            // Locked Preview View
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1B0507), Color(0xFF0F0B18))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(CoinGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "ተቆልፏል",
                            tint = CoinGold,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "ይህ የትርጉም ፊልም የተቆለፈ ነው",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "ሙሉ ፊልሙን በከፍተኛ ጥራት ለማየት በኮይን ይክፈቱ",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CinemaRed)
                            .clickable(onClick = onUnlockClick)
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                            .testTag("unlock_in_player_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = CoinGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "በ ${movie.coinPrice} ኮይን ክፈት (Unlock)",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Buffering Indicator
        if (isUnlocked && isBuffering) {
            CircularProgressIndicator(
                color = CinemaRed,
                strokeWidth = 3.dp,
                modifier = Modifier.size(44.dp)
            )
        }

        // Overlay Controls
        if (isUnlocked) {
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    // Top Controls Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Title & Translator Chip
                        Column {
                            Text(
                                text = movie.titleAmharic,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ትርጉም: ${movie.translator}",
                                    color = CinemaRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• K3 Movie Stream",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Audio & Quality Switchers
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Audio Track Selector
                            Box {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.Black.copy(alpha = 0.6f))
                                        .clickable { showAudioMenu = true }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "ድምፅ 🔊",
                                        color = AccentCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                DropdownMenu(
                                    expanded = showAudioMenu,
                                    onDismissRequest = { showAudioMenu = false },
                                    modifier = Modifier.background(DarkBackground)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("አማርኛ ትርጉም (ዋና)", color = TextPrimary, fontSize = 12.sp) },
                                        onClick = {
                                            selectedAudioTrack = "አማርኛ ትርጉም (ዋና)"
                                            showAudioMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("ኦሪጅናል ድምፅ (Original)", color = TextPrimary, fontSize = 12.sp) },
                                        onClick = {
                                            selectedAudioTrack = "ኦሪጅናል ድምፅ (Original)"
                                            showAudioMenu = false
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Quality Selector
                            Box {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.Black.copy(alpha = 0.6f))
                                        .clickable { showQualityMenu = true }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = selectedQuality.split(" ").first(),
                                        color = CoinGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                DropdownMenu(
                                    expanded = showQualityMenu,
                                    onDismissRequest = { showQualityMenu = false },
                                    modifier = Modifier.background(DarkBackground)
                                ) {
                                    listOf("1080p Full HD", "720p HD", "480p SD").forEach { quality ->
                                        DropdownMenuItem(
                                            text = { Text(quality, color = TextPrimary, fontSize = 12.sp) },
                                            onClick = {
                                                selectedQuality = quality
                                                showQualityMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Center Play/Pause Controls
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rewind 10s
                        IconButton(
                            onClick = {
                                videoViewRef?.let { vv ->
                                    val newPos = (vv.currentPosition - 10000).coerceAtLeast(0)
                                    vv.seekTo(newPos)
                                    currentPositionMs = newPos.toLong()
                                }
                            },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastRewind,
                                contentDescription = "ወደ ኋላ 10 ሰከንድ",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        // Play/Pause
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(CinemaRed)
                                .clickable {
                                    videoViewRef?.let { vv ->
                                        if (vv.isPlaying) {
                                            vv.pause()
                                            isPlaying = false
                                        } else {
                                            vv.start()
                                            isPlaying = true
                                        }
                                    }
                                }
                                .testTag("play_pause_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "አቁም" else "አጫውት",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        // Fast Forward 10s
                        IconButton(
                            onClick = {
                                videoViewRef?.let { vv ->
                                    val dur = vv.duration
                                    val newPos = (vv.currentPosition + 10000).coerceAtMost(dur)
                                    vv.seekTo(newPos)
                                    currentPositionMs = newPos.toLong()
                                }
                            },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastForward,
                                contentDescription = "ወደ ፊት 10 ሰከንድ",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Bottom Seekbar & Actions
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        // Slider
                        val progress = if (durationMs > 0) (currentPositionMs.toFloat() / durationMs.toFloat()) else 0f
                        Slider(
                            value = progress.coerceIn(0f, 1f),
                            onValueChange = { frac ->
                                val targetMs = (frac * durationMs).toLong()
                                videoViewRef?.seekTo(targetMs.toInt())
                                currentPositionMs = targetMs
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = CinemaRed,
                                activeTrackColor = CinemaRed,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Timestamp
                            Text(
                                text = "${formatTime(currentPositionMs)} / ${formatTime(durationMs)}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )

                            // Fullscreen Toggle
                            IconButton(
                                onClick = onToggleFullscreen,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                    contentDescription = "ሙሉ ማያ",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
