package com.example.kerjaku.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopUpScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val nominals = listOf(50000.0, 100000.0, 150000.0, 250000.0)
    var selectedAmount by remember { mutableStateOf<Double?>(null) }

    val isLoading = viewModel.isLoading
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Simulasi nomor Virtual Account
    val virtualAccount = "8839210${viewModel.profile?.phone?.takeLast(4) ?: "9999"}"

    Scaffold(
        topBar = { TopAppBar(title = { Text("Top Up Saldo") }) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Pilih Nominal Top-Up", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(120.dp)
            ) {
                items(nominals) { amount ->
                    OutlinedButton(
                        onClick = { selectedAmount = amount },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedAmount == amount) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text("Rp ${amount.toInt()}")
                    }
                }
            }

            if (selectedAmount != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Transfer ke Virtual Account Bank:")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(virtualAccount, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(virtualAccount))
                                Toast.makeText(context, "VA disalin", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Salin")
                            }
                        }
                        Text("Nominal: Rp ${selectedAmount!!.toInt()}", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.topUpBalance(selectedAmount!!) {
                            Toast.makeText(context, "Top-Up Berhasil Simulasi!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    else Text("Konfirmasi Pembayaran (Simulasi)")
                }
            }
        }
    }
}