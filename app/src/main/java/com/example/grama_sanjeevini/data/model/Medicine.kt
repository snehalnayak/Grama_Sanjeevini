package com.example.grama_sanjeevini.data.model

data class Medicine(
    val id: String = "",
    val medicineName: String = "",
    val emergency: Boolean = false,
    val available: Boolean = true,
    val shopName: String = "",
    val address: String = "",
    val distance: Double = 0.0
)
