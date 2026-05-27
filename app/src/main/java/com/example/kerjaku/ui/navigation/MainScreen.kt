package com.example.kerjaku.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kerjaku.ui.job.JobViewModel
import com.example.kerjaku.ui.profile.ProfileScreen
import com.example.kerjaku.ui.profile.ProfileViewModel
import com.example.kerjaku.ui.worker.FindJobScreen

@Composable
fun MainScreen(
    rootNavController: NavHostController,
    profileViewModel: ProfileViewModel
) {
    // NavController khusus untuk mengelola perpindahan antar tab di BottomBar
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = bottomNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.CariKerja.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // TAB 1: Mode Pekerja (Mencari Pekerjaan)
            composable(Screen.CariKerja.route) {
                val jobViewModel: JobViewModel = viewModel()
                FindJobScreen(
                    navController = rootNavController, // Pakai rootNav agar layar detail menutupi tab bar
                    viewModel = jobViewModel
                )
            }

            // TAB 2: Mode Pelanggan (Mengelola Pekerjaan yang Dibuat)
            composable(Screen.KelolaPekerja.route) {
                // TODO: Panggil CustomerHomeScreen(navController = rootNavController)
                Text("Dasbor Kelola Pekerja")
            }

            // TAB 3: Profil Universal
            composable(Screen.Profil.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onLogoutClick = {
                        rootNavController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true } // Bersihkan seluruh riwayat navigasi
                        }
                    },
                    onNavigateToEdit = {
                        rootNavController.navigate(Screen.EditProfile.route)
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.CariKerja,
        Screen.KelolaPekerja,
        Screen.Profil
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    screen.icon?.let { icon ->
                        Icon(imageVector = icon, contentDescription = screen.title)
                    }
                },
                label = { Text(text = screen.title ?: "") },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Mencegah penumpukan instance halaman yang sama
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}