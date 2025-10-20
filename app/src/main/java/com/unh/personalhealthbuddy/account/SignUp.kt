package com.unh.personalhealthbuddy.account

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.unh.personalhealthbuddy.R

/**
 * A composable function that defines the UI for the user registration screen.
 * It includes input fields for username, email, and password, along with
 * Firebase authentication logic.
 *
 * @param navController The controller used to handle navigation events.
 */
@Composable
fun SignUpScreen(navController: NavHostController) {
    // State variables to hold the current value of the input fields and UI states.
    // 'remember' ensures that the state is preserved across recompositions (UI redraws).
    val username = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val emailErrorState = remember { mutableStateOf(false) }
    val passwordErrorState = remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // State to hold and display the result of the registration attempt.
    val registrationMessage = remember { mutableStateOf("") }

    // The main layout composable, arranging elements vertically.
    Column(
        modifier = Modifier
            .fillMaxSize() // Occupies the entire available screen space.
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.Top, // Aligns children to the top.
        horizontalAlignment = Alignment.CenterHorizontally // Centers children horizontally.
    ) {
        // A container for the back navigation icon, aligned to the top-start.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            // A clickable icon that navigates to the "welcome" screen when pressed.
            IconButton(onClick = { navController.navigate("welcome") }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Welcome"
                )
            }
        }

        // The title of the screen.
        Text(
            text = "Sign Up",
            modifier = Modifier.padding(bottom = 30.dp, top = 16.dp)
        )

        // Text field for username input.
        OutlinedTextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text(stringResource(R.string.user_name)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User Icon") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(0.9f) // Sets the width to 90% of the parent.
        )

        // Text field for email input. The value is trimmed to remove leading/trailing whitespace.
        OutlinedTextField(
            value = email.value.trim(),
            onValueChange = { email.value = it },
            // The 'isError' parameter visually indicates a validation error if true.
            isError = emailErrorState.value,
            label = { Text(stringResource(R.string.email)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(0.9f)
        )

        // Text field for password input.
        OutlinedTextField(
            value = password.value.trim(),
            onValueChange = { password.value = it },
            isError = passwordErrorState.value,
            label = { Text(stringResource(R.string.password)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
            // A trailing icon that toggles the password's visibility.
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
                }
            },
            // Transforms the input visually, hiding or showing the password based on state.
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(0.9f)
        )

        // A horizontal container for the checkbox and its associated text.
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = isChecked, onCheckedChange = { isChecked = it })
            Text(
                text = "I agree to the terms and privacy policy",
                modifier = Modifier.padding(start = 8.dp),
                textAlign = TextAlign.Start
            )
        }

        // The primary action button to initiate the sign-up process.
        Button(
            onClick = {
                val auth = FirebaseAuth.getInstance()

                // Performs simple client-side validation before contacting Firebase.
                emailErrorState.value = email.value.isBlank()
                passwordErrorState.value = password.value.length < 6
                if (emailErrorState.value || passwordErrorState.value) return@Button

                // Calls Firebase Authentication to create a new user. This is an asynchronous operation.
                auth.createUserWithEmailAndPassword(email.value, password.value)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // On success, updates the message and clears all input fields.
                            registrationMessage.value = "Registration successful! Welcome ${email.value}"
                            username.value = ""
                            email.value = ""
                            password.value = ""
                            isChecked = false
                            passwordVisible = false
                        } else {
                            // On failure, captures the error message to display to the user.
                            registrationMessage.value =
                                "Registration failed: ${task.exception?.localizedMessage}"
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
            Text(text = stringResource(id = R.string.button_sign_up))
        }

        // Conditionally displays the registration status message if it's not empty.
        if (registrationMessage.value.isNotEmpty()) {
            Text(
                text = registrationMessage.value,
                // Changes the text color to green for success and red for failure.
                color = if (registrationMessage.value.contains("successful")) Color.Green else Color.Red,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(0.9f),
                textAlign = TextAlign.Start
            )
        }

        // Provides a link to the sign-in screen for users who already have an account.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Already have an account? ")
            Text(
                text = "Sign in",
                color = colorResource(id = R.color.teal_700),
                // Makes the text clickable to trigger navigation.
                modifier = Modifier.clickable { navController.navigate("sign-in") }
            )
        }
    }
}