package com.unh.personal_health_buddy.firebase


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.unh.personal_health_buddy.R
import com.unh.personal_health_buddy.navigation.AppNavigation
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
/*
* Updated the Code using Chat GTP as there was certain errors in the original code.
* Reference Code: YouTube & Chat GPT, Gemini and Copilot
* Reference : Stack Overflow, GitHub, W3Schools
*
* */

// Cached email pattern for better performance
val EMAIL_PATTERN = Patterns.EMAIL_ADDRESS

// ---------------- VALIDATION FUNCTIONS --------------------

fun isValidUsername(name: String): Boolean {
    if (name.isBlank()) return false
    var hasLetter = false
    for (char in name) {
        if (char.isLetter()) hasLetter = true
        if (!char.isLetterOrDigit()) return false
    }
    return hasLetter
}

fun isValidFullName(fullName: String): Boolean {
    if (fullName.isBlank()) return false

    // Must contain at least one letter
    var hasLetter = false
    var hasDigit = false

    for (char in fullName) {
        when {
            char.isLetter() -> hasLetter = true
            char.isDigit() -> hasDigit = true
            !char.isWhitespace() && !char.isLetterOrDigit() -> return false
        }
    }

    // Must have at least one letter and cannot be completely numeric
    return hasLetter && !(!hasLetter && hasDigit)
}

inline fun isValidEmail(email: String): Boolean =
    email.isNotBlank() && EMAIL_PATTERN.matcher(email).matches()

inline fun isValidPassword(password: String): Boolean {
    if (password.length < 6) return false
    var hasAlphanumeric = false
    var hasSpecial = false

    for (char in password) {
        if (char.isLetterOrDigit()) hasAlphanumeric = true
        else hasSpecial = true

        if (hasAlphanumeric && hasSpecial) return true
    }
    return false
}

// ---------------- PASSWORD RESET --------------------

fun performResetPassword(
    email: String,
    emailErrorState: MutableState<Boolean>,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    // Quick validation check
    if (!isValidEmail(email)) {
        emailErrorState.value = true
        return
    }

    emailErrorState.value = false

    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
                Log.d("ResetPassword", "Reset link sent successfully")
            } else {
                onFailure(task.exception ?: Exception("Unknown error"))
            }
        }
}

// ---------------- EMAIL AUTH --------------------

fun performSignUp(
    email: String,
    password: String,
    fullName: String,
    emailErrorState: MutableState<Boolean>,
    passwordErrorState: MutableState<Boolean>,
    nameErrorState: MutableState<Boolean>,
    navController: NavController
) {
    // Validate all fields
    nameErrorState.value = !isValidFullName(fullName)
    emailErrorState.value = !isValidEmail(email)
    passwordErrorState.value = !isValidPassword(password)

    // Early return if any validation fails
    if (nameErrorState.value || emailErrorState.value || passwordErrorState.value) {
        return
    }

    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Optionally update user profile with display name
                task.result?.user?.updateProfile(
                    com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build()
                )
                navController.navigate("sign-in")
                Log.d("SignUp", "User created successfully")
            } else {
                Log.e("SignUp", "Sign up failed", task.exception)
            }
        }
}

fun performSignIn(
    email: String,
    password: String,
    emailErrorState: MutableState<Boolean>,
    passwordErrorState: MutableState<Boolean>,
    context: Context,
    navController: NavHostController
) {
    // Quick blank check
    emailErrorState.value = email.isBlank()
    passwordErrorState.value = password.isBlank()

    if (emailErrorState.value || passwordErrorState.value) return

    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navController.navigate("home") {
                    popUpTo("sign-in") { inclusive = true }
                }
            } else {
                emailErrorState.value = true
                passwordErrorState.value = true
                Toast.makeText(
                    context,
                    task.exception?.localizedMessage ?: "Sign-in failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
}

// ---------------- LOGOUT --------------------

inline fun performLogOut(navController: NavController) {
    FirebaseAuth.getInstance().signOut()
    Log.d("LogOut", "User signed out")
    navController.navigate("sign-in")
}

// ---------------- GOOGLE AUTH --------------------

fun createGoogleSignInOptions(googleClientId: String): GoogleSignInOptions =
    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(googleClientId)
        .requestEmail()
        .build()

@Composable
fun rememberFirebaseAuthLauncher(
    onSignInSuccess: () -> Unit,
    onSignInFailure: (Exception) -> Unit
): ActivityResultLauncher<Intent> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK || result.data == null) {
            onSignInFailure(IllegalStateException("Google Sign-In canceled"))
            return@rememberLauncherForActivityResult
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken

            if (idToken.isNullOrBlank()) {
                onSignInFailure(IllegalStateException("Missing ID token"))
                return@rememberLauncherForActivityResult
            }

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            scope.launch {
                try {
                    FirebaseAuth.getInstance().signInWithCredential(credential).await()
                    onSignInSuccess()
                } catch (e: Exception) {
                    onSignInFailure(e)
                }
            }
        } catch (e: ApiException) {
            onSignInFailure(e)
        }
    }
}

fun performGoogleAuthentication(
    launcher: ActivityResultLauncher<Intent>,
    context: Context,
    onSuccessNav: () -> Unit,
    onFailureToast: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val existingUser = auth.currentUser

    // If already signed in, just navigate
    if (existingUser?.email != null) {
        val emails = arrayOf(existingUser.email!!)
        androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle("Choose verified account")
            .setItems(emails) { _, _ -> onSuccessNav() }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
        return
    }

    // Launch Google Sign-In
    try {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        launcher.launch(GoogleSignIn.getClient(context, gso).signInIntent)
    } catch (e: Exception) {
        onFailureToast(e.message ?: "Google Sign-In failed")
    }
}

// ---------------- SETUP AUTH --------------------

@Composable
fun SetupAuthentication(navController: NavHostController, activity: Activity) {
    val gso = remember {
        createGoogleSignInOptions(activity.getString(R.string.default_web_client_id))
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(activity, gso) }

    val launcher = rememberFirebaseAuthLauncher(
        onSignInSuccess = {
            navController.navigate("home") {
                popUpTo("sign-in") { inclusive = true }
            }
        },
        onSignInFailure = { e ->
            Log.e("Auth", "Google Sign-In failed", e)
        }
    )

    AppNavigation(
        navController = navController,
        googleSignInClient = googleSignInClient,
        launcher = launcher
    )
    Log.d("SetupAuthentication", "Authentication setup completed")
}


