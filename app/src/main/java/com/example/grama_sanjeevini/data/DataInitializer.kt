package com.example.grama_sanjeevini.data

import android.content.Context
import com.example.grama_sanjeevini.data.model.Medicine
import com.example.grama_sanjeevini.data.model.Pharmacist
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DataInitializer(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    suspend fun initializeDataIfFirstTime() {
        val isFirstRun = prefs.getBoolean("is_first_run", true)
        if (isFirstRun) {
            insertDummyPharmacists()
            prefs.edit().putBoolean("is_first_run", false).apply()
        }
    }

    private suspend fun insertDummyPharmacists() {
        val collection = db.collection("pharmacists")
        val batch = db.batch()

        for (i in 1..20) {
            val pharmacistId = "pharmacist$i"
            val pharmacist = Pharmacist(
                email = "pharmacist$i@example.com",
                password = "password$i",
                shopName = "Grama Shop $i",
                address = "Address Street $i, Village $i",
                distance = (1..30).random().toDouble()
            )
            val docRef = collection.document(pharmacistId)
            batch.set(docRef, pharmacist)

            // Add demo emergency medicines to the first pharmacist
            if (i == 1) {
                val emergencyMeds = listOf(
                    Medicine(medicineName = "Insulin", emergency = true, available = true, shopName = pharmacist.shopName, address = pharmacist.address, distance = pharmacist.distance),
                    Medicine(medicineName = "Snake Venom Antidote", emergency = true, available = true, shopName = pharmacist.shopName, address = pharmacist.address, distance = pharmacist.distance),
                    Medicine(medicineName = "Adrenaline Injection", emergency = true, available = false, shopName = pharmacist.shopName, address = pharmacist.address, distance = pharmacist.distance),
                    Medicine(medicineName = "Oxygen Cylinder", emergency = true, available = true, shopName = pharmacist.shopName, address = pharmacist.address, distance = pharmacist.distance)
                )
                
                emergencyMeds.forEach { med ->
                    val medRef = docRef.collection("medicines").document()
                    batch.set(medRef, med)
                }
            }
        }

        try {
            batch.commit().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
