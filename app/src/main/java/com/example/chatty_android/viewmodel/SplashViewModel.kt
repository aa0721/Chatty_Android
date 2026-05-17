package com.example.chatty_android.viewmodel

import androidx.lifecycle.ViewModel
import com.example.chatty_android.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {
    val isLoggedIn: Flow<Boolean> = authRepository.isLoggedIn
}
