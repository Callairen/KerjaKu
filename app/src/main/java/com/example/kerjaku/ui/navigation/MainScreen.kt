package com.example.kerjaku.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            composable(Screen.CariKerja.route) {
                val jobViewModel: JobViewModel = viewModel()
                FindJobScreen(
                    navController = rootNavController,
                    viewModel = jobViewModel
                )
            }

            composable(Screen.KelolaPekerja.route) {
                val jobViewModel: JobViewModel = viewModel()
                com.example.kerjaku.ui.customer.CustomerHomeScreen(
                    navController = rootNavController,
                    viewModel = jobViewModel
                )
            }

            composable(Screen.Profil.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    navController = rootNavController,
                    onLogoutClick = {
                        rootNavController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
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

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
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
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    unselectedTextColor = Color.White.copy(alpha = 0.7f),
                    indicatorColor = Color.White
                )
            )
        }
    }
}
