package com.unh.personal_health_buddy.features

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.Authentication.FirestoreHelper
import com.unh.personal_health_buddy.database.Prescription
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicateScreen(navController: NavController) {
    // light + blue theme style
    val primaryBlue = Color(0xFF1877F2)
    val lightBlueBackground = Color(0xFFF3F6FF)

    val softTeal = primaryBlue
    val tealLight = lightBlueBackground
    val scope = rememberCoroutineScope()

    var isMenuExpanded by remember { mutableStateOf(false) }
    var showAddPrescriptionDialog by remember { mutableStateOf(false) }
    var prescriptions by remember { mutableStateOf(emptyList<Prescription>()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedPrescription by remember { mutableStateOf<Prescription?>(null) }

    // --- NOTIFICATION STATE ---
    var showNotifications by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf<List<com.unh.personal_health_buddy.features.HealthNotification>>(emptyList()) }

    // Load prescriptions from Firestore on launch
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val loadedPrescriptions = withContext(Dispatchers.IO) {
                FirestoreHelper.readAllPrescriptions()
            }
            prescriptions = loadedPrescriptions
        } catch (e: Exception) {
            Log.e("MedicateScreen", "Error loading prescriptions: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    // Generate notifications when prescriptions are loaded
    LaunchedEffect(prescriptions, isLoading) {
        if (!isLoading) {
            notifications = generateHealthNotifications(
                bmi = null,
                bmiCategory = "",
                bloodType = null,
                hasPrescriptions = prescriptions.isNotEmpty(),
                lastBmiCheckDays = 0
            )
            if (prescriptions.isNotEmpty()) {
                delay(1500)
                showNotifications = true
            }
        }
    }

    val backgroundGradient = Brush.verticalGradient(
        listOf(tealLight, Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Medication",
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = softTeal
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = softTeal
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    // 🔻 Move FAB closer to bottom
                    modifier = Modifier.padding(bottom = 24.dp, end = 16.dp)
                ) {
                    AnimatedVisibility(isMenuExpanded) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            MiniFabWithText(
                                icon = Icons.Default.Add,
                                text = "Add Prescription",
                                onClick = {
                                    showAddPrescriptionDialog = true
                                    isMenuExpanded = false
                                },
                                activeColor = softTeal
                            )

                            MiniFabWithText(
                                icon = Icons.Default.Delete,
                                text = "Clear All",
                                onClick = {
                                    scope.launch {
                                        try {
                                            withContext(Dispatchers.IO) {
                                                FirestoreHelper.deleteAllPrescriptions()
                                            }
                                            prescriptions = emptyList()
                                            isMenuExpanded = false
                                        } catch (e: Exception) {
                                            Log.e("MedicateScreen", "Error clearing prescriptions: ${e.message}")
                                        }
                                    }
                                },
                                activeColor = softTeal
                            )
                        }
                    }

                    FloatingActionButton(
                        onClick = { isMenuExpanded = !isMenuExpanded },
                        containerColor = softTeal,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Menu")
                    }
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = softTeal)
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    if (prescriptions.isEmpty()) {
                        EmptyPrescriptionState(activeColor = softTeal)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            items(prescriptions) { prescription ->
                                PrescriptionCard(
                                    prescription = prescription,
                                    activeColor = softTeal,
                                    onDetailsClick = { selectedPrescription = prescription }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Prescription dialog
        if (showAddPrescriptionDialog) {
            AddPrescriptionDialog(
                onDismiss = { showAddPrescriptionDialog = false },
                onSave = { prescription ->
                    scope.launch {
                        try {
                            withContext(Dispatchers.IO) {
                                FirestoreHelper.writePrescription(prescription)
                            }
                            val updatedPrescriptions = withContext(Dispatchers.IO) {
                                FirestoreHelper.readAllPrescriptions()
                            }
                            prescriptions = updatedPrescriptions
                            showAddPrescriptionDialog = false
                        } catch (e: Exception) {
                            Log.e("MedicateScreen", "Error saving prescription: ${e.message}")
                        }
                    }
                },
                activeColor = softTeal
            )
        }

        // Details dialog
        selectedPrescription?.let { item ->
            PrescriptionDetailsDialog(
                prescription = item,
                activeColor = softTeal,
                onDismiss = { selectedPrescription = null },
                onDelete = { id ->
                    scope.launch {
                        try {
                            withContext(Dispatchers.IO) {
                                FirestoreHelper.deletePrescription(id)
                            }
                            val updatedPrescriptions = withContext(Dispatchers.IO) {
                                FirestoreHelper.readAllPrescriptions()
                            }
                            prescriptions = updatedPrescriptions
                            selectedPrescription = null
                        } catch (e: Exception) {
                            Log.e("MedicateScreen", "Error deleting prescription: ${e.message}")
                        }
                    }
                }
            )
        }

        // NOTIFICATION DIALOG
        if (showNotifications && notifications.isNotEmpty()) {
            HealthNotificationDialog(
                notifications = notifications,
                onDismiss = { showNotifications = false },
                onClearNotification = { id ->
                    notifications = notifications.filter { it.id != id }
                    if (notifications.isEmpty()) {
                        showNotifications = false
                    }
                }
            )
        }
    }
}

@Composable
private fun MiniFabWithText(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    activeColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = activeColor,
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )

        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = Color.White,
            contentColor = activeColor,
            elevation = FloatingActionButtonDefaults.elevation(4.dp)
        ) {
            Icon(icon, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPrescriptionDialog(
    onDismiss: () -> Unit,
    onSave: (Prescription) -> Unit,
    activeColor: Color
) {
    var medName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Once daily") }
    var form by remember { mutableStateOf("Tablet") }
    var timeOfDay by remember { mutableStateOf("Morning") }
    var withFood by remember { mutableStateOf("With meal") }
    var notes by remember { mutableStateOf("") }

    val frequencyOptions = listOf(
        "Once daily", "Twice daily", "Three times daily", "Every 6 hours", "Every 8 hours", "As needed"
    )
    val formOptions = listOf("Tablet", "Capsule", "Syrup", "Injection", "Ointment", "Other")
    val timeOfDayOptions = listOf("Morning", "Afternoon", "Evening", "Night", "Before sleep", "As needed")
    val withFoodOptions = listOf("Before meal", "After meal", "With meal", "Doesn't matter")

    var expandFrequency by remember { mutableStateOf(false) }
    var expandForm by remember { mutableStateOf(false) }
    var expandTimeOfDay by remember { mutableStateOf(false) }
    var expandWithFood by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(Color.White),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    "Add Prescription",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = activeColor
                )

                CustomField(
                    value = medName,
                    onChange = { medName = it },
                    label = "Medication Name",
                    icon = Icons.Default.Medication,
                    activeColor = activeColor
                )
                CustomField(
                    value = dosage,
                    onChange = { dosage = it },
                    label = "Dosage (e.g. 10mg)",
                    icon = Icons.Default.Info,
                    activeColor = activeColor
                )

                // Frequency
                ExposedDropdownMenuBox(
                    expanded = expandFrequency,
                    onExpandedChange = { expandFrequency = !expandFrequency }
                ) {
                    OutlinedTextField(
                        value = frequency,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Frequency") },
                        leadingIcon = { Icon(Icons.Default.Schedule, null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandFrequency) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = activeColor,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = activeColor
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandFrequency,
                        onDismissRequest = { expandFrequency = false }
                    ) {
                        frequencyOptions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    frequency = item
                                    expandFrequency = false
                                }
                            )
                        }
                    }
                }

                // Form
                ExposedDropdownMenuBox(
                    expanded = expandForm,
                    onExpandedChange = { expandForm = !expandForm }
                ) {
                    OutlinedTextField(
                        value = form,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Form") },
                        leadingIcon = { Icon(Icons.Default.Info, null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandForm) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = activeColor,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = activeColor
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandForm,
                        onDismissRequest = { expandForm = false }
                    ) {
                        formOptions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    form = item
                                    expandForm = false
                                }
                            )
                        }
                    }
                }

                // Time of Day
                ExposedDropdownMenuBox(
                    expanded = expandTimeOfDay,
                    onExpandedChange = { expandTimeOfDay = !expandTimeOfDay }
                ) {
                    OutlinedTextField(
                        value = timeOfDay,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Time of Day") },
                        leadingIcon = { Icon(Icons.Default.Schedule, null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandTimeOfDay) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = activeColor,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = activeColor
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandTimeOfDay,
                        onDismissRequest = { expandTimeOfDay = false }
                    ) {
                        timeOfDayOptions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    timeOfDay = item
                                    expandTimeOfDay = false
                                }
                            )
                        }
                    }
                }

                // With Food
                ExposedDropdownMenuBox(
                    expanded = expandWithFood,
                    onExpandedChange = { expandWithFood = !expandWithFood }
                ) {
                    OutlinedTextField(
                        value = withFood,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("With Food?") },
                        leadingIcon = { Icon(Icons.Default.Info, null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandWithFood) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = activeColor,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = activeColor
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandWithFood,
                        onDismissRequest = { expandWithFood = false }
                    ) {
                        withFoodOptions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    withFood = item
                                    expandWithFood = false
                                }
                            )
                        }
                    }
                }

                CustomField(
                    value = notes,
                    onChange = { notes = it },
                    label = "Notes (Optional)",
                    icon = Icons.Default.Note,
                    keyboardType = KeyboardType.Text,
                    activeColor = activeColor
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val newP = Prescription(
                                name = medName,
                                dosage = dosage,
                                frequency = frequency,
                                form = form,
                                timeOfDay = timeOfDay,
                                withFood = withFood,
                                notes = notes
                            )
                            onSave(newP)
                        },
                        colors = ButtonDefaults.buttonColors(activeColor),
                        enabled = medName.isNotBlank() && dosage.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    activeColor: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedBorderColor = activeColor,
            unfocusedBorderColor = Color.LightGray,
            cursorColor = activeColor
        )
    )
}

@Composable
private fun EmptyPrescriptionState(activeColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(activeColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Medication,
                contentDescription = null,
                tint = activeColor.copy(alpha = 0.7f),
                modifier = Modifier.size(55.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            "No Prescriptions Added",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = activeColor
        )

        Text(
            "Tap the + button to add your first prescription.",
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
    }
}

@Composable
private fun PrescriptionCard(
    prescription: Prescription,
    activeColor: Color,
    onDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(activeColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Medication,
                        null,
                        tint = activeColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        prescription.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF004D40)
                    )
                    Text(
                        "${prescription.dosage} • ${prescription.frequency} • ${prescription.form}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        "${prescription.timeOfDay} • ${prescription.withFood}",
                        fontSize = 13.sp,
                        color = Color(0xFF607D8B)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = onDetailsClick,
                    colors = ButtonDefaults.outlinedButtonColors(activeColor),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(Icons.Default.Info, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Details")
                }
            }
            if (prescription.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Notes: ${prescription.notes}",
                    fontSize = 13.sp,
                    color = Color(0xFF455A64)
                )
            }
        }
    }
}

@Composable
private fun PrescriptionDetailsDialog(
    prescription: Prescription,
    activeColor: Color,
    onDismiss: () -> Unit,
    onDelete: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(Color.White),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = prescription.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = activeColor
                )
                Text("Dosage: ${prescription.dosage}")
                Text("Frequency: ${prescription.frequency}")
                Text("Form: ${prescription.form}")
                Text("Time of Day: ${prescription.timeOfDay}")
                Text("With Food: ${prescription.withFood}")
                if (prescription.notes.isNotBlank()) Text("Notes: ${prescription.notes}")
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    TextButton(onClick = { onDelete(prescription.id) }) {
                        Text("Delete", color = Color.Red, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicateScreenPreview() {
    PersonalHealthBuddyTheme {
        MedicateScreen(navController = rememberNavController())
    }
}
