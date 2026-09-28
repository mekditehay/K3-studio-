package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.DesignOrder
import com.example.data.model.PortfolioItem
import com.example.data.model.ServiceType
import com.example.data.model.TierType
import com.example.data.repository.ReceiptScanResult
import com.example.data.repository.StudioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppLanguage {
    AMHARIC, ENGLISH
}

enum class StudioTab {
    STUDIO, SERVICES, NEW_ORDER, MY_ORDERS, CHAT, DESIGNER_DESK
}

data class OrderFormState(
    val selectedService: ServiceType = ServiceType.LOGO_DESIGN,
    val selectedTier: TierType = TierType.VIP,
    val projectTitle: String = "",
    val tagline: String = "",
    val briefDescription: String = "",
    val colorStylePref: String = "",
    val clientName: String = "አበበ ደስታ",
    val clientEmail: String = "kduse378@gmail.com",
    val clientPhone: String = "0912345678",
    val paymentMethod: String = "TELEBIRR",
    val telebirrAccount: String = "0985273614",
    val transactionId: String = "",
    val receiptScreenshotUri: String = "",
    val isScanning: Boolean = false,
    val scanProgress: Float = 0f,
    val scanVerified: Boolean = false,
    val scanResultSummary: String = "",
    val isSubmitting: Boolean = false
) {
    val totalBirr: Double
        get() = selectedTier.getPriceFor(selectedService)
}

data class GoogleUserProfile(
    val name: String = "Kidus E.",
    val email: String = "kduse378@gmail.com",
    val phone: String = "0912345678",
    val avatarUrl: String = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
    val isSignedIn: Boolean = true
)

class StudioViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = StudioRepository(database.orderDao(), database.chatDao())

    val allOrders: StateFlow<List<DesignOrder>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeVipOrders: StateFlow<List<DesignOrder>> = repository.activeVipOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingCount: StateFlow<Int> = repository.pendingVerificationCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val unreadChatCount: StateFlow<Int> = repository.unreadMessageCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _currentTab = MutableStateFlow(StudioTab.STUDIO)
    val currentTab: StateFlow<StudioTab> = _currentTab.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.AMHARIC)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _userProfile = MutableStateFlow(GoogleUserProfile())
    val userProfile: StateFlow<GoogleUserProfile> = _userProfile.asStateFlow()

    private val _orderForm = MutableStateFlow(OrderFormState())
    val orderForm: StateFlow<OrderFormState> = _orderForm.asStateFlow()

    private val _activeChatOrderId = MutableStateFlow<Long?>(null)
    val activeChatOrderId: StateFlow<Long?> = _activeChatOrderId.asStateFlow()

    private val _activeChatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val activeChatMessages: StateFlow<List<ChatMessage>> = _activeChatMessages.asStateFlow()

    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    val portfolioItems: List<PortfolioItem> = repository.getPortfolioItems()

    init {
        // Auto select first order for chat when available
        viewModelScope.launch {
            allOrders.collect { orders ->
                if (_activeChatOrderId.value == null && orders.isNotEmpty()) {
                    selectOrderForChat(orders.first().id)
                }
            }
        }
    }

    fun setTab(tab: StudioTab) {
        _currentTab.value = tab
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.AMHARIC) AppLanguage.ENGLISH else AppLanguage.AMHARIC
    }

    fun showToast(msg: String) {
        _snackBarMessage.value = msg
    }

    fun clearToast() {
        _snackBarMessage.value = null
    }

    fun selectService(service: ServiceType) {
        _orderForm.value = _orderForm.value.copy(selectedService = service)
    }

    fun selectTier(tier: TierType) {
        _orderForm.value = _orderForm.value.copy(selectedTier = tier)
    }

    fun updateOrderField(
        projectTitle: String? = null,
        tagline: String? = null,
        briefDescription: String? = null,
        colorPref: String? = null,
        clientName: String? = null,
        clientPhone: String? = null,
        paymentMethod: String? = null
    ) {
        _orderForm.value = _orderForm.value.copy(
            projectTitle = projectTitle ?: _orderForm.value.projectTitle,
            tagline = tagline ?: _orderForm.value.tagline,
            briefDescription = briefDescription ?: _orderForm.value.briefDescription,
            colorStylePref = colorPref ?: _orderForm.value.colorStylePref,
            clientName = clientName ?: _orderForm.value.clientName,
            clientPhone = clientPhone ?: _orderForm.value.clientPhone,
            paymentMethod = paymentMethod ?: _orderForm.value.paymentMethod
        )
    }

    // Receipt scanner simulation
    fun triggerReceiptScan(rawSimulatedData: String = "Telebirr 0985273614") {
        viewModelScope.launch {
            _orderForm.value = _orderForm.value.copy(
                isScanning = true,
                scanProgress = 0.1f,
                scanVerified = false
            )

            // Simulate animated laser scanning steps
            delay(400)
            _orderForm.value = _orderForm.value.copy(scanProgress = 0.45f)
            delay(400)
            _orderForm.value = _orderForm.value.copy(scanProgress = 0.85f)
            delay(400)

            val scanResult: ReceiptScanResult = repository.scanTelebirrReceipt(
                rawInput = rawSimulatedData,
                targetAmount = _orderForm.value.totalBirr
            )

            _orderForm.value = _orderForm.value.copy(
                isScanning = false,
                scanProgress = 1.0f,
                scanVerified = true,
                transactionId = scanResult.transactionId,
                scanResultSummary = scanResult.extractedSummary,
                receiptScreenshotUri = "simulated_receipt_${System.currentTimeMillis()}"
            )
            showToast("✅ የቴሌብር ደረሰኝ በተሳካ ሁኔታ ተቃኝቷል! (0985273614)")
        }
    }

    fun submitOrder(onSuccess: (Long) -> Unit) {
        val form = _orderForm.value
        if (form.projectTitle.isBlank()) {
            showToast("እባክዎ የፕሮጀክቱን ወይም የድርጅቱን ስም ያስገቡ")
            return
        }
        if (!form.scanVerified && form.transactionId.isBlank()) {
            // Trigger automatic quick scan if not done yet
            triggerReceiptScan()
            return
        }

        viewModelScope.launch {
            _orderForm.value = _orderForm.value.copy(isSubmitting = true)
            val isVip = form.selectedTier == TierType.VIP || form.selectedTier == TierType.PRO
            val estimatedDelivery = when (form.selectedTier) {
                TierType.PRO -> "በ6 - 12 ሰዓት (Ultra Rush Pro)"
                TierType.VIP -> "በ12 - 24 ሰዓት (VIP Priority)"
                TierType.NORMAL -> "በ48 ሰዓት (መደበኛ)"
            }

            val newOrder = DesignOrder(
                clientName = form.clientName.ifBlank { _userProfile.value.name },
                clientEmail = form.clientEmail.ifBlank { _userProfile.value.email },
                clientPhone = form.clientPhone.ifBlank { _userProfile.value.phone },
                serviceCategory = form.selectedService.id,
                tier = form.selectedTier.id,
                priceBirr = form.totalBirr,
                projectTitle = form.projectTitle,
                taglineOrSlogan = form.tagline,
                briefDescription = form.briefDescription.ifBlank { "ምርጥና ዘመናዊ ዲዛይን በቴሌብር ክፍያ" },
                colorStylePref = form.colorStylePref.ifBlank { "Modern Vibrant & Professional" },
                paymentMethod = form.paymentMethod,
                telebirrNumber = form.telebirrAccount,
                transactionId = form.transactionId.ifBlank { "TP" + System.currentTimeMillis().toString().takeLast(8) },
                receiptImageUri = form.receiptScreenshotUri,
                scanVerified = true,
                extractedScanDetails = form.scanResultSummary,
                status = "PENDING_VERIFICATION",
                isVipPriority = isVip,
                estimatedDelivery = estimatedDelivery,
                createdAt = System.currentTimeMillis()
            )

            val orderId = repository.createOrder(newOrder)
            _orderForm.value = OrderFormState() // Reset
            showToast(if (isVip) "🔥 አዲስ VIP ትዕዛዝ ተልኳል! ዲዛይነሩ ፈጣን ምላሽ ይሰጥዎታል" else "ትዕዛዝዎ ተመዝግቧል!")
            selectOrderForChat(orderId)
            _currentTab.value = StudioTab.MY_ORDERS
            onSuccess(orderId)
        }
    }

    fun selectOrderForChat(orderId: Long) {
        _activeChatOrderId.value = orderId
        viewModelScope.launch {
            repository.getMessagesForOrder(orderId).collect { msgs ->
                _activeChatMessages.value = msgs
            }
        }
        viewModelScope.launch {
            repository.markMessagesRead(orderId, "CLIENT")
        }
    }

    fun sendChatMessage(text: String, isClient: Boolean = true) {
        val orderId = _activeChatOrderId.value ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            val sender = if (isClient) "CLIENT" else "DESIGNER"
            val senderName = if (isClient) _userProfile.value.name else "ዋና ዲዛይነር (K3)"
            repository.sendMessage(orderId, sender, senderName, text)

            // If client sent a message and order is VIP, provide quick realistic auto-acknowledgment if first message
            if (isClient) {
                delay(1200)
                val reply = "ሰላም ${_userProfile.value.name}! መልእክትዎ ደርሶኛል። በስራው ላይ እያስተካከልኩ ነው፤ በቅርቡ ዝርዝሩን አሳውቅዎታለሁ።"
                repository.sendMessage(orderId, "DESIGNER", "ዋና ዲዛይነር (K3)", reply)
            }
        }
    }

    // Designer Desk Actions
    fun updateOrderStatusFromDesk(orderId: Long, newStatus: String, notes: String? = null) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus, notes)
            showToast("የስራው ሁኔታ ወደ '$newStatus' ተቀይሯል")
        }
    }

    fun deliverDraftFromDesk(orderId: Long, draftUrl: String, notes: String) {
        viewModelScope.launch {
            repository.deliverDraft(orderId, draftUrl, notes)
            showToast("ረቂቅ ዲዛይን ለደንበኛው ተልኳል! ✨")
        }
    }

    fun populateFromPortfolio(item: PortfolioItem) {
        _orderForm.value = _orderForm.value.copy(
            selectedService = item.category,
            projectTitle = item.titleEnglish + " Style",
            briefDescription = "ከፖርትፎሊዮ የታየው '${item.titleAmharic}' አይነት ዲዛይን እፈልጋለሁ።"
        )
        _currentTab.value = StudioTab.NEW_ORDER
        showToast("የተመረጠው ስታይል ወደ ትዕዛዝ መስሪያው ተላልፏል")
    }

    fun simulateGoogleLogin() {
        _userProfile.value = GoogleUserProfile(
            name = "Kidus E.",
            email = "kduse378@gmail.com",
            phone = "0985273614",
            isSignedIn = true
        )
        showToast("በGoogle Account ተገናኝተዋል (kduse378@gmail.com)")
    }
}
