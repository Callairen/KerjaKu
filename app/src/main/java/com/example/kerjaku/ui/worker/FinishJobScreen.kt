package com.example.kerjaku.ui.worker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kerjaku.ui.job.JobViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinishJobScreen(
    applicationId: String,
    navController: NavController,
    viewModel: JobViewModel
) {
    var notes by remember { mutableStateOf("") }
    var proofUrl by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.actionSuccess.collectAsState()

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            viewModel.resetActionState()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Laporan Penyelesaian") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Text("Berikan deskripsi singkat dan tautan foto hasil kerja Anda kepada pelanggan.")
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Catatan Pengerjaan") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = proofUrl,
                onValueChange = { proofUrl = it },
                label = { Text("Link Bukti Foto (Gdrive/Imgur dll)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.finishJob(applicationId, notes, proofUrl) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && notes.isNotBlank()
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                else Text("Kirim Laporan Selesai")
            }
        }
    }
}