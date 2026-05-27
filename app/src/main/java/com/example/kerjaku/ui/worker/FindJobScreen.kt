package com.example.kerjaku.ui.worker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kerjaku.data.model.Job
import com.example.kerjaku.ui.job.JobViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindJobScreen(
    navController: NavController,
    viewModel: JobViewModel
) {
    val jobs by viewModel.jobs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchOpenJobs()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Katalog Pekerjaan") },
                actions = {
                    IconButton(onClick = { navController.navigate("worker_tracker") }) {
                        Icon(Icons.Default.List, contentDescription = "Pekerjaan Saya")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                if (jobs.isEmpty()) {
                    Text("Belum ada pekerjaan harian di sekitarmu.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(jobs) { job ->
                            JobItemCard(job = job, onClick = {
                                navController.navigate("job_detail/${job.id}")
                            })
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobItemCard(job: Job, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(job.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Upah: Rp${job.wage}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Lokasi: ${job.village ?: "-"}, ${job.city}", style = MaterialTheme.typography.bodySmall)
            Text("Durasi: ${job.duration_days} hari", style = MaterialTheme.typography.bodySmall)
        }
    }
}