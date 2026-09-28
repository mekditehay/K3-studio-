package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DesignOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM design_orders ORDER BY isVipPriority DESC, createdAt DESC")
    fun getAllOrders(): Flow<List<DesignOrder>>

    @Query("SELECT * FROM design_orders WHERE clientEmail = :email ORDER BY createdAt DESC")
    fun getOrdersByEmail(email: String): Flow<List<DesignOrder>>

    @Query("SELECT * FROM design_orders WHERE id = :orderId")
    fun getOrderById(orderId: Long): Flow<DesignOrder?>

    @Query("SELECT * FROM design_orders WHERE id = :orderId")
    suspend fun getOrderByIdOnce(orderId: Long): DesignOrder?

    @Query("SELECT * FROM design_orders WHERE isVipPriority = 1 AND status != 'COMPLETED' ORDER BY createdAt ASC")
    fun getActiveVipOrders(): Flow<List<DesignOrder>>

    @Query("SELECT COUNT(*) FROM design_orders WHERE status = 'PENDING_VERIFICATION'")
    fun getPendingVerificationCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM design_orders")
    fun getTotalOrdersCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: DesignOrder): Long

    @Update
    suspend fun updateOrder(order: DesignOrder)

    @Query("UPDATE design_orders SET status = :status, designerNotes = :notes WHERE id = :orderId")
    suspend fun updateStatus(orderId: Long, status: String, notes: String?)

    @Query("UPDATE design_orders SET status = :status, draftPreviewUrl = :draftUrl, designerNotes = :notes WHERE id = :orderId")
    suspend fun deliverDraft(orderId: Long, status: String, draftUrl: String, notes: String?)

    @Query("DELETE FROM design_orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: Long)
}
