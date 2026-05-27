package com.example.kerjaku.ui.job

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJobScreen(
    navController: NavController,
    viewModel: JobViewModel
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var wage by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val actionSuccess by viewModel.actionSuccess.collectAsState()

    LaunchedEffect(actionSuccess) {
        if (actionSuccess) {
            viewModel.resetActionState()
            viewModel.fetchMyPostedJobs() // Segarkan daftar pekerjaan
            navController.popBackStack()  // Kembali ke Dasbor setelah sukses
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Buat Pekerjaan Baru") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul Pekerjaan (Contoh: Pebaikan Keran)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            OutlinedTextField(
                value = wage,
                onValueChange = { wage = it },
                label = { Text("Upah (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Durasi Pengerjaan (Hari)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Kota / Kabupaten") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("Kecamatan") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = village,
                onValueChange = { village = it },
                label = { Text("Kelurahan / Desa") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.createNewJob(
                        title = title,
                        description = description,
                        wage = wage.toDoubleOrNull() ?: 0.0,
                        duration = duration.toIntOrNull() ?: 1,
                        city = city,
                        district = district,
                        village = village
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && title.isNotBlank() && wage.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Posting Pekerjaan")
                }
            }
        }
    }
}