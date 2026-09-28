package com.example.data.repository

import com.example.data.dao.ChatDao
import com.example.data.dao.OrderDao
import com.example.data.model.ChatMessage
import com.example.data.model.DesignOrder
import com.example.data.model.PortfolioItem
import com.example.data.model.ServiceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudioRepository(
    private val orderDao: OrderDao,
    private val chatDao: ChatDao
) {
    val allOrders: Flow<List<DesignOrder>> = orderDao.getAllOrders()
    val activeVipOrders: Flow<List<DesignOrder>> = orderDao.getActiveVipOrders()
    val pendingVerificationCount: Flow<Int> = orderDao.getPendingVerificationCount()
    val totalOrdersCount: Flow<Int> = orderDao.getTotalOrdersCount()
    val unreadMessageCount: Flow<Int> = chatDao.getUnreadCount()

    fun getOrdersForClient(email: String): Flow<List<DesignOrder>> {
        return orderDao.getOrdersByEmail(email)
    }

    fun getOrderById(orderId: Long): Flow<DesignOrder?> {
        return orderDao.getOrderById(orderId)
    }

    suspend fun getOrderByIdOnce(orderId: Long): DesignOrder? {
        return orderDao.getOrderByIdOnce(orderId)
    }

    fun getMessagesForOrder(orderId: Long): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForOrder(orderId)
    }

    suspend fun createOrder(order: DesignOrder): Long {
        val newId = orderDao.insertOrder(order)
        // Add initial system message
        val welcomeMsg = if (order.isVipPriority) {
            "⚡ የVIP ትዕዛዝዎ ተመዝግቧል! ለዲዛይነሩ ቅድሚያ ማሳወቂያ ደርሶታል፤ ክፍያዎን በቴሌብር (0985273614) ስላረጋገጡ በፍጥነት ስራውን እንጀምራለን።"
        } else {
            "የዲዛይን ትዕዛዝዎ ተመዝግቧል! ዲዛይነሩ በቅርቡ ስራውን ይጀምራል። ማንኛውም ጥያቄ ካለዎት እዚህ ማውራት ይችላሉ።"
        }
        chatDao.insertMessage(
            ChatMessage(
                orderId = newId,
                sender = "SYSTEM",
                senderName = "K3 Studio System",
                text = welcomeMsg
            )
        )
        return newId
    }

    suspend fun sendMessage(orderId: Long, sender: String, senderName: String, text: String, isDraft: Boolean = false, draftUrl: String? = null): Long {
        val msgId = chatDao.insertMessage(
            ChatMessage(
                orderId = orderId,
                sender = sender,
                senderName = senderName,
                text = text,
                attachmentUrl = draftUrl,
                isDraftDelivery = isDraft
            )
        )
        // If client sends message, designer might reply after some status change
        return msgId
    }

    suspend fun updateOrderStatus(orderId: Long, newStatus: String, notes: String? = null) {
        orderDao.updateStatus(orderId, newStatus, notes)
        val statusText = when (newStatus) {
            "IN_DESIGN" -> "🎨 ዲዛይነሩ በፕሮጀክቱ ላይ መስራት ጀምሯል!"
            "DRAFT_READY" -> "✨ ረቂቅ ዲዛይን ተጠናቆ ለዕይታ ቀርቧል! እባክዎ ይመልከቱት።"
            "COMPLETED" -> "🎉 ፕሮጀክቱ በስኬት ተጠናቋል! ዋናዎቹ ፋይሎች ተልከዋል።"
            else -> "የፕሮጀክት ሁኔታ ተቀይሯል፡ $newStatus"
        }
        chatDao.insertMessage(
            ChatMessage(
                orderId = orderId,
                sender = "SYSTEM",
                senderName = "K3 Studio System",
                text = statusText
            )
        )
    }

    suspend fun deliverDraft(orderId: Long, draftUrl: String, notes: String) {
        orderDao.deliverDraft(orderId, "DRAFT_READY", draftUrl, notes)
        chatDao.insertMessage(
            ChatMessage(
                orderId = orderId,
                sender = "DESIGNER",
                senderName = "ዋና ዲዛይነር (K3)",
                text = "የተሰራውን ረቂቅ ይኸው ልኬልዎታለሁ፡ $notes",
                attachmentUrl = draftUrl,
                isDraftDelivery = true
            )
        )
    }

    suspend fun markMessagesRead(orderId: Long, currentSender: String) {
        chatDao.markMessagesAsRead(orderId, currentSender)
    }

    // Realistic Telebirr OCR Scanning extraction simulation
    fun scanTelebirrReceipt(rawInput: String, targetAmount: Double): ReceiptScanResult {
        // Telebirr receipt patterns typically contain:
        // Transaction ID: TPXXXXXXXX.XXXX.XXXX or MPXXXXX or 10-12 characters
        // Receiver: 0985273614 or 251985273614
        // Status: Completed / Successful
        val isCorrectReceiver = rawInput.contains("0985273614") || 
                                rawInput.contains("985273614") || 
                                rawInput.contains("K3") || 
                                rawInput.lowercase().contains("telebirr")

        val generatedTxnId = "TP" + (240927..240928).random() + "." + (1000..9999).random() + "." + ('A'..'Z').random() + (10000..99999).random()

        return ReceiptScanResult(
            transactionId = generatedTxnId,
            receiverNumber = "0985273614",
            amountDetected = targetAmount,
            isReceiverMatched = true,
            isAuthenticTelebirr = true,
            extractedSummary = "ቴሌብር ክፍያ ተረጋግጧል | ወደ 0985273614 | ድምር: ${targetAmount.toInt()} ETB | ሁኔታ: የተሳካ (Successful)"
        )
    }

    // Portfolio items
    fun getPortfolioItems(): List<PortfolioItem> {
        return listOf(
            PortfolioItem(
                id = "p1",
                titleAmharic = "አቢሲንያ የቡና ኤክስፖርት ሎጎ",
                titleEnglish = "Abyssinia Specialty Coffee",
                category = ServiceType.LOGO_DESIGN,
                tag = "VIP Logo",
                imageUrl = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=800&auto=format&fit=crop&q=80",
                clientName = "Habesha Coffee Exporters",
                descriptionAmharic = "ወርቃማ የጀበና እና የቡና ቅጠል ጥምረት የተሰራበት ዘመናዊ ቪአይፒ ሎጎ"
            ),
            PortfolioItem(
                id = "p2",
                titleAmharic = "ናሆም የፋሽን ስቱዲዮ ፖርትሬት",
                titleEnglish = "Nahom Fashion Studio",
                category = ServiceType.PHOTO_EDITING,
                tag = "Pro Retouch",
                imageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800&auto=format&fit=crop&q=80",
                clientName = "Nahom Model Agency",
                descriptionAmharic = "የከፍተኛ ደረጃ የፊት ቆዳ ማስተካከልና የጀርባ ላይቲንግ ማሳመር"
            ),
            PortfolioItem(
                id = "p3",
                titleAmharic = "አዲስ ቴክኖሎጂ ፊንቴክ ሎጎ",
                titleEnglish = "AddisPay FinTech",
                category = ServiceType.LOGO_DESIGN,
                tag = "Minimalist",
                imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=80",
                clientName = "AddisPay Technologies",
                descriptionAmharic = "ለሞባይል ባንኪንግ መተግበሪያ የተሰራ የሳይበር ንክኪ ያለው ሎጎ"
            ),
            PortfolioItem(
                id = "p4",
                titleAmharic = "የምግብ ፌስቲቫል የማስታወቂያ ባነር",
                titleEnglish = "Taste of Ethiopia Flyer",
                category = ServiceType.SOCIAL_MEDIA,
                tag = "Social Pack",
                imageUrl = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800&auto=format&fit=crop&q=80",
                clientName = "Ethio Culinary Fest",
                descriptionAmharic = "ለቴሌግራም እና ለኢንስታግራም ማስተዋወቂያ የተዘጋጁ ማራኪ ፖስተሮች"
            ),
            PortfolioItem(
                id = "p5",
                titleAmharic = "የሙዚቃ ቪዲዮ ከለር ግሬዲንግ",
                titleEnglish = "Cinematic Music Reel",
                category = ServiceType.VIDEO_EDITING,
                tag = "4K Cinematic",
                imageUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=800&auto=format&fit=crop&q=80",
                clientName = "Danny Beats",
                descriptionAmharic = "የቲክቶክ እና የዩቲዩብ ቪዲዮዎችን በሲኒማቲክ ቀለም እና ትራንዚሽን ማቀናበር"
            ),
            PortfolioItem(
                id = "p6",
                titleAmharic = "ዘውድ ጌጣጌጥ እና አልባሳት ሎጎ",
                titleEnglish = "Zewd Royal Jewelry",
                category = ServiceType.LOGO_DESIGN,
                tag = "Luxury Gold",
                imageUrl = "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=800&auto=format&fit=crop&q=80",
                clientName = "Zewd Jewelry Addis",
                descriptionAmharic = "የንግሥና ዘውድ እና የወርቅ አንጸባራቂ ዲዛይን የተዋሃደበት አርማ"
            )
        )
    }
}

data class ReceiptScanResult(
    val transactionId: String,
    val receiverNumber: String,
    val amountDetected: Double,
    val isReceiverMatched: Boolean,
    val isAuthenticTelebirr: Boolean,
    val extractedSummary: String
)
