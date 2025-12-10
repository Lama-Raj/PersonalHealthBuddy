import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.*
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Face2
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.unh.personal_health_buddy.R
import java.io.File
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

import com.unh.personal_health_buddy.database.Gender
import com.unh.personal_health_buddy.database.HealthInformation
import com.unh.personal_health_buddy.database.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.unh.personal_health_buddy.authentication.FirestoreHelper
import com.unh.personal_health_buddy.database.UserDataCache
import com.unh.personal_health_buddy.ui.theme.AccentOrange
import com.unh.personal_health_buddy.ui.theme.ButtonBlue
import com.unh.personal_health_buddy.ui.theme.ChatGreen
import com.unh.personal_health_buddy.ui.theme.White
import kotlinx.coroutines.withContext
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale





fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "profile01.jpg")
    file.outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }
    Log.d("saveBitmapToCache", "File path: ${file.absolutePath}")
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}



fun validateUserInput(user: User, firebaseEmail: String): Boolean {
    return user.firstname.isNotBlank() &&
            user.lastname.isNotBlank() &&
            user.dateOfBirth.isNotBlank() &&
            user.homeAddress.isNotBlank() &&
            user.email.isNotBlank() &&
            user.email == firebaseEmail &&
            user.phoneNumber.isNotBlank() &&
            user.phoneNumber.length == 10 &&  // Add this validation
            user.phoneNumber.all { it.isDigit() } &&  // Add this validation
            user.city.isNotBlank()
}

@Composable
fun SaveButton(
    enabled: Boolean,
    isSaving: Boolean,
    onSave: () -> Unit
) {
    if (isSaving) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp
        )
    } else {
        Button(
            onClick = onSave,
            enabled = enabled,
            modifier = Modifier.height(36.dp)
        ) {
            Text("Save")
        }
    }
    Log.d("SaveButton", "Enabled: $enabled")
}


@Composable
fun TopBarWithSave(
    title: String,
    onBack: () -> Unit,
    onSave: () -> Unit,
    enabled: Boolean,
    isSaving: Boolean,
    titleColor: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBack() }
        ) {

            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Back",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = titleColor
            )

        }

        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                strokeWidth = 2.dp
            )
        } else {
            Button(
                onClick = onSave,
                enabled = enabled,
                modifier = Modifier.height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBlue,
                    contentColor = White
                ),
            ) {
                Text("Save")
            }
        }
    }

    Log.d("TopBarWithSave", "Title: $title")
}






// Define your custom colors
val ButtonBlue = Color(0xFF1976D2)
val White = Color(0xFFFFFFFF)

@Composable
fun RoundedIcon(
    imageVector: ImageVector,
    containerColor: Color = ButtonBlue,
    contentColor: Color = White,
    size: Dp = 36.dp,
    padding: Dp = 8.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(containerColor, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.padding(padding)
        )
    }
}



fun isValidImageType(context: Context, uri: Uri): Boolean {
    val mimeType = context.contentResolver.getType(uri)
    return mimeType == "image/jpeg" || mimeType == "image/png"
}



@Composable
fun BackHeader(
    title: String,
    onBack: () -> Unit,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBack() }
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Back",
                tint = color,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = title,
                color = color,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold   // ← MAKE IT BOLD HERE
                )
            )
        }
    }
    Log.d("BackHeader", "Title: $title")
}




// ============================================
// GLOBAL STATE TO STORE TEMPORARY PROFILE IMAGE
// ============================================
object TempProfileStorage {
    var tempProfileBitmap: Bitmap? = null
}

// ============================================
// ACCOUNT FORM SCREEN COMPOSABLES
// ============================================

@Composable
fun ProfileImage(capturedBitmap: ImageBitmap?) {
    val modifier = Modifier
        .size(140.dp)
        .clip(CircleShape)
        .border(1.dp, Color.White, CircleShape)

    if (capturedBitmap != null) {
        Image(
            bitmap = capturedBitmap,
            contentDescription = "Captured Image",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "Default Avatar",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
    Log.d("ProfileImage", "CapturedBitmap: $capturedBitmap")
}
@Composable
fun PhotoOptionsMenu(
    showMenu: Boolean,
    onToggleMenu: () -> Unit,
    onTakePhoto: () -> Unit,
    onUpload: () -> Unit,
    onDelete: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onToggleMenu)
                .padding(16.dp),
        ) {
            Text(
                modifier = Modifier.offset(x = 20.dp),
                text = "Photo Options",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFFFFFFF),
                fontWeight = FontWeight.Bold
            )

            Icon(
                modifier = Modifier.offset(20.dp),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Show photo options"
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = onToggleMenu,
        ) {
            // Take Photo
            DropdownMenuItem(
                text = { Text("Take Photo", color = Color.White) },
                onClick = onTakePhoto,
                modifier = Modifier.background(ChatGreen)
            )

            // Upload from Gallery
            DropdownMenuItem(
                text = { Text("Upload from Gallery", color = Color.White) },
                onClick = onUpload,
                modifier = Modifier.background(ChatGreen)
            )

            // Delete Photo
            DropdownMenuItem(
                text = { Text("Delete Photo", color = Color.White) },
                onClick = onDelete,
                modifier = Modifier.background(ChatGreen)
            )
        }
    }
    Log.d("PhotoOptionsMenu", "ShowMenu: $showMenu")
}

