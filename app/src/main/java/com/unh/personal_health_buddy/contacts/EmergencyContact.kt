package com.unh.personal_health_buddy.contacts

import RoundedIcon
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.Authentication.FirestoreHelper
import com.unh.personal_health_buddy.database.EmergencyContact
import com.unh.personal_health_buddy.database.UserDataCache
import com.unh.personal_health_buddy.ui.theme.AppSurfaceLight
import com.unh.personal_health_buddy.ui.theme.ButtonBlue
import com.unh.personal_health_buddy.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EmergencyContactScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()
    var showDialog by remember { mutableStateOf(false) }
    var emergencyContacts by remember { mutableStateOf<List<EmergencyContact>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var contactToDelete by remember { mutableStateOf<EmergencyContact?>(null) }

    // NEW: Notification State
    var showNotification by remember { mutableStateOf(false) }
    var notificationMessage by remember { mutableStateOf("") }
    var notificationType by remember { mutableStateOf("success") } // "success" or "delete"

    // Facebook-like blue + light background
    val primaryBlue = Color(0xFF1877F2)
    val lightBlueBackground = Color(0xFFF3F6FF)
    val lightBlueCard = Color(0xFFE8F0FE)

    // Auto-hide notification after 3 seconds
    LaunchedEffect(showNotification) {
        if (showNotification) {
            delay(3000)
            showNotification = false
        }
    }

    // Load contacts with cache
    LaunchedEffect(Unit) {
        if (UserDataCache.isDataLoaded) {
            emergencyContacts = UserDataCache.emergencyContacts
            return@LaunchedEffect
        }

        isLoading = true
        try {
            val result = FirestoreHelper.readAllEmergencyContacts()
            emergencyContacts = result
            UserDataCache.emergencyContacts = result
            UserDataCache.isDataLoaded = true
        } catch (e: Exception) {
            Log.e("EmergencyContactScreen", "Error loading contacts: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lightBlueBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 SIMPLE TITLE ROW (no blue box/card)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = primaryBlue
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Emergency Contact",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,      // <- BOLD TITLE
                    color = primaryBlue,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        CircularProgressIndicator(color = primaryBlue)
                    }
                }
                emergencyContacts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Card(
                            modifier = Modifier.width(280.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, lightBlueCard),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(96.dp)
                                        .background(
                                            color = lightBlueCard,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.People,
                                        contentDescription = null,
                                        tint = primaryBlue,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No Contacts Added",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = primaryBlue,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap the + button to add an emergency contact.",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                }
                else -> {
                    Text(
                        text = "Saved Contacts",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = primaryBlue
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    emergencyContacts.forEach { contact ->
                        EmergencyContactCard(
                            contact = contact,
                            onDelete = { contactToDelete = contact },
                            primaryBlue = primaryBlue,
                            lightBlueCard = lightBlueCard
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // FAB
        FloatingActionButton(
            onClick = { showDialog = true },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = primaryBlue,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Emergency Contact",
                modifier = Modifier.size(28.dp)
            )
        }

        // Add Dialog
        if (showDialog) {
            AddEmergencyContactDialog(
                onDismiss = { showDialog = false },
                onSave = { newContact ->
                    emergencyContacts = emergencyContacts + newContact
                    UserDataCache.emergencyContacts = emergencyContacts
                    showDialog = false

                    // Show success notification
                    notificationMessage = "Contact added: ${newContact.firstname} ${newContact.lastname}"
                    notificationType = "success"
                    showNotification = true
                },
                primaryBlue = primaryBlue
            )
        }

        // Delete Confirmation
        if (contactToDelete != null) {
            AlertDialog(
                onDismissRequest = { contactToDelete = null },
                title = { Text("Delete Contact") },
                text = {
                    Text(
                        "Are you sure you want to delete ${contactToDelete?.firstname} ${contactToDelete?.lastname}?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val contact = contactToDelete
                            if (contact != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        FirestoreHelper.deleteEmergencyContact(contact.contactId)
                                        withContext(Dispatchers.Main) {
                                            emergencyContacts =
                                                emergencyContacts.filter { it.contactId != contact.contactId }
                                            UserDataCache.emergencyContacts = emergencyContacts
                                            contactToDelete = null

                                            // Show delete notification
                                            notificationMessage =
                                                "Contact deleted: ${contact.firstname} ${contact.lastname}"
                                            notificationType = "delete"
                                            showNotification = true
                                        }
                                    } catch (e: Exception) {
                                        Log.e("EmergencyContactScreen", "Error deleting: ${e.message}")
                                        withContext(Dispatchers.Main) { contactToDelete = null }
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { contactToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Notification Pop-up at Bottom
        AnimatedVisibility(
            visible = showNotification,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp, start = 16.dp, end = 16.dp)
        ) {
            EmergencyContactNotification(
                message = notificationMessage,
                type = notificationType,
                primaryBlue = primaryBlue,
                lightBlueCard = lightBlueCard
            )
        }
    }
}

@Composable
fun EmergencyContactNotification(
    message: String,
    type: String,
    primaryBlue: Color,
    lightBlueCard: Color
) {
    val backgroundColor =
        if (type == "delete") Color(0xFFFFEBEE) else lightBlueCard
    val iconColor =
        if (type == "delete") Color(0xFFD32F2F) else primaryBlue
    val icon = if (type == "delete") Icons.Default.Delete else Icons.Default.PersonAdd

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = message,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun EmergencyContactCard(
    contact: EmergencyContact,
    onDelete: () -> Unit,
    primaryBlue: Color,
    lightBlueCard: Color
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, lightBlueCard),
        colors = CardDefaults.cardColors(
            containerColor = primaryBlue
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${contact.firstname} ${contact.lastname}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = AppSurfaceLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Phone: ${contact.phoneNumber}",
                    fontSize = 14.sp,
                    color = AppSurfaceLight
                )
                Text(
                    text = "Relationship: ${contact.relationship}",
                    fontSize = 14.sp,
                    color = AppSurfaceLight
                )
            }

            Box {
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "More Options",
                        tint = AppSurfaceLight
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
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
                                Text("Delete", color = Color.Red)
                            }
                        },
                        onClick = {
                            expanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmergencyContactDialog(
    onDismiss: () -> Unit,
    onSave: (EmergencyContact) -> Unit,
    primaryBlue: Color
) {
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    var firstnameError by remember { mutableStateOf(false) }
    var lastnameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }

    val firstnameFocus = remember { FocusRequester() }
    val lastnameFocus = remember { FocusRequester() }
    val phoneFocus = remember { FocusRequester() }
    val relationshipFocus = remember { FocusRequester() }

    val relationships = listOf("Parent", "Sibling", "Friend", "Others")

    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.all { it.isLetter() || it.isWhitespace() }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Emergency Contact",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = primaryBlue
                )

                Spacer(modifier = Modifier.height(16.dp))

                // First Name
                OutlinedTextField(
                    value = firstname,
                    onValueChange = {
                        if (it.all { char -> char.isLetter() || char.isWhitespace() }) {
                            firstname = it
                            firstnameError = false
                        }
                    },
                    label = { Text("First Name", color = Color.DarkGray) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { lastnameFocus.requestFocus() }
                    ),
                    singleLine = true,
                    isError = firstnameError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(firstnameFocus)
                        .onKeyEvent { keyEvent ->
                            if (keyEvent.key == Key.Tab && keyEvent.type == KeyEventType.KeyDown) {
                                lastnameFocus.requestFocus()
                                true
                            } else false
                        },
                    leadingIcon = {
                        RoundedIcon(Icons.Default.Person, primaryBlue, Color.White)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = primaryBlue,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = primaryBlue
                    )
                )
                if (firstnameError) {
                    Text(
                        text = "First name must contain only letters",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Last Name
                OutlinedTextField(
                    value = lastname,
                    onValueChange = {
                        if (it.all { char -> char.isLetter() || char.isWhitespace() }) {
                            lastname = it
                            lastnameError = false
                        }
                    },
                    label = { Text("Last Name", color = Color.DarkGray) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { phoneFocus.requestFocus() }
                    ),
                    singleLine = true,
                    isError = lastnameError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(lastnameFocus)
                        .onKeyEvent { keyEvent ->
                            if (keyEvent.key == Key.Tab && keyEvent.type == KeyEventType.KeyDown) {
                                phoneFocus.requestFocus()
                                true
                            } else false
                        },
                    leadingIcon = {
                        RoundedIcon(Icons.Default.Person, primaryBlue, Color.White)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = primaryBlue,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = primaryBlue
                    )
                )
                if (lastnameError) {
                    Text(
                        text = "Last name must contain only letters",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Phone Number
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                            phoneNumber = it
                            phoneError = false
                        }
                    },
                    label = { Text("Phone Number", color = Color.DarkGray) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { relationshipFocus.requestFocus() }
                    ),
                    singleLine = true,
                    isError = phoneError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(phoneFocus)
                        .onKeyEvent { keyEvent ->
                            if (keyEvent.key == Key.Tab && keyEvent.type == KeyEventType.KeyDown) {
                                relationshipFocus.requestFocus()
                                true
                            } else false
                        },
                    leadingIcon = {
                        RoundedIcon(Icons.Default.Phone, primaryBlue, Color.White)
                    },
                    supportingText = {
                        Text(
                            text = "${phoneNumber.length}/10",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = primaryBlue,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = primaryBlue
                    )
                )
                if (phoneError) {
                    Text(
                        text = "Phone number must be 10 digits",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Relationship Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = it }
                ) {
                    OutlinedTextField(
                        value = relationship,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Relationship", color = Color.DarkGray) },
                        leadingIcon = {
                            RoundedIcon(Icons.Default.People, primaryBlue, Color.White)
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .focusRequester(relationshipFocus)
                            .onKeyEvent { keyEvent ->
                                if (keyEvent.key == Key.Tab && keyEvent.type == KeyEventType.KeyDown) {
                                    focusManager.clearFocus()
                                    true
                                } else if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyDown) {
                                    expandedDropdown = true
                                    true
                                } else false
                            },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = primaryBlue,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = primaryBlue
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        relationships.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    relationship = option
                                    expandedDropdown = false
                                    focusManager.clearFocus()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isSaving,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue,
                            contentColor = Color.White
                        ),
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            firstnameError = !isValidName(firstname)
                            lastnameError = !isValidName(lastname)
                            phoneError = phoneNumber.length != 10

                            if (!firstnameError && !lastnameError && !phoneError && relationship.isNotBlank()) {
                                isSaving = true
                                val newContact = EmergencyContact(
                                    contactId = "",
                                    firstname = firstname,
                                    lastname = lastname,
                                    phoneNumber = phoneNumber,
                                    relationship = relationship
                                )

                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        FirestoreHelper.writeEmergencyContact(newContact)
                                        withContext(Dispatchers.Main) {
                                            onSave(newContact)
                                        }
                                    } catch (e: Exception) {
                                        Log.e("AddEmergencyContact", "Error saving: ${e.message}")
                                        withContext(Dispatchers.Main) {
                                            isSaving = false
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isSaving &&
                                firstname.isNotBlank() &&
                                lastname.isNotBlank() &&
                                phoneNumber.length == 10 &&
                                relationship.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue,
                            contentColor = Color.White,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.White
                        )
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        firstnameFocus.requestFocus()
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewEmergencyContactScreen() {
    EmergencyContactScreen(rememberNavController())
}
