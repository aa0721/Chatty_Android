package com.example.chatty_android.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chatty_android.ui.screens.AddFriendScreen
import com.example.chatty_android.ui.screens.ChatScreen
import com.example.chatty_android.ui.screens.LoginScreen
import com.example.chatty_android.ui.screens.MainScreen
import com.example.chatty_android.ui.screens.RegisterScreen
import com.example.chatty_android.ui.screens.SettingsScreen
import com.example.chatty_android.ui.screens.SplashScreen
import com.example.chatty_android.viewmodel.SplashViewModel

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val CHAT = "chat/{userId}"
    const val ADD_FRIEND = "add_friend"
    const val SETTINGS = "settings"

    fun chat(userId: Long) = "chat/$userId"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(Routes.SPLASH) {
            val vm: SplashViewModel = hiltViewModel()
            val isLoggedIn by vm.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
            SplashScreen(
                onNavigateToHome = { navController.navigate(Routes.MAIN) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                isLoggedIn = isLoggedIn
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Routes.MAIN) { popUpTo(0) { inclusive = true } } },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Routes.MAIN) { popUpTo(0) { inclusive = true } } },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(Routes.MAIN) {
            MainScreen(navController = navController)
        }
        composable(
            route = Routes.CHAT,
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) {
            ChatScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.ADD_FRIEND) {
            AddFriendScreen(
                onBack = { navController.popBackStack() },
                onFriendAdded = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = { navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } } },
                onAccountDeleted = { navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } } }
            )
        }
    }
}
