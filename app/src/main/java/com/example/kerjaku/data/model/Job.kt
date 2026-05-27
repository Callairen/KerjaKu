package com.example.kerjaku.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Job(
    val id: String = "",
    val employer_id: String,
    val category_id: Long? = null,
    val title: String,
    val description: String? = null,
    val wage: Double,
    val duration_days: Int,
    val city: String,
    val district: String? = null,
    val village: String? = null,
    val status: String = "OPEN",
    val created_at: String? = null
)