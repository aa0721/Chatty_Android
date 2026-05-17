package com.example.chatty_android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.chatty_android.common.constants.Constants
import com.example.chatty_android.common.utils.EncryptionUtils
import com.example.chatty_android.data.dao.UserDao
import com.example.chatty_android.data.entity.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey(Constants.KEY_IS_LOGGED_IN)
        private val KEY_USER_ID = longPreferencesKey(Constants.KEY_USER_ID)
        private val KEY_REMEMBER_PASSWORD = booleanPreferencesKey(Constants.KEY_REMEMBER_PASSWORD)
        private val KEY_SAVED_USERNAME = stringPreferencesKey(Constants.KEY_SAVED_USERNAME)
        private val KEY_SAVED_PASSWORD = stringPreferencesKey(Constants.KEY_SAVED_PASSWORD)
    }

    val isLoggedIn = dataStore.data.map { it[KEY_IS_LOGGED_IN] ?: false }
    val currentUserId = dataStore.data.map { it[KEY_USER_ID] ?: -1L }

    suspend fun login(username: String, password: String): Result<User> {
        val user = userDao.getUserByUsername(username)
            ?: return Result.failure(Exception("用户不存在"))

        val passwordHash = EncryptionUtils.hashPassword(password)
        if (user.passwordHash != passwordHash) {
            return Result.failure(Exception("密码错误"))
        }

        userDao.updateLastSeen(user.id, System.currentTimeMillis())
        return Result.success(user)
    }

    suspend fun register(username: String, password: String, avatarPath: String = ""): Result<User> {
        val existing = userDao.countByUsername(username)
        if (existing > 0) {
            return Result.failure(Exception("用户名已存在"))
        }

        val passwordHash = EncryptionUtils.hashPassword(password)
        val user = User(
            username = username,
            passwordHash = passwordHash,
            avatarPath = avatarPath
        )

        val userId = userDao.insert(user)
        val friendCode = generateFriendCode(userId)
        userDao.updateFriendCode(userId, friendCode)

        val createdUser = user.copy(id = userId, friendCode = friendCode)
        return Result.success(createdUser)
    }

    suspend fun saveLoginState(userId: Long) {
        dataStore.edit {
            it[KEY_IS_LOGGED_IN] = true
            it[KEY_USER_ID] = userId
        }
    }

    suspend fun saveCredentials(username: String, password: String) {
        // Store password hashed for remembered credentials
        val hashedPassword = EncryptionUtils.hashPassword(password)
        dataStore.edit {
            it[KEY_REMEMBER_PASSWORD] = true
            it[KEY_SAVED_USERNAME] = username
            it[KEY_SAVED_PASSWORD] = hashedPassword
        }
    }

    suspend fun clearCredentials() {
        dataStore.edit {
            it[KEY_REMEMBER_PASSWORD] = false
            it.remove(KEY_SAVED_USERNAME)
            it.remove(KEY_SAVED_PASSWORD)
        }
    }

    suspend fun getRememberPassword(): Boolean {
        return dataStore.data.first()[KEY_REMEMBER_PASSWORD] ?: false
    }

    suspend fun getSavedUsername(): String {
        return dataStore.data.first()[KEY_SAVED_USERNAME] ?: ""
    }

    suspend fun getSavedPassword(): String {
        return dataStore.data.first()[KEY_SAVED_PASSWORD] ?: ""
    }

    suspend fun logout() {
        dataStore.edit {
            it[KEY_IS_LOGGED_IN] = false
            it.remove(KEY_USER_ID)
        }
    }

    private fun generateFriendCode(userId: Long): String {
        val code = ((userId * 7919 + 1009) % 10000).toInt()
        return code.toString().padStart(4, '0')
    }
}
