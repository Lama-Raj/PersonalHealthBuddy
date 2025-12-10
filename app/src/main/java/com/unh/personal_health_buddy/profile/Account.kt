package com.unh.personal_health_buddy.profile

import TempProfileStorage
import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
// Ensure this matches your package (lowercase 'authentication')
import com.unh.personal_health_buddy.authentication.FirestoreHelper
import com.unh.personal_health_buddy.R
import com.unh.personal_health_buddy.database.*
import com.unh.personal_health_buddy.ui.theme.TextColor
import com.unh.personal_health_buddy.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.Calendar

// ---------------- Constants & Colors ----------------
val BLOOD_GROUPS = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")
val GENDER_OPTIONS = listOf("Male", "Female", "Others")
val PrimaryBlue = Color(0xFF1877F2)
val LightBlueBg = Color(0xFFF3F6FF)

// ---------------- Helper Components ----------------

@Composable
fun PhotoOptionsMenu(
    showMenu: Boolean,
    onDismiss: () -> Unit,
    onTakePhoto: () -> Unit,
    onUpload: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = onDismiss,
        modifier = Modifier.background(White)
    ) {
        DropdownMenuItem(
            text = { Text("Take Photo") },
            onClick = onTakePhoto,
            leadingIcon = { Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PrimaryBlue) }
        )
        DropdownMenuItem(
            text = { Text("Upload from Gallery") },
            onClick = onUpload,
            leadingIcon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = PrimaryBlue) }
        )
        DropdownMenuItem(
            text = { Text("Remove Photo", color = Color.Red) },
            onClick = onDelete,
            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
        )
    }
}

// *** STYLED ROW COMPONENT ***
@Composable
fun StyledEditableRow(
    label: String,
    value: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    isDropdown: Boolean = false,
    dropdownOptions: List<String> = emptyList(),
    isDateField: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val displayValue = if (value.isBlank()) "Not Set" else value
    var expandedDropdown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Container matching ProfileRow style
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextColor.copy(alpha = 0.6f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Content Area
        if (isEditing && !readOnly) {
            if (isDateField) {
                Box(modifier = Modifier.fillMaxWidth().clickable {
                    val cal = Calendar.getInstance()
                    try {
                        if (value.isNotBlank()) {
                            val parts = value.split("-")
                            if (parts.size == 3) cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                        }
                    } catch (_: Exception) {}
                    DatePickerDialog(context, { _, y, m, d ->
                        onValueChange(String.format("%04d-%02d-%02d", y, m + 1, d))
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                }) {
                    Text(
                        text = value.ifBlank { "Select Date" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = if(value.isBlank()) Color.Gray else TextColor,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.align(Alignment.CenterEnd).size(20.dp)
                    )
                }
            } else if (isDropdown) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.fillMaxWidth().clickable { expandedDropdown = true }) {
                        Text(
                            text = value.ifBlank { "Select Option" },
                            style = MaterialTheme.typography.bodyLarge,
                            color = if(value.isBlank()) Color.Gray else TextColor,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.background(White)
                    ) {
                        dropdownOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = { onValueChange(option); expandedDropdown = false }
                            )
                        }
                    }
                }
            } else {
                // Clean TextField without standard borders to fit the card look
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(
                        color = TextColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (value.isEmpty()) Text("Enter $label", color = Color.Gray.copy(alpha = 0.5f))
                            innerTextField()
                            Box(
                                Modifier.align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(PrimaryBlue.copy(alpha = 0.3f))
                            )
                        }
                    }
                )
            }
        } else {
            // View Mode
            Text(
                text = displayValue,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = if (displayValue == "Not Set") TextColor.copy(alpha = 0.4f) else TextColor
            )
        }
    }
}

// ---------------- Bottom Section (Content) ----------------

