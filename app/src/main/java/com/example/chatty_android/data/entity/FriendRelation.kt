package com.example.chatty_android.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "friend_relations",
    indices = [
        Index(value = ["from_user_id", "to_user_id"], unique = true)
    ]
)
data class FriendRelation(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "from_user_id")
    val fromUserId: Long,

    @ColumnInfo(name = "to_user_id")
    val toUserId: Long,

    @ColumnInfo(name = "status")
    val status: String = STATUS_PENDING,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val STATUS_PENDING = "pending"
        const val STATUS_ACCEPTED = "accepted"
        const val STATUS_BLOCKED = "blocked"
    }
}
