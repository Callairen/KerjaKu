package com.example.kerjaku.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kerjaku.data.network.SupabaseApi
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var fullName by mutableStateOf("")
    var role by mutableStateOf("customer") // 'worker' atau 'customer'

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun login(onSuccess: () -> Unit) = viewModelScope.launch {
        isLoading = true
        errorMessage = null
        try {
            SupabaseApi.client.auth.signInWith(Email) {
                this.email = this@AuthViewModel.email
                this.password = this@AuthViewModel.password
            }
            onSuccess()
        } catch (e: Exception) {
            errorMessage = "Login gagal: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    fun register(onSuccess: () -> Unit) = viewModelScope.launch {
        isLoading = true
        errorMessage = null
        try {
            SupabaseApi.client.auth.signUpWith(Email) {
                this.email = this@AuthViewModel.email
                this.password = this@AuthViewModel.password
            }

            val uid = SupabaseApi.client.auth.currentUserOrNull()?.id ?: return@launch
            val profileData = mapOf(
                "id" to uid,
                "full_name" to fullName,
                "role" to role
            )
            SupabaseApi.client.postgrest.from("profiles").insert(profileData)

            onSuccess()
        } catch (e: Exception) {
            errorMessage = "Registrasi gagal: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    fun isLoggedIn(): Boolean {
        return SupabaseApi.client.auth.currentUserOrNull() != null
    }
}
