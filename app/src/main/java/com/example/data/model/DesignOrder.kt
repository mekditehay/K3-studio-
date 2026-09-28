package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "design_orders")
data class DesignOrder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientName: String,
    val clientEmail: String,
    val clientPhone: String,
    val serviceCategory: String, // "LOGO", "PHOTO", "VIDEO", "SOCIAL"
    val tier: String, // "NORMAL", "VIP", "PRO"
    val priceBirr: Double,
    val projectTitle: String,
    val taglineOrSlogan: String = "",
    val briefDescription: String = "",
    val colorStylePref: String = "",
    val paymentMethod: String = "TELEBIRR", // "TELEBIRR", "CBE"
    val telebirrNumber: String = "0985273614",
    val transactionId: String = "",
    val receiptImageUri: String = "",
    val scanVerified: Boolean = false,
    val extractedScanDetails: String = "",
    val status: String = "PENDING_VERIFICATION", // PENDING_VERIFICATION, IN_DESIGN, DRAFT_READY, COMPLETED
    val isVipPriority: Boolean = false,
    val estimatedDelivery: String = "",
    val designerNotes: String? = null,
    val draftPreviewUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
