package com.example.kerjaku.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String = "",
    @SerialName("full_name") val fullName: String, val email: String? = null,
    val phone: String? = null,
    val city: String? = null,
    val district: String? = null,
    val village: String? = null,
    val detail_address: String? = null,
    val balance: Double = 0.0,
    val role: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null, // Tambahkan @SerialName    val ktp_url: String? = null,
    val is_verified: Boolean = false
) {

}