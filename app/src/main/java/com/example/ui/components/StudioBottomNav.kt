package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.DesignServices
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkSurface
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioObsidian
import com.example.ui.theme.TextGrayLight
import com.example.ui.theme.TextGrayMuted
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.StudioTab

@Composable
fun StudioBottomNav(
    currentTab: StudioTab,
    language: AppLanguage,
    unreadCount: Int,
    onTabSelected: (StudioTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .border(width = 0.8.dp, color = StudioBorder),
        containerColor = StudioDarkSurface,
        tonalElevation = 8.dp
    ) {
        // 1. Home
        NavigationBarItem(
            selected = currentTab == StudioTab.STUDIO,
            onClick = { onTabSelected(StudioTab.STUDIO) },
            icon = {
                Icon(
                    imageVector = if (currentTab == StudioTab.STUDIO) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = if (language == AppLanguage.AMHARIC) "ስቱዲዮ" else "Studio",
                    fontSize = 10.sp,
                    fontWeight = if (currentTab == StudioTab.STUDIO) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StudioCyan,
                selectedTextColor = StudioCyan,
                unselectedIconColor = TextGrayMuted,
                unselectedTextColor = TextGrayMuted,
                indicatorColor = StudioCyan.copy(alpha = 0.15f)
            )
        )

        // 2. Services / Pricing
        NavigationBarItem(
            selected = currentTab == StudioTab.SERVICES,
            onClick = { onTabSelected(StudioTab.SERVICES) },
            icon = {
                Icon(
                    imageVector = if (currentTab == StudioTab.SERVICES) Icons.Filled.DesignServices else Icons.Outlined.DesignServices,
                    contentDescription = "Services"
                )
            },
            label = {
                Text(
                    text = if (language == AppLanguage.AMHARIC) "አገልግሎቶች" else "Services",
                    fontSize = 10.sp,
                    fontWeight = if (currentTab == StudioTab.SERVICES) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StudioCyan,
                selectedTextColor = StudioCyan,
                unselectedIconColor = TextGrayMuted,
                unselectedTextColor = TextGrayMuted,
                indicatorColor = StudioCyan.copy(alpha = 0.15f)
            )
        )

        // 3. New Order (Prominent center action button)
        NavigationBarItem(
            selected = currentTab == StudioTab.NEW_ORDER,
            onClick = { onTabSelected(StudioTab.NEW_ORDER) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(StudioCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Order",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(34.dp)
                            .padding(2.dp)
                    )
                }
            },
            label = {
                Text(
                    text = if (language == AppLanguage.AMHARIC) "አዲስ እዘዝ" else "Order",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioCyan
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StudioCyan,
                selectedTextColor = StudioCyan,
                unselectedIconColor = StudioCyan,
                unselectedTextColor = StudioCyan,
                indicatorColor = Color.Transparent
            )
        )

        // 4. My Orders
        NavigationBarItem(
            selected = currentTab == StudioTab.MY_ORDERS,
            onClick = { onTabSelected(StudioTab.MY_ORDERS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == StudioTab.MY_ORDERS) Icons.Filled.Receipt else Icons.Outlined.Receipt,
                    contentDescription = "Orders"
                )
            },
            label = {
                Text(
                    text = if (language == AppLanguage.AMHARIC) "ትዕዛዞቼ" else "Orders",
                    fontSize = 10.sp,
                    fontWeight = if (currentTab == StudioTab.MY_ORDERS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StudioCyan,
                selectedTextColor = StudioCyan,
                unselectedIconColor = TextGrayMuted,
                unselectedTextColor = TextGrayMuted,
                indicatorColor = StudioCyan.copy(alpha = 0.15f)
            )
        )

        // 5. Chat with Designer
        NavigationBarItem(
            selected = currentTab == StudioTab.CHAT,
            onClick = { onTabSelected(StudioTab.CHAT) },
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge(
                                containerColor = StudioGold,
                                contentColor = Color.Black
                            ) {
                                Text(unreadCount.toString(), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == StudioTab.CHAT) Icons.Filled.Chat else Icons.Outlined.Chat,
                        contentDescription = "Chat"
                    )
                }
            },
            label = {
                Text(
                    text = if (language == AppLanguage.AMHARIC) "ውይይት" else "Chat",
                    fontSize = 10.sp,
                    fontWeight = if (currentTab == StudioTab.CHAT) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StudioCyan,
                selectedTextColor = StudioCyan,
                unselectedIconColor = TextGrayMuted,
                unselectedTextColor = TextGrayMuted,
                indicatorColor = StudioCyan.copy(alpha = 0.15f)
            )
        )
    }
}
