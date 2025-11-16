package com.unh.personal_health_buddy.features

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.unh.personal_health_buddy.ui.theme.EmergencyRed
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import java.util.UUID

// --- UPDATED DATA CLASS ---
private data class Contact(
    val id: String = UUID.randomUUID().toString(),
    val firstName: String,
    val lastName: String,
    val relationship: String,
    val phone: String,
    val countryCode: String,
    val email: String,
    val city: String,
    val address: String
)
// --- END DATA CLASS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyContactScreen(navController: NavController) {

    // --- STYLING ---
    // Using the new light blue gradient
    val newGradientStart = Color(0xFFE3F2FD) // Shining (Light) Blue
    val newGradientEnd = Color(0xFFFFFFFF)   // White
    val vibrantGradient = Brush.verticalGradient(colors = listOf(newGradientStart, newGradientEnd))
    val activeColor = EmergencyRed // Keep using the theme's red for text/icons



    // --- STATE FOR FAB ---
    var isMenuExpanded by remember { mutableStateOf(false) }
    // --- END STATE ---

    // --- NEW STATE for Dialog ---
    var showAddContactDialog by remember { mutableStateOf(false) }
    // --- END STATE ---

    // --- NEW STATE for Contacts List ---
    // !! This is now just for display.
    var contacts by remember { mutableStateOf(emptyList<Contact>()) }
    // --- END STATE ---

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(vibrantGradient)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.padding(top = 40.dp),
                    title = {
                        Text(
                            text = "Emergency Contact",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 33.sp,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        // Invisible button to balance the title
                        IconButton(onClick = { }, enabled = false) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.Transparent)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = activeColor,
                        navigationIconContentColor = activeColor
                    )
                )
            },
            // --- ADD FLOATING ACTION BUTTON ---
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Animated menu options
                    AnimatedVisibility(visible = isMenuExpanded) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MiniFabWithText(
                                icon = Icons.Default.Add,
                                text = "Add Contact",
                                onClick = {
                                    showAddContactDialog = true // Open the dialog
                                    isMenuExpanded = false // Close the FAB menu
                                },
                                activeColor = activeColor,
                            )
                            MiniFabWithText(
                                icon = Icons.Default.Delete,
                                text = "Delete Contact",
                                onClick = { /* TODO: Handle delete */ },
                                activeColor = activeColor,
                            )
                        }
                    }

                    // Main FAB
                    FloatingActionButton(
                        onClick = { isMenuExpanded = !isMenuExpanded },
                        containerColor = activeColor,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Contact"
                        )
                    }
                }
            },
            // --- END FAB ---
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp), // Add padding for content
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top // Changed from spacedBy
            ) {
                // --- NEW CONTENT ---
                if (contacts.isEmpty()) {
                    EmptyContactsState(activeColor = activeColor)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(contacts) { contact ->
                            ContactCard(contact = contact, activeColor = activeColor)
                        }
                    }
                }
                // --- END CONTENT ---
            }
        }

        // --- ADD DIALOG ---
        if (showAddContactDialog) {
            AddContactDialog(
                onDismiss = { showAddContactDialog = false },
                onSave = { contact ->
                    // --- ADD SAVE LOGIC ---
                    contacts = contacts + contact // Add the new contact to the list
                    // --- END SAVE LOGIC ---
                    showAddContactDialog = false
                },
                activeColor = activeColor
            )
        }
        // --- END DIALOG ---
    }
}

// --- NEW COMPOSABLE FOR MINI FAB ---
@Composable
private fun MiniFabWithText(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    activeColor: Color, // Use the theme color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Text Label
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = activeColor, // Use theme color
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )

        // Mini FAB
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = Color.White,
            contentColor = activeColor // Use theme color
        ) {
            Icon(imageVector = icon, contentDescription = text)
        }
    }
}
// --- END COMPOSABLE ---

