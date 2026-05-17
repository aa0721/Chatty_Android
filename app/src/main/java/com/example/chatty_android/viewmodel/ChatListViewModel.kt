package com.example.chatty_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatty_android.data.repository.AuthRepository
import com.example.chatty_android.data.repository.ChatRepository
import com.example.chatty_android.data.repository.ConversationInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<ConversationInfo>>(emptyList())
    val conversations: StateFlow<List<ConversationInfo>> = _conversations.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = authRepository.currentUserId.first()
            if (userId > 0) {
                chatRepository.getConversations(userId).collectLatest { list ->
                    _conversations.value = list
                }
            }
        }
    }
}
