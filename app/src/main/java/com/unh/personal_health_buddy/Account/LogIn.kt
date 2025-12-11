package com.unh.personal_health_buddy.account

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.unh.personal_health_buddy.authentication.FirestoreHelper
import com.unh.personal_health_buddy.R
import com.unh.personal_health_buddy.database.UserDataCache
import com.unh.personal_health_buddy.ui.theme.AppSurfaceLight
import com.unh.personal_health_buddy.ui.theme.ButtonBlue
import com.unh.personal_health_buddy.ui.theme.ChatGreen
import com.unh.personal_health_buddy.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

/*
* Updated the Code using Chat GTP as there was certain errors in the original code.
* Reference Code: YouTube & Chat GPT, Gemini and Copilot
* Reference : Stack Overflow, GitHub, W3Schools
*
* */

@Composable
fun SignInScreen(
    navController: NavHostController,
    googleSignInClient: GoogleSignInClient?,
    launcher: ActivityResultLauncher<Intent>
) {
    val context = LocalContext.current
    val activity = context as Activity
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val emailErrorState = remember { mutableStateOf(false) }
    val passwordErrorState = remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var showAccountDialog by remember { mutableStateOf(false) }
    var existingUserEmail by remember { mutableStateOf<String?>(null) }

    // Helper function to fetch user data after successful login
    suspend fun fetchUserDataAfterLogin() {
        try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

            Log.d("SignIn", "Fetching user data for: $uid")

            UserDataCache.clear() // Clear old cache first

            UserDataCache.user = FirestoreHelper.getUser(uid)
            UserDataCache.emergencyContacts = FirestoreHelper.readAllEmergencyContacts()
            UserDataCache.healthInfo = FirestoreHelper.getHealthInformation()

            // Load profile image
            UserDataCache.user?.profileImageUrl?.let { url ->
                try {
                    val stream = URL(url).openStream()
                    UserDataCache.profileBitmap = BitmapFactory.decodeStream(stream)
                } catch (e: Exception) {
                    Log.e("SignIn", "Error loading image: ${e.message}")
                }
            }

            UserDataCache.isDataLoaded = true
            Log.d("SignIn", "User data cached successfully")
        } catch (e: Exception) {
            Log.e("SignIn", "Error fetching data: ${e.message}")
        }
    }

    // ---------------- One Tap launcher ----------------
    val oneTapLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential: SignInCredential =
                    Identity.getSignInClient(context).getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                if (!idToken.isNullOrEmpty()) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    isLoading = true
                    FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Fetch user data before navigating
                                CoroutineScope(Dispatchers.Main).launch {
                                    withContext(Dispatchers.IO) {
                                        fetchUserDataAfterLogin()
                                    }
                                    isLoading = false
                                    navController.navigate("home") {
                                        popUpTo("sign-in") { inclusive = true }
                                    }
                                }
                            } else {
                                isLoading = false
                                errorMessage = "You are not a valid user. Please sign up first."
                                showErrorDialog = true
                            }
                        }
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "You are not a valid user. Please sign up first."
                showErrorDialog = true
            }
        }
    }

    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            icon = {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Info",
                    tint = Color.Blue
                )
            },
            title = {
                Text(text = "Sign In Failed")
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

    // Account selection dialog
    if (showAccountDialog && existingUserEmail != null) {
        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            title = {
                Text(
                    text = "Choose verified account",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            showAccountDialog = false
                            isLoading = true
                            // Fetch data before navigating
                            CoroutineScope(Dispatchers.Main).launch {
                                withContext(Dispatchers.IO) {
                                    fetchUserDataAfterLogin()
                                }
                                isLoading = false
                                navController.navigate("home") {
                                    popUpTo("sign-in") { inclusive = true }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = existingUserEmail!!,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Loading overlay
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(color = ButtonBlue)
                Text("Loading your data...", color = Color.White)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppSurfaceLight)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            IconButton(onClick = { navController.navigate("welcome") }) {
                Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Back", tint = ButtonBlue)
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Sign In",
            fontSize = 14.sp,
            color = ButtonBlue
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email.value,
            onValueChange = {
                email.value = it
                if (emailErrorState.value) emailErrorState.value = false
            },
            isError = emailErrorState.value,
            label = { Text("Email", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(0.9f),
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = ButtonBlue,
                focusedContainerColor = AppSurfaceLight,
                unfocusedContainerColor = AppSurfaceLight
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password.value,
            onValueChange = {
                password.value = it
                if (passwordErrorState.value) passwordErrorState.value = false
            },
            isError = passwordErrorState.value,
            label = { Text("Password", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
            trailingIcon = {
                val icon =
                    if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = "Toggle Password")
                }
            },
            singleLine = true,
            visualTransformation =
                if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(0.9f),
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = ButtonBlue,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = AppSurfaceLight,
                unfocusedContainerColor = AppSurfaceLight
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Forgot Password?",
            fontSize = 14.sp,
            color = ButtonBlue,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 8.dp)
                .clickable(enabled = !isLoading) { navController.navigate("reset-password") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (email.value.isBlank() || password.value.isBlank()) {
                    emailErrorState.value = email.value.isBlank()
                    passwordErrorState.value = password.value.isBlank()
                    errorMessage = "Please enter both email and password"
                    showErrorDialog = true
                    return@Button
                }

                isLoading = true
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email.value, password.value)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Fetch user data before navigating
                            CoroutineScope(Dispatchers.Main).launch {
                                withContext(Dispatchers.IO) {
                                    fetchUserDataAfterLogin()
                                }
                                isLoading = false
                                navController.navigate("home") {
                                    popUpTo("sign-in") { inclusive = true }
                                }
                            }
                        } else {
                            isLoading = false
                            emailErrorState.value = true
                            passwordErrorState.value = true
                            errorMessage = when (task.exception) {
                                is FirebaseAuthInvalidUserException ->
                                    "No account found with this email address"
                                is FirebaseAuthInvalidCredentialsException ->
                                    "Invalid email or password. Please try again."
                                else -> task.exception?.localizedMessage
                                    ?: "Invalid credentials. Please check your email and password."
                            }
                            showErrorDialog = true
                        }
                    }
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonBlue,
                contentColor = White
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = ChatGreen,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Sign In", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("OR", color = ButtonBlue, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                val existingUser = FirebaseAuth.getInstance().currentUser
                val userEmail = existingUser?.email

                if (!userEmail.isNullOrEmpty()) {
                    existingUserEmail = userEmail
                    showAccountDialog = true
                    return@OutlinedButton
                }

                val oneTapClient = Identity.getSignInClient(activity)
                val signInRequest = BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            .setServerClientId(activity.getString(R.string.default_web_client_id))
                            .setFilterByAuthorizedAccounts(false)
                            .build()
                    )
                    .setAutoSelectEnabled(true)
                    .build()

                oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener { result ->
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        oneTapLauncher.launch(intentSenderRequest)
                    }
                    .addOnFailureListener { e ->
                        errorMessage =
                            "No Google account found. Please sign up first or use email and password to sign in."
                        showErrorDialog = true
                    }
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp),
            enabled = !isLoading
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign in with Google", color = ButtonBlue, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Don't have an account? Sign Up",
            fontSize = 14.sp,
            color = ButtonBlue,
            modifier = Modifier.clickable(enabled = !isLoading) { navController.navigate("sign-up") }
        )
    }

    Log.d("SignInScreen", "Sign in screen displayed")
}