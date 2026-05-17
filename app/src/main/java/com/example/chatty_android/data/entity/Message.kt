package com.example.chatty_android.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["sender_id"]),
        Index(value = ["receiver_id"]),
        Index(value = ["timestamp"])
    ]
)
data class Message(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "sender_id")
    val senderId: Long,

    @ColumnInfo(name = "receiver_id")
    val receiverId: Long,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "content_type")
    val contentType: String = CONTENT_TYPE_TEXT,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,

    @ColumnInfo(name = "encryption_type")
    val encryptionType: String = ENCRYPTION_NONE,

    @ColumnInfo(name = "local_id")
    val localId: String = ""
) {
    companion object {
        const val CONTENT_TYPE_TEXT = "text"
        const val CONTENT_TYPE_IMAGE = "image"
        const val CONTENT_TYPE_FILE = "file"
        const val CONTENT_TYPE_VOICE = "voice"

        const val ENCRYPTION_NONE = "none"
        const val ENCRYPTION_AES = "aes"
        const val ENCRYPTION_E2E = "e2e"
    }
}