// --- NEW COMPOSABLE FOR DIALOG ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onSave: (Contact) -> Unit,
    activeColor: Color
) {
    // All the state variables from your form
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var selectedRelationship by remember { mutableStateOf("Parent") }
    var relationshipExpanded by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(CountryCode("+1", "🇺🇸", "US")) }
    var email by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var showDiscardDialog by remember { mutableStateOf(false) } // For inner dialog

    val relationships = listOf("Parent", "Sibling", "Friend", "Relatives", "Other")
    // A simple check. You can make this more robust.
    val isFormValid = firstName.isNotBlank() && lastName.isNotBlank() && phoneNumber.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()), // Make dialog scrollable
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add New Contact",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = activeColor
                )

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // ---- Relationship dropdown ----
                ExposedDropdownMenuBox(
                    expanded = relationshipExpanded,
                    onExpandedChange = { relationshipExpanded = !relationshipExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedRelationship,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Relationship") },
                        leadingIcon = { Icon(Icons.Default.Group, contentDescription = null) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = relationshipExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = relationshipExpanded,
                        onDismissRequest = { relationshipExpanded = false }
                    ) {
                        relationships.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedRelationship = option
                                    relationshipExpanded = false
                                }
                            )
                        }
                    }
                }

                // ---- Country code + phone ----
                PhoneNumberField(
                    selectedCountry = selectedCountry,
                    onCountrySelected = { selectedCountry = it },
                    phoneNumber = phoneNumber,
                    onPhoneChange = { phoneNumber = it }
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (Optional)") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City (Optional)") },
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address (Optional)") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newContact = Contact(
                                firstName = firstName,
                                lastName = lastName,
                                relationship = selectedRelationship,
                                phone = phoneNumber,
                                countryCode = selectedCountry.code,
                                email = email,
                                city = city,
                                address = address
                            )
                            onSave(newContact)
                        },
                        enabled = isFormValid,
                        colors = ButtonDefaults.buttonColors(containerColor = activeColor)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }

    // --- Discard dialog (currently unused, but here if you want it) ---
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showDiscardDialog = false
                        onDismiss() // Call the main onDismiss
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Yes, discard")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDiscardDialog = false },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("No, keep")
                }
            },
            title = {
                Text(
                    text = "Discard changes?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text("Unsaved changes will be lost.")
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
// --- END COMPOSABLE ---

// --- NEW COMPOSABLES FOR LIST ---
@Composable
private fun EmptyContactsState(activeColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp), // Avoid FAB
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.People,
            contentDescription = "No Contacts",
            tint = activeColor.copy(alpha = 0.5f),
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Contacts Added",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = activeColor
        )
        Text(
            text = "Tap the '+' button to add an emergency contact.",
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun ContactCard(contact: Contact, activeColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Contact",
                tint = activeColor,
                modifier = Modifier
                    .size(40.dp)
                    .background(activeColor.copy(alpha = 0.1f), CircleShape)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Name and Relationship
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${contact.firstName} ${contact.lastName}", // Show full name
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black.copy(alpha = 0.8f)
                )
                Text(
                    text = contact.relationship,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Call Button
            OutlinedButton(
                onClick = { /* TODO: Implement call intent */ },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = activeColor),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(activeColor))) // Fixed typo
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Call")
            }
        }
    }
}
// --- END COMPOSABLES ---

// ------------------ Country Code Dropdown ------------------
data class CountryCode(val code: String, val flag: String, val country: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberField(
    selectedCountry: CountryCode,
    onCountrySelected: (CountryCode) -> Unit,
    phoneNumber: String,
    onPhoneChange: (String) -> Unit
) {
    val countryList = listOf(
        CountryCode("+1", "🇺🇸", "United States"),
        CountryCode("+91", "🇮🇳", "India"),
        CountryCode("+44", "🇬🇧", "United Kingdom"),
        CountryCode("+81", "🇯🇵", "Japan"),
        CountryCode("+977", "🇳🇵", "Nepal"),
        CountryCode("+61", "🇦🇺", "Australia"),
    )

    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .clickable { expanded = true }
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .padding(vertical = 16.dp, horizontal = 12.dp) // Adjusted padding
                    .menuAnchor(),
                contentAlignment = Alignment.Center
            ) {
                Text("${selectedCountry.flag} ${selectedCountry.code}")
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                countryList.forEach { country ->
                    DropdownMenuItem(
                        text = { Text("${country.flag}  ${country.country}  (${country.code})") },
                        onClick = {
                            onCountrySelected(country)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneChange,
            label = { Text("Phone Number") }, // Fixed typo
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
// --- END HELPER COMPOSABLES ---


@Preview(showBackground = true)
@Composable
fun EmergencyContactScreenPreview() {
    PersonalHealthBuddyTheme {
        EmergencyContactScreen(navController = rememberNavController())
    }
}