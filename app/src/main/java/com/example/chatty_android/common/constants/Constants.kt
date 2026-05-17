package com.example.chatty_android.common.constants

object Constants {
    // DataStore
    const val PREFERENCES_NAME = "chatty_prefs"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_USER_ID = "user_id"
    const val KEY_DARK_THEME = "dark_theme"
    const val KEY_THEME_COLOR = "theme_color"
    const val KEY_LANGUAGE = "language"
    const val KEY_REMEMBER_PASSWORD = "remember_password"
    const val KEY_SAVED_USERNAME = "saved_username"
    const val KEY_SAVED_PASSWORD = "saved_password"

    // Database
    const val DATABASE_NAME = "chatty_db"

    // Encryption
    const val AES_ALGORITHM = "AES"
    const val AES_MODE = "AES/GCM/NoPadding"
    const val KEY_STORE_ALIAS = "chatty_key"
    const val RSA_ALGORITHM = "RSA"
    const val RSA_MODE = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"

    // Permission request codes
    const val REQUEST_CODE_CAMERA = 1001
    const val REQUEST_CODE_GALLERY = 1002
    const val REQUEST_CODE_STORAGE = 1003

    // File
    const val MAX_IMAGE_SIZE = 10 * 1024 * 1024L // 10MB
    const val IMAGE_COMPRESS_QUALITY = 85
    const val AVATAR_DIR = "avatars"
    const val IMAGE_DIR = "images"
    const val AVATAR_SIZE_DP = 120

    // Message
    const val MESSAGE_PAGE_SIZE = 20
    const val MAX_MESSAGE_LENGTH = 5000

    // Auth
    const val MIN_USERNAME_LENGTH = 3
    const val MIN_PASSWORD_LENGTH = 6
    const val MAX_USERNAME_LENGTH = 20
}
