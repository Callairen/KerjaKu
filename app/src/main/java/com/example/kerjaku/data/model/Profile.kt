package com.example.kerjaku.data.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String = "",
    @SerialName("full_name") val fullName: String = "",
    val phone: String? = null,
    val city: String? = null,
    val role: String = "", // 'worker' atau 'customer'
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("ktp_url") val ktpUrl: String? = null,
    @SerialName("is_verified") val isVerified: Boolean = false
) {
    val full_name: String
}