package com.unh.personal_health_buddy.Account

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.ui.text.style.TextAlign
import com.unh.personal_health_buddy.firebase.performResetPassword


//@Composable
//fun ResetPasswordDialog(
//    navController: NavHostController,
//    onDismiss: () -> Unit
//) {
//    val email = remember { mutableStateOf("") }
//    val emailErrorState = remember { mutableStateOf(false) }
//    val context = LocalContext.current
//    val showSuccess = remember { mutableStateOf(false) }
//
//    AlertDialog(
//        onDismissRequest = { onDismiss() },
//        confirmButton = {
//            TextButton(onClick = {
//                performResetPassword(
//                    email.value,
//                    emailErrorState,
//                    onSuccess = { showSuccess.value = true },
//                    onFailure = { e -> Log.e("ResetPassword", "Error: ${e.message}") }
//                )
//            }) {
//                Text("Send")
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = { navController.popBackStack() }) {
//                Text("Cancel")
//            }
//        },
//        title = {
//            Text(modifier = Modifier.fillMaxWidth(),
//                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
//                text = "Forgot Password")
//                },
//        text = {
//            Column {
//                OutlinedTextField(
//                    value = email.value,
//                    onValueChange = { email.value = it },
//                    label = { Text("Email") },
//                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
//                    isError = emailErrorState.value,
//                    singleLine = true,
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                if (emailErrorState.value) {
//                    Text(
//                        text = "Please enter a valid email",
//                        color = MaterialTheme.colorScheme.error,
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//                if (showSuccess.value) {
//                    Text(
//                        text = "If this email is registered, a reset link has been sent.",
//                        color = MaterialTheme.colorScheme.primary,
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//            }
//        }
//    )
//    Log.d("ResetPasswordDialog", "Displayed")
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewResetPasswordDialog() {
//    val navController = rememberNavController()
//    ResetPasswordDialog(
//        navController = navController,
//        onDismiss = {}
//    )
//}


@Composable
fun ResetPasswordDialog(
    navController: NavHostController,
    onDismiss: () -> Unit
) {
    val email = remember { mutableStateOf("") }
    val emailErrorState = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val showSuccess = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                performResetPassword(
                    email.value,
                    emailErrorState,
                    onSuccess = {
                        showSuccess.value = true
                        email.value = "" // Clear the email field
                    },
                    onFailure = { e -> Log.e("ResetPassword", "Error: ${e.message}") }
                )
            }) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Cancel")
            }
        },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Forgot Password"
            )
        },
        text = {
            Column {
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
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (showSuccess.value) {
                    Text(
                        text = "If this email is registered, a reset link has been sent.",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
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