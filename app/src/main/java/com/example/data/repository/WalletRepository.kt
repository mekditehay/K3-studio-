package com.example.data.repository

import com.example.data.dao.WalletDao
import com.example.data.model.CoinTransaction
import com.example.data.model.CoinWallet
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WalletRepository(private val walletDao: WalletDao) {
    val wallet: Flow<CoinWallet?> = walletDao.getWallet()
    val transactions: Flow<List<CoinTransaction>> = walletDao.getAllTransactions()

    private suspend fun getCurrentWallet(): CoinWallet {
        return walletDao.getWalletOnce() ?: CoinWallet(
            id = 1,
            balance = 50,
            totalEarned = 50,
            totalSpent = 0,
            adsWatchedCount = 0
        ).also {
            walletDao.insertOrUpdateWallet(it)
        }
    }

    suspend fun addRewardCoins(amount: Int, reason: String, type: String = "REWARDED_AD") {
        val current = getCurrentWallet()
        val newBalance = current.balance + amount
        val newEarned = current.totalEarned + amount
        val newAdsCount = if (type == "REWARDED_AD") current.adsWatchedCount + 1 else current.adsWatchedCount

        walletDao.insertOrUpdateWallet(
            current.copy(
                balance = newBalance,
                totalEarned = newEarned,
                adsWatchedCount = newAdsCount
            )
        )
        walletDao.insertTransaction(
            CoinTransaction(
                amount = amount,
                type = type,
                description = reason
            )
        )
    }

    suspend fun spendCoins(amount: Int, movieTitle: String): Boolean {
        val current = getCurrentWallet()
        if (current.balance < amount) {
            return false
        }
        val newBalance = current.balance - amount
        val newSpent = current.totalSpent + amount

        walletDao.insertOrUpdateWallet(
            current.copy(
                balance = newBalance,
                totalSpent = newSpent
            )
        )
        walletDao.insertTransaction(
            CoinTransaction(
                amount = -amount,
                type = "MOVIE_PURCHASE",
                description = "$movieTitle ፊልም መግዣ (-$amount ኮይን)"
            )
        )
        return true
    }

    suspend fun claimDailyBonus(): Pair<Boolean, Int> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val current = getCurrentWallet()
        if (current.lastDailyBonusDate == today) {
            return Pair(false, 0) // Already claimed today
        }
        val bonusAmount = 30
        walletDao.insertOrUpdateWallet(
            current.copy(
                balance = current.balance + bonusAmount,
                totalEarned = current.totalEarned + bonusAmount,
                lastDailyBonusDate = today
            )
        )
        walletDao.insertTransaction(
            CoinTransaction(
                amount = bonusAmount,
                type = "DAILY_REWARD",
                description = "የዕለቱ የ $bonusAmount ኮይን ስጦታ ተቀብለዋል!"
            )
        )
        return Pair(true, bonusAmount)
    }
}
