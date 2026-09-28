package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.ui.viewmodel.StudioViewModel

enum class DeskFilter {
    ALL, VIP_URGENT, PENDING_VERIFICATION, IN_DESIGN
}

@Composable
fun DesignerDeskScreen(
    viewModel: StudioViewModel,
    language: AppLanguage,
    onOpenClientChat: (Long) -> Unit
) {
    val orders by viewModel.allOrders.collectAsState()
    val activeVipOrders by viewModel.activeVipOrders.collectAsState()
    val pendingCount by viewModel.pendingCount.collectAsState()

    var activeFilter by remember { mutableStateOf(DeskFilter.ALL) }
    var orderForDraftDelivery by remember { mutableStateOf<DesignOrder?>(null) }
    var draftNotesText by remember { mutableStateOf("") }
    var draftUrlText by remember { mutableStateOf("https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=80") }

    val filteredOrders = orders.filter { order ->
        when (activeFilter) {
            DeskFilter.ALL -> true
            DeskFilter.VIP_URGENT -> order.isVipPriority && order.status != "COMPLETED"
            DeskFilter.PENDING_VERIFICATION -> order.status == "PENDING_VERIFICATION"
            DeskFilter.IN_DESIGN -> order.status == "IN_DESIGN"
        }
    }

    val totalRevenue = orders.sumOf { it.priceBirr }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioObsidian)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Desk Title & VIP Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(StudioPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (language == AppLanguage.AMHARIC) "የዲዛይነር መቆጣጠሪያ ዴስክ" else "Designer Hub Console",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "የVIP እና ኖርማል ትዕዛዞች ማስተዳደሪያ",
                        color = TextGrayMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Urgent VIP Alert Banner if VIP orders exist!
        if (activeVipOrders.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                StudioGold.copy(alpha = 0.35f * pulseAlpha),
                                StudioPurple.copy(alpha = 0.25f)
                            )
                        )
                    )
                    .border(1.5.dp, StudioGold, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = StudioGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚡ አስቸኳይ VIP ትዕዛዝ (${activeVipOrders.size} ስራዎች)!",
                            color = StudioGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "ደንበኞች ክፍያ ፈጽመዋል፤ በቅድሚያ እንዲሰሩ ይፈለጋል",
                            color = TextWhite,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Live Statistics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DeskStatCard(
                modifier = Modifier.weight(1f),
                title = "ትዕዛዞች",
                value = orders.size.toString(),
                accentColor = StudioCyan
            )
            DeskStatCard(
                modifier = Modifier.weight(1f),
                title = "VIP አስቸኳይ",
                value = activeVipOrders.size.toString(),
                accentColor = StudioGold
            )
            DeskStatCard(
                modifier = Modifier.weight(1.2f),
                title = "ቴሌብር ገቢ",
                value = "${totalRevenue.toInt()} ብር",
                accentColor = StudioEmerald
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Pills
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterTabPill(
                    title = "ሁሉም (${orders.size})",
                    isSelected = activeFilter == DeskFilter.ALL,
                    onClick = { activeFilter = DeskFilter.ALL }
                )
            }
            item {
                FilterTabPill(
                    title = "VIP አስቸኳይ ⚡ (${activeVipOrders.size})",
                    isSelected = activeFilter == DeskFilter.VIP_URGENT,
                    onClick = { activeFilter = DeskFilter.VIP_URGENT }
                )
            }
            item {
                FilterTabPill(
                    title = "ማረጋገጫ (${pendingCount})",
                    isSelected = activeFilter == DeskFilter.PENDING_VERIFICATION,
                    onClick = { activeFilter = DeskFilter.PENDING_VERIFICATION }
                )
            }
            item {
                FilterTabPill(
                    title = "በስራ ላይ",
                    isSelected = activeFilter == DeskFilter.IN_DESIGN,
                    onClick = { activeFilter = DeskFilter.IN_DESIGN }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Orders List for Designer
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredOrders, key = { it.id }) { order ->
                DesignerOrderCard(
                    order = order,
                    onApprovePayment = {
                        viewModel.updateOrderStatusFromDesk(order.id, "IN_DESIGN", "የቴሌብር ክፍያ ተረጋግጧል፤ ስራ ተጀምሯል")
                    },
                    onOpenDraftDialog = {
                        orderForDraftDelivery = order
                        draftNotesText = "የመጀመሪያ ዙር ረቂቅ ዲዛይን ተጠናቋል"
                    },
                    onMarkCompleted = {
                        viewModel.updateOrderStatusFromDesk(order.id, "COMPLETED", "ስራው ተጠናቆ ዋናዎቹ ፋይሎች ተልከዋል")
                    },
                    onOpenChat = {
                        onOpenClientChat(order.id)
                    }
                )
            }
        }
    }

    // Deliver Draft Dialog
    if (orderForDraftDelivery != null) {
        val targetOrder = orderForDraftDelivery!!
        AlertDialog(
            onDismissRequest = { orderForDraftDelivery = null },
            title = {
                Text(
                    text = "ረቂቅ ለደንበኛው ላክ (#${targetOrder.id})",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "ለ፡ ${targetOrder.clientName} (${targetOrder.projectTitle})",
                        color = TextGrayLight,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = draftNotesText,
                        onValueChange = { draftNotesText = it },
                        label = { Text("ለደንበኛው የሚላክ ማስታወሻ") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioCyan,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = draftUrlText,
                        onValueChange = { draftUrlText = it },
                        label = { Text("የረቂቁ ፎቶ ማስፈንጠሪያ (Draft URL)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioCyan,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deliverDraftFromDesk(targetOrder.id, draftUrlText, draftNotesText)
                        orderForDraftDelivery = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                    modifier = Modifier.bounceClick()
                ) {
                    Text("ረቂቁን ላክ ✨", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { orderForDraftDelivery = null }) {
                    Text("ተመለስ", color = TextGrayLight)
                }
            },
            containerColor = StudioCardBg,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun DeskStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    accentColor: Color
) {
    Card(
        modifier = modifier.border(1.dp, StudioBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = TextGrayMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = accentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun FilterTabPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) StudioCyan else StudioCardBg)
            .border(1.dp, if (isSelected) StudioCyan else StudioBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.Black else TextGrayLight,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun DesignerOrderCard(
    order: DesignOrder,
    onApprovePayment: () -> Unit,
    onOpenDraftDialog: () -> Unit,
    onMarkCompleted: () -> Unit,
    onOpenChat: () -> Unit
) {
    val tierColor = when (order.tier) {
        "PRO" -> StudioGold
        "VIP" -> StudioPurple
        else -> StudioEmerald
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (order.isVipPriority) tierColor else StudioBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(tierColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${order.tier} TIER",
                            color = tierColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "#${order.id} ${order.clientName} (${order.clientPhone})",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${order.priceBirr.toInt()} ብር",
                    color = StudioCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Project & Brief
            Text(
                text = order.projectTitle,
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "መመሪያ፡ ${order.briefDescription}",
                color = TextGrayLight,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
            if (order.colorStylePref.isNotBlank()) {
                Text(
                    text = "ቀለም/ስታይል፡ ${order.colorStylePref}",
                    color = StudioCyan,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Telebirr Receipt OCR Status
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0C1322))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StudioEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ቴሌብር 0985273614 | Txn: ${order.transactionId}",
                        color = TextGrayLight,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Designer Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (order.status == "PENDING_VERIFICATION") {
                    Button(
                        onClick = onApprovePayment,
                        colors = ButtonDefaults.buttonColors(containerColor = TelebirrPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .bounceClick()
                    ) {
                        Text("ክፍያ አረጋግጥ & ጀምር", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (order.status == "IN_DESIGN") {
                    Button(
                        onClick = onOpenDraftDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = StudioGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .bounceClick()
                    ) {
                        Text("ረቂቅ ላክ ✨", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (order.status == "DRAFT_READY") {
                    Button(
                        onClick = onMarkCompleted,
                        colors = ButtonDefaults.buttonColors(containerColor = StudioEmerald),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .bounceClick()
                    ) {
                        Text("ስራውን አጠናቅቅ ✓", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Chat button
                OutlinedButton(
                    onClick = onOpenChat,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.bounceClick()
                ) {
                    Icon(Icons.Default.Chat, null, tint = StudioCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("አውራ", fontSize = 11.sp, color = StudioCyan)
                }
            }
        }
    }
}
