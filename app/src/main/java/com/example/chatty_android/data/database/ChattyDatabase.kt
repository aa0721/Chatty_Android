package com.example.chatty_android.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chatty_android.data.dao.FriendRelationDao
import com.example.chatty_android.data.dao.MessageDao
import com.example.chatty_android.data.dao.UserDao
import com.example.chatty_android.data.entity.FriendRelation
import com.example.chatty_android.data.entity.Message
import com.example.chatty_android.data.entity.User

@Database(
    entities = [
        User::class,
        FriendRelation::class,
        Message::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ChattyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun friendRelationDao(): FriendRelationDao
    abstract fun messageDao(): MessageDao
}
