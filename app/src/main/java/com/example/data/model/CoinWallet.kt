package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin_wallet")
data class CoinWallet(
    @PrimaryKey
    val id: Int = 1,
    val balance: Int = 50, // Welcome bonus of 50 coins
    val totalEarned: Int = 50,
    val totalSpent: Int = 0,
    val adsWatchedCount: Int = 0,
    val lastDailyBonusDate: String = ""
)

@Entity(tableName = "coin_transactions")
data class CoinTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Int, // +25 for ad, -50 for movie purchase
    val type: String, // REWARDED_AD, WELCOME_BONUS, DAILY_REWARD, MOVIE_PURCHASE, LUCKY_SPIN
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "purchased_movies")
data class PurchasedMovie(
    @PrimaryKey
    val movieId: Long,
    val coinsSpent: Int,
    val purchasedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "watch_history")
data class WatchHistory(
    @PrimaryKey
    val movieId: Long,
    val progressSeconds: Long,
    val durationSeconds: Long,
    val lastWatchedTimestamp: Long = System.currentTimeMillis()
)
