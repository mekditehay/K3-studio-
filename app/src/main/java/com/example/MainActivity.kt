package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ProfileDialog
import com.example.ui.components.StudioBottomNav
import com.example.ui.components.StudioTopBar
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DesignerDeskScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyOrdersScreen
import com.example.ui.screens.NewOrderScreen
import com.example.ui.screens.ServicesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioObsidian
import com.example.ui.viewmodel.StudioTab
import com.example.ui.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
    private val studioViewModel: StudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                K3DesignStudioApp(studioViewModel = studioViewModel)
            }
        }
    }
}

@Composable
fun K3DesignStudioApp(studioViewModel: StudioViewModel) {
    val context = LocalContext.current
    val currentTab by studioViewModel.currentTab.collectAsState()
    val language by studioViewModel.language.collectAsState()
    val userProfile by studioViewModel.userProfile.collectAsState()
    val unreadCount by studioViewModel.unreadChatCount.collectAsState()
    val activeVipOrders by studioViewModel.activeVipOrders.collectAsState()
    val snackbarMsg by studioViewModel.snackBarMessage.collectAsState()

    var showProfileDialog by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            studioViewModel.clearToast()
        }
    }

    // Handle back button on secondary screens
    if (currentTab != StudioTab.STUDIO) {
        BackHandler {
            studioViewModel.setTab(StudioTab.STUDIO)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = StudioObsidian,
        topBar = {
            StudioTopBar(
                language = language,
                currentTab = currentTab,
                pendingVipCount = activeVipOrders.size,
                userProfile = userProfile,
                onToggleLanguage = { studioViewModel.toggleLanguage() },
                onTabSelected = { studioViewModel.setTab(it) },
                onProfileClick = { showProfileDialog = true }
            )
        },
        bottomBar = {
            StudioBottomNav(
                currentTab = currentTab,
                language = language,
                unreadCount = unreadCount,
                onTabSelected = { studioViewModel.setTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "tabCrossfade") { tab ->
                when (tab) {
                    StudioTab.STUDIO -> {
                        HomeScreen(
                            viewModel = studioViewModel,
                            language = language,
                            onNavigateToOrder = { service, tier ->
                                studioViewModel.selectService(service)
                                studioViewModel.selectTier(tier)
                                studioViewModel.setTab(StudioTab.NEW_ORDER)
                            },
                            onNavigateToPortfolioOrder = { portfolioItem ->
                                studioViewModel.populateFromPortfolio(portfolioItem)
                            }
                        )
                    }

                    StudioTab.SERVICES -> {
                        ServicesScreen(
                            viewModel = studioViewModel,
                            language = language,
                            onSelectServiceAndTier = { service, tier ->
                                studioViewModel.selectService(service)
                                studioViewModel.selectTier(tier)
                                studioViewModel.setTab(StudioTab.NEW_ORDER)
                            }
                        )
                    }

                    StudioTab.NEW_ORDER -> {
                        NewOrderScreen(
                            viewModel = studioViewModel,
                            language = language,
                            onOrderPlaced = { orderId ->
                                studioViewModel.setTab(StudioTab.MY_ORDERS)
                            }
                        )
                    }

                    StudioTab.MY_ORDERS -> {
                        MyOrdersScreen(
                            viewModel = studioViewModel,
                            language = language,
                            onNavigateToChat = { orderId ->
                                studioViewModel.selectOrderForChat(orderId)
                                studioViewModel.setTab(StudioTab.CHAT)
                            },
                            onNavigateToNewOrder = {
                                studioViewModel.setTab(StudioTab.NEW_ORDER)
                            }
                        )
                    }

                    StudioTab.CHAT -> {
                        ChatScreen(
                            viewModel = studioViewModel,
                            language = language
                        )
                    }

                    StudioTab.DESIGNER_DESK -> {
                        DesignerDeskScreen(
                            viewModel = studioViewModel,
                            language = language,
                            onOpenClientChat = { orderId ->
                                studioViewModel.selectOrderForChat(orderId)
                                studioViewModel.setTab(StudioTab.CHAT)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showProfileDialog) {
        ProfileDialog(
            profile = userProfile,
            language = language,
            onDismiss = { showProfileDialog = false },
            onSimulateLogin = { studioViewModel.simulateGoogleLogin() }
        )
    }
}
