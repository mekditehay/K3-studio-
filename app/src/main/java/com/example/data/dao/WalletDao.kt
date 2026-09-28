package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CoinTransaction
import com.example.data.model.CoinWallet
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM coin_wallet WHERE id = 1 LIMIT 1")
    fun getWallet(): Flow<CoinWallet?>

    @Query("SELECT * FROM coin_wallet WHERE id = 1 LIMIT 1")
    suspend fun getWalletOnce(): CoinWallet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWallet(wallet: CoinWallet)

    @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<CoinTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CoinTransaction)
}
