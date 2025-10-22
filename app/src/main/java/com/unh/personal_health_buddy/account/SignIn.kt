package com.unh.personal_health_buddy.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.unh.personal_health_buddy.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * A composable function that defines the UI for the user sign-in screen.
 * It includes fields for email and password and uses Firebase for authentication.
 *
 * @param navController The controller used to handle navigation events.
 */
@Composable
fun SignInScreen(navController: NavHostController) {
    // State variables to hold the current value of the input fields and UI states.
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val signInMessage = remember { mutableStateOf("") }

    // A coroutine scope tied to this composable's lifecycle, used for launching async operations.
    val coroutineScope = rememberCoroutineScope()
    // An instance of the Firebase Authentication service.
    val auth = FirebaseAuth.getInstance()
    // A manager to control keyboard focus (e.g., clearing focus).
    val focusManager = LocalFocusManager.current

    // The main layout composable, arranging elements vertically.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // A container for the back navigation icon.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        // The title of the screen.
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 30.dp, top = 16.dp)
        )

        // Text field for email input.
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Enter your email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                // Sets the keyboard's action button to "Next".
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(0.9f)
        )

        // Text field for password input.
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Enter your password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
            // An icon at the end of the field to toggle password visibility.
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
                }
            },
            singleLine = true,
            // Hides or shows the password text based on the 'passwordVisible' state.
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                // Sets the keyboard's action button to "Done".
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(0.9f)
        )

        // A container for the "Forgot password?" link, aligned to the end (right).
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Forgot password?",
                color = colorResource(id = R.color.teal_700),
                // Makes the text clickable to navigate to the password reset screen.
                modifier = Modifier.clickable {
                    navController.navigate("reset-password")
                }
            )
        }

        // Conditionally displays a message to the user if one exists.
        if (signInMessage.value.isNotEmpty()) {
            Text(
                text = signInMessage.value,
                // Sets the text color to green for success or red for failure.
                color = if (signInMessage.value.contains("successful")) Color.Green else Color.Red,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(0.9f),
                textAlign = TextAlign.Start
            )
        }

        // The primary action button to initiate the sign-in process.
        Button(
            onClick = {
                // Launches a coroutine to handle the asynchronous sign-in operation without blocking the UI thread.
                coroutineScope.launch {
                    try {
                        // Calls Firebase to sign in and waits for the result. '.await()' suspends the coroutine.
                        auth.signInWithEmailAndPassword(email.value, password.value).await()
                        // On success, update the message, clear the form, and navigate to the home screen.
                        signInMessage.value = "Sign in successful! Welcome back."
                        email.value = ""
                        password.value = ""
                        passwordVisible = false
                        navController.navigate("home") {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    } catch (e: Exception) {
                        // If the 'await()' call fails, the exception is caught.
                        // Update the message to show the error.
                        signInMessage.value = "Sign in failed: ${e.localizedMessage}"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(start = 32.dp, end = 32.dp, top = 8.dp),
            border = BorderStroke(1.dp, colorResource(id = R.color.purple_500)),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.purple_500),
                contentColor = Color.White
            )
        ) {
            Text(text = "Sign In")
        }

        // A link to the sign-up screen for new users.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Don't have any account? ")
            Text(
                text = "Sign up",
                color = colorResource(id = R.color.teal_700),
                modifier = Modifier.clickable { navController.navigate("sign-up") }
            )
        }
    }
}

/**
 * A preview function for the SignInScreen, allowing it to be rendered
 * in the Android Studio design pane.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignInScreenPreview() {
    val navController = rememberNavController()
    SignInScreen(navController = navController)
}