@Composable
fun StyledBottomSection(
    user: User?,
    emergencyContacts: List<EmergencyContact>,
    healthInfo: HealthInformation?,
    isLoading: Boolean,
    isEditing: Boolean,
    // ... all your parameters ...
    editableFirstname: String, onFirstnameChange: (String) -> Unit,
    editableLastname: String, onLastnameChange: (String) -> Unit,
    editableDateOfBirth: String, onDateOfBirthChange: (String) -> Unit,
    editableGender: String, onGenderChange: (String) -> Unit,
    editableEmail: String, onEmailChange: (String) -> Unit,
    editablePhoneNumber: String, onPhoneNumberChange: (String) -> Unit,
    editableHomeAddress: String, onHomeAddressChange: (String) -> Unit,
    editableCity: String, onCityChange: (String) -> Unit,
    editableEmergencyContacts: MutableList<EmergencyContact>,
    editableBloodGroup: String, onBloodGroupChange: (String) -> Unit,
    editableAllergies: String, onAllergiesChange: (String) -> Unit,
    editableMedication: String, onMedicationChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp)) {
        if (isLoading) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            SectionHeader("Personal Information")

            user?.let { u ->
                StyledEditableRow("First Name", if (isEditing) editableFirstname else u.firstname, isEditing, onFirstnameChange)
                StyledEditableRow("Last Name", if (isEditing) editableLastname else u.lastname, isEditing, onLastnameChange)
                StyledEditableRow("Date of Birth", if (isEditing) editableDateOfBirth else u.dateOfBirth, isEditing, onDateOfBirthChange, isDateField = true)

                // *** UPDATED GENDER ROW ***
                StyledEditableRow(
                    label = "Gender",
                    value = if (isEditing) editableGender else u.gender.name,
                    isEditing = isEditing,
                    onValueChange = onGenderChange,
                    isDropdown = true,                // Enable dropdown mode
                    dropdownOptions = GENDER_OPTIONS  // Pass the MALE/FEMALE/OTHER list
                )

                StyledEditableRow("Email", if (isEditing) editableEmail else u.email, isEditing, onEmailChange, readOnly = true)
                StyledEditableRow("Phone Number", if (isEditing) editablePhoneNumber else u.phoneNumber, isEditing, onPhoneNumberChange, keyboardType = KeyboardType.Phone)
                StyledEditableRow("Home Address", if (isEditing) editableHomeAddress else u.homeAddress, isEditing, onHomeAddressChange)
                StyledEditableRow("City", if (isEditing) editableCity else u.city, isEditing, onCityChange)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ... (Rest of the function remains the same: Emergency Contacts & Health Info) ...

            SectionHeader("Emergency Contacts")
            val contactsToDisplay = if (isEditing) editableEmergencyContacts else emergencyContacts
            if (contactsToDisplay.isNotEmpty()) {
                contactsToDisplay.forEachIndexed { index, c ->
                    if(index > 0) Spacer(modifier = Modifier.height(12.dp))
                    Text("Contact ${index + 1}", style = MaterialTheme.typography.labelSmall, color = PrimaryBlue, modifier = Modifier.padding(start=4.dp, bottom=4.dp))

                    StyledEditableRow("First Name", c.firstname, isEditing, { v -> if (editableEmergencyContacts.size > index) editableEmergencyContacts[index] = editableEmergencyContacts[index].copy(firstname = v) })
                    StyledEditableRow("Last Name", c.lastname, isEditing, { v -> if (editableEmergencyContacts.size > index) editableEmergencyContacts[index] = editableEmergencyContacts[index].copy(lastname = v) })
                    StyledEditableRow("Phone", c.phoneNumber, isEditing, { v -> if (editableEmergencyContacts.size > index) editableEmergencyContacts[index] = editableEmergencyContacts[index].copy(phoneNumber = v) }, keyboardType = KeyboardType.Phone)
                    StyledEditableRow("Relationship", c.relationship, isEditing, { v -> if (editableEmergencyContacts.size > index) editableEmergencyContacts[index] = editableEmergencyContacts[index].copy(relationship = v) })
                }
            } else {
                Text("No emergency contacts added.", fontStyle = FontStyle.Italic, color = TextColor.copy(alpha = 0.6f), modifier = Modifier.padding(start=4.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("Health Information")
            val displayBlood = if (isEditing) editableBloodGroup else healthInfo?.bloodGroup ?: ""
            val displayAllergies = if (isEditing) editableAllergies else healthInfo?.allergies ?: ""
            val displayMedication = if (isEditing) editableMedication else healthInfo?.medication ?: ""

            // Blood Group uses the same Dropdown logic
            StyledEditableRow("Blood Group", displayBlood, isEditing, onBloodGroupChange, isDropdown = true, dropdownOptions = BLOOD_GROUPS)
            StyledEditableRow("Allergies", displayAllergies, isEditing, onAllergiesChange)
            StyledEditableRow("Medications", displayMedication, isEditing, onMedicationChange)
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        ),
        color = TextColor.copy(alpha = 0.75f),
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

// ---------------- MAIN SCREEN ----------------

@Composable
fun AccountScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val context = LocalContext.current

    // Data State
    var user by remember { mutableStateOf(UserDataCache.user) }
    var emergencyContacts by remember { mutableStateOf(UserDataCache.emergencyContacts) }
    var healthInfo by remember { mutableStateOf(UserDataCache.healthInfo) }
    var currentProfileBitmap by remember { mutableStateOf<Bitmap?>(TempProfileStorage.tempProfileBitmap) }

    // Edit State
    var isEditing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(!UserDataCache.isDataLoaded) }
    var isSaving by remember { mutableStateOf(false) }

    // Image Editing State
    var newProfileBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isProfileImageDeleted by remember { mutableStateOf(false) }
    var showPhotoOptions by remember { mutableStateOf(false) }

    // Text Field States
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

    // Dialog States
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var isDeleting by remember { mutableStateOf(false) }
    var deleteError by remember { mutableStateOf<String?>(null) }
    var expandedDropdown by remember { mutableStateOf(false) }
    var expandedEditDropdown by remember { mutableStateOf(false) }

    // --- LAUNCHERS ---
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let { newProfileBitmap = it; isProfileImageDeleted = false; showPhotoOptions = false }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) cameraLauncher.launch(null) else Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT < 28) MediaStore.Images.Media.getBitmap(context.contentResolver, it) else ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it))
            newProfileBitmap = bitmap; isProfileImageDeleted = false; showPhotoOptions = false
        }
    }

    // --- HELPER FUNCTIONS ---
    val initializeEditableStates: (User?, List<EmergencyContact>, HealthInformation?) -> Unit = { loadedUser, loadedContacts, loadedHealth ->
        loadedUser?.let { u ->
            editableFirstname = u.firstname; editableLastname = u.lastname; editableDateOfBirth = u.dateOfBirth; editableGender = u.gender.name; editableEmail = u.email; editablePhoneNumber = u.phoneNumber; editableHomeAddress = u.homeAddress; editableCity = u.city
        }
        editableEmergencyContacts.clear(); editableEmergencyContacts.addAll(loadedContacts.map { it.copy() })
        loadedHealth?.let { h -> editableBloodGroup = h.bloodGroup; editableAllergies = h.allergies; editableMedication = h.medication } ?: run { editableBloodGroup = ""; editableAllergies = ""; editableMedication = "" }
        newProfileBitmap = null; isProfileImageDeleted = false
    }

    // --- FETCH LOGIC ---
    LaunchedEffect(UserDataCache.isDataLoaded, user?.profileImageUrl) {
        if (UserDataCache.isDataLoaded) {
            user = UserDataCache.user
            emergencyContacts = UserDataCache.emergencyContacts
            healthInfo = UserDataCache.healthInfo
            initializeEditableStates(user, emergencyContacts, healthInfo)

            if (UserDataCache.profileBitmap != null) {
                currentProfileBitmap = UserDataCache.profileBitmap
            }
            isLoading = false
        }

        // Backup download logic (if cache missed)
        val url = user?.profileImageUrl
        if (currentProfileBitmap == null && !url.isNullOrEmpty()) {
            try {
                withContext(Dispatchers.IO) {
                    val stream = URL(url).openStream()
                    val bitmap = BitmapFactory.decodeStream(stream)
                    withContext(Dispatchers.Main) {
                        if (bitmap != null) {
                            currentProfileBitmap = bitmap
                            UserDataCache.profileBitmap = bitmap
                            TempProfileStorage.tempProfileBitmap = bitmap
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AccountScreen", "Error loading image: ${e.message}")
            }
        }
    }

    // --- SAVE LOGIC ---
    val onSaveClick: () -> Unit = {
        if (!isSaving) {
            isSaving = true
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Local copy to avoid smart cast error
                    val localNewBitmap = newProfileBitmap

                    // 1. Update Local Cache Immediately
                    if (isProfileImageDeleted) {
                        FirestoreHelper.deleteUserProfileImage()
                        TempProfileStorage.tempProfileBitmap = null
                        currentProfileBitmap = null
                        UserDataCache.profileBitmap = null
                    } else if (localNewBitmap != null) {
                        TempProfileStorage.tempProfileBitmap = localNewBitmap
                        currentProfileBitmap = localNewBitmap
                        UserDataCache.profileBitmap = localNewBitmap
                    }

                    // 2. Prepare Data Objects
                    val parsedGender = try { Gender.valueOf(editableGender.uppercase()) } catch (_: Exception) { Gender.OTHER }
                    val updatedUser = user?.copy(firstname = editableFirstname, lastname = editableLastname, dateOfBirth = editableDateOfBirth, gender = parsedGender, email = editableEmail, phoneNumber = editablePhoneNumber, homeAddress = editableHomeAddress, city = editableCity) ?: User(firstname = editableFirstname, lastname = editableLastname, gender = parsedGender, email = editableEmail)
                    val updatedHealth = HealthInformation(bloodGroup = editableBloodGroup, allergies = editableAllergies, medication = editableMedication)

                    // 3. MASTER UPDATE (Uploads image -> Gets URL -> Saves User)
                    // The Helper uses "userId/profile.jpg" ensuring overwrite and folder structure.
                    FirestoreHelper.updateUserData(
                        userId,
                        updatedUser,
                        editableEmergencyContacts.toList(),
                        updatedHealth,
                        localNewBitmap // Pass the new bitmap
                    )

                    // 4. Re-sync Local User Object (to capture the new URL string)
                    val refreshedUser = FirestoreHelper.getUser(userId)

                    UserDataCache.user = refreshedUser
                    UserDataCache.emergencyContacts = editableEmergencyContacts.toList()
                    UserDataCache.healthInfo = updatedHealth

                    withContext(Dispatchers.Main) {
                        user = refreshedUser
                        emergencyContacts = editableEmergencyContacts.toList()
                        healthInfo = updatedHealth
                        isEditing = false
                        Toast.makeText(context, "Saved successfully", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show() }
                } finally {
                    isSaving = false
                }
            }
        }
    }

    // --- UI STRUCTURE ---
    val gradientBackground = Brush.verticalGradient(listOf(LightBlueBg, White))

    val displayBitmap = remember(newProfileBitmap, isProfileImageDeleted, currentProfileBitmap) {
        when {
            newProfileBitmap != null -> newProfileBitmap?.asImageBitmap()
            isProfileImageDeleted -> null
            currentProfileBitmap != null -> currentProfileBitmap?.asImageBitmap()
            else -> null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
    ) {

        // --- 1. Top Header Section ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("profile") }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextColor)
                }

                Text(
                    text = "Account Details",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
                    color = PrimaryBlue
                )

                // Right Options/Edit Button
                Box {
                    if (isEditing) {
                        IconButton(onClick = { expandedEditDropdown = true }) {
                            Icon(Icons.Default.Check, contentDescription = "Save", tint = PrimaryBlue)
                        }
                    } else {
                        IconButton(onClick = { expandedDropdown = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = PrimaryBlue)
                        }
                    }

                    DropdownMenu(expanded = expandedDropdown, onDismissRequest = { expandedDropdown = false }, modifier = Modifier.background(White)) {
                        DropdownMenuItem(
                            text = { Text("Edit Details") },
                            onClick = { expandedDropdown = false; initializeEditableStates(user, emergencyContacts, healthInfo); isEditing = true },
                            leadingIcon = { Icon(Icons.Default.Edit, null, tint = PrimaryBlue) }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Delete Account", color = Color.Red) },
                            onClick = { expandedDropdown = false; showDeleteDialog = true },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                        )
                    }

                    DropdownMenu(expanded = expandedEditDropdown, onDismissRequest = { expandedEditDropdown = false }, modifier = Modifier.background(White)) {
                        DropdownMenuItem(text = { Text("Save Changes") }, onClick = { expandedEditDropdown = false; onSaveClick() }, enabled = !isLoading)
                        DropdownMenuItem(text = { Text("Cancel Editing") }, onClick = { expandedEditDropdown = false; isEditing = false; newProfileBitmap = null; isProfileImageDeleted = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Profile Image (Centered with Edit Overlay)
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(LightBlueBg, CircleShape)
                    .clip(CircleShape)
                    .border(2.dp, PrimaryBlue.copy(alpha = 0.4f), CircleShape)
                    .clickable(enabled = isEditing, onClick = { showPhotoOptions = true }),
                contentAlignment = Alignment.Center
            ) {
                if (displayBitmap != null) {
                    Image(
                        bitmap = displayBitmap,
                        contentDescription = "Profile",
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile_picture),
                        contentDescription = "Default Profile",
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                if (isEditing) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Edit", tint = White, modifier = Modifier.size(28.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${user?.firstname ?: "User"} ${user?.lastname ?: ""}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                color = TextColor
            )
        }

        // --- 2. Bottom Content Section ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(LightBlueBg)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                    StyledBottomSection(
                        user, emergencyContacts, healthInfo, isLoading, isEditing,
                        editableFirstname, { if(it.all{c -> c.isDigit().not()}) editableFirstname = it },
                        editableLastname, { if(it.all{c -> c.isDigit().not()}) editableLastname = it },
                        editableDateOfBirth, { editableDateOfBirth = it },
                        editableGender, { editableGender = it },
                        editableEmail, { editableEmail = it },
                        editablePhoneNumber, { if(it.length <= 10 && it.all { c -> c.isDigit() }) editablePhoneNumber = it },
                        editableHomeAddress, { editableHomeAddress = it },
                        editableCity, { editableCity = it },
                        editableEmergencyContacts,
                        editableBloodGroup, { editableBloodGroup = it },
                        editableAllergies, { editableAllergies = it },
                        editableMedication, { editableMedication = it }
                    )
                }

                // Overlays
                if (showPhotoOptions) {
                    Box(modifier = Modifier.fillMaxSize().clickable { showPhotoOptions = false }, contentAlignment = Alignment.Center) {
                        PhotoOptionsMenu(
                            showMenu = true,
                            onDismiss = { showPhotoOptions = false },
                            onTakePhoto = { showPhotoOptions = false; if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) cameraLauncher.launch(null) else permissionLauncher.launch(Manifest.permission.CAMERA) },
                            onUpload = { showPhotoOptions = false; galleryLauncher.launch("image/*") },
                            onDelete = { isProfileImageDeleted = true; newProfileBitmap = null; showPhotoOptions = false }
                        )
                    }
                }

                if (isLoading || isSaving) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
            }
        }
    }

    // --- DIALOGS ---
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = White,
            icon = { Icon(Icons.Default.Warning, "Warning", tint = Color(0xFFFF9800)) },
            title = { Text("Delete Account?", color = TextColor) },
            text = { Text("Are you absolutely sure? This cannot be undone.", color = TextColor.copy(alpha=0.8f)) },
            confirmButton = { TextButton(onClick = { showDeleteDialog = false; showPasswordDialog = true }) { Text("Continue", color = Color.Red) } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel", color = TextColor) } }
        )
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) { showPasswordDialog = false; passwordInput = ""; deleteError = null } },
            containerColor = White,
            title = { Text("Confirm Password", color = TextColor) },
            text = {
                Column {
                    Text("Enter password to confirm deletion:", color = TextColor)
                    OutlinedTextField(
                        value = passwordInput, onValueChange = { passwordInput = it; deleteError = null },
                        visualTransformation = PasswordVisualTransformation(), isError = deleteError != null,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, focusedLabelColor = PrimaryBlue)
                    )
                    if (deleteError != null) Text(deleteError!!, color = MaterialTheme.colorScheme.error)
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
                                    if(FirestoreHelper.deleteUserAccountWithReauth(email, passwordInput)) {
                                        withContext(Dispatchers.Main) { UserDataCache.clear(); TempProfileStorage.tempProfileBitmap = null; navController.navigate("welcome") { popUpTo(0) { inclusive = true } } }
                                    } else { withContext(Dispatchers.Main) { deleteError = "Failed to delete."; isDeleting = false } }
                                } catch (e: Exception) { withContext(Dispatchers.Main) { deleteError = e.message; isDeleting = false } }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text(if (isDeleting) "Deleting..." else "Delete Account") }
            },
            dismissButton = { TextButton(onClick = { showPasswordDialog = false; passwordInput = "" }) { Text("Cancel", color = TextColor) } }
        )
    }
}

// Basic TextField Composable
@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions = androidx.compose.foundation.text.KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    onTextLayout: (androidx.compose.ui.text.TextLayoutResult) -> Unit = {},
    interactionSource: androidx.compose.foundation.interaction.MutableInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
    cursorBrush: Brush = Brush.verticalGradient(listOf(PrimaryBlue, PrimaryBlue)),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit = @Composable { innerTextField -> innerTextField() }
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        decorationBox = decorationBox
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AccountScreenPreview() {
    AccountScreen(navController = rememberNavController())
}