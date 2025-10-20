package com.unh.personalhealthbuddy.account


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.unh.personalhealthbuddy.R

// The '@Composable' annotation marks this function as a building block for UI.
// It describes a piece of the screen's appearance and logic.
@Composable
fun SignUp(navController: NavHostController? = null) {
    // State variables hold data that can change and trigger UI updates.
    // 'remember' ensures the state survives recomposition (UI redraws).
    val username = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    // The 'by' keyword delegates the getter and setter, simplifying access to the value.
    var isChecked by remember { mutableStateOf(false) }


    // A Column arranges its children in a vertical sequence.
    Column(
        // The modifier configures the composable's size, padding, and behavior.
        modifier = Modifier.fillMaxSize() // Makes the Column take up the entire screen.
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.Top, // Aligns children to the top of the Column.
        horizontalAlignment = Alignment.CenterHorizontally // Centers children horizontally.
    ) {

        // A Box is a layout composable that places its children on top of one another.
        Box(
            modifier = Modifier.fillMaxWidth() // Makes the Box take the full width of its parent.
                .padding(top = 10.dp, start = 24.dp, end = 24.dp),
            contentAlignment = Alignment.TopStart // Aligns content to the top-left corner.
        ) {
            // An IconButton provides a clickable area around an Icon.
            IconButton(onClick = { navController?.navigate("welcome") }) { // The action to perform on click.
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // The visual asset for the icon.
                    contentDescription = "Back to Welcome" // Text for accessibility services.
                )
            }
        }

        // A Text composable displays a string of text.
        Text(
            text = "Sign Up",
            modifier = Modifier.padding(bottom = 30.dp, start = 24.dp, end = 24.dp, top = 8.dp),
        )

        // An OutlinedTextField is a text input field with a border.
        OutlinedTextField(
            modifier = Modifier.padding(bottom = 30.dp, start = 24.dp, end = 24.dp, top = 8.dp),
            label = { Text(stringResource(R.string.user_name)) }, // A label that floats when the field is focused.
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User Icon") }, // An icon at the start of the field.
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), // Configures the keyboard type.
            value = username.value, // The displayed text, bound to the state variable.
            onValueChange = { username.value = it } // A callback that updates the state when the text changes.
        )

        OutlinedTextField(
            modifier = Modifier.padding(bottom = 30.dp)
                .padding(start = 24.dp, end = 24.dp, top = 8.dp),
            label = { Text(stringResource(R.string.email)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), // Keyboard optimized for email entry.
            value = email.value,
            onValueChange = { email.value = it }
        )

        OutlinedTextField(
            modifier = Modifier.padding(bottom = 30.dp, start = 24.dp, end = 24.dp, top = 8.dp),
            label = { Text(stringResource(R.string.password)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), // Hides the input for security.
            value = password.value,
            onValueChange = { password.value = it }
        )

        // A Row arranges its children in a horizontal sequence.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp,start = 24.dp, end = 24.dp, top = 8.dp)
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically // Centers children vertically within the Row.
        ) {
            // A Checkbox is a UI element that can be toggled on or off.
            Checkbox(
                modifier = Modifier.padding(start = 30.dp, bottom = 30.dp),
                checked = isChecked, // The current checked state, bound to the state variable.
                onCheckedChange = { isChecked = it } // Callback that updates the state on toggle.
            )

            Text(
                text = "I agree to the health terms of services and privacy policy",
                modifier = Modifier
                    .padding(start = 8.dp, end = 24.dp, bottom = 24.dp)
                    .weight(1f), // Takes up the remaining horizontal space in the Row.
                textAlign = TextAlign.Start
            )
        }

        // A Button is a clickable element that triggers an action.
        Button(
            onClick = { /* handle sign-up */ }, // Logic to execute on click goes here.
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp)
                .width(50.dp),
            border = BorderStroke(1.dp, colorResource(id = R.color.purple_500)), // Defines the button's border.
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.purple_500), // Sets the background color.
                contentColor = Color.White // Sets the color of the text/icon inside.
            )
        ) {
            Text(
                text = stringResource(id = R.string.button_sign_up),
                color = Color.White
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.Center // Centers children horizontally in the Row.
        ) {
            Text(text = "Don't have an account? ")

            Text(
                text = "Sign in",
                color = colorResource(id = R.color.teal_700),
                // The clickable modifier makes any composable interactive.
                modifier = Modifier.clickable {
                    navController?.navigate("sign-in") // Navigates to a different screen.
                }
            )
        }
    }
}


// The '@Preview' annotation allows Android Studio to display this composable in the design pane.
// It is not included in the final application build.
@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    // Call the composable that needs to be previewed.
    // A NavController is created here for the preview to work without errors.
    SignUp(rememberNavController())
}