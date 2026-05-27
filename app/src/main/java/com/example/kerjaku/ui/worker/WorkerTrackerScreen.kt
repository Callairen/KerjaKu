package com.example.kerjaku.ui.worker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kerjaku.data.model.JobApplication
import com.example.kerjaku.ui.job.JobViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerTrackerScreen(
    navController: NavController,
    viewModel: JobViewModel
) {
    val applications by viewModel.myApplications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchMyApplications()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Pekerjaan Saya") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (applications.isEmpty()) {
                Text("Anda belum melamar atau memiliki pekerjaan yang sedang berjalan.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(applications) { app ->
                        ApplicationTrackerCard(
                            application = app,
                            onFinishClick = {
                                // Navigasi ke form pelaporan selesai
                                navController.navigate("finish_job/${app.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationTrackerCard(application: JobApplication, onFinishClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(application.jobs?.title ?: "Pekerjaan Tidak Diketahui", style = MaterialTheme.typography.titleLarge)
            Text("Lokasi: ${application.jobs?.city ?: "-"}")
            Spacer(modifier = Modifier.height(12.dp))

            // Logika UI berdasarkan status
            when (application.status) {
                "APPLIED" -> {
                    Text("Status: Menunggu Persetujuan Pelanggan", color = MaterialTheme.colorScheme.tertiary)
                }
                "ACCEPTED" -> {
                    Text("Status: Sedang Berjalan", color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onFinishClick, modifier = Modifier.fillMaxWidth()) {
                        Text("Tandai Selesai")
                    }
                }
                "FINISHED" -> {
                    Text("Status: Selesai (Menunggu Verifikasi)", color = MaterialTheme.colorScheme.secondary)
                }
                "APPROVED_AND_PAID" -> {
                    Text("Status: Tuntas & Upah Telah Diterima", color = MaterialTheme.colorScheme.primary)
                }
                else -> Text("Status: ${application.status}")
            }
        }
    }
}