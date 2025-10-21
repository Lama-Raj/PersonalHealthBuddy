package com.unh.personal_health_buddy.authentication

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

fun performSignUp(
    email: String,
    password: String,
    emailErrorState: MutableState<Boolean>,
    passwordErrorState: MutableState<Boolean>,
    navController: NavController
) {
    val isEmailValid = isValidEmail(email)
    val isPasswordValid = isValidPassword(password)
    emailErrorState.value = !isEmailValid
    passwordErrorState.value = !isPasswordValid

    if (isEmailValid && isPasswordValid) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SignUp", "createUserWithEmail:success")
                    navController.navigate("home")
                } else {
                    Log.w("SignUp", "createUserWithEmail:failure", task.exception)
                }
            }
    }
}

fun performSignIn(
    email: String,
    password: String,
    emailErrorState: MutableState<Boolean>,
    passwordErrorState: MutableState<Boolean>,
    navController: NavController
) {
    val isEmailValid = isValidEmail(email)
    val isPasswordValid = isValidPassword(password)
    emailErrorState.value = !isEmailValid
    passwordErrorState.value = !isPasswordValid

    if (isEmailValid && isPasswordValid) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SignIn", "signInWithEmail:success")
                    navController.navigate("home")
                } else {
                    Log.w("SignIn", "signInWithEmail:failure", task.exception)
                }
            }
    }
}

fun isValidEmail(email: String): Boolean =
    Patterns.EMAIL_ADDRESS.matcher(email).matches()

fun isValidPassword(password: String): Boolean =
    password.length >= 6
