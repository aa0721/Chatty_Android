package com.example.chatty_android.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.chatty_android.data.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): User?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE friend_code = :friendCode")
    suspend fun getUserByFriendCode(friendCode: String): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    fun observeUser(userId: Long): Flow<User?>

    @Query("SELECT * FROM users WHERE id IN (:userIds)")
    suspend fun getUsersByIds(userIds: List<Long>): List<User>

    @Query("SELECT * FROM users WHERE username LIKE '%' || :query || '%' OR nickname LIKE '%' || :query || '%'")
    suspend fun searchUsers(query: String): List<User>

    @Query("UPDATE users SET last_seen = :timestamp WHERE id = :userId")
    suspend fun updateLastSeen(userId: Long, timestamp: Long)

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    suspend fun countByUsername(username: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE id = :userId)")
    suspend fun exists(userId: Long): Boolean

    @Query("UPDATE users SET friend_code = :friendCode WHERE id = :userId")
    suspend fun updateFriendCode(userId: Long, friendCode: String)
}
