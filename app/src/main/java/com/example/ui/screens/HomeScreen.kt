package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.PortfolioItem
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
import com.example.ui.theme.StudioPink
import com.example.ui.theme.StudioPurple
import com.example.ui.theme.TelebirrPrimary
import com.example.ui.theme.TelebirrSecondary
import com.example.ui.theme.TextGrayLight
import com.example.ui.theme.TextGrayMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.StudioTab
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun HomeScreen(
    viewModel: StudioViewModel,
    language: AppLanguage,
    onNavigateToOrder: (ServiceType, TierType) -> Unit,
    onNavigateToPortfolioOrder: (PortfolioItem) -> Unit
) {
    var selectedFilterCategory by remember { mutableStateOf<ServiceType?>(null) }
    val portfolioList = viewModel.portfolioItems.filter {
        selectedFilterCategory == null || it.category == selectedFilterCategory
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioObsidian),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Visual Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(22.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.studio_hero_banner_1790565690332),
                    contentDescription = "Studio Hero",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    StudioObsidian.copy(alpha = 0.5f),
                                    StudioObsidian.copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(StudioGold.copy(alpha = 0.25f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = StudioGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == AppLanguage.AMHARIC) "ፈጣን የVIP ዲዛይን አገልግሎት" else "VIP Express Turnaround",
                            color = StudioGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (language == AppLanguage.AMHARIC) "ዘመናዊ የሎጎና ሚዲያ ዲዛይን ስቱዲዮ" else "Modern Logo & Creative Design Studio",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (language == AppLanguage.AMHARIC) "በቴሌብር (0985273614) ይክፈሉ • ደረሰኝ ስካን ያድርጉ • በፈጣን ያግኙ" else "Pay with Telebirr 0985273614 • Auto Receipt Scan • Fast Delivery",
                        color = TextGrayLight,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Telebirr Quick Pay Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                TelebirrPrimary.copy(alpha = 0.25f),
                                TelebirrSecondary.copy(alpha = 0.2f)
                            )
                        )
                    )
                    .border(1.dp, TelebirrPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TelebirrPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ቴሌብር ክፍያ (Telebirr Pay)",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "0985273614 (K3 Creative)",
                                color = TelebirrSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.selectService(ServiceType.LOGO_DESIGN)
                            viewModel.selectTier(TierType.VIP)
                            viewModel.setTab(StudioTab.NEW_ORDER)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TelebirrPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.bounceClick()
                    ) {
                        Text(
                            text = if (language == AppLanguage.AMHARIC) "አሁን እዘዝ" else "Order Now",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Section Title: Services & Pricing
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.AMHARIC) "የአገልግሎቶች ዋጋ ዝርዝር" else "Services & Pricing Tiers",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (language == AppLanguage.AMHARIC) "Normal • VIP • PRO" else "Normal • VIP • PRO",
                        color = StudioCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (language == AppLanguage.AMHARIC) "የሚፈልጉትን አገልግሎት ይምረጡና በቀጥታ በቴሌብር ይዘዙ" else "Select your package and order directly with Telebirr",
                    color = TextGrayMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Service Packages Cards
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ServiceType.values().forEach { service ->
                    ServicePackageCard(
                        service = service,
                        language = language,
                        onSelectPackage = { tier ->
                            onNavigateToOrder(service, tier)
                        }
                    )
                }
            }
        }

        // VIP Perks Callout Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                StudioPurple.copy(alpha = 0.25f),
                                StudioCyan.copy(alpha = 0.15f)
                            )
                        )
                    )
                    .border(1.dp, StudioPurple.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = StudioGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.AMHARIC) "የVIP እና PRO ጥቅሞች" else "VIP & PRO Perks",
                            color = StudioGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PerkItem(
                            title = if (language == AppLanguage.AMHARIC) "ፈጣን ርክክብ" else "Super Fast",
                            desc = if (language == AppLanguage.AMHARIC) "በ6 - 24 ሰዓት" else "6-24 Hours",
                            icon = Icons.Default.Bolt,
                            color = StudioCyan
                        )
                        PerkItem(
                            title = if (language == AppLanguage.AMHARIC) "ያልተገደበ እይታ" else "Unlimited",
                            desc = if (language == AppLanguage.AMHARIC) "እስኪወዱ ድረስ" else "Free Revisions",
                            icon = Icons.Default.Check,
                            color = StudioEmerald
                        )
                        PerkItem(
                            title = if (language == AppLanguage.AMHARIC) "ዋና ቬክተር" else "Source Files",
                            desc = if (language == AppLanguage.AMHARIC) "AI, PSD, PNG" else "Vector / Master",
                            icon = Icons.Default.AutoAwesome,
                            color = StudioGold
                        )
                    }
                }
            }
        }

        // Section Title: Portfolio Showcase
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.AMHARIC) "የተሰሩ ስራዎች (Portfolio)" else "Studio Portfolio",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${portfolioList.size} ስራዎች",
                        color = TextGrayMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Filter Pills Row
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterPill(
                        title = if (language == AppLanguage.AMHARIC) "ሁሉም" else "All",
                        isSelected = selectedFilterCategory == null,
                        onClick = { selectedFilterCategory = null }
                    )
                }
                items(ServiceType.values()) { cat ->
                    FilterPill(
                        title = if (language == AppLanguage.AMHARIC) cat.titleAmharic.substringBefore(" (") else cat.titleEnglish,
                        isSelected = selectedFilterCategory == cat,
                        onClick = { selectedFilterCategory = cat }
                    )
                }
            }
        }

        // Portfolio Showcase Grid/Row
        item {
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(portfolioList) { item ->
                    PortfolioCard(
                        item = item,
                        language = language,
                        onOrderSimilar = { onNavigateToPortfolioOrder(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun ServicePackageCard(
    service: ServiceType,
    language: AppLanguage,
    onSelectPackage: (TierType) -> Unit
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
            .border(1.dp, StudioBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Service Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(StudioCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = serviceIcon,
                        contentDescription = null,
                        tint = StudioCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == AppLanguage.AMHARIC) service.titleAmharic else service.titleEnglish,
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = service.shortDescAmharic,
                        color = TextGrayMuted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pricing 3-Tier Badges with Bouncing Click!
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Normal Tier
                TierPriceButton(
                    modifier = Modifier.weight(1f),
                    tierTitle = "Normal",
                    priceBirr = service.normalPrice,
                    badgeColor = StudioEmerald,
                    onClick = { onSelectPackage(TierType.NORMAL) }
                )

                // VIP Tier
                TierPriceButton(
                    modifier = Modifier.weight(1.1f),
                    tierTitle = "VIP ⚡",
                    priceBirr = service.vipPrice,
                    badgeColor = StudioPurple,
                    isHighlighted = true,
                    onClick = { onSelectPackage(TierType.VIP) }
                )

                // PRO Tier
                TierPriceButton(
                    modifier = Modifier.weight(1f),
                    tierTitle = "PRO 👑",
                    priceBirr = service.proPrice,
                    badgeColor = StudioGold,
                    onClick = { onSelectPackage(TierType.PRO) }
                )
            }
        }
    }
}

@Composable
fun TierPriceButton(
    modifier: Modifier = Modifier,
    tierTitle: String,
    priceBirr: Double,
    badgeColor: Color,
    isHighlighted: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isHighlighted) badgeColor.copy(alpha = 0.2f) else StudioCardBgVariant
            )
            .border(
                1.dp,
                if (isHighlighted) badgeColor else StudioBorder,
                RoundedCornerShape(12.dp)
            )
            .bounceClick(minScale = 0.90f) { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = tierTitle,
                color = badgeColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "${priceBirr.toInt()} ብር",
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun PerkItem(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = title, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = desc, color = TextGrayMuted, fontSize = 10.sp)
    }
}

@Composable
fun FilterPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) StudioCyan else StudioCardBg)
            .border(1.dp, if (isSelected) StudioCyan else StudioBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.Black else TextGrayLight,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
        )
    }
}

@Composable
fun PortfolioCard(
    item: PortfolioItem,
    language: AppLanguage,
    onOrderSimilar: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .border(1.dp, StudioBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.titleEnglish,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Tag badge
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(StudioObsidian.copy(alpha = 0.75f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.tag,
                        color = StudioGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (language == AppLanguage.AMHARIC) item.titleAmharic else item.titleEnglish,
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.clientName,
                    color = TextGrayMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onOrderSimilar,
                    colors = ButtonDefaults.buttonColors(containerColor = StudioCardBgVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .bounceClick()
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = StudioCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.AMHARIC) "ይህን አይነት እዘዝ" else "Order Similar",
                        color = StudioCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
