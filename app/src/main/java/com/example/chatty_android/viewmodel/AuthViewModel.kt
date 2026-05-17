package com.example.chatty_android.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatty_android.common.constants.Constants
import com.example.chatty_android.common.utils.ImageUtils
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class LoginFormState(
    val username: String = "",
    val password: String = "",
    val rememberPassword: Boolean = false,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false
)

data class RegisterFormState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val avatarUri: Uri? = null,
    val avatarPath: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val isProcessingAvatar: Boolean = false
)

sealed interface AuthEvent {
    data class LoginSuccess(val userId: Long) : AuthEvent
    data object RegisterSuccess : AuthEvent
    data class Error(val message: String) : AuthEvent
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginFormState())
    val loginState: StateFlow<LoginFormState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterFormState())
    val registerState: StateFlow<RegisterFormState> = _registerState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    // --- Login ---

    fun onLoginUsernameChanged(value: String) {
        _loginState.update { it.copy(username = value, usernameError = null) }
    }

    fun onLoginPasswordChanged(value: String) {
        _loginState.update { it.copy(password = value, passwordError = null) }
    }

    fun onRememberPasswordChanged(value: Boolean) {
        _loginState.update { it.copy(rememberPassword = value) }
    }

    fun loadSavedCredentials() {
        viewModelScope.launch {
            val remember = authRepository.getRememberPassword()
            if (remember) {
                val savedUsername = authRepository.getSavedUsername()
                _loginState.update {
                    it.copy(username = savedUsername, rememberPassword = true)
                }
            }
        }
    }

    fun login() {
        val state = _loginState.value
        var hasError = false

        if (state.username.isBlank()) {
            _loginState.update { it.copy(usernameError = "请输入用户名") }
            hasError = true
        }
        if (state.password.isBlank()) {
            _loginState.update { it.copy(passwordError = "请输入密码") }
            hasError = true
        }
        if (hasError) return

        _loginState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = authRepository.login(state.username, state.password)
            result.fold(
                onSuccess = { user ->
                    authRepository.saveLoginState(user.id)
                    if (state.rememberPassword) {
                        authRepository.saveCredentials(state.username, state.password)
                    } else {
                        authRepository.clearCredentials()
                    }
                    _loginState.update { it.copy(isLoading = false) }
                    delay(1)
                    _events.emit(AuthEvent.LoginSuccess(user.id))
                },
                onFailure = { e ->
                    _loginState.update { it.copy(isLoading = false) }
                    _events.emit(AuthEvent.Error(e.message ?: "登录失败"))
                }
            )
        }
    }

    // --- Register ---

    fun onRegisterUsernameChanged(value: String) {
        _registerState.update { it.copy(username = value, usernameError = null) }
    }

    fun onRegisterPasswordChanged(value: String) {
        _registerState.update { it.copy(password = value, passwordError = null, confirmPasswordError = null) }
    }

    fun onConfirmPasswordChanged(value: String) {
        _registerState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun processAvatarUri(uri: Uri) {
        _registerState.update { it.copy(isProcessingAvatar = true) }
        viewModelScope.launch {
            val croppedFile = withContext(Dispatchers.IO) {
                ImageUtils.cropToSquare(appContext, uri)
            }
            if (croppedFile != null) {
                _registerState.update {
                    it.copy(
                        avatarUri = Uri.fromFile(croppedFile),
                        avatarPath = croppedFile.absolutePath,
                        isProcessingAvatar = false
                    )
                }
            } else {
                _registerState.update { it.copy(isProcessingAvatar = false) }
                _events.emit(AuthEvent.Error("图片处理失败，请重试"))
            }
        }
    }

    fun register() {
        val state = _registerState.value
        var hasError = false

        if (state.username.isBlank() || state.username.length < Constants.MIN_USERNAME_LENGTH) {
            _registerState.update { it.copy(usernameError = "用户名至少${Constants.MIN_USERNAME_LENGTH}个字符") }
            hasError = true
        }
        if (state.password.length < Constants.MIN_PASSWORD_LENGTH) {
            _registerState.update { it.copy(passwordError = "密码至少${Constants.MIN_PASSWORD_LENGTH}位") }
            hasError = true
        }
        if (state.password != state.confirmPassword) {
            _registerState.update { it.copy(confirmPasswordError = "两次密码输入不一致") }
            hasError = true
        }
        if (hasError) return

        _registerState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = authRepository.register(state.username, state.password, state.avatarPath ?: "")
            result.fold(
                onSuccess = { user ->
                    authRepository.saveLoginState(user.id)
                    _registerState.update { it.copy(isLoading = false) }
                    delay(1)
                    _events.emit(AuthEvent.RegisterSuccess)
                },
                onFailure = { e ->
                    _registerState.update { it.copy(isLoading = false) }
                    _events.emit(AuthEvent.Error(e.message ?: "注册失败"))
                }
            )
        }
    }
}
