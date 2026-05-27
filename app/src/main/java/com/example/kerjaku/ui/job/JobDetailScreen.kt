package com.example.kerjaku.ui.job

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun JobDetailScreen(
    jobId: String,
    navController: NavController,
    viewModel: JobViewModel
) {
    val job by viewModel.selectedJob.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.actionSuccess.collectAsState()

    LaunchedEffect(jobId) {
        viewModel.getJobDetail(jobId)
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            viewModel.resetActionState()
            navController.popBackStack() // Kembali ke katalog setelah berhasil apply
        }
    }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            if (isLoading || job == null) {
                CircularProgressIndicator()
            } else {
                job?.let {
                    Text(text = it.title, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Upah: Rp ${it.wage}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Deskripsi:", style = MaterialTheme.typography.titleSmall)
                    Text(text = it.description ?: "Tidak ada deskripsi detail.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Lokasi: ${it.village}, ${it.district}, ${it.city}")
                    Text(text = "Durasi: ${it.duration_days} hari kerja")

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { viewModel.applyJob(jobId) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text("Ambil Pekerjaan Ini")
                    }
                }
            }
        }
    }
}