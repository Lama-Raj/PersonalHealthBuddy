package com.unh.personal_health_buddy.screen

// IMPORTS for layout, images, and text
// import androidx.compose.foundation.border // No longer needed
// NEW IMPORTS for Card
// IMPORTS for project resources and theme
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unh.personal_health_buddy.R
import com.unh.personal_health_buddy.ui.theme.BloodOrange
import com.unh.personal_health_buddy.ui.theme.BmiPink
import com.unh.personal_health_buddy.ui.theme.ChatGreen
import com.unh.personal_health_buddy.ui.theme.EmergencyRed
import com.unh.personal_health_buddy.ui.theme.LightBlueBackground
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import com.unh.personal_health_buddy.ui.theme.ReportsCyan


/**
 * A simple data class to hold info for our feature cards.
 *
 * IMPORTANT: Replace 'R.drawable.profile' with your actual icon resource IDs.
 * (e.g., R.drawable.bmi_icon, R.drawable.blood_icon, etc.)
 */
data class Feature(
    val text: String,
    @DrawableRes val imageId: Int // This ensures we provide a valid drawable resource ID.
)

@OptIn(ExperimentalMaterial3Api::class) // We need this to use the Material 3 Card.
@Composable // This annotation marks the function as a piece of UI.
fun DashboardScreen(){
    // !! IMPORTANT: Replace these with your actual drawable resources!
    val feature1 = Feature("BMI\nStatus", R.drawable.bmical) // Pink
    val feature2 = Feature("Blood Group\nInfo", R.drawable.blood)      // Orange
    val feature3 = Feature("Reports", R.drawable.reports)     // Blue
    val feature4 = Feature("Emergency\nContact", R.drawable.call)    // Red
    val feature5 = Feature("Chat\nWith AI", R.drawable.chatai) // Green


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

            // --- THIS IS THE MANUAL LAYOUT ---
            // We use a Column to create the two rows.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    // This padding is crucial:
                    // - top = 32.dp: Pushes the layout down to avoid overlapping.
                    // - start/end/bottom = 16.dp: Adds space on the sides and bottom.
                    .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp)),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Reduced padding
            ) {
                // This is the TOP row of cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp), // Reduced padding
                    verticalAlignment = Alignment.CenterVertically // Ensure cards in row align
                ) {
                    StandardFeatureCard(
                        feature = feature1,
                        onClick = { /* TODO: Handle BMI click */ },
                        backgroundColor = BmiPink,
                        modifier = Modifier
                            .weight(1f) // Each takes 1/3 of the width
                            .aspectRatio(1f) // Makes the card square
                    )
                    StandardFeatureCard(
                        feature = feature2,
                        onClick = { /* TODO: Handle Blood Group click */ },
                        backgroundColor = BloodOrange,
                        modifier = Modifier
                            .weight(1f) // Each takes 1/3 of the width
                            .aspectRatio(1f) // Makes the card square
                    )
                    StandardFeatureCard(
                        feature = feature3,
                        onClick = { /* TODO: Handle Reports click */ },
                        backgroundColor = ReportsCyan,
                        modifier = Modifier
                            .weight(1f) // Each takes 1/3 of the width
                            .aspectRatio(1f) // Makes the card square
                    )
                }

                // This is the BOTTOM row of cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp), // Reduced padding
                    verticalAlignment = Alignment.CenterVertically // Ensure cards in row align
                ) {
                    StandardFeatureCard(
                        feature = feature4,
                        onClick = { /* TODO: Handle Emergency click */ },
                        backgroundColor = EmergencyRed,
                        modifier = Modifier
                            .weight(1f) // Takes 1/3 of the space
                            .aspectRatio(1f) // Makes the card square
                    )
                    // We call the 'LargeFeatureCard' for the chat feature
                    LargeFeatureCard(
                        feature = feature5,
                        onClick = { { /* TODO: Handle Chat click */ } },
                        backgroundColor = ChatGreen,
                        modifier = Modifier
                            .weight(2f) // Takes 2/3 of the space (twice as wide)
                            .aspectRatio(2f)  // Makes height 1/2 of width
                    )
                }
            }
            // --- END OF THE MANUAL LAYOUT ---
        }

        // The 'Column' composable arranges items vertically, one below the other.
        Column(
            modifier = Modifier
                .offset(y = (30.dp))
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
 * A composable function for the standard "clickable box" (e.g., BMI, Reports).
 * This displays an IMAGE and a title in a vertical column.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardFeatureCard(
    feature: Feature,
    onClick: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp), // Padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // We use Image instead of Icon now
            Image(
                painter = painterResource(id = feature.imageId),
                contentDescription = "${feature.text} Illustration",
                modifier = Modifier
                    .size(56.dp), // Increased from 48.dp
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp)) // Increased from 6.dp
            Text(
                text = feature.text,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp, // Increased from 14.sp
                lineHeight = 17.sp, // Increased from 16.sp
                color = Color.White,
                textAlign = TextAlign.Center // Center the text
            )
        }
    }
}

/**
 * A composable function for the large "Chat With AI" card.
 * This displays text on the left and an IMAGE on the right.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargeFeatureCard(
    feature: Feature,
    onClick: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        // Use a Row to arrange items side-by-side
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp), // Padding
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Puts space between items
        ) {
            Text(
                text = feature.text,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp, // Slightly larger text
                color = Color.White,
                textAlign = TextAlign.Start // Align text to the start
            )
            // We use Image instead of Icon
            Image(
                painter = painterResource(id = feature.imageId),
                contentDescription = "${feature.text} Illustration",
                modifier = Modifier
                    .size(72.dp), // Increased from 64.dp
                contentScale = ContentScale.Fit
            )
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

