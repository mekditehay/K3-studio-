package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ChatDao
import com.example.data.dao.OrderDao
import com.example.data.model.ChatMessage
import com.example.data.model.DesignOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DesignOrder::class,
        ChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "k3_design_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateSeedData(database.orderDao(), database.chatDao())
                    }
                }
            }
        }

        suspend fun populateSeedData(orderDao: OrderDao, chatDao: ChatDao) {
            val order1 = DesignOrder(
                clientName = "ዮናስ ታደሰ",
                clientEmail = "kduse378@gmail.com",
                clientPhone = "0911223344",
                serviceCategory = "LOGO",
                tier = "VIP",
                priceBirr = 60.0,
                projectTitle = "አቢሲንያ የቡና ኤክስፖርት (Habesha Coffee)",
                taglineOrSlogan = "የሀገር ፍቅር በጣፋጭ ቡና",
                briefDescription = "ባህላዊ የጀበና እና ዘመናዊ የቡና ቅጠል የያዘ የቪአይፒ ሎጎ እንፈልጋለን። ወርቃማና ጥቁር ቀለሞች ይመረጣሉ።",
                colorStylePref = "Gold & Matte Black, Modern Minimalist",
                paymentMethod = "TELEBIRR",
                telebirrNumber = "0985273614",
                transactionId = "TP240927.1432.A78901",
                scanVerified = true,
                extractedScanDetails = "Telebirr Txn: TP240927.1432.A78901 | Receiver: 0985273614 | Amount: 60.00 ETB | Verified",
                status = "IN_DESIGN",
                isVipPriority = true,
                estimatedDelivery = "ዛሬ ማታ 4:00 ሰዓት (VIP Express)",
                designerNotes = "የቡናውን እና ጀበናውን ቬክተር ረቂቅ እየሰራሁ ነው፣ በ2 ሰዓት ውስጥ ረቂቁ ይላካል።",
                createdAt = System.currentTimeMillis() - 3600000 * 4
            )

            val order2 = DesignOrder(
                clientName = "ሰላማዊት አበበ",
                clientEmail = "selam@example.com",
                clientPhone = "0922446688",
                serviceCategory = "PHOTO",
                tier = "PRO",
                priceBirr = 200.0,
                projectTitle = "የሠርግ እና የፋሽን ፎቶዎች ማስተካከል",
                taglineOrSlogan = "Selam Fashion Hub",
                briefDescription = "5 የፋሽን ስቱዲዮ ፎቶዎችን የፊት ጥራት፣ የልብስ ቀለም እና የጀርባ ላይት ማስተካከል",
                colorStylePref = "Warm Golden Hour Cinematic",
                paymentMethod = "TELEBIRR",
                telebirrNumber = "0985273614",
                transactionId = "TP240927.1610.F34211",
                scanVerified = true,
                extractedScanDetails = "Telebirr Txn: TP240927.1610.F34211 | Receiver: 0985273614 | Amount: 200.00 ETB | Verified",
                status = "DRAFT_READY",
                isVipPriority = true,
                estimatedDelivery = "ተጠናቋል (ረቂቅ ተልኳል)",
                designerNotes = "የመጀመሪያው ዙር የቀለም ማስተካከያ ተጠናቋል! ረቂቁን በውይይት መስኮት ይመልከቱ።",
                draftPreviewUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800&auto=format&fit=crop&q=80",
                createdAt = System.currentTimeMillis() - 3600000 * 8
            )

            val order3 = DesignOrder(
                clientName = "ዳዊት ከበደ",
                clientEmail = "dawit@example.com",
                clientPhone = "0944112233",
                serviceCategory = "LOGO",
                tier = "NORMAL",
                priceBirr = 30.0,
                projectTitle = "ቴክ ዞን የሞባይል ጥገና",
                taglineOrSlogan = "ፈጣን እና ታማኝ አገልግሎት",
                briefDescription = "የስልክ እና የቴክኖሎጂ አርማ ያለበት ቀላልና ፅዱ ሎጎ",
                colorStylePref = "Cyan & Blue",
                paymentMethod = "TELEBIRR",
                telebirrNumber = "0985273614",
                transactionId = "TP240927.0915.K82019",
                scanVerified = true,
                extractedScanDetails = "Telebirr Txn: TP240927.0915.K82019 | Receiver: 0985273614 | Amount: 30.00 ETB | Verified",
                status = "PENDING_VERIFICATION",
                isVipPriority = false,
                estimatedDelivery = "ነገ 12:00 ሰዓት",
                createdAt = System.currentTimeMillis() - 3600000 * 1
            )

            val id1 = orderDao.insertOrder(order1)
            val id2 = orderDao.insertOrder(order2)
            orderDao.insertOrder(order3)

            // Seed initial chat messages for Order 1 (VIP)
            chatDao.insertMessage(
                ChatMessage(
                    orderId = id1,
                    sender = "SYSTEM",
                    senderName = "K3 Studio System",
                    text = "ክፍያዎ በቴሌብር (0985273614) ተረጋግጧል! የVIP ቅድሚያ ተሰጥቶታል ⚡",
                    timestamp = System.currentTimeMillis() - 3600000 * 4
                )
            )
            chatDao.insertMessage(
                ChatMessage(
                    orderId = id1,
                    sender = "CLIENT",
                    senderName = "ዮናስ",
                    text = "ሰላም ዲዛይነር! ጀበናው ትንሽ ዘመናዊ ሆኖ ወርቃማ ጥላ እንዲኖረው እፈልጋለሁ።",
                    timestamp = System.currentTimeMillis() - 3600000 * 3
                )
            )
            chatDao.insertMessage(
                ChatMessage(
                    orderId = id1,
                    sender = "DESIGNER",
                    senderName = "ዋና ዲዛይነር (K3)",
                    text = "ሰላም ዮናስ! ተረድቼሃለሁ። የVIP ትዕዛዝ ስለሆነ ልዩ ትኩረት ሰጥቼ እየሰራሁት ነው። በ2 ሰዓት ውስጥ ረቂቁን እልክልሃለሁ!",
                    timestamp = System.currentTimeMillis() - 3600000 * 2
                )
            )

            // Seed chat for Order 2 (Pro)
            chatDao.insertMessage(
                ChatMessage(
                    orderId = id2,
                    sender = "DESIGNER",
                    senderName = "ዋና ዲዛይነር (K3)",
                    text = "ሰላም ሰላማዊት! ፎቶዎቹን በPro Master ጥራት አጠናቅቄ ረቂቁን አዘጋጅቻለሁ። ማስተካከያ ካለ ይንገሩኝ።",
                    timestamp = System.currentTimeMillis() - 3600000 * 1,
                    isDraftDelivery = true
                )
            )
        }
    }
}
