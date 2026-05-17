package com.example.chatty_android.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chatty_android.data.entity.FriendRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendRelationDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(relation: FriendRelation): Long

    @Query("SELECT * FROM friend_relations WHERE to_user_id = :userId AND status = :status")
    suspend fun getPendingRequests(userId: Long, status: String): List<FriendRelation>

    @Query("SELECT * FROM friend_relations WHERE (from_user_id = :userId OR to_user_id = :userId) AND status = :status")
    suspend fun getFriends(userId: Long, status: String): List<FriendRelation>

    @Query("SELECT * FROM friend_relations WHERE (from_user_id = :userId OR to_user_id = :userId) AND status = :status")
    fun observeFriends(userId: Long, status: String): Flow<List<FriendRelation>>

    @Query("SELECT * FROM friend_relations WHERE (from_user_id = :userA AND to_user_id = :userB) OR (from_user_id = :userB AND to_user_id = :userA)")
    suspend fun getRelation(userA: Long, userB: Long): FriendRelation?

    @Query("UPDATE friend_relations SET status = :status WHERE id = :relationId")
    suspend fun updateStatus(relationId: Long, status: String)

    @Query("SELECT EXISTS(SELECT 1 FROM friend_relations WHERE ((from_user_id = :userA AND to_user_id = :userB) OR (from_user_id = :userB AND to_user_id = :userA)) AND status = :status)")
    suspend fun isFriend(userA: Long, userB: Long, status: String): Boolean

    @Query("DELETE FROM friend_relations WHERE id = :relationId")
    suspend fun deleteById(relationId: Long)
}
