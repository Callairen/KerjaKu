package com.example.kerjaku.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")

    // Anda dapat menambahkan rute lain nanti seperti:
    // object Profile : Screen("profile")
    // data class WorkerDetail(val workerId: String) : Screen("worker_detail/$workerId")
}