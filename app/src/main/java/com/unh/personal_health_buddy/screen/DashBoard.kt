package com.unh.personal_health_buddy.screen

// IMPORTS for layout, images, and text

// NEW IMPORTS for the grid

// IMPORTS for project resources and theme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unh.personal_health_buddy.R
import com.unh.personal_health_buddy.ui.theme.BloodOrange
import com.unh.personal_health_buddy.ui.theme.BloodOrangeDark
import com.unh.personal_health_buddy.ui.theme.BmiPink
import com.unh.personal_health_buddy.ui.theme.BmiPinkDark
import com.unh.personal_health_buddy.ui.theme.ChatGreen
import com.unh.personal_health_buddy.ui.theme.EmergencyRed
import com.unh.personal_health_buddy.ui.theme.EmergencyRedDark
import com.unh.personal_health_buddy.ui.theme.LightBlueBackground
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import com.unh.personal_health_buddy.ui.theme.ReportsCyan
import com.unh.personal_health_buddy.ui.theme.ReportsCyanDark

/**
 * A sealed class to represent the different features on our dashboard.
 * This makes managing the grid items much cleaner.
 *
 * IMPORTANT: Replace 'R.drawable.profile' with your actual icon resource IDs.
 * (e.g., R.drawable.bmi_icon, R.drawable.blood_icon, etc.)
 */
sealed class Feature(
    val title: String,
    val iconRes: Int, // This is the icon resource ID
    val backgroundColor: Color,
    val iconBackgroundColor: Color? = null, // Only for square buttons
    val span: Int = 1 // How many columns this item should span
) {
    // replace R.drawable.profile with your real icons
    object Bmi : Feature("BMI Status", R.drawable.profile, BmiPink, BmiPinkDark)
    object BloodGroup : Feature("Blood Group", R.drawable.profile, BloodOrange, BloodOrangeDark)
    object Reports : Feature("Reports", R.drawable.profile, ReportsCyan, ReportsCyanDark)
    object Emergency : Feature("Emergency", R.drawable.profile, EmergencyRed, EmergencyRedDark)
    // The "Chat" object spans 2 columns
    object Chat : Feature("Chat With AI", R.drawable.profile, ChatGreen, span = 2)
}

// A list containing all the features we want to display.
val features = listOf(
    Feature.Bmi,
    Feature.BloodGroup,
    Feature.Reports,
    Feature.Emergency,
    Feature.Chat
)

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

            // --- THIS IS THE GRID YOU WERE MISSING ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // We want 3 columns
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 32.dp), // Padding inside the white card
                horizontalArrangement = Arrangement.spacedBy(16.dp), // Space between columns
                verticalArrangement = Arrangement.spacedBy(16.dp)     // Space between rows
            ) {
                // This loops through our 'features' list
                items(features, span = { feature -> GridItemSpan(feature.span) }) { feature ->
                    // We use a 'when' statement to decide which button composable to use
                    when (feature) {
                        is Feature.Chat -> WideFeatureButton(
                            title = feature.title,
                            iconRes = feature.iconRes,
                            backgroundColor = feature.backgroundColor
                        )
                        else -> FeatureButton(
                            title = feature.title,
                            iconRes = feature.iconRes,
                            backgroundColor = feature.backgroundColor,
                            iconBackgroundColor = feature.iconBackgroundColor ?: Color.White
                        )
                    }
                }
            }
            // --- END OF THE GRID ---

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

/**
 * A composable function for the square feature buttons (BMI, Reports, etc.)
 */
@Composable
fun FeatureButton(
    title: String,
    iconRes: Int,
    backgroundColor: Color,
    iconBackgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(110.dp) // Set a fixed height
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize() // Fill the Box
        ) {
            // Circle background for icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes), // <-- REPLACE THIS ICON
                    contentDescription = title,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * A composable function for the wide feature button (Chat with AI)
 */
@Composable
fun WideFeatureButton(
    title: String,
    iconRes: Int,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(110.dp) // Match the height of the other buttons
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Title text
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                modifier = Modifier.weight(1f) // Takes up remaining space
            )

            // Faded white circle background for icon
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    // This creates a semi-transparent white background
                    .background(Color.White.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes), // <-- REPLACE THIS ICON
                    contentDescription = title,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
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