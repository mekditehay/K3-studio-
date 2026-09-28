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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceType
import com.example.data.model.TierType
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
import com.example.ui.theme.TelebirrSecondary
import com.example.ui.theme.TextGrayLight
import com.example.ui.theme.TextGrayMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun ServicesScreen(
    viewModel: StudioViewModel,
    language: AppLanguage,
    onSelectServiceAndTier: (ServiceType, TierType) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioObsidian),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Column {
                Text(
                    text = if (language == AppLanguage.AMHARIC) "የአገልግሎቶች እና ዋጋ ማውጫ" else "Services & Price Guide",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == AppLanguage.AMHARIC) "በቴሌብር (0985273614) ይክፈሉ • ደረሰኝ ስካን ያድርጉ • በፈጣን ያግኙ" else "Pay via Telebirr 0985273614 • Normal, VIP & PRO options",
                    color = TextGrayMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Each Service Detailed Breakdown
        items(ServiceType.values()) { service ->
            DetailedServiceCard(
                service = service,
                language = language,
                onSelectTier = { tier ->
                    onSelectServiceAndTier(service, tier)
                }
            )
        }
    }
}

@Composable
fun DetailedServiceCard(
    service: ServiceType,
    language: AppLanguage,
    onSelectTier: (TierType) -> Unit
) {
    val serviceIcon = when (service) {
        ServiceType.LOGO_DESIGN -> Icons.Default.Brush
        ServiceType.PHOTO_EDITING -> Icons.Default.CameraAlt
        ServiceType.VIDEO_EDITING -> Icons.Default.Movie
        ServiceType.SOCIAL_MEDIA -> Icons.Default.Share
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, StudioBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(StudioCyan.copy(alpha = 0.3f), StudioPurple.copy(alpha = 0.3f)))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = serviceIcon, contentDescription = null, tint = StudioCyan, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (language == AppLanguage.AMHARIC) service.titleAmharic else service.titleEnglish,
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = service.shortDescAmharic,
                        color = TextGrayLight,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pricing 3 Tiers breakdown
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Normal
                ServiceTierRow(
                    tier = TierType.NORMAL,
                    price = service.normalPrice,
                    accentColor = StudioEmerald,
                    onOrder = { onSelectTier(TierType.NORMAL) }
                )

                // VIP
                ServiceTierRow(
                    tier = TierType.VIP,
                    price = service.vipPrice,
                    accentColor = StudioPurple,
                    isFeatured = true,
                    onOrder = { onSelectTier(TierType.VIP) }
                )

                // PRO
                ServiceTierRow(
                    tier = TierType.PRO,
                    price = service.proPrice,
                    accentColor = StudioGold,
                    onOrder = { onSelectTier(TierType.PRO) }
                )
            }
        }
    }
}

@Composable
fun ServiceTierRow(
    tier: TierType,
    price: Double,
    accentColor: Color,
    isFeatured: Boolean = false,
    onOrder: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isFeatured) accentColor.copy(alpha = 0.12f) else StudioCardBgVariant)
            .border(1.dp, if (isFeatured) accentColor else StudioBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tier.titleAmharic,
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tier.badgeAmharic,
                            color = accentColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "⏱ ${tier.deliverySpeedAmharic} • ${tier.revisionsAmharic}",
                    color = TextGrayMuted,
                    fontSize = 10.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${price.toInt()} ብር",
                    color = if (isFeatured) accentColor else TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onOrder,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.bounceClick()
                ) {
                    Text(
                        text = "እዘዝ",
                        color = if (tier == TierType.NORMAL || tier == TierType.PRO) Color.Black else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
