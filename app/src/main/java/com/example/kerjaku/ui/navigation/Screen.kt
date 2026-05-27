package com.example.kerjaku.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    // Auth Routes
    object Login : Screen("login")
    object Register : Screen("register")

    // Main Container Route
    object Main : Screen("main")

    // Bottom Navigation Routes
    object CariKerja : Screen("cari_kerja", "Cari Kerja", Icons.Filled.Work)
    object KelolaPekerja : Screen("kelola_pekerja", "Kelola Pekerja", Icons.Filled.Home)
    object Profil : Screen("profil", "Profil", Icons.Filled.AccountCircle)

    // Sub-routes (Detail & Forms)
    object EditProfile : Screen("edit_profile")
    object CreateJob : Screen("create_job")
    object JobDetail : Screen("job_detail/{jobId}") {
        fun createRoute(jobId: String) = "job_detail/$jobId"
    }
    object ManageApplicants : Screen("manage_applicants/{jobId}") {
        fun createRoute(jobId: String) = "manage_applicants/$jobId"
    }

    object WorkerTracker : Screen("worker_tracker")
    object FinishJob : Screen("finish_job/{applicationId}") {
        fun createRoute(applicationId: String) = "finish_job/$applicationId"
    }
}
