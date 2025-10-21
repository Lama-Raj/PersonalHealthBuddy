package com.unh.personal_health_buddy.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.unh.personal_health_buddy.R
import com.unh.personal_health_buddy.ui.theme.ButtonBlue
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import com.unh.personal_health_buddy.ui.theme.PrimaryDarkBlue

@Composable
fun MainWelcomeScreen() {
// This arranges things up and down.
    Column(
        // This changes how the screen looks.
        modifier = Modifier.fillMaxSize(), // Make the column fill the whole screen.
        horizontalAlignment = Alignment.CenterHorizontally, // Center all items left and right.
        verticalArrangement = Arrangement.Center // Center the items up and down.
    ) {
        // Logo Image
        Image(
            // Load the picture file named 'logo'.
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Personal Health Buddy Logo", // Good for people who use screen readers.
            modifier = Modifier
                .size(210.dp) // Set picture size to 210 dp.
                // Move picture up by 100 dp.
                .offset(x = 0.dp, y = (-100).dp)
        )

        Text(
            text = "Personal Health\nBuddy", // \n creates a line break.
            style = TextStyle(
                lineHeight = 1.2.em // Controls line spacing.
            ),
            textAlign = TextAlign.Center, // Centers the text block.
            color = PrimaryDarkBlue, // Sets the text color.
            fontSize = 30.sp, // Sets the text size.
            fontWeight = FontWeight.Bold, // Sets the text to bold.
            // Shifts the text visually up.
            modifier = Modifier.offset(y = (-90).dp)
        )

        // Secondary Text: "Let's get started!"
        Text(
            text = "Let’s get started!",
            style = TextStyle(
                lineHeight = 1.2.em // Line spacing property.
            ),
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            // Shifts the text up.
            modifier = Modifier.offset(y = (-75).dp)
        )

        // Tagline Text: "Your health, all in one place"
        Text(
            text = "Your health, all in one place",
            style = TextStyle(
                lineHeight = 1.2.em // Line spacing property.
            ),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Light, // Use a light font weight.
            // Shifts the text up.
            modifier = Modifier.offset(y = (-60).dp)
        )
        // Use style help from Gemini.
        Button(
            onClick = { /* This button does navigation to Login Screen. */ },
            colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue), // Solid blue background.
            modifier = Modifier
                .fillMaxWidth(0.80f) // Set button width.
                .height(60.dp), // Set fixed button height.
            shape = MaterialTheme.shapes.extraLarge // Make corners very round.
        ) {
            Text(
                text = "Login",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White // White text color.
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Space between the two buttons.
        // Use style help from Gemini.
        // 2. Sign Up Button (Outlined)
        OutlinedButton(
            onClick = { /* This button does navigation to Sign Up Screen. */ },
            modifier = Modifier
                .fillMaxWidth(fraction = 0.80f) // Set button width.
                .height(60.dp),
            shape = MaterialTheme.shapes.extraLarge, // Make corners very round.
            border = BorderStroke(1.2.dp, ButtonBlue), // Set the blue border line.
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = ButtonBlue, // Set the text color to blue.
                containerColor = Color.White // Set the background to white.
            )
        ) {
            Text(
                text = "Sign Up",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PersonalHealthBuddyTheme {
        MainWelcomeScreen()
    }
}
