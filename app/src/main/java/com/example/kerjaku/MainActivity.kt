package com.example.kerjaku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.kerjaku.ui.navigation.KerjaKuNavGraph

// PENTING: Jika di Theme.kt namanya KerjakuTheme (k kecil), ubah import ini menjadi KerjakuTheme
import com.example.kerjaku.ui.theme.KerjaKuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // PENTING: Pastikan nama ini sama dengan yang ada di Theme.kt
            KerjaKuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Memanggil rute navigasi utama aplikasi
                    KerjaKuNavGraph()
                }
            }
        }
    }
}