package com.example.chatty_android.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.example.chatty_android.navigation.Routes
import com.example.chatty_android.viewmodel.MainEvent
import com.example.chatty_android.viewmodel.MainViewModel

data class BottomTab(
    val label: String,
    val icon: ImageVector
)

private val tabs = listOf(
    BottomTab("聊天", Icons.Default.Email),
    BottomTab("通讯录", Icons.Default.Person),
    BottomTab("我的", Icons.Default.Face)
)

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { event ->
                when (event) {
                    is MainEvent.Logout -> {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Medium else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // 直接切换，不使用任何动画 = 彻底解决崩溃
            if (selectedTab == 0) {
                ChatListScreen(
                    onChatClick = { userId ->
                        navController.navigate(Routes.chat(userId))
                    }
                )
            }
            if (selectedTab == 1) {
                ContactsScreen(
                    onContactClick = { userId ->
                        navController.navigate(Routes.chat(userId))
                    },
                    onAddFriendClick = {
                        navController.navigate(Routes.ADD_FRIEND)
                    },
                    onMomentsClick = { }
                )
            }
            if (selectedTab == 2) {
                ProfileScreen(
                    onNavigateToSettings = {
                        navController.navigate(Routes.SETTINGS)
                    }
                )
            }
        }
    }
}