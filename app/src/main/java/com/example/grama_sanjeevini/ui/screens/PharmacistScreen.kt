package com.example.grama_sanjeevini.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grama_sanjeevini.data.model.Medicine
import com.example.grama_sanjeevini.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacistScreen(
    pharmacistId: String,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    var newMedicineName by remember { mutableStateOf("") }
    var isEmergency by remember { mutableStateOf(false) }
    
    val medicines by viewModel.pharmacistMedicines.collectAsState()
    val currentPharmacist by viewModel.loginStatus.collectAsState()

    LaunchedEffect(pharmacistId) {
        viewModel.loadPharmacistMedicines(pharmacistId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pharmacist Dashboard", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Add New Medicine", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = newMedicineName,
                        onValueChange = { newMedicineName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isEmergency, onCheckedChange = { isEmergency = it })
                        Text("Emergency")
                    }
                    Button(onClick = {
                        if (newMedicineName.isNotBlank()) {
                            val medicineToAdd = Medicine(
                                medicineName = newMedicineName,
                                emergency = isEmergency,
                                available = true,
                                shopName = currentPharmacist?.shopName ?: "",
                                address = currentPharmacist?.address ?: "",
                                distance = currentPharmacist?.distance ?: 0.0
                            )
                            viewModel.addMedicine(pharmacistId, medicineToAdd) { success ->
                                if (success) {
                                    Toast.makeText(context, "Medicine added successfully", Toast.LENGTH_SHORT).show()
                                    newMedicineName = ""
                                    isEmergency = false
                                } else {
                                    Toast.makeText(context, "Failed to add medicine", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Add to Stock")
                    }
                }
            }

            items(medicines) { item ->
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(item.medicineName)
                            if (item.emergency) {
                                Text(
                                    text = "EMERGENCY",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Switch(checked = item.available, onCheckedChange = { available ->
                            viewModel.updateMedicineAvailability(pharmacistId, item.id, available)
                        })
                    }
                }
            }
        }
    }
}
