package com.example.kerjaku.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JobCategory(
    val id: Long = 0,
    val name: String = "",
    @SerialName("icon_name") val iconName: String? = null,
    val description: String? = null
)