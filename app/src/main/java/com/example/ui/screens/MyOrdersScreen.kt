package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DesignOrder
import com.example.ui.components.bounceClick
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCardBg
import com.example.ui.theme.StudioCardBgVariant
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioObsidian
import com.example.ui.theme.StudioPurple
import com.example.ui.theme.TelebirrPrimary
import com.example.ui.theme.TextGrayLight
import com.example.ui.theme.TextGrayMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.StudioTab
import com.example.ui.viewmodel.StudioViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyOrdersScreen(
    viewModel: StudioViewModel,
    language: AppLanguage,
    onNavigateToChat: (Long) -> Unit,
    onNavigateToNewOrder: () -> Unit
) {
    val orders by viewModel.allOrders.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioObsidian)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (language == AppLanguage.AMHARIC) "የዲዛይን ትዕዛዞቼ" else "My Design Orders",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = if (language == AppLanguage.AMHARIC) "የስራዎችን ሂደት እና ረቂቆችን እዚህ ይከታተሉ" else "Track design progress and drafts",
                    color = TextGrayMuted,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = onNavigateToNewOrder,
                colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.bounceClick()
            ) {
                Icon(Icons.Default.Add, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (language == AppLanguage.AMHARIC) "አዲስ እዘዝ" else "New",
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = TextGrayMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (language == AppLanguage.AMHARIC) "ምንም የታዘዘ ስራ የለም" else "No active orders yet",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (language == AppLanguage.AMHARIC) "የመጀመሪያ ሎጎዎን ወይም ፎቶዎን አሁን በቴሌብር ይዘዙ" else "Order your first logo or photo edit with Telebirr",
                        color = TextGrayMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToNewOrder,
                        colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("አሁን እዘዝ", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(orders, key = { it.id }) { order ->
                    OrderTrackerCard(
                        order = order,
                        language = language,
                        onOpenChat = { onNavigateToChat(order.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderTrackerCard(
    order: DesignOrder,
    language: AppLanguage,
    onOpenChat: () -> Unit
) {
    val tierColor = when (order.tier) {
        "PRO" -> StudioGold
        "VIP" -> StudioPurple
        else -> StudioEmerald
    }

    val statusText = when (order.status) {
        "PENDING_VERIFICATION" -> if (language == AppLanguage.AMHARIC) "ክፍያ በመረጋገጥ ላይ..." else "Verifying Payment"
        "IN_DESIGN" -> if (language == AppLanguage.AMHARIC) "በዲዛይን ሂደት ላይ 🎨" else "In Design Queue 🎨"
        "DRAFT_READY" -> if (language == AppLanguage.AMHARIC) "ረቂቅ ዝግጁ ነው! ✨" else "Draft Ready! ✨"
        "COMPLETED" -> if (language == AppLanguage.AMHARIC) "ተጠናቋል ✓" else "Completed ✓"
        else -> order.status
    }

    val statusStep = when (order.status) {
        "PENDING_VERIFICATION" -> 1
        "IN_DESIGN" -> 2
        "DRAFT_READY" -> 3
        "COMPLETED" -> 4
        else -> 1
    }

    val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(order.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (order.isVipPriority) tierColor.copy(alpha = 0.6f) else StudioBorder,
                RoundedCornerShape(18.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Category, Tier badge, Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(tierColor.copy(alpha = 0.2f))
                            .border(1.dp, tierColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${order.tier} TIER",
                            color = tierColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = when (order.serviceCategory) {
                            "LOGO" -> "የሎጎ ዲዛይን"
                            "PHOTO" -> "ፎቶ ኤዲቲንግ"
                            "VIDEO" -> "ቪዲዮ ኤዲቲንግ"
                            "SOCIAL" -> "ሶሻል ሚዲያ"
                            else -> order.serviceCategory
                        },
                        color = TextGrayLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "${order.priceBirr.toInt()} ብር",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Project Title & Tagline
            Text(
                text = order.projectTitle,
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            if (order.taglineOrSlogan.isNotBlank()) {
                Text(
                    text = "\"${order.taglineOrSlogan}\"",
                    color = StudioCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Telebirr receipt tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0C1322))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = StudioEmerald,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ቴሌብር Txn: ${order.transactionId.take(16)} (0985273614)",
                    color = TextGrayMuted,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4-Step Progress Indicator
            OrderStepProgress(currentStep = statusStep, tierColor = tierColor)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ሁኔታ፡ $statusText",
                    color = if (statusStep == 3) StudioGold else if (statusStep == 4) StudioEmerald else StudioCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "⏱ ${order.estimatedDelivery}",
                    color = TextGrayLight,
                    fontSize = 11.sp
                )
            }

            // Draft thumbnail if available
            if (order.draftPreviewUrl != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, StudioGold, RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = order.draftPreviewUrl,
                        contentDescription = "Draft Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.8f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "የተላከ ረቂቅ (Preview Ready)",
                            color = StudioGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions: Open Chat with Designer
            Button(
                onClick = onOpenChat,
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick(),
                colors = ButtonDefaults.buttonColors(containerColor = StudioCardBgVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    tint = StudioCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.AMHARIC) "ከዲዛይነሩ ጋር አውራ (Chat with Designer)" else "Chat with Designer",
                    color = StudioCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OrderStepProgress(
    currentStep: Int,
    tierColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..4) {
            val isPassed = i <= currentStep
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (isPassed) tierColor else StudioBorder),
                contentAlignment = Alignment.Center
            ) {
                if (isPassed) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            if (i < 4) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .background(if (i < currentStep) tierColor else StudioBorder)
                )
            }
        }
    }
}
