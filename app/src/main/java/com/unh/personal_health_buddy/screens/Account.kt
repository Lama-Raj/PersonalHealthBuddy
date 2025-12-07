import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.OutlinedTextField

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.unh.personal_health_buddy.Authentication.FirestoreHelper
import com.unh.personal_health_buddy.database.*
import com.unh.personal_health_buddy.ui.theme.ChatGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.URL


@Composable
fun EditableOrInfoRow(
    label: String,
    value: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    isDropdown: Boolean = false,
    dropdownOptions: List<String> = emptyList()
) {
    val displayValue = if (value.isBlank()) "N/A" else value
    var expandedDropdown by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.weight(0.4f)
        )

        if (isEditing && !readOnly) {
            if (isDropdown && dropdownOptions.isNotEmpty()) {
                Box(modifier = Modifier.weight(0.6f)) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.White
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f)
                        )
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { expandedDropdown = !expandedDropdown }
                    )

                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        dropdownOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onValueChange(option)
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    modifier = Modifier
                        .weight(0.6f)
                        .height(50.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f)
                    )
                )
            }
        } else {
            Text(
                text = displayValue,
                fontWeight = FontWeight.Normal,
                color = if (readOnly && isEditing) Color.White.copy(alpha = 0.6f) else Color.White,
                fontStyle = if (displayValue == "N/A") FontStyle.Italic else FontStyle.Normal,
                modifier = Modifier.weight(0.6f)
            )
        }
    }
}

val BLOOD_GROUPS = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")




@Composable
fun AccountTopSection(
    navController: NavHostController,
    user: User?,
    expandedDropdown: Boolean,
    onOptionsClick: () -> Unit,
    onDismissDropdown: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val firstName = user?.firstname ?: "User"

    // Top headline (keeps as-is)


    LaunchedEffect(user?.profileImageUrl, TempProfileStorage.tempProfileBitmap) {
        val tempBitmap = TempProfileStorage.tempProfileBitmap
        if (tempBitmap != null) {
            profileBitmap = tempBitmap
        } else {
            user?.profileImageUrl?.let { url ->
                try {
                    withContext(Dispatchers.IO) {
                        val stream = URL(url).openStream()
                        profileBitmap = BitmapFactory.decodeStream(stream)
                    }
                } catch (e: Exception) {
                    Log.e("AccountTopSection", "Error loading image: ${e.message}")
                }
            }
        }
    }

    val imageBitmap = profileBitmap?.asImageBitmap()

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF8EEA91))
                .offset(y = (-32).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            BackHeader(
                title = "Profile",
                onBack = { navController.navigate("profile") },
                color = Color(0xFFFFFFFF)

            )

            Text(
                modifier = Modifier.offset(y = (-32).dp),
                text = "User Information",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFFFFF)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default Profile",
                    modifier = Modifier.size(120.dp),
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // <-- FIX: explicitly set color here so fetched firstName is not default black
            Text(
                text = firstName,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFFFFFF)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp, end = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = onOptionsClick)
                    .padding(top = 18.dp, start = 8.dp, end = 8.dp)
                    .offset(y = (-32).dp)
            ) {
                Text(
                    text = "Options",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFFFFFFF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Show options",
                    tint = Color(0xFFFFFFFF),
                    modifier = Modifier.size(20.dp)
                )
            }

            DropdownMenu(
                expanded = expandedDropdown,
                onDismissRequest = onDismissDropdown
            ) {
                // --- NEW ITEM: Navigate to Account Form ---
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit User Details",
                                tint = ChatGreen
                            )
                            Text(
                                text = "Account Form",
                                color = ChatGreen
                            )

                        }
                    },
                    onClick = {
                        onDismissDropdown()
                        navController.navigate("account-form")
                    }
                )
                Divider()
                // --- EXISTING ITEM: Delete Account ---
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                            Text("Delete Account", color = Color.Red)
                        }
                    },
                    onClick = {
                        onDismissDropdown()
                        onDeleteClick()
                    }
                )
            }
        }
    }
}


