package com.example.grama_sanjeevini.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grama_sanjeevini.data.model.Item
import com.example.grama_sanjeevini.data.model.Medicine
import com.example.grama_sanjeevini.data.model.MedicineSearchResult
import com.example.grama_sanjeevini.data.model.Pharmacist
import com.example.grama_sanjeevini.data.repository.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository = MainRepository()) : ViewModel() {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    private val _searchResults = MutableStateFlow<List<MedicineSearchResult>>(emptyList())
    val searchResults: StateFlow<List<MedicineSearchResult>> = _searchResults.asStateFlow()

    private val _loginStatus = MutableStateFlow<Pharmacist?>(null)
    val loginStatus: StateFlow<Pharmacist?> = _loginStatus.asStateFlow()

    private val _pharmacistMedicines = MutableStateFlow<List<Medicine>>(emptyList())
    val pharmacistMedicines: StateFlow<List<Medicine>> = _pharmacistMedicines.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Removed loadItems() from init to save resources on startup

    fun loadItems() {
        if (_items.value.isNotEmpty()) return // Already loaded
        viewModelScope.launch {
            _isLoading.value = true
            _items.value = repository.getItems()
            _isLoading.value = false
        }
    }

    fun searchMedicines(query: String, radius: Int? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _searchResults.value = repository.searchMedicines(query, radius)
            _isLoading.value = false
        }
    }

    fun login(email: String, pass: String, onResult: (Pharmacist?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val pharmacist = repository.loginPharmacist(email, pass)
            _loginStatus.value = pharmacist
            _isLoading.value = false
            onResult(pharmacist)
            if (pharmacist != null) {
                android.util.Log.d("ViewModelLogin", "Login successful for ID: ${pharmacist.id}")
            } else {
                android.util.Log.e("ViewModelLogin", "Login failed in ViewModel")
            }
        }
    }

    fun addMedicine(pharmacistId: String, medicine: Medicine, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.addMedicine(pharmacistId, medicine)
            if (success) loadPharmacistMedicines(pharmacistId) // Refresh list
            onResult(success)
        }
    }

    fun loadPharmacistMedicines(pharmacistId: String) {
        // Load the pharmacist profile if not already loaded (e.g. on direct navigation or activity restart)
        if (_loginStatus.value == null) {
            viewModelScope.launch {
                _loginStatus.value = repository.getPharmacist(pharmacistId)
            }
        }

        repository.listenToMedicines(pharmacistId) { medicines ->
            _pharmacistMedicines.value = medicines
        }
    }

    fun updateMedicineAvailability(pharmacistId: String, medicineId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            repository.updateMedicineAvailability(pharmacistId, medicineId, isAvailable)
        }
    }
}
