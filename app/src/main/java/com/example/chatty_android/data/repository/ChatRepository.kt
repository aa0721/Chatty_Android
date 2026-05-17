package com.example.chatty_android.data.repository

import com.example.chatty_android.data.dao.MessageDao
import com.example.chatty_android.data.dao.UnreadPerSender
import com.example.chatty_android.data.dao.UserDao
import com.example.chatty_android.data.entity.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

data class ConversationInfo(
    val otherUserId: Long,
    val otherUserName: String,
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int
)

@Singleton
class ChatRepository @Inject constructor(
    private val messageDao: MessageDao,
    private val userDao: UserDao
) {
    suspend fun sendMessage(senderId: Long, receiverId: Long, content: String): Message {
        val message = Message(
            senderId = senderId,
            receiverId = receiverId,
            content = content,
            contentType = Message.CONTENT_TYPE_TEXT
        )
        val id = messageDao.insert(message)
        return message.copy(id = id)
    }

    fun getMessages(userId1: Long, userId2: Long): Flow<List<Message>> {
        return messageDao.getMessagesBetween(userId1, userId2)
    }

    fun getConversations(userId: Long): Flow<List<ConversationInfo>> {
        val messagesFlow = messageDao.getRecentConversations(userId)
        val unreadFlow = messageDao.observeUnreadCounts(userId)

        return combine(messagesFlow, unreadFlow) { messages, unreadCounts ->
            messages.map { msg ->
                val otherUserId = if (msg.senderId == userId) msg.receiverId else msg.senderId
                val otherUser = userDao.getUserById(otherUserId)
                val unreadCount = unreadCounts.find { it.senderId == otherUserId }?.unreadCount ?: 0

                ConversationInfo(
                    otherUserId = otherUserId,
                    otherUserName = otherUser?.nickname?.ifEmpty { otherUser?.username } ?: "未知用户",
                    lastMessage = if (msg.contentType == Message.CONTENT_TYPE_IMAGE) "[图片]" else msg.content,
                    timestamp = msg.timestamp,
                    unreadCount = unreadCount
                )
            }
        }
    }

    suspend fun markAsRead(senderId: Long, receiverId: Long) {
        messageDao.markAsRead(senderId, receiverId)
    }
}
