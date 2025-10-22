package com.unh.personal_health_buddy.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unh.personal_health_buddy.R
import com.unh.personal_health_buddy.ui.theme.LightBlueBackground
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme


@Composable // This annotation marks the function as a piece of UI.
fun DashboardScreen(){
    // The 'Box' composable allows UI elements to be stacked on top of each other.
    Box(
        // A 'modifier' is used to change the appearance or behavior of a composable.
        modifier = Modifier
            .fillMaxSize() // This makes the Box take up the whole screen.
            .background(LightBlueBackground) // This sets the background color of the Box.
    ) {
        // This is the second Box, which acts as the white card.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter) // This positions the white card at the bottom of the parent Box.
                .fillMaxWidth() // This makes the card take the full width of the screen.
                .fillMaxHeight(0.65f) // This makes the card cover 65% of the screen's height.
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)) // This rounds only the top corners.
                .background(Color.White) // This sets the background of this Box to white.
        ) {
            // The feature grid will be added here later.
        }

        // The 'Column' composable arranges items vertically, one below the other.
        Column(
            modifier = Modifier
                .offset(y = (30).dp)
                .align(Alignment.TopStart) // This positions the column at the top-left of the parent Box.
                .padding(40.dp) // This adds 16dp of space around the column's content.
        ) {
            // The 'Image' composable is used to display a picture.
            Image(
                painter = painterResource(id = R.drawable.profile), // This loads the image from the 'drawable' folder.
                contentDescription = "User Profile Picture", // This is text for screen readers.
                modifier = Modifier
                    .size(90.dp) // This sets the size of the image to 90x90 dp.
                    .clip(CircleShape) // This clips the image into a circular shape.
            )

            Spacer(modifier = Modifier.height(10.dp)) // This creates an 10dp vertical space.

            // The 'Text' composable displays a string of text.
            Text(
                text = "welcome !",
                style = MaterialTheme.typography.titleMedium // This applies a pre-defined text style.
            )
            Text(
                text = "User",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "How is it going today?",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // This is another 'Image' composable for the doctor illustration.
        Image(
            painter = painterResource(id = R.drawable.doctor),
            contentDescription = "Doctor Illustration",
            modifier = Modifier
                .align(Alignment.TopEnd) // This positions the image at the top-right of the parent Box.
                .offset(x = (-20).dp, y = 120.dp) // This moves the image from its aligned position.
                .size(size = 180.dp) // This sets the size of the image.
        )
    }
}


@Preview(showBackground = true) // This annotation tells Android Studio to show a preview of this UI.
@Composable // This marks the function as a piece of UI.
fun DashboardScreenPreview() {
    // This provides the app's theme to the preview so it looks correct.
    PersonalHealthBuddyTheme {
        // This calls the main screen function to render it in the preview panel.
        DashboardScreen()
    }
}