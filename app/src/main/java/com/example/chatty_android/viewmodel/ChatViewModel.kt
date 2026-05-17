package com.example.chatty_android.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatty_android.data.dao.UserDao
import com.example.chatty_android.data.entity.Message
import com.example.chatty_android.data.entity.User
import com.example.chatty_android.data.repository.AuthRepository
import com.example.chatty_android.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val userDao: UserDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val chatUserId: Long = savedStateHandle.get<Long>("userId") ?: 0L

    private val _myUserId = MutableStateFlow(0L)

    private val _contactUser = MutableStateFlow<User?>(null)
    val contactUser: StateFlow<User?> = _contactUser.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    init {
        viewModelScope.launch {
            val uid = authRepository.currentUserId.first()
            _myUserId.value = uid

            // Load contact user info
            val user = userDao.getUserById(chatUserId)
            _contactUser.value = user

            // Observe messages
            if (uid > 0 && chatUserId > 0) {
                chatRepository.getMessages(uid, chatUserId).collectLatest { msgs ->
                    _messages.value = msgs
                }
                // Mark messages from this user as read
                chatRepository.markAsRead(chatUserId, uid)
            }
        }
    }

    fun isMyMessage(msg: Message): Boolean = msg.senderId == _myUserId.value

    fun onInputChanged(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        val uid = _myUserId.value
        if (text.isEmpty() || uid <= 0 || chatUserId <= 0) return

        viewModelScope.launch {
            chatRepository.sendMessage(uid, chatUserId, content = text)
            _inputText.value = ""
        }
    }
}