@Composable
fun AccountTopSection(
    navController: NavHostController,
    user: User?,
    expandedDropdown: Boolean,
    onOptionsClick: () -> Unit,
    onDismissDropdown: () -> Unit,
    onDeleteClick: () -> Unit,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    isLoading: Boolean
) {
    var profileBitmap by remember { mutableStateOf<Bitmap?>(TempProfileStorage.tempProfileBitmap) }
    val firstName = user?.firstname ?: "User"
    var expandedEditDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(user?.profileImageUrl) {
        if (profileBitmap == null) {
            user?.profileImageUrl?.let { url ->
                try {
                    withContext(Dispatchers.IO) {
                        val stream = URL(url).openStream()
                        val bitmap = BitmapFactory.decodeStream(stream)
                        profileBitmap = bitmap
                        TempProfileStorage.tempProfileBitmap = bitmap // store for caching
                    }
                } catch (e: Exception) {
                    Log.e("AccountTopSection", "Error loading image: ${e.message}")
                }
            }
        }
    }

    val imageBitmap = profileBitmap?.asImageBitmap()

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BackHeader(
                title = "Profile",
                onBack = { navController.navigate("profile") },
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default Profile",
                    modifier = Modifier.size(120.dp),
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = firstName,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Top-right dropdown (Options)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 0.dp, end = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = onOptionsClick)
                    .padding(top = 16.dp, start = 8.dp, end = 8.dp)
            ) {
                Text(
                    text = "Options",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Show options",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            DropdownMenu(
                expanded = expandedDropdown,
                onDismissRequest = onDismissDropdown
            ) {
                DropdownMenuItem(
                    text = { Text("Account Form", color = Color(0xFF1976D2)) },
                    onClick = {
                        onDismissDropdown()
                        navController.navigate("account-form")
                    }
                )
                Divider()
                DropdownMenuItem(
                    text = { Text("Delete Account", color = Color.Red) },
                    onClick = {
                        onDismissDropdown()
                        onDeleteClick()
                    }
                )
            }
        }

        // Bottom-right Edit dropdown
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 8.dp)
        ) {
            if (isEditing) {
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { expandedEditDropdown = true }
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Options",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Show edit options",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expandedEditDropdown,
                        onDismissRequest = { expandedEditDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Save Changes") },
                            onClick = {
                                expandedEditDropdown = false
                                onSaveClick()
                            },
                            enabled = !isLoading
                        )
                        DropdownMenuItem(
                            text = { Text("Cancel") },
                            onClick = {
                                expandedEditDropdown = false
                                onCancelClick()
                            }
                        )
                    }
                }
            } else {
                Text(
                    text = "Edit",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable(onClick = onEditClick)
                        .padding(8.dp)
                )
            }
        }
    }
}




