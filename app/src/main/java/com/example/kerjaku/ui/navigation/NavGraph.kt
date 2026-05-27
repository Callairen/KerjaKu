package com.example.kerjaku.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kerjaku.ui.auth.AuthViewModel
import com.example.kerjaku.ui.auth.LoginScreen
import com.example.kerjaku.ui.auth.RegisterScreen
import com.example.kerjaku.ui.profile.EditProfileScreen
import com.example.kerjaku.ui.profile.ProfileViewModel

@Composable
fun KerjaKuNavGraph() {
    val rootNavController = rememberNavController()

    // Inisialisasi ViewModels di level Root agar state terjaga
    val authViewModel: AuthViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    NavHost(
        navController = rootNavController,
        startDestination = Screen.Login.route
    ) {

        // --- AUTENTIKASI ---
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    // Jika login sukses, arahkan ke MainScreen (Container Bottom Nav)
                    rootNavController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    rootNavController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    rootNavController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    rootNavController.popBackStack()
                }
            )
        }

        // --- MAIN CONTAINER (TABS) ---
        composable(Screen.Main.route) {
            MainScreen(
                rootNavController = rootNavController,
                profileViewModel = profileViewModel
            )
        }

        // --- HALAMAN DETAIL & FORM ---
        // (Rute ini diletakkan di Root NavHost agar ketika dibuka, menutupi Bottom Navigation)

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                viewModel = profileViewModel,
                onNavigateBack = {
                    rootNavController.popBackStack()
                }
            )
        }

        // TODO: composable(Screen.JobDetail.route) { backStackEntry -> ... }
        // TODO: composable(Screen.CreateJob.route) { ... }
    }
}