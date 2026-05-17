package com.example.chatty_android.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chatty_android.data.entity.Message
import kotlinx.coroutines.flow.Flow

data class UnreadPerSender(
    val senderId: Long,
    val unreadCount: Int
)

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: Message): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<Message>)

    @Query("SELECT * FROM messages WHERE (sender_id = :userId1 AND receiver_id = :userId2) OR (sender_id = :userId2 AND receiver_id = :userId1) ORDER BY timestamp ASC")
    fun getMessagesBetween(userId1: Long, userId2: Long): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE id IN (SELECT MAX(id) FROM messages WHERE sender_id = :userId OR receiver_id = :userId GROUP BY MIN(sender_id, receiver_id) || '_' || MAX(sender_id, receiver_id)) ORDER BY timestamp DESC")
    fun getRecentConversations(userId: Long): Flow<List<Message>>

    @Query("SELECT sender_id AS senderId, COUNT(*) AS unreadCount FROM messages WHERE receiver_id = :userId AND is_read = 0 GROUP BY sender_id")
    fun observeUnreadCounts(userId: Long): Flow<List<UnreadPerSender>>

    @Query("UPDATE messages SET is_read = 1 WHERE sender_id = :senderId AND receiver_id = :receiverId AND is_read = 0")
    suspend fun markAsRead(senderId: Long, receiverId: Long)

    @Query("SELECT COUNT(*) FROM messages WHERE receiver_id = :userId AND is_read = 0")
    fun observeTotalUnread(userId: Long): Flow<Int>

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteById(messageId: Long)

    @Query("DELETE FROM messages WHERE (sender_id = :userA AND receiver_id = :userB) OR (sender_id = :userB AND receiver_id = :userA)")
    suspend fun deleteConversation(userA: Long, userB: Long)
}
