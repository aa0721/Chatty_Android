package com.example.chatty_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatty_android.data.entity.User
import com.example.chatty_android.data.repository.AuthRepository
import com.example.chatty_android.data.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FriendEvent {
    data class Error(val message: String) : FriendEvent
    data object FriendAdded : FriendEvent
}

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _friends = MutableStateFlow<List<User>>(emptyList())
    val friends: StateFlow<List<User>> = _friends.asStateFlow()

    private val _searchResult = MutableStateFlow<User?>(null)
    val searchResult: StateFlow<User?> = _searchResult.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _events = MutableSharedFlow<FriendEvent>()
    val events: SharedFlow<FriendEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            val userId = authRepository.currentUserId.first()
            if (userId > 0) {
                friendRepository.observeFriends(userId).collectLatest { users ->
                    _friends.value = users
                }
            }
        }
    }

    fun searchByFriendCode(code: String) {
        if (code.isBlank()) return
        _isSearching.value = true
        _searchResult.value = null

        viewModelScope.launch {
            val user = friendRepository.searchByFriendCode(code)
            _searchResult.value = user
            _isSearching.value = false
            if (user == null) {
                _events.emit(FriendEvent.Error("未找到该好友码对应的用户"))
            }
        }
    }

    fun clearSearch() {
        _searchResult.value = null
    }

    fun addFriend() {
        val targetUser = _searchResult.value ?: return
        viewModelScope.launch {
            val myUserId = authRepository.currentUserId.first()
            if (myUserId <= 0) {
                _events.emit(FriendEvent.Error("请先登录"))
                return@launch
            }
            if (targetUser.id == myUserId) {
                _events.emit(FriendEvent.Error("不能添加自己为好友"))
                return@launch
            }
            val result = friendRepository.addFriend(myUserId, targetUser.id)
            result.fold(
                onSuccess = {
                    _searchResult.value = null
                    _events.emit(FriendEvent.FriendAdded)
                },
                onFailure = { e ->
                    _events.emit(FriendEvent.Error(e.message ?: "添加失败"))
                }
            )
        }
    }
}
