package com.example.kerjaku.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkerProfile(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("category_id") val categoryId: Long? = null,
    val bio: String? = null,
    @SerialName("daily_rate") val dailyRate: Double = 0.0,
    @SerialName("years_experience") val yearsExperience: Int = 0,
    @SerialName("avg_rating") val avgRating: Double = 0.0,
    // Opsional: Untuk kemudahan menampung data join dari Supabase
    val profile: Profile? = null
)