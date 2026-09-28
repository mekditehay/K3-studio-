package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val sender: String, // "CLIENT", "DESIGNER", "SYSTEM"
    val senderName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val attachmentUrl: String? = null,
    val isDraftDelivery: Boolean = false,
    val isRead: Boolean = false
)
