package com.example.kerjaku.ui.job

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

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

    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val actionSuccess by viewModel.actionSuccess.collectAsState()

    //img picker
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImageUri = uri
        }
    )

    LaunchedEffect(actionSuccess) {
        if (actionSuccess) {
            viewModel.resetActionState()
            viewModel.fetchMyPostedJobs()
            navController.popBackStack()
        }
    }

    // ERROR DIALOG
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.clearErrorMessage()
            },
            title = {
                Text("Pembuatan Gagal")
            },
            text = {
                Text(errorMessage ?: "")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearErrorMessage()
                        navController.navigate("top_up")
                    }
                ) {
                    Text("Isi Saldo")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.clearErrorMessage()
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Buat Lowongan Baru",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
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

            // JUDUL
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                },
                label = {
                    Text("Judul Pekerjaan")
                },
                placeholder = {
                    Text("Contoh: Perbaikan Keran")
                },
                modifier = Modifier.fillMaxWidth()
            )

            // DESKRIPSI
            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                },
                label = {
                    Text("Deskripsi Lengkap")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            // UPAH
            OutlinedTextField(
                value = wage,
                onValueChange = {
                    wage = it
                },
                label = {
                    Text("Upah (Rp)")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // DURASI
            OutlinedTextField(
                value = duration,
                onValueChange = {
                    duration = it
                },
                label = {
                    Text("Durasi Pengerjaan (Hari)")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // KOTA
            OutlinedTextField(
                value = city,
                onValueChange = {
                    city = it
                },
                label = {
                    Text("Kota / Kabupaten")
                },
                modifier = Modifier.fillMaxWidth()
            )

            // KECAMATAN
            OutlinedTextField(
                value = district,
                onValueChange = {
                    district = it
                },
                label = {
                    Text("Kecamatan")
                },
                modifier = Modifier.fillMaxWidth()
            )

            // DESA
            OutlinedTextField(
                value = village,
                onValueChange = {
                    village = it
                },
                label = {
                    Text("Kelurahan / Desa")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // PICK IMAGE
            Text(
                text = "Foto Lokasi/Pekerjaan (Opsional)",
                style = MaterialTheme.typography.titleSmall
            )

            OutlinedButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (selectedImageUri == null)
                        "Pilih Gambar"
                    else
                        "Ganti Gambar"
                )
            }

            // PREVIEW IMAGE
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Preview Gambar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BUTTON SUBMIT
            Button(
                onClick = {

                    // KONVERSI URI -> BYTE ARRAY
                    val imageBytes = selectedImageUri?.let { uri ->
                        context.contentResolver
                            .openInputStream(uri)
                            ?.readBytes()
                    }

                    viewModel.createNewJob(
                        title = title,
                        description = description,
                        wage = wage.toDoubleOrNull() ?: 0.0,
                        duration = duration.toIntOrNull() ?: 1,
                        city = city,
                        district = district,
                        village = village,
                        imageBytes = imageBytes
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading &&
                        title.isNotBlank() &&
                        wage.isNotBlank()
            ) {

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Posting Pekerjaan")
                }
            }
        }
    }
}