@Composable
fun EmergencyContactSection(
    emergencyFirstname: MutableState<String>,
    emergencyLastname: MutableState<String>,
    emergencyPhone: MutableState<String>,
    emergencyRelationship: MutableState<String>
) {
    var relationExpanded by remember { mutableStateOf(false) }
    val relationOptions = listOf("Sibling", "Parent", "Friend", "Relative", "Spouse")

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Emergency Contact", style = MaterialTheme.typography.titleMedium)
    }
}
@Composable
fun AccountFormTop(
    navController: NavHostController,
    profileBitmap: Bitmap?,

    // User info
    firstname: MutableState<String>,
    lastname: MutableState<String>,
    dateOfBirth: MutableState<String>,
    homeAddress: MutableState<String>,
    gender: MutableState<Gender>,
    email: MutableState<String>,
    phoneNumber: MutableState<String>,
    city: MutableState<String>,

    // Health info
    bloodGroup: MutableState<String>,
    allergies: MutableState<String>,
    medications: MutableState<String>
) {
    val context = LocalContext.current

    // FIX 1: Add 'profileBitmap' as a key to remember.
    // If the parent passes a new image, this state will now update correctly.
    var capturedBitmap by remember(profileBitmap) { mutableStateOf(profileBitmap) }

    // Initialize preview based on current capturedBitmap
    var previewImage by remember(capturedBitmap) {
        mutableStateOf(capturedBitmap?.asImageBitmap())
    }

    var showMenu by remember { mutableStateOf(false) }
    var isValid by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    // ---------------- USER OBJECT ----------------
    val user by remember {
        derivedStateOf {
            User(
                firstname = firstname.value,
                lastname = lastname.value,
                dateOfBirth = dateOfBirth.value,
                homeAddress = homeAddress.value,
                gender = gender.value,
                email = email.value,
                phoneNumber = phoneNumber.value,
                city = city.value
            )
        }
    }

    // ---------------- VALIDATION ----------------
    LaunchedEffect(user) {
        try {
            val (_, firebaseEmail) = FirestoreHelper.getVerifiedUser()
            isValid = validateUserInput(user, firebaseEmail)
        } catch (e: Exception) {
            isValid = false
        }
    }

    // ---------------- CAMERA LAUNCHER ----------------
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                capturedBitmap = it
                TempProfileStorage.tempProfileBitmap = it
            }
        }

    // ---------------- PERMISSION LAUNCHER ----------------
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------------- GALLERY ----------------
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                val bitmap = ImageDecoder.decodeBitmap(source)
                capturedBitmap = bitmap
                TempProfileStorage.tempProfileBitmap = bitmap
            }
        }

    // ---------------- UI ----------------
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBarWithSave(
                title = "Account",
                onBack = { navController.navigate("profile") },
                onSave = {
                    if (!isValid) return@TopBarWithSave
                    isSaving = true

                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val healthInfo = HealthInformation(
                                bloodGroup = bloodGroup.value,
                                allergies = allergies.value,
                                medication = medications.value
                            )

                            withContext(Dispatchers.IO) {
                                FirestoreHelper.writeUser(user, capturedBitmap)
                                FirestoreHelper.writeHealthInformation(healthInfo)
                            }

                            // Clear fields
                            firstname.value = ""
                            lastname.value = ""
                            dateOfBirth.value = ""
                            homeAddress.value = ""
                            gender.value = Gender.OTHER
                            email.value = ""
                            phoneNumber.value = ""
                            city.value = ""
                            bloodGroup.value = ""
                            allergies.value = ""
                            medications.value = ""

                            capturedBitmap = null // Preview updates automatically due to state observation
                            Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Save failed: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isSaving = false
                        }
                    }
                },
                enabled = isValid,
                isSaving = isSaving,
                titleColor = Color.White,
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileImage(previewImage)

            PhotoOptionsMenu(
                showMenu = showMenu,
                onToggleMenu = { showMenu = !showMenu },
                onTakePhoto = {
                    showMenu = false
                    val permission = android.Manifest.permission.CAMERA
                    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(null)
                    } else {
                        permissionLauncher.launch(permission)
                    }
                },
                onUpload = {
                    showMenu = false
                    galleryLauncher.launch("image/*")
                },
                onDelete = {
                    showMenu = false

                    // FIX 2: Add visual feedback and robust error handling
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            // 1. Delete from server
                            withContext(Dispatchers.IO) {
                                FirestoreHelper.deleteUserProfileImage()
                            }

                            // 2. Update UI only if server delete succeeded
                            capturedBitmap = null
                            TempProfileStorage.tempProfileBitmap = null

                            Toast.makeText(context, "Photo removed", Toast.LENGTH_SHORT).show()
                            Log.d("DeletePhoto", "Profile image deleted successfully")

                        } catch (e: Exception) {
                            // 3. Tell the user WHY it failed
                            Log.e("DeletePhoto", "Failed to delete: ${e.message}")
                            Toast.makeText(context, "Could not delete: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AccountFormBottom(
    firstname: MutableState<String>,
    lastname: MutableState<String>,
    dateOfBirth: MutableState<String>,
    homeAddress: MutableState<String>,
    gender: MutableState<Gender>,
    email: MutableState<String>,
    phoneNumber: MutableState<String>,
    city: MutableState<String>
) {
    var genderExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val datePickerState = rememberDatePickerState()

    // Date Picker
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dateOfBirth.value = formatter.format(date)
                    }
                    showDatePicker = false
                    focusManager.moveFocus(FocusDirection.Down)
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Wrap everything in a Card with darker background
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF009688)) // Darker blue
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // FIRST NAME
            OutlinedTextField(
                value = firstname.value,
                onValueChange = { firstname.value = it },
                label = { Text("First Name", color = Color.White, fontSize = 14.sp) },
                leadingIcon = {
                    RoundedIcon(Icons.Default.Person, ButtonBlue, White)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF009688),
                    unfocusedContainerColor =  Color(0xFF009688),
                    disabledContainerColor =  Color(0xFF009688),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
            )

            Spacer(Modifier.height(8.dp))

            // LAST NAME
            OutlinedTextField(
                value = lastname.value,
                onValueChange = { lastname.value = it },
                label = { Text("Last Name", color = Color.White, fontSize = 14.sp) },
                leadingIcon = {
                    RoundedIcon(Icons.Default.Person, ButtonBlue, White)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor =  Color(0xFF009688),
                    unfocusedContainerColor =  Color(0xFF009688),
                    disabledContainerColor =  Color(0xFF009688),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
            )

            Spacer(Modifier.height(8.dp))

            // DATE OF BIRTH
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = dateOfBirth.value,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date of Birth", color = Color.White, fontSize = 14.sp) },
                    placeholder = { Text("DD/MM/YYYY", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp) },
                    leadingIcon = {
                        RoundedIcon(Icons.Default.DateRange, ButtonBlue, White)
                    },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date", tint = Color.White)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor =  Color(0xFF009688),
                        unfocusedContainerColor =  Color(0xFF009688),
                        disabledContainerColor =  Color(0xFF009688),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }

            Spacer(Modifier.height(8.dp))

            // HOME ADDRESS
            OutlinedTextField(
                value = homeAddress.value,
                onValueChange = { homeAddress.value = it },
                label = { Text("Home Address", color = Color.White, fontSize = 14.sp) },
                leadingIcon = {
                    RoundedIcon(Icons.Default.Home, ButtonBlue, White)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor =  Color(0xFF009688),
                    unfocusedContainerColor =  Color(0xFF009688),
                    disabledContainerColor =  Color(0xFF009688),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
            )

            Spacer(Modifier.height(8.dp))

            // CITY
            OutlinedTextField(
                value = city.value,
                onValueChange = { city.value = it },
                label = { Text("City", color = Color.White, fontSize = 14.sp) },
                leadingIcon = {
                    RoundedIcon(Icons.Default.LocationCity, ButtonBlue, White)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor =  Color(0xFF009688),
                    unfocusedContainerColor =  Color(0xFF009688),
                    disabledContainerColor =  Color(0xFF009688),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
            )

            Spacer(Modifier.height(8.dp))

            // GENDER
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = gender.value.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender", color = Color.White, fontSize = 14.sp) },
                    leadingIcon = {
                        RoundedIcon(Icons.Default.Face, ButtonBlue, White)
                    },
                    trailingIcon = {
                        IconButton(onClick = { genderExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Gender", tint = Color.White)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor =  Color(0xFF009688),
                        unfocusedContainerColor =  Color(0xFF009688),
                        disabledContainerColor =  Color(0xFF009688),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { genderExpanded = true }
                )

                DropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false }
                ) {
                    Gender.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name) },
                            onClick = {
                                gender.value = option
                                genderExpanded = false
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // EMAIL
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email", color = Color.White, fontSize = 14.sp) },
                leadingIcon = {
                    RoundedIcon(Icons.Default.Email, ButtonBlue, White)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor =  Color(0xFF009688),
                    unfocusedContainerColor =  Color(0xFF009688),
                    disabledContainerColor =  Color(0xFF009688),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
            )

            Spacer(Modifier.height(8.dp))

            // PHONE
            val isPhoneValid = phoneNumber.value.length == 10 && phoneNumber.value.all { it.isDigit() }

            OutlinedTextField(
                value = phoneNumber.value,
                onValueChange = { newValue ->
                    if (newValue.length <= 10 && newValue.all { it.isDigit() }) {
                        phoneNumber.value = newValue
                    }
                },
                label = { Text("Phone Number", color = Color.White, fontSize = 14.sp) },
                leadingIcon = {
                    RoundedIcon(Icons.Default.Phone, ButtonBlue, White)
                },
                singleLine = true,
                isError = phoneNumber.value.isNotEmpty() && !isPhoneValid,
                supportingText = {
                    if (phoneNumber.value.isNotEmpty() && !isPhoneValid) {
                        Text(
                            "Must be exactly 10 digits",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor =  Color(0xFF009688),
                    unfocusedContainerColor =  Color(0xFF009688),
                    disabledContainerColor =  Color(0xFF009688),
                    errorContainerColor =  Color(0xFF009688),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    errorTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    errorBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    errorLabelColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
            )
        }
    }
}



@Composable
fun AccountFormScreen(navController: NavHostController) {
    // Use cached profile bitmap - no duplicate declaration
    var profileBitmap by remember { mutableStateOf(UserDataCache.profileBitmap) }

    // User info state
    val firstname = remember { mutableStateOf("") }
    val lastname = remember { mutableStateOf("") }
    val dateOfBirth = remember { mutableStateOf("") }
    val homeAddress = remember { mutableStateOf("") }
    val gender = remember { mutableStateOf(Gender.OTHER) }
    val email = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val city = remember { mutableStateOf("") }

    // Health info state
    val bloodGroup = remember { mutableStateOf("") }
    val allergies = remember { mutableStateOf("") }
    val medications = remember { mutableStateOf("") }

    // Load data from cache when screen opens
    LaunchedEffect(UserDataCache.isDataLoaded) {
        if (UserDataCache.isDataLoaded) {
            // Load profile image from cache
            profileBitmap = UserDataCache.profileBitmap

            // Check temp storage first (for newly taken photos)
            TempProfileStorage.tempProfileBitmap?.let {
                profileBitmap = it
                UserDataCache.profileBitmap = it  // Update cache too
            }
        }
    }

    // Update when new photo is taken
    LaunchedEffect(TempProfileStorage.tempProfileBitmap) {
        TempProfileStorage.tempProfileBitmap?.let {
            profileBitmap = it
            UserDataCache.profileBitmap = it  // Update cache too
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChatGreen), // Light green background for entire screen
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // FIXED TOP SECTION (doesn't scroll)
        AccountFormTop(
            navController = navController,
            profileBitmap = profileBitmap,  // Pass the loaded bitmap

            firstname = firstname,
            lastname = lastname,
            dateOfBirth = dateOfBirth,
            homeAddress = homeAddress,
            gender = gender,
            email = email,
            phoneNumber = phoneNumber,
            city = city,

            bloodGroup = bloodGroup,
            allergies = allergies,
            medications = medications,
        )

        // SCROLLABLE BOTTOM SECTION
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AccountFormBottom(
                firstname = firstname,
                lastname = lastname,
                dateOfBirth = dateOfBirth,
                homeAddress = homeAddress,
                gender = gender,
                email = email,
                phoneNumber = phoneNumber,
                city = city
            )
        }
    }
    Log.d("AccountFormScreen", "Recomposing AccountFormScreen")
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AccountFormScreenPreview() {
    AccountFormScreen(navController = rememberNavController())
}