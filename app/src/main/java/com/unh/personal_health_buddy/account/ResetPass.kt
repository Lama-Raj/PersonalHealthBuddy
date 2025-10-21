package com.unh.personal_health_buddy.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.unh.personal_health_buddy.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ResetPasswordScreen(navController: NavHostController) {
    val email = remember { mutableStateOf("") }
    val resetMessage = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            IconButton(onClick = {
                if (navController.currentDestination?.route != "sign-in") {
                    navController.navigate("sign-in")
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Back to Sign In"
                )
            }
        }

        // Title
        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 30.dp, top = 16.dp)
        )

        // Email field
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Enter your email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(0.9f)
        )

        // Message
        if (resetMessage.value.isNotEmpty()) {
            Text(
                text = resetMessage.value,
                color = if (resetMessage.value.contains("sent")) Color.Green else Color.Red,
                modifier = Modifier
                    .padding(start = 32.dp, top = 4.dp, bottom = 8.dp)
                    .fillMaxWidth(0.9f),
                textAlign = TextAlign.Start
            )
        }

        // Reset button
        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        auth.sendPasswordResetEmail(email.value).await()
                        resetMessage.value = "Password reset email sent successfully."
                        email.value = ""
                        focusManager.clearFocus()
                    } catch (e: Exception) {
                        resetMessage.value = "Reset failed: ${e.localizedMessage}"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 8.dp),
            border = BorderStroke(1.dp, colorResource(id = R.color.purple_500)),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.purple_500),
                contentColor = Color.White
            )
        ) {
            Text(text = "Send Reset Password")
        }

        // Link back to Sign In
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Remember your password? ")
            Text(
                text = "Sign in",
                color = colorResource(id = R.color.teal_700),
                modifier = Modifier.clickable {
                    navController.navigate("sign-in")
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResetPasswordScreenPreview() {
    val navController = rememberNavController()
    ResetPasswordScreen(navController = navController)
}