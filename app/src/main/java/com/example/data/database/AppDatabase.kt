package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.MovieDao
import com.example.data.dao.WalletDao
import com.example.data.model.CoinTransaction
import com.example.data.model.CoinWallet
import com.example.data.model.Movie
import com.example.data.model.PurchasedMovie
import com.example.data.model.WatchHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Movie::class,
        CoinWallet::class,
        CoinTransaction::class,
        PurchasedMovie::class,
        WatchHistory::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun walletDao(): WalletDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "k3_movie_database"
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
                        populateInitialData(database.movieDao(), database.walletDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(movieDao: MovieDao, walletDao: WalletDao) {
            // Initial Wallet with 50 Welcome Coins
            val existingWallet = walletDao.getWalletOnce()
            if (existingWallet == null) {
                walletDao.insertOrUpdateWallet(
                    CoinWallet(
                        id = 1,
                        balance = 75, // 75 Welcome Coins!
                        totalEarned = 75,
                        totalSpent = 0,
                        adsWatchedCount = 0
                    )
                )
                walletDao.insertTransaction(
                    CoinTransaction(
                        amount = 75,
                        type = "WELCOME_BONUS",
                        description = "የእንኳን ደህና መጡ 75 ነፃ ኮይን ስጦታ!"
                    )
                )
            }

            // Initial Seed Dubbed Movies (ምርጥ የትርጉም ፊልሞች)
            if (movieDao.getMovieCount() == 0) {
                val seedMovies = listOf(
                    Movie(
                        titleAmharic = "ጆን ዊክ 4 (የትርጉም ፊልም)",
                        titleOriginal = "John Wick: Chapter 4",
                        translator = "ሄኖክ ትርጉም",
                        genre = "የተግባር (Action)",
                        durationMinutes = 169,
                        releaseYear = 2023,
                        rating = 4.9f,
                        posterUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=600&auto=format&fit=crop&q=80",
                        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                        descriptionAmharic = "ጆን ዊክ ከፍተኛ ዋጋ በራሱ ላይ የተጣለበትን የከፍተኛው ጠረጴዛ (High Table) ጥምረት ለመበቀል ወደ ጀርመን እና ፈረንሳይ የሚያደርገው አስደናቂ ፍልሚያ። በአስተርጓሚ ሄኖክ በድምቀት የተተረጎመ።",
                        descriptionEnglish = "John Wick uncovers a path to defeating The High Table. Translated by Henok.",
                        coinPrice = 60,
                        isFeatured = true,
                        isTrending = true,
                        viewsCount = 28400,
                        likesCount = 8900,
                        uploaderName = "K3 Movie Official"
                    ),
                    Movie(
                        titleAmharic = "ኤክስትራክሽን 2 (Extraction 2)",
                        titleOriginal = "Extraction 2",
                        translator = "ሄኖክ ትርጉም",
                        genre = "የተግባር (Action)",
                        durationMinutes = 123,
                        releaseYear = 2023,
                        rating = 4.8f,
                        posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80",
                        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                        descriptionAmharic = "ታይለር ሬክ ከሞት አፋፍ ተርፎ አደገኛ የሆነውን የጆርጂያ እስር ቤት ጥሶ የታገተችውን ቤተሰብ ለማስለቀቅ የሚያደርገው የማያባራ ጦርነት። በሄኖክ የላቀ የትርጉም አቀራረብ።",
                        descriptionEnglish = "Tyler Rake is back for another high-stakes mission.",
                        coinPrice = 50,
                        isFeatured = true,
                        isTrending = true,
                        viewsCount = 31200,
                        likesCount = 9450,
                        uploaderName = "K3 Movie Official"
                    ),
                    Movie(
                        titleAmharic = "ዴድፑል እና ዎልቨሪን (Deadpool & Wolverine)",
                        titleOriginal = "Deadpool & Wolverine",
                        translator = "ዳኒ ስቱዲዮ",
                        genre = "ቀልድና ተግባር (Action/Comedy)",
                        durationMinutes = 128,
                        releaseYear = 2024,
                        rating = 4.9f,
                        posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop&q=80",
                        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                        descriptionAmharic = "ዴድፑል የራሱን ዓለም ለማዳን ከተሰላቹት ዎልቨሪን ጋር ተጣምሮ የሚያደርገው አስቂኝ እና እልህ አስጨራሽ ትግል። ዳኒ በልዩ አነጋገር እና ቀልዶች አዘጋጅቶ ያቀረበው።",
                        descriptionEnglish = "Deadpool teams up with Wolverine in this blockbuster marvel spectacle.",
                        coinPrice = 80,
                        isFeatured = true,
                        isTrending = true,
                        viewsCount = 42100,
                        likesCount = 13200,
                        uploaderName = "ዳኒ ትርጉም ቻናል"
                    ),
                    Movie(
                        titleAmharic = "ባሁባሊ 2: መደምደሚያው (Baahubali 2)",
                        titleOriginal = "Baahubali 2: The Conclusion",
                        translator = "ዮኒ ትርጉም",
                        genre = "የህንድ ትርጉም (Bollywood)",
                        durationMinutes = 167,
                        releaseYear = 2022,
                        rating = 4.9f,
                        posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
                        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
                        descriptionAmharic = "ካታፓ ባሁባሊን ለምን ገደለው? የንግሥናው ዙፋን ሴራ እና የታላቁ ባሁባሊ የጀግንነት ታሪክ ማጠቃለያ። ዮኒ በሚያምር ግጥማዊ ስሜት የተረጎመው።",
                        descriptionEnglish = "The epic conclusion of Baahubali's legendary story.",
                        coinPrice = 40,
                        isFeatured = false,
                        isTrending = true,
                        viewsCount = 19800,
                        likesCount = 6700,
                        uploaderName = "ዮኒ ሲኒማ"
                    ),
                    Movie(
                        titleAmharic = "ስኩዊድ ጌም: የትርጉም ድራማ (Squid Game)",
                        titleOriginal = "Squid Game: The Challenge",
                        translator = "ቴዲ ትርጉም",
                        genre = "የኮሪያ ትርጉም (K-Drama)",
                        durationMinutes = 98,
                        releaseYear = 2023,
                        rating = 4.7f,
                        posterUrl = "https://images.unsplash.com/photo-1618336753974-aae8e04506aa?w=600&auto=format&fit=crop&q=80",
                        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        descriptionAmharic = "በገንዘብ ችግር የተጠቁ 456 ሰዎች ለ45.6 ቢሊዮን ዎንድ የሚያደርጉት ገዳይ የልጅነት ጨዋታዎች ውድድር። በቴዲ የተተረጎመ።",
                        descriptionEnglish = "Contestants compete in lethal games for a massive cash prize.",
                        coinPrice = 45,
                        isFeatured = false,
                        isTrending = true,
                        viewsCount = 24500,
                        likesCount = 7600,
                        uploaderName = "ቴዲ ሙቪ"
                    ),
                    Movie(
                        titleAmharic = "የተደበቀው ተዋጊ (The Hidden Warrior)",
                        titleOriginal = "The Hidden Warrior",
                        translator = "ራስ ትርጉም",
                        genre = "የተግባር (Action)",
                        durationMinutes = 104,
                        releaseYear = 2024,
                        rating = 4.6f,
                        posterUrl = "https://images.unsplash.com/photo-1542204165-65bf26472b9b?w=600&auto=format&fit=crop&q=80",
                        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                        descriptionAmharic = "ነፃ የትርጉም ፊልም! ያለ ምንም ኮይን ክፍያ ወዲያውኑ ይመልከቱ። መንደሩን ከጥፋት ለመታደግ ጫካ ውስጥ የመሸገው ብቸኛ ተዋጊ ታሪክ።",
                        descriptionEnglish = "Free to watch movie! No coins required.",
                        coinPrice = 0, // Free!
                        isFeatured = false,
                        isTrending = false,
                        viewsCount = 15300,
                        likesCount = 4200,
                        uploaderName = "K3 Movie"
                    ),
                    Movie(
                        titleAmharic = "አላዲን: አስደናቂው ጉዞ (Aladdin)",
                        titleOriginal = "Aladdin & The Magic Lamp",
                        translator = "አቤል ትርጉም",
                        genre = "ፍቅርና ድራማ (Romance)",
                        durationMinutes = 115,
                        releaseYear = 2023,
                        rating = 4.8f,
                        posterUrl = "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=600&auto=format&fit=crop&q=80",
                        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                        descriptionAmharic = "ነፃ የተተረጎመ ድንቅ የፍቅርና አስማት ፊልም። አላዲን ከጃስሚን ጋር ለመገናኘት የጂኒን አስማት ሲጠቀም።",
                        descriptionEnglish = "Free dubbed movie suitable for all viewers.",
                        coinPrice = 0, // Free!
                        isFeatured = false,
                        isTrending = false,
                        viewsCount = 17900,
                        likesCount = 5100,
                        uploaderName = "K3 Movie"
                    ),
                    Movie(
                        titleAmharic = "ፉሪዮሳ: የማድ ማክስ ታሪክ (Furiosa)",
                        titleOriginal = "Furiosa: A Mad Max Saga",
                        translator = "ሄኖክ ትርጉም",
                        genre = "የተግባር (Action)",
                        durationMinutes = 148,
                        releaseYear = 2024,
                        rating = 4.9f,
                        posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80",
                        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                        descriptionAmharic = "ወጣቷ ፉሪዮሳ ከቤቷ ተነጥቃ በበረሃው አምባገነኖች መካከል ለነፃነቷ የምታደርገው ብርቱ ትግል። በአስተርጓሚ ሄኖክ በልዩ ብቃት የተተረጎመ።",
                        descriptionEnglish = "Young Furiosa falls into the hands of a great Biker Horde.",
                        coinPrice = 70,
                        isFeatured = true,
                        isTrending = true,
                        viewsCount = 38900,
                        likesCount = 11200,
                        uploaderName = "ሄኖክ ስቱዲዮ"
                    )
                )
                movieDao.insertMovies(seedMovies)
            }
        }
    }
}
