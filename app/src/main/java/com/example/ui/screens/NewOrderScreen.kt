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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceType
import com.example.data.model.TierType
import com.example.ui.components.ReceiptScannerCard
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
import com.example.ui.theme.TextGrayLight
import com.example.ui.theme.TextGrayMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun NewOrderScreen(
    viewModel: StudioViewModel,
    language: AppLanguage,
    onOrderPlaced: (Long) -> Unit
) {
    val formState by viewModel.orderForm.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioObsidian),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step 1: Title Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(StudioCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("1", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.AMHARIC) "አገልግሎቱን ይምረጡ" else "Select Service",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Service selector tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ServiceType.values().forEach { service ->
                        val isSelected = formState.selectedService == service
                        val icon = when (service) {
                            ServiceType.LOGO_DESIGN -> Icons.Default.Brush
                            ServiceType.PHOTO_EDITING -> Icons.Default.CameraAlt
                            ServiceType.VIDEO_EDITING -> Icons.Default.Movie
                            ServiceType.SOCIAL_MEDIA -> Icons.Default.Share
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) StudioCyan.copy(alpha = 0.2f) else StudioCardBg)
                                .border(
                                    1.5.dp,
                                    if (isSelected) StudioCyan else StudioBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.selectService(service) }
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) StudioCyan else TextGrayMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when (service) {
                                        ServiceType.LOGO_DESIGN -> "ሎጎ"
                                        ServiceType.PHOTO_EDITING -> "ፎቶ"
                                        ServiceType.VIDEO_EDITING -> "ቪዲዮ"
                                        ServiceType.SOCIAL_MEDIA -> "ሶሻል"
                                    },
                                    color = if (isSelected) TextWhite else TextGrayMuted,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Step 2: Tier Selection (Normal, VIP, PRO)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(StudioGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("2", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.AMHARIC) "የጥቅል ደረጃ ይምረጡ (Tier)" else "Choose Tier",
                            color = TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Price display
                    Text(
                        text = "${formState.totalBirr.toInt()} ብር",
                        color = StudioCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tier Cards (Normal vs VIP vs Pro)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TierType.values().forEach { tier ->
                        val isSelected = formState.selectedTier == tier
                        val price = tier.getPriceFor(formState.selectedService)
                        val borderColor = when (tier) {
                            TierType.NORMAL -> StudioEmerald
                            TierType.VIP -> StudioPurple
                            TierType.PRO -> StudioGold
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) borderColor else StudioBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { viewModel.selectTier(tier) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) borderColor.copy(alpha = 0.12f) else StudioCardBg
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, if (isSelected) borderColor else TextGrayMuted, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(CircleShape)
                                                    .background(borderColor)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (language == AppLanguage.AMHARIC) tier.titleAmharic else tier.titleEnglish,
                                                color = TextWhite,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(borderColor.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = tier.badgeAmharic,
                                                    color = borderColor,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "⏱ ${tier.deliverySpeedAmharic} • ${tier.revisionsAmharic}",
                                            color = TextGrayLight,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "${price.toInt()} ብር",
                                    color = if (isSelected) borderColor else TextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // Step 3: Project Brief & Details Form
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(StudioPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("3", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.AMHARIC) "የዲዛይን ዝርዝር መረጃ (Brief)" else "Design Brief",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Project Title / Business Name
                OutlinedTextField(
                    value = formState.projectTitle,
                    onValueChange = { viewModel.updateOrderField(projectTitle = it) },
                    label = { Text("የድርጅቱ ወይም የፕሮጀክቱ ስም (Brand Name)") },
                    placeholder = { Text("ምሳሌ፡ አቢሲንያ ኮፊ፣ ቴክ ዞን...") },
                    leadingIcon = { Icon(Icons.Default.Title, null, tint = StudioCyan) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StudioCyan,
                        unfocusedBorderColor = StudioBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = StudioCardBg,
                        unfocusedContainerColor = StudioCardBg
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Slogan / Tagline
                OutlinedTextField(
                    value = formState.tagline,
                    onValueChange = { viewModel.updateOrderField(tagline = it) },
                    label = { Text("መፈክር ወይም መግለጫ (Tagline/Slogan)") },
                    placeholder = { Text("ምሳሌ፡ ጥራት ቅድሚያችን ነው") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StudioCyan,
                        unfocusedBorderColor = StudioBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = StudioCardBg,
                        unfocusedContainerColor = StudioCardBg
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Color / Style preference
                OutlinedTextField(
                    value = formState.colorStylePref,
                    onValueChange = { viewModel.updateOrderField(colorPref = it) },
                    label = { Text("የቀለም ምርጫና ስታይል (Colors & Style)") },
                    placeholder = { Text("ምሳሌ፡ ጥቁርና ወርቃማ፣ ሚኒማሊስት፣ 3D...") },
                    leadingIcon = { Icon(Icons.Default.ColorLens, null, tint = StudioGold) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StudioCyan,
                        unfocusedBorderColor = StudioBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = StudioCardBg,
                        unfocusedContainerColor = StudioCardBg
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Specific instructions / Brief
                OutlinedTextField(
                    value = formState.briefDescription,
                    onValueChange = { viewModel.updateOrderField(briefDescription = it) },
                    label = { Text("ልዩ የዲዛይን ፍላጎትና ማብራሪያ (Detailed Brief)") },
                    placeholder = { Text("ስለሚፈልጉት ዲዛይን በዝርዝር ይንገሩን...") },
                    leadingIcon = { Icon(Icons.Default.Description, null, tint = StudioPurple) },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StudioCyan,
                        unfocusedBorderColor = StudioBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = StudioCardBg,
                        unfocusedContainerColor = StudioCardBg
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Client Name & Phone
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = formState.clientName,
                        onValueChange = { viewModel.updateOrderField(clientName = it) },
                        label = { Text("ስምዎት") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioCyan,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = StudioCardBg,
                            unfocusedContainerColor = StudioCardBg
                        )
                    )
                    OutlinedTextField(
                        value = formState.clientPhone,
                        onValueChange = { viewModel.updateOrderField(clientPhone = it) },
                        label = { Text("ስልክ ቁጥር") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioCyan,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = StudioCardBg,
                            unfocusedContainerColor = StudioCardBg
                        )
                    )
                }
            }
        }

        // Step 4: Telebirr Payment & OCR Scanner
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(StudioEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("4", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.AMHARIC) "ክፍያና ደረሰኝ ስካነር (Telebirr)" else "Payment & Scanner",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                ReceiptScannerCard(
                    telebirrNumber = formState.telebirrAccount,
                    amountBirr = formState.totalBirr,
                    isScanning = formState.isScanning,
                    scanProgress = formState.scanProgress,
                    scanVerified = formState.scanVerified,
                    transactionId = formState.transactionId,
                    scanSummary = formState.scanResultSummary,
                    onTriggerScan = { viewModel.triggerReceiptScan(it) },
                    onCopyNumber = { viewModel.showToast("የቴሌብር ቁጥር (0985273614) ተቀድቷል") }
                )
            }
        }

        // Submit Button with Bouncy Spring Interaction!
        item {
            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = {
                    viewModel.submitOrder { orderId ->
                        onOrderPlaced(orderId)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .bounceClick(minScale = 0.92f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (formState.selectedTier.isPriority) StudioGold else StudioCyan
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = !formState.isSubmitting
            ) {
                if (formState.isSubmitting) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (formState.selectedTier.isPriority) Icons.Default.Bolt else Icons.Default.Send,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.AMHARIC) {
                                if (formState.selectedTier.isPriority) "ትዕዛዙን በVIP ቅድሚያ ላክ (${formState.totalBirr.toInt()} ብር) ⚡"
                                else "ትዕዛዙን ላክ (${formState.totalBirr.toInt()} ብር)"
                            } else {
                                "Submit Order (${formState.totalBirr.toInt()} ETB)"
                            },
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
