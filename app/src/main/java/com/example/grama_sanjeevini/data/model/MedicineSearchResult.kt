package com.example.grama_sanjeevini.data.model

data class MedicineSearchResult(
    val medicineName: String,
    val shopName: String,
    val address: String,
    val isAvailable: Boolean,
    val isEmergency: Boolean,
    val distance: Double = 0.0
)
