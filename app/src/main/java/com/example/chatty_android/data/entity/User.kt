package com.example.chatty_android.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["friend_code"])
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    @ColumnInfo(name = "nickname")
    val nickname: String = "",

    @ColumnInfo(name = "avatar_path")
    val avatarPath: String = "",

    @ColumnInfo(name = "friend_code")
    val friendCode: String = "",

    @ColumnInfo(name = "phone")
    val phone: String = "",

    @ColumnInfo(name = "email")
    val email: String = "",

    @ColumnInfo(name = "public_key")
    val publicKey: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_seen")
    val lastSeen: Long = System.currentTimeMillis()
)
