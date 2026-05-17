package com.example.chatty_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import com.example.chatty_android.data.repository.SettingsRepository
import com.example.chatty_android.navigation.NavGraph
import com.example.chatty_android.ui.theme.ChattyAndroidTheme
import com.example.chatty_android.ui.theme.ThemeColor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeColor by settingsRepository.themeColor.collectAsStateWithLifecycle(initialValue = ThemeColor.CHINESE_RED)
            val isDark by settingsRepository.isDarkTheme.collectAsStateWithLifecycle(initialValue = false)

            ChattyAndroidTheme(themeColor = themeColor, darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavGraph()
                }
            }
        }
    }
}
