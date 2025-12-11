package com.unh.personal_health_buddy.account

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.unh.personal_health_buddy.firebase.isValidEmail
import com.unh.personal_health_buddy.firebase.isValidFullName
import com.unh.personal_health_buddy.firebase.isValidPassword
import com.unh.personal_health_buddy.firebase.performSignUp
import com.unh.personal_health_buddy.ui.theme.ButtonBlue
import com.unh.personal_health_buddy.ui.theme.White


@Composable
fun SignUpScreen(
    navController: NavHostController,
    googleSignInClient: GoogleSignInClient?,
    launcher: ActivityResultLauncher<Intent>
) {
    val context = LocalContext.current
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val nameErrorState = remember { mutableStateOf(false) }
    val emailErrorState = remember { mutableStateOf(false) }
    val passwordErrorState = remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Terms and Conditions Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            icon = {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Terms and Conditions",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = "Terms and Conditions",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("1. Medical Disclaimer\n")
                            }
                            append("Personal Health Buddy does not provide medical advice. All health information (BMI, blood group info, chatbot responses) is for informational purposes only.\n\n")

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("2. User Responsibility\n")
                            }
                            append("You must provide accurate information when creating and updating your profile.\n\n")

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("3. Data Storage\n")
                            }
                            append("Your data will be stored securely in Firebase, including profile details, health info, emergency contacts, and photos.\n\n")

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("4. Notifications\n")
                            }
                            append("The app may send notifications related to health reminders, profile updates, or emergency contact changes. Notifications may include BMI reminders, medication updates, profile changes, and emergency contact alerts.\n\n")

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("5. Authentication & Security\n")
                            }
                            append("Google Sign-In and Firebase Authentication handle login securely. You are responsible for keeping your account credentials safe.\n\n")

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("6. Location Access\n")
                            }
                            append("Location access is used only to show nearby health services on the map and is not stored.\n\n")

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("7. Photo Upload Policy\n")
                            }
                            append("Uploaded photos must be your own, and by uploading them you allow the app to store them in Firebase Storage.")
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTermsDialog = false
                        isChecked = true
                    }
                ) {
                    Text("Accept")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            icon = {
                Icon(
                    Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(text = "Invalid Credentials")
            },
            text = {
                Text(text = errorMessage)
            },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            IconButton(onClick = { navController.navigate("sign-in") }) {
                Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Back")
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = name.value,
            onValueChange = {
                name.value = it
                if (nameErrorState.value) nameErrorState.value = false
            },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Person Icon") },
            isError = nameErrorState.value,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.9f)
        )
        if (nameErrorState.value) {
            Text(
                text = "Name must contain letters and cannot be only numbers",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 32.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email.value,
            onValueChange = {
                email.value = it
                if (emailErrorState.value) emailErrorState.value = false
            },
            isError = emailErrorState.value,
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(0.9f)
        )
        if (emailErrorState.value) {
            Text(
                text = "Please enter a valid email address",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 32.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password.value,
            onValueChange = {
                password.value = it
                if (passwordErrorState.value) passwordErrorState.value = false
            },
            isError = passwordErrorState.value,
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = "Toggle Password")
                }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(0.9f)
        )
        if (passwordErrorState.value) {
            Text(
                text = "Password must be 6+ characters with at least one special character",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 32.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = isChecked, onCheckedChange = { isChecked = it })
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = buildAnnotatedString {
                    append("I agree to the ")
                    withStyle(style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )) {
                        append("Terms and Conditions")
                    }
                },
                modifier = Modifier.clickable { showTermsDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                // Clear previous errors
                nameErrorState.value = false
                emailErrorState.value = false
                passwordErrorState.value = false

                // Check terms acceptance first
                if (!isChecked) {
                    errorMessage = "Please accept the Terms and Conditions to continue."
                    showErrorDialog = true
                    return@Button
                }

                // Validate all fields
                val isNameValid = isValidFullName(name.value)
                val isEmailValid = isValidEmail(email.value)
                val isPasswordValid = isValidPassword(password.value)

                nameErrorState.value = !isNameValid
                emailErrorState.value = !isEmailValid
                passwordErrorState.value = !isPasswordValid

                // Show error dialog if any validation fails
                if (!isNameValid || !isEmailValid || !isPasswordValid) {
                    val errors = mutableListOf<String>()

                    if (!isNameValid) {
                        errors.add("• Name must contain letters and cannot be only numbers")
                    }
                    if (!isEmailValid) {
                        errors.add("• Please enter a valid email address")
                    }
                    if (!isPasswordValid) {
                        errors.add("• Password must be 6+ characters with at least one special character")
                    }

                    errorMessage = errors.joinToString("\n")
                    showErrorDialog = true
                    return@Button
                }

                // Perform sign up with all validations passed
                performSignUp(
                    email = email.value,
                    password = password.value,
                    fullName = name.value,
                    emailErrorState = emailErrorState,
                    passwordErrorState = passwordErrorState,
                    nameErrorState = nameErrorState,
                    navController = navController
                )
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonBlue,
                contentColor = White
            ),
        ) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Already have an account? Sign In",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                navController.navigate("sign-in") {
                    popUpTo("sign-up") { inclusive = true }
                }
            }
        )
    }
}