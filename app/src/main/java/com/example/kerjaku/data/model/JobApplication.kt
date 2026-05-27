package com.example.kerjaku.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JobApplication(
    val id: String? = null,
    val job_id: String,
    val worker_id: String,
    val status: String = "APPLIED",
    val completion_notes: String? = null,
    val completion_proof_url: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)