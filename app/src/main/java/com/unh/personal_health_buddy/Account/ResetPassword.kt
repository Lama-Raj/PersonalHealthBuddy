package com.unh.personal_health_buddy.account

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.firebase.performResetPassword
import kotlinx.coroutines.delay
/*
* Updated the Code using Chat GTP as there was certain errors in the original code.
* Reference Code: YouTube & Chat GPT, Gemini and Copilot
* Reference : Stack Overflow, GitHub, W3Schools
*
* */
@Composable
fun ResetPasswordDialog(
    navController: NavHostController,
    onDismiss: () -> Unit
) {
    val email = remember { mutableStateOf("") }
    val emailErrorState = remember { mutableStateOf(false) }
    val showSuccess = remember { mutableStateOf(false) }

    // 1. Handle redirection after success
    if (showSuccess.value) {
        LaunchedEffect(Unit) {
            delay(2500) // Wait 2.5 seconds for user to read the message
            // Navigate back to sign-in screen
            navController.navigate("sign-in") {
                popUpTo("sign-in") { inclusive = true }
            }
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            // Only show "Send" button if we haven't succeeded yet
            if (!showSuccess.value) {
                TextButton(onClick = {
                    performResetPassword(
                        email.value,
                        emailErrorState,
                        onSuccess = {
                            showSuccess.value = true
                            email.value = ""
                        },
                        onFailure = { e -> Log.e("ResetPassword", "Error: ${e.message}") }
                    )
                }) {
                    Text("Send")
                }
            }
        },
        dismissButton = {
            // Only show "Cancel" button if we haven't succeeded yet
            if (!showSuccess.value) {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Cancel")
                }
            }
        },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = if (showSuccess.value) "Email Sent" else "Forgot Password"
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (showSuccess.value) {
                    // 2. Success View
                    Text(
                        text = "If this email is registered, a reset link has been sent.\n\nRedirecting to login...",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    )
                } else {
                    // 3. Input View
                    OutlinedTextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
                        isError = emailErrorState.value,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (emailErrorState.value) {
                        Text(
                            text = "Please enter a valid email",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    )
    Log.d("ResetPasswordDialog", "Displayed")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewResetPasswordDialog() {
    val navController = rememberNavController()
    ResetPasswordDialog(
        navController = navController,
        onDismiss = {}
    )
}