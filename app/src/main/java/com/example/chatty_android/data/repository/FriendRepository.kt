package com.example.chatty_android.data.repository

import com.example.chatty_android.data.dao.FriendRelationDao
import com.example.chatty_android.data.dao.UserDao
import com.example.chatty_android.data.entity.FriendRelation
import com.example.chatty_android.data.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendRepository @Inject constructor(
    private val friendRelationDao: FriendRelationDao,
    private val userDao: UserDao
) {
    suspend fun searchByFriendCode(friendCode: String): User? {
        return userDao.getUserByFriendCode(friendCode)
    }

    suspend fun addFriend(fromUserId: Long, toUserId: Long): Result<Unit> {
        val existing = friendRelationDao.getRelation(fromUserId, toUserId)
        if (existing != null) {
            return when (existing.status) {
                FriendRelation.STATUS_ACCEPTED -> Result.failure(Exception("已经是好友了"))
                FriendRelation.STATUS_PENDING -> {
                    if (existing.fromUserId == fromUserId) {
                        Result.failure(Exception("已发送好友请求，等待对方确认"))
                    } else {
                        friendRelationDao.updateStatus(existing.id, FriendRelation.STATUS_ACCEPTED)
                        Result.success(Unit)
                    }
                }
                else -> Result.failure(Exception("无法添加该用户为好友"))
            }
        }

        val relation = FriendRelation(
            fromUserId = fromUserId,
            toUserId = toUserId,
            status = FriendRelation.STATUS_ACCEPTED
        )
        friendRelationDao.insert(relation)
        return Result.success(Unit)
    }

    suspend fun deleteFriend(userA: Long, userB: Long) {
        val relation = friendRelationDao.getRelation(userA, userB) ?: return
        friendRelationDao.deleteById(relation.id)
    }

    suspend fun getFriends(userId: Long): List<User> {
        val relations = friendRelationDao.getFriends(userId, FriendRelation.STATUS_ACCEPTED)
        val friendIds = relations.map { rel ->
            if (rel.fromUserId == userId) rel.toUserId else rel.fromUserId
        }
        if (friendIds.isEmpty()) return emptyList()
        return userDao.getUsersByIds(friendIds)
    }

    fun observeFriends(userId: Long): Flow<List<User>> {
        return friendRelationDao.observeFriends(userId, FriendRelation.STATUS_ACCEPTED).map { relations ->
            val friendIds = relations.map { rel ->
                if (rel.fromUserId == userId) rel.toUserId else rel.fromUserId
            }
            if (friendIds.isEmpty()) emptyList()
            else userDao.getUsersByIds(friendIds)
        }
    }
}