@Composable
fun BottomSection(
    user: User?,
    emergencyContacts: List<EmergencyContact>,
    healthInfo: HealthInformation?,
    isLoading: Boolean,
    isEditing: Boolean,
    editableFirstname: String,
    editableLastname: String,
    editableDateOfBirth: String,
    editableGender: String,
    editableEmail: String,
    editablePhoneNumber: String,
    editableHomeAddress: String,
    editableCity: String,
    editableEmergencyContacts: MutableList<EmergencyContact>,
    editableBloodGroup: String,
    editableAllergies: String,
    editableMedication: String,
    onFirstnameChange: (String) -> Unit,
    onLastnameChange: (String) -> Unit,
    onDateOfBirthChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onHomeAddressChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onBloodGroupChange: (String) -> Unit,
    onAllergiesChange: (String) -> Unit,
    onMedicationChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            user?.let { u ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor =  Color(0xFF009688))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Personal Information",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        EditableOrInfoRow(
                            label = "First Name",
                            value = if (isEditing) editableFirstname else u.firstname,
                            isEditing = isEditing,
                            onValueChange = onFirstnameChange
                        )
                        EditableOrInfoRow(
                            label = "Last Name",
                            value = if (isEditing) editableLastname else u.lastname,
                            isEditing = isEditing,
                            onValueChange = onLastnameChange
                        )
                        EditableOrInfoRow(
                            label = "Date of Birth",
                            value = if (isEditing) editableDateOfBirth else u.dateOfBirth,
                            isEditing = isEditing,
                            onValueChange = onDateOfBirthChange
                        )
                        EditableOrInfoRow(
                            label = "Gender",
                            value = if (isEditing) editableGender else u.gender.name,
                            isEditing = isEditing,
                            onValueChange = onGenderChange
                        )
                        EditableOrInfoRow(
                            label = "Email",
                            value = if (isEditing) editableEmail else u.email,
                            isEditing = isEditing,
                            onValueChange = onEmailChange,
                            readOnly = true
                        )
                        EditableOrInfoRow(
                            label = "Phone Number",
                            value = if (isEditing) editablePhoneNumber else u.phoneNumber,
                            isEditing = isEditing,
                            onValueChange = onPhoneNumberChange
                        )
                        EditableOrInfoRow(
                            label = "Home Address",
                            value = if (isEditing) editableHomeAddress else u.homeAddress,
                            isEditing = isEditing,
                            onValueChange = onHomeAddressChange
                        )
                        EditableOrInfoRow(
                            label = "City",
                            value = if (isEditing) editableCity else u.city,
                            isEditing = isEditing,
                            onValueChange = onCityChange
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Emergency Contacts Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF009688))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Emergency Contacts",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    val contactsToDisplay = if (isEditing) editableEmergencyContacts else emergencyContacts

                    if (contactsToDisplay.isNotEmpty()) {
                        contactsToDisplay.forEachIndexed { index, c ->
                            if (index > 0) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(thickness = 0.5.dp, color = Color.LightGray)
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            EditableOrInfoRow(
                                label = "First Name",
                                value = c.firstname,
                                isEditing = isEditing,
                                onValueChange = { newValue ->
                                    if (editableEmergencyContacts.size > index) {
                                        editableEmergencyContacts[index] =
                                            editableEmergencyContacts[index].copy(firstname = newValue)
                                    }
                                }
                            )
                            EditableOrInfoRow(
                                label = "Last Name",
                                value = c.lastname,
                                isEditing = isEditing,
                                onValueChange = { newValue ->
                                    if (editableEmergencyContacts.size > index) {
                                        editableEmergencyContacts[index] =
                                            editableEmergencyContacts[index].copy(lastname = newValue)
                                    }
                                }
                            )
                            EditableOrInfoRow(
                                label = "Phone",
                                value = c.phoneNumber,
                                isEditing = isEditing,
                                onValueChange = { newValue ->
                                    if (editableEmergencyContacts.size > index) {
                                        editableEmergencyContacts[index] =
                                            editableEmergencyContacts[index].copy(phoneNumber = newValue)
                                    }
                                }
                            )
                            EditableOrInfoRow(
                                label = "Relationship",
                                value = c.relationship,
                                isEditing = isEditing,
                                onValueChange = { newValue ->
                                    if (editableEmergencyContacts.size > index) {
                                        editableEmergencyContacts[index] =
                                            editableEmergencyContacts[index].copy(relationship = newValue)
                                    }
                                }
                            )
                        }
                    } else {
                        Text(
                            "No emergency contacts added.",
                            fontStyle = FontStyle.Italic,
                            color = Color.Black
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Health Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor =  Color(0xFF009688))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Health Information",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    val currentBloodGroup = if (isEditing) editableBloodGroup else healthInfo?.bloodGroup ?: ""
                    val currentAllergies = if (isEditing) editableAllergies else healthInfo?.allergies ?: ""
                    val currentMedication = if (isEditing) editableMedication else healthInfo?.medication ?: ""

                    if (healthInfo != null || isEditing) {
                        EditableOrInfoRow(
                            label = "Blood Group",
                            value = currentBloodGroup,
                            isEditing = isEditing,
                            onValueChange = onBloodGroupChange,
                            isDropdown = true,
                            dropdownOptions = BLOOD_GROUPS
                        )
                        EditableOrInfoRow(
                            label = "Allergies",
                            value = currentAllergies,
                            isEditing = isEditing,
                            onValueChange = onAllergiesChange
                        )
                        EditableOrInfoRow(
                            label = "Medications",
                            value = currentMedication,
                            isEditing = isEditing,
                            onValueChange = onMedicationChange
                        )
                    } else {
                        Text(
                            "No health information added.",
                            fontStyle = FontStyle.Italic,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}







@Composable
fun AccountScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val context = LocalContext.current

    var user by remember { mutableStateOf(UserDataCache.user) }
    var emergencyContacts by remember { mutableStateOf(UserDataCache.emergencyContacts) }
    var healthInfo by remember { mutableStateOf(UserDataCache.healthInfo) }
    var isLoading by remember { mutableStateOf(!UserDataCache.isDataLoaded) }
    var isSaving by remember { mutableStateOf(false) }

    var isEditing by remember { mutableStateOf(false) }

    var editableFirstname by remember { mutableStateOf("") }
    var editableLastname by remember { mutableStateOf("") }
    var editableDateOfBirth by remember { mutableStateOf("") }
    var editableGender by remember { mutableStateOf("") }
    var editableEmail by remember { mutableStateOf("") }
    var editablePhoneNumber by remember { mutableStateOf("") }
    var editableHomeAddress by remember { mutableStateOf("") }
    var editableCity by remember { mutableStateOf("") }
    val editableEmergencyContacts = remember { mutableStateListOf<EmergencyContact>() }
    var editableBloodGroup by remember { mutableStateOf("") }
    var editableAllergies by remember { mutableStateOf("") }
    var editableMedication by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var isDeleting by remember { mutableStateOf(false) }
    var deleteError by remember { mutableStateOf<String?>(null) }
    var expandedDropdown by remember { mutableStateOf(false) }

    val initializeEditableStates: (User?, List<EmergencyContact>, HealthInformation?) -> Unit = { loadedUser, loadedContacts, loadedHealth ->
        loadedUser?.let { u ->
            editableFirstname = u.firstname
            editableLastname = u.lastname
            editableDateOfBirth = u.dateOfBirth
            editableGender = u.gender.name
            editableEmail = u.email
            editablePhoneNumber = u.phoneNumber
            editableHomeAddress = u.homeAddress
            editableCity = u.city
        }
        editableEmergencyContacts.clear()
        editableEmergencyContacts.addAll(loadedContacts.map { it.copy() })
        loadedHealth?.let { h ->
            editableBloodGroup = h.bloodGroup
            editableAllergies = h.allergies
            editableMedication = h.medication
        } ?: run {
            editableBloodGroup = ""
            editableAllergies = ""
            editableMedication = ""
        }
    }

    LaunchedEffect(UserDataCache.isDataLoaded) {
        if (UserDataCache.isDataLoaded) {
            user = UserDataCache.user
            emergencyContacts = UserDataCache.emergencyContacts
            healthInfo = UserDataCache.healthInfo
            initializeEditableStates(user, emergencyContacts, healthInfo)
            isLoading = false
        }
    }

    val onSaveClick: () -> Unit = {
        if (!isSaving) {
            isSaving = true
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val parsedGender = try {
                        Gender.valueOf(editableGender.uppercase())
                    } catch (e: IllegalArgumentException) {
                        Log.w("AccountScreen", "Invalid gender string: $editableGender. Defaulting to OTHER.")
                        Gender.OTHER
                    }

                    val updatedUser = user?.copy(
                        firstname = editableFirstname,
                        lastname = editableLastname,
                        dateOfBirth = editableDateOfBirth,
                        gender = parsedGender,
                        email = editableEmail,
                        phoneNumber = editablePhoneNumber,
                        homeAddress = editableHomeAddress,
                        city = editableCity
                    ) ?: User(
                        firstname = editableFirstname,
                        lastname = editableLastname,
                        gender = parsedGender,
                        email = editableEmail
                    )

                    val updatedHealth = HealthInformation(
                        bloodGroup = editableBloodGroup,
                        allergies = editableAllergies,
                        medication = editableMedication
                    ).takeIf { it.bloodGroup.isNotBlank() || it.allergies.isNotBlank() || it.medication.isNotBlank() }

                    FirestoreHelper.updateUserData(
                        userId,
                        updatedUser,
                        editableEmergencyContacts.toList(),
                        updatedHealth
                    )

                    UserDataCache.user = updatedUser
                    UserDataCache.emergencyContacts = editableEmergencyContacts.toList()
                    UserDataCache.healthInfo = updatedHealth

                    withContext(Dispatchers.Main) {
                        user = updatedUser
                        emergencyContacts = editableEmergencyContacts.toList()
                        healthInfo = updatedHealth

                        Toast.makeText(context, "Changes saved successfully", Toast.LENGTH_SHORT).show()
                        isEditing = false
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("AccountScreen", "Save error: ${e.message}")
                        Toast.makeText(context, "Error saving changes: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } finally {
                    isSaving = false
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFFF9800)
                )
            },
            title = { Text(text = "Delete Account?") },
            text = {
                Column {
                    Text(text = "This action cannot be undone. All your data will be permanently deleted including:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Personal information", style = MaterialTheme.typography.bodySmall)
                    Text("• Emergency contacts", style = MaterialTheme.typography.bodySmall)
                    Text("• Health information", style = MaterialTheme.typography.bodySmall)
                    Text("• Profile pictures", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Are you absolutely sure?",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; showPasswordDialog = true }) {
                    Text("Continue", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) {
                    showPasswordDialog = false
                    passwordInput = ""
                    deleteError = null
                }
            },
            title = { Text(text = "Confirm Password") },
            text = {
                Column {
                    Text("Please enter your password to confirm account deletion:")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it; deleteError = null },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        enabled = !isDeleting,
                        isError = deleteError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (deleteError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = deleteError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (passwordInput.isNotBlank() && !isDeleting) {
                            isDeleting = true
                            val email = user?.email ?: FirebaseAuth.getInstance().currentUser?.email ?: ""
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val currentUser = FirebaseAuth.getInstance().currentUser
                                    if (currentUser != null) {
                                        val success = FirestoreHelper.deleteUserAccountWithReauth(email, passwordInput)

                                        withContext(Dispatchers.Main) {
                                            if (success) {
                                                Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_LONG).show()
                                                UserDataCache.clear()
                                                TempProfileStorage.tempProfileBitmap = null
                                                navController.navigate("welcome") { popUpTo(0) { inclusive = true } }
                                            } else {
                                                deleteError = "Authentication failed or data deletion error."
                                                isDeleting = false
                                            }
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            deleteError = "No user found. Please try again."
                                            isDeleting = false
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        deleteError = when {
                                            e.message?.contains("password", ignoreCase = true) == true -> "Incorrect password. Please try again."
                                            e.message?.contains("network", ignoreCase = true) == true -> "Network error. Please check your connection."
                                            else -> "Error: ${e.message}"
                                        }
                                        isDeleting = false
                                    }
                                }
                            }
                        }
                    },
                    enabled = passwordInput.isNotBlank() && !isDeleting,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    } else {
                        Text("Delete Account")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPasswordDialog = false
                        passwordInput = ""
                        deleteError = null
                    },
                    enabled = !isDeleting
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ChatGreen)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Section with Edit functionality integrated
            AccountTopSection(
                navController = navController,
                user = user,
                expandedDropdown = expandedDropdown,
                onOptionsClick = { expandedDropdown = true },
                onDismissDropdown = { expandedDropdown = false },
                onDeleteClick = { showDeleteDialog = true },
                isEditing = isEditing,
                onEditClick = {
                    initializeEditableStates(user, emergencyContacts, healthInfo)
                    isEditing = true
                },
                onSaveClick = onSaveClick,
                onCancelClick = { isEditing = false },
                isLoading = isSaving
            )

            // Scrollable Bottom Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                BottomSection(
                    user = user,
                    emergencyContacts = emergencyContacts,
                    healthInfo = healthInfo,
                    isLoading = isLoading,
                    isEditing = isEditing,
                    editableFirstname = editableFirstname,
                    editableLastname = editableLastname,
                    editableDateOfBirth = editableDateOfBirth,
                    editableGender = editableGender,
                    editableEmail = editableEmail,
                    editablePhoneNumber = editablePhoneNumber,
                    editableHomeAddress = editableHomeAddress,
                    editableCity = editableCity,
                    editableEmergencyContacts = editableEmergencyContacts,
                    editableBloodGroup = editableBloodGroup,
                    editableAllergies = editableAllergies,
                    editableMedication = editableMedication,
                    onFirstnameChange = { newValue ->
                        if (newValue.isEmpty() || !newValue.all { it.isDigit() }) {
                            editableFirstname = newValue
                        }
                    },
                    onLastnameChange = { newValue ->
                        if (newValue.isEmpty() || !newValue.all { it.isDigit() }) {
                            editableLastname = newValue
                        }
                    },
                    onDateOfBirthChange = { editableDateOfBirth = it },
                    onGenderChange = { editableGender = it },
                    onEmailChange = { editableEmail = it },
                    onPhoneNumberChange = { newValue ->
                        if (newValue.length <= 10 && newValue.all { it.isDigit() }) {
                            editablePhoneNumber = newValue
                        }
                    },
                    onHomeAddressChange = { editableHomeAddress = it },
                    onCityChange = { editableCity = it },
                    onBloodGroupChange = { editableBloodGroup = it },
                    onAllergiesChange = { editableAllergies = it },
                    onMedicationChange = { editableMedication = it }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (isLoading || isSaving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = false) {}
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}









@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AccountScreenPreview() {
    AccountScreen(navController = rememberNavController())
}