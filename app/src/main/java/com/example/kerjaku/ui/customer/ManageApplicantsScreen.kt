package com.example.kerjaku.ui.customer

import androidx.compose.foundation.BorderStroke
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
                                application.id?.let { appId -> viewModel.acceptApplicant(appId, jobId) }
                            },
                            onVerify = {
                                application.id?.let { appId -> viewModel.verifyJob(appId, jobId) }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicantItemCard(
    application: JobApplication,
    onAccept: () -> Unit,
    onVerify: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = application.profiles?.fullName ?: "Pengguna Tidak Diketahui",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Kontak: ${application.profiles?.phone ?: "-"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Lokasi Pekerja: ${application.profiles?.city ?: "-"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            when (application.status) {
                "APPLIED" -> {
                    Button(onClick = onAccept, modifier = Modifier.fillMaxWidth()) {
                        Text("Terima Pekerja Ini")
                    }
                }
                "ACCEPTED" -> {
                    Text(
                        text = "Status: Sedang Berjalan",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Menunggu pekerja menyelesaikan tugasnya.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                "FINISHED" -> {
                    Text(
                        text = "Status: Laporan Selesai Diterima",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Catatan Pekerja: ${application.completion_notes ?: "Tidak ada"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Tautan Bukti: ${application.completion_proof_url ?: "Tidak ada"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onVerify,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Verifikasi & Selesaikan Pembayaran")
                    }
                }
                "APPROVED_AND_PAID" -> {
                    Text(
                        text = "Status: Selesai & Dibayar",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                else -> {
                    Text(
                        text = "Status: ${application.status}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
