package com.example.kerjaku.ui.customer

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
fun ManageApplicantsScreen(
    jobId: String,
    navController: NavController,
    viewModel: JobViewModel
) {
    val applicants by viewModel.applicants.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.actionSuccess.collectAsState()

    LaunchedEffect(jobId) {
        viewModel.fetchApplicants(jobId)
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            viewModel.resetActionState()
            // Kembali secara otomatis jika pelamar sudah diterima dan status berganti ON_GOING
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Daftar Pelamar") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (applicants.isEmpty()) {
                Text("Belum ada pekerja yang melamar pekerjaan ini.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(applicants) { application ->
                        ApplicantItemCard(
                            application = application,
                            onAccept = {
                                application.id?.let { appId ->
                                    viewModel.acceptApplicant(appId, jobId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicantItemCard(application: JobApplication, onAccept: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Kita memanfaatkan relasi profiles yang diambil tadi
            Text(application.profiles?.full_name ?: "Pengguna Tidak Diketahui", style = MaterialTheme.typography.titleLarge)
            Text("Kontak: ${application.profiles?.phone ?: "-"}")
            Text("Lokasi Pekerja: ${application.profiles?.city ?: "-"}")

            Spacer(modifier = Modifier.height(12.dp))

            if (application.status == "APPLIED") {
                Button(onClick = onAccept, modifier = Modifier.fillMaxWidth()) {
                    Text("Terima Pekerja Ini")
                }
            } else {
                Text("Status: ${application.status}", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}