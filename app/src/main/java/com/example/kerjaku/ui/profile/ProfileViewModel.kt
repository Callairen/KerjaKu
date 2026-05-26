package com.example.kerjaku.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kerjaku.data.model.Profile
import com.example.kerjaku.data.network.SupabaseApi
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    var profile by mutableStateOf<Profile?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadProfile() = viewModelScope.launch {
        isLoading = true
        errorMessage = null
        try {
            val uid = SupabaseApi.client.auth.currentUserOrNull()?.id ?: return@launch
            profile = SupabaseApi.client.postgrest["profiles"]
                .select(columns = Columns.ALL) {
                    filter { eq("id", uid) }
                }.decodeSingleOrNull<Profile>()
        } catch (e: Exception) {
            errorMessage = "Gagal memuat profil: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    fun uploadAvatar(imageBytes: ByteArray, onSuccess: (String) -> Unit) = viewModelScope.launch {
        isLoading = true
        errorMessage = null
        try {
            val uid = SupabaseApi.client.auth.currentUserOrNull()?.id ?: return@launch
            val path = "$uid/avatar_${System.currentTimeMillis()}.jpg"

            SupabaseApi.client.storage.from("avatars").upload(path, imageBytes) {
                upsert = true
            }
            
            val url = SupabaseApi.client.storage.from("avatars").publicUrl(path)

            SupabaseApi.client.postgrest.from("profiles").update(
                mapOf("avatar_url" to url)
            ) {
                filter { eq("id", uid) }
            }

            loadProfile()
            onSuccess(url)
        } catch (e: Exception) {
            errorMessage = "Gagal unggah foto: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    fun updateProfileData(phone: String, city: String, onSuccess: () -> Unit) = viewModelScope.launch {
        isLoading = true
        errorMessage = null
        try {
            val uid = SupabaseApi.client.auth.currentUserOrNull()?.id ?: return@launch

            SupabaseApi.client.postgrest["profiles"].update(
                {
                    set("phone", phone)
                    set("city", city)
                }
            ) { filter { eq("id", uid) } }

            loadProfile() 
            onSuccess()
        } catch (e: Exception) {
            errorMessage = "Gagal memperbarui profil: ${e.message}"
        } finally {
            isLoading = false
        }
    }
}
