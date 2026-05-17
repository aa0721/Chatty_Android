package com.example.chatty_android.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatty_android.common.utils.ImageUtils
import com.example.chatty_android.data.dao.UserDao
import com.example.chatty_android.data.entity.User
import com.example.chatty_android.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface MainEvent {
    data object Logout : MainEvent
    data object AvatarUpdated : MainEvent
    data class Error(val message: String) : MainEvent
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userDao: UserDao,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _events = MutableSharedFlow<MainEvent>()
    val events: SharedFlow<MainEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUserId.collectLatest { userId ->
                if (userId > 0) {
                    val user = userDao.getUserById(userId)
                    _currentUser.value = user
                }
            }
        }
    }

    fun processAndUpdateAvatar(sourceUri: Uri) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            try {
                val croppedFile = withContext(Dispatchers.IO) {
                    ImageUtils.cropToSquare(appContext, sourceUri)
                }
                if (croppedFile == null) {
                    _events.emit(MainEvent.Error("图片处理失败"))
                    return@launch
                }
                val avatarPath = withContext(Dispatchers.IO) {
                    ImageUtils.saveAvatar(appContext, Uri.fromFile(croppedFile), user.id)
                }
                if (avatarPath.isNotEmpty()) {
                    val updated = user.copy(avatarPath = avatarPath)
                    userDao.update(updated)
                    _currentUser.value = updated
                    _events.emit(MainEvent.AvatarUpdated)
                }
            } catch (e: Exception) {
                _events.emit(MainEvent.Error("头像更新失败"))
            }
        }
    }

    fun updateNickname(nickname: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(nickname = nickname)
            userDao.update(updated)
            _currentUser.value = updated
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            delay(1)
            _events.emit(MainEvent.Logout)
        }
    }
}
