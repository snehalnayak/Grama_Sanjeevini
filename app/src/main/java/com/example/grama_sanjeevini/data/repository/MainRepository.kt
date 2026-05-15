package com.example.grama_sanjeevini.data.repository

import android.util.Log
import com.example.grama_sanjeevini.data.model.Item
import com.example.grama_sanjeevini.data.model.Medicine
import com.example.grama_sanjeevini.data.model.MedicineSearchResult
import com.example.grama_sanjeevini.data.model.MedicineShop
import com.example.grama_sanjeevini.data.model.Pharmacist
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MainRepository {
    // Initializing Firebase lazily to avoid blocking the UI thread during app startup
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val pharmacistsCollection by lazy { db.collection("pharmacists") }

    suspend fun loginPharmacist(email: String, password: String): Pharmacist? {
        return try {
            Log.d("FirestoreLogin", "Attempting login for email: $email")
            
            // Search all documents in "pharmacists" collection for matching email and password
            val querySnapshot = pharmacistsCollection
                .whereEqualTo("email", email.trim())
                .whereEqualTo("password", password.trim())
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0]
                val pharmacist = document.toObject(Pharmacist::class.java)?.copy(id = document.id)
                Log.d("FirestoreLogin", "Login successful! Found pharmacist: ${pharmacist?.shopName} with Document ID: ${document.id}")
                pharmacist
            } else {
                Log.w("FirestoreLogin", "Login failed: No matching pharmacist document found for provided credentials.")
                null
            }
        } catch (e: Exception) {
            Log.e("FirestoreLogin", "Login error: ${e.message}. Check Firestore rules or composite indexes.", e)
            null
        }
    }

    suspend fun registerPharmacist(pharmacist: Pharmacist): Boolean {
        return try {
            pharmacistsCollection.document(pharmacist.email).set(pharmacist).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getPharmacist(pharmacistId: String): Pharmacist? {
        return try {
            val document = pharmacistsCollection.document(pharmacistId).get().await()
            document.toObject(Pharmacist::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addMedicine(pharmacistId: String, medicine: Medicine): Boolean {
        return try {
            Log.d("Firestore", "Attempting to add medicine to: pharmacists/$pharmacistId/medicines")
            
            // Fetch latest shop details from the pharmacist document
            val pharmacist = getPharmacist(pharmacistId)
            val finalMedicine = if (pharmacist != null) {
                medicine.copy(
                    shopName = pharmacist.shopName,
                    address = pharmacist.address,
                    distance = pharmacist.distance
                )
            } else {
                medicine
            }

            pharmacistsCollection.document(pharmacistId)
                .collection("medicines")
                .add(finalMedicine)
                .await()
            Log.d("Firestore", "Medicine added successfully to $pharmacistId")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to add medicine to $pharmacistId: ${e.message}", e)
            false
        }
    }

    suspend fun getMedicines(pharmacistId: String): List<Medicine> {
        return try {
            val querySnapshot = pharmacistsCollection.document(pharmacistId)
                .collection("medicines")
                .get()
                .await()
            
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(Medicine::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun listenToMedicines(pharmacistId: String, onUpdate: (List<Medicine>) -> Unit) {
        pharmacistsCollection.document(pharmacistId)
            .collection("medicines")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }
                val medicines = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Medicine::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                onUpdate(medicines)
            }
    }

    suspend fun updateMedicineAvailability(pharmacistId: String, medicineId: String, isAvailable: Boolean): Boolean {
        return try {
            pharmacistsCollection.document(pharmacistId)
                .collection("medicines")
                .document(medicineId)
                .update("available", isAvailable)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getItems(): List<Item> {
        return listOf(
            Item(1, "Paracetamol", "This medicine is used for cold"),
            Item(2, "dolo 650", "This medicine is used for fever and cold"),
            Item(3, "ORS Powder", "This hydration")
        )
    }

    suspend fun getMedicineShops(): List<MedicineShop> {
        return listOf(
            MedicineShop(1, "City Pharma", "near junior college, kr pete", "9876543210", 4.5, true),
            MedicineShop(2, "Wellness Meds", "TB circle, kr pete", "8765432109", 4.2, true),
            MedicineShop(3, "Village Sanjeevini", "jaynagara, kr pete", "7654321098", 4.8, false),
            MedicineShop(4, "Care Plus Pharmacy", "mysore Road, kr pete", "6543210987", 4.0, true)
        )
    }

    suspend fun searchMedicines(query: String, radius: Int? = null): List<MedicineSearchResult> {
        return try {
            Log.d("FirestoreSearch", "User searched for: '$query' with radius: $radius")
            
            // Fetch all medicines from all pharmacies using collectionGroup
            val querySnapshot = db.collectionGroup("medicines").get().await()
            val allMedicinesCount = querySnapshot.size()
            Log.d("FirestoreSearch", "Total medicines fetched from Firestore: $allMedicinesCount")

            val searchResults = mutableListOf<MedicineSearchResult>()
            val foundShops = mutableSetOf<String>()

            for (document in querySnapshot.documents) {
                try {
                    // Safe extraction with null safety
                    val medName = document.getString("medicineName") ?: ""
                    val isAvail = document.getBoolean("available") ?: false
                    val isEmerg = document.getBoolean("emergency") ?: false
                    val shop = document.getString("shopName") ?: ""
                    val addr = document.getString("address") ?: ""
                    val dist = document.getDouble("distance") ?: 0.0

                    // Search condition: contains query (ignore case)
                    val matchesQuery = medName.contains(query, ignoreCase = true)
                    
                    // Filter condition: within selected radius
                    val withinRadius = radius == null || dist <= radius.toDouble()

                    if (matchesQuery && withinRadius) {
                        searchResults.add(
                            MedicineSearchResult(
                                medicineName = medName,
                                shopName = shop,
                                address = addr,
                                isAvailable = isAvail,
                                isEmergency = isEmerg,
                                distance = dist
                            )
                        )
                        if (shop.isNotBlank()) foundShops.add(shop)
                    }
                } catch (mapError: Exception) {
                    Log.e("FirestoreSearch", "Error mapping document ${document.id}: ${mapError.message}")
                }
            }

            Log.d("FirestoreSearch", "Unique shops found: ${foundShops.joinToString(", ")}")
            Log.d("FirestoreSearch", "Final search results count: ${searchResults.size}")

            searchResults.sortedBy { it.distance }
        } catch (e: Exception) {
            Log.e("FirestoreSearch", "Critical search failure: ${e.message}", e)
            emptyList()
        }
    }
}
