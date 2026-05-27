package com.example.kerjaku.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kerjaku.ui.job.JobViewModel
import com.example.kerjaku.ui.navigation.Screen
import com.example.kerjaku.ui.worker.JobItemCard

@Composable
fun CustomerHomeScreen(
    navController: NavController,
    viewModel: JobViewModel
) {
    val myJobs by viewModel.myPostedJobs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchMyPostedJobs()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.CreateJob.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Buat Pekerjaan")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Text("Pekerjaan yang Saya Buat", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else if (myJobs.isEmpty()) {
                Text("Anda belum membuat daftar pekerjaan apa pun. Tekan tombol + untuk membuat pekerjaan baru.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(myJobs) { job ->
                        JobItemCard(job = job, onClick = {
                            // Nantinya di Fase 5 kita arahkan ke halaman "Manajemen Pelamar"
                            // navController.navigate("manage_applicants/${job.id}")
                        })
                    }
                }
            }
        }
    }
}