package com.example.chatty_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatty_android.data.dao.UserDao
import com.example.chatty_android.data.repository.AuthRepository
import com.example.chatty_android.data.repository.ChatRepository
import com.example.chatty_android.data.repository.FriendRepository
import com.example.chatty_android.data.repository.SettingsRepository
import com.example.chatty_android.ui.theme.ThemeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SettingsEvent {
    data object Logout : SettingsEvent
    data object AccountDeleted : SettingsEvent
    data class Error(val message: String) : SettingsEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val friendRepository: FriendRepository,
    private val chatRepository: ChatRepository,
    private val userDao: UserDao
) : ViewModel() {

    private val _themeColor = MutableStateFlow(ThemeColor.CHINESE_RED)
    val themeColor: StateFlow<ThemeColor> = _themeColor.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            settingsRepository.themeColor.collectLatest { _themeColor.value = it }
        }
        viewModelScope.launch {
            settingsRepository.isDarkTheme.collectLatest { _isDarkTheme.value = it }
        }
    }

    fun setThemeColor(color: ThemeColor) {
        viewModelScope.launch { settingsRepository.setThemeColor(color) }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDarkTheme(enabled) }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            delay(1)
            _events.emit(SettingsEvent.Logout)
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                val userId = authRepository.currentUserId.first()
                if (userId > 0) {
                    val friends = friendRepository.getFriends(userId)
                    friends.forEach { friend ->
                        friendRepository.deleteFriend(userId, friend.id)
                    }
                    val user = userDao.getUserById(userId)
                    if (user != null) {
                        userDao.delete(user)
                    }
                }
                authRepository.logout()
                delay(1)
                _events.emit(SettingsEvent.AccountDeleted)
            } catch (e: Exception) {
                _events.emit(SettingsEvent.Error(e.message ?: "注销失败"))
            }
        }
    }
}
