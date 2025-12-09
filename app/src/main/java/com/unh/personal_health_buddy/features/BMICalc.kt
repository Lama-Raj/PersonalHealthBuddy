package com.unh.personal_health_buddy.features

// Import all the necessary theme colors
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.R
import com.unh.personal_health_buddy.database.Gender
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import com.unh.personal_health_buddy.ui.theme.PrimaryDarkBlue
import java.text.DecimalFormat



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiScreen(navController: NavController) {
    // Defines the main composable function for the BMI screen.
    // It accepts a NavController to handle navigation (like going back).

    // --- State for inputs ---
    // Remembers the current value of the gender selection.
    var selectedGender by remember { mutableStateOf(Gender.MALE) }
    // Remembers the height slider's value in inches, defaults to 65f.
    var heightInches by remember { mutableFloatStateOf(65f) }
    // Remembers the weight slider's value in pounds, defaults to 150f.
    var weightLbs by remember { mutableFloatStateOf(150f) }

    // --- State for calculated BMI and its category ---
    // Remembers the calculated BMI result, starts as null.
    var bmiResult by remember { mutableStateOf<Float?>(null) }
    // Remembers the text category (e.g., "Healthy"), starts empty.
    var bmiCategory by remember { mutableStateOf("") }
    // Remembers the suggested weight range text, starts empty.
    var suggestedWeight by remember { mutableStateOf("") }

    // --- Dynamic Vibrant Color Scheme based on Gender ---
    // Decides which color set to use based on the selected gender.
    val (newActiveColor, newGradientStart, newGradientEnd) = if (selectedGender == Gender.MALE) {
        // Checks if the selected gender is MALE.
        // Assigns blue colors for active, gradient start, and gradient end.
        Triple(
            Color(0xFF1877F2), // active blue (Facebook-like)
            Color(0xFFE8F1FF), // soft light blue at the top
            Color.White        // fade to white at the bottom
        )
    } else {
        // Assigns pink colors if FEMALE is selected.
        Triple(
            Color(0xFFE91E63), // pink accent for female
            Color(0xFFFCE4EC), // soft pink at the top
            Color.White        // fade to white at the bottom
        )
    }

    // Creates a vertical gradient brush.
    val vibrantGradient = Brush.verticalGradient(
        colors = listOf(
            newGradientStart,
            newGradientEnd
        )
    )



    // Uses a Box to fill the entire screen.
    Box(
        modifier = Modifier
            .fillMaxSize() // Modifies the Box to fill all available space.
            .background(vibrantGradient) // Applies the created gradient as the background.
    ) {
        // Sets up the basic screen layout structure (top bar, content).
        Scaffold(
            topBar = {
                // Defines the content for the top app bar area.
                TopAppBar(
                    title = { Text("Calculate Your BMI")
                            },
                    navigationIcon = {
                        // Defines a clickable icon button.
                        IconButton(onClick = { navController.popBackStack() }) {
                            // When clicked, navigates back to the previous screen.
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    // Configures the colors for the top app bar.
                    colors = TopAppBarDefaults.topAppBarColors(
                        // Makes the bar's background transparent to show the gradient.
                        containerColor = Color.Transparent,
                        // Sets the title text color to the dynamic active color.
                        titleContentColor = newActiveColor,
                        // Sets the back arrow color to the dynamic active color.
                        navigationIconContentColor = newActiveColor
                    )
                )
            },
            // Makes the Scaffold's own background transparent.
            containerColor = Color.Transparent
        ) { paddingValues ->
            // Provides padding values to avoid content overlapping the top bar.

            // Creates and remembers the vertical scroll position.
            val scrollState = rememberScrollState()

            // Arranges its children vertically.
            Column(
                modifier = Modifier
                    .padding(paddingValues) // Applies the padding from the Scaffold.
                    .fillMaxSize()
                    // Makes the Column's content scrollable using the scrollState.
                    .verticalScroll(scrollState)
                    // Adds 16dp of padding around the *entire* scrollable content.
                    .padding(16.dp),
                // Aligns all children to be centered horizontally.
                horizontalAlignment = Alignment.CenterHorizontally,
                // Puts 16dp of vertical space between each child.
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Creates a card composable for holding inputs.
                Card(
                    modifier = Modifier.fillMaxWidth(), // Makes the card take the full available width.
                    shape = RoundedCornerShape(16.dp), // Sets the card's corners to be rounded.
                    // Sets the card's background to semi-transparent white.
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    // Adds a small shadow effect under the card.
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    // Arranges the inputs vertically inside the card.
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Calls the composable function to show gender buttons.
                        GenderSelector(
                            selectedGender = selectedGender, // Passes the currently remembered gender.
                            // Provides a lambda to update the gender when selected.
                            onGenderSelect = { newGender ->
                                selectedGender = newGender
                                // Resets the results when gender is changed to prevent showing stale data.
                                bmiResult = null
                                bmiCategory = ""
                                suggestedWeight = ""
                            },
                            activeColor = newActiveColor // Passes the dynamic color for highlighting.
                        )

                        // Calls the composable function to show the height slider.
                        SliderInput(
                            label = "Height (ft / in)",
                            value = heightInches,
                            onValueChange = { heightInches = it },
                            range = 48f..84f, // 4ft to 7ft
                            displayValue = formatInchesToFtIn(heightInches),
                            activeColor = newActiveColor
                        )

                        // Calls the composable function to show the weight slider.
                        SliderInput(
                            label = "Weight (lbs)",
                            value = weightLbs,
                            onValueChange = { weightLbs = it },
                            range = 80f..350f, // 80lbs to 350lbs
                            displayValue = "${weightLbs.toInt()} lbs",
                            activeColor = newActiveColor
                        )

                        // Creates a clickable button.
                        Button(
                            // Defines the code to run when the button is clicked.
                            onClick = {
                                // Calculates BMI using the imperial formula.
                                val bmi = (weightLbs / (heightInches * heightInches)) * 703
                                // Updates the remembered BMI result state.
                                bmiResult = bmi
                                // Updates the remembered category state by calling the helper.
                                bmiCategory = getBmiCategory(bmi)
                                // Updates the remembered suggested weight state.
                                suggestedWeight = getSuggestedWeight(heightInches)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            // Sets the button's background to the dynamic active color.
                            colors = ButtonDefaults.buttonColors(containerColor = newActiveColor)
                        ) {
                            Text(text = "Calculate", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }

                // --- Result Area ---
                // Arranges the result text and image horizontally.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    // Aligns the top of the text and image.
                    verticalAlignment = Alignment.Top
                ) {
                    // Left Side: Result Text
                    // Calls the composable to display text results.
                    ResultCard(
                        modifier = Modifier.weight(1f), // Assigns 1 unit of horizontal space (half).
                        bmi = bmiResult,
                        category = bmiCategory,
                        suggestedWeight = suggestedWeight,
                        activeColor = newActiveColor
                    )


                    // Right Side: Silhouette Image
                    // Creates a container for the image.
                    Box(
                        modifier = Modifier
                            .weight(1f) // Assigns 1 unit of horizontal space (the other half).
                            .padding(start = 8.dp),
                        contentAlignment = Alignment.TopCenter // Aligns the image inside the Box.
                    ) {
                        // Selects the image resource ID based on gender.
                        val imageRes = if (selectedGender == Gender.FEMALE) {
                            R.drawable.ladyicon
                        } else {
                            R.drawable.profile
                        }
                        // Displays the selected image.
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = "Body Silhouette",
                            modifier = Modifier
                                .fillMaxHeight(0.5f) // Makes the image take up half the available height.
                                .padding(top = 20.dp),
                            // Scales the image to fit within the bounds without cropping.
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // --- BMI Categories Table ---
                // Calls the composable to display the category info table.
                BmiCategoryTable()
            }
        }
    }
}

@Composable
fun GenderSelector(
    // Defines the composable for the MALE/FEMALE button row.
    selectedGender: com.unh.personal_health_buddy.database.Gender,
    onGenderSelect: (Gender) -> Unit,
    activeColor: Color // Pass the new color
) {
    // Arranges the two buttons horizontally.
    Row(
        modifier = Modifier.fillMaxWidth(),
        // Puts 8dp of space between the buttons.
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Calls the composable for the "Male" button.
        GenderButton(
            text = "Male",
            icon = Icons.Default.Male,
            // Passes true if MALE is the currently selected gender.
            isSelected = selectedGender == Gender.MALE,
            // Calls the provided lambda with MALE when clicked.
            onClick = { onGenderSelect(Gender.MALE) },
            // Makes the button take half the available width.
            modifier = Modifier.weight(1f),
            activeColor = activeColor
        )
        // Calls the composable for the "Female" button.
        GenderButton(
            text = "Female",
            icon = Icons.Default.Female,
            isSelected = selectedGender == Gender.FEMALE,
            onClick = { onGenderSelect(Gender.FEMALE) },
            modifier = Modifier.weight(1f),
            activeColor = activeColor
        )
    }
}

@Composable
fun GenderButton(
    // Defines the composable for a single gender button (reusable).
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color // Pass the new color
) {
    // Selects the dynamic color if selected, or transparent white if not.
    val backgroundColor = if (isSelected) activeColor else Color.White.copy(alpha = 0.5f)
    // Selects white text if selected, or dark grey if not.
    val textColor = if (isSelected) Color.White else Color.Black.copy(alpha = 0.7f)
    // Selects white icon if selected, or the dynamic color if not.
    val iconColor = if (isSelected) Color.White else activeColor

    // Uses a Card for the button's visual container.
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }, // Makes the card respond to clicks.
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor), // Sets the card's background.
    ) {
        // Arranges the icon and text horizontally.
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = text, tint = iconColor)
            // Adds a fixed 8dp gap between icon and text.
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = textColor, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun SliderInput(
    // Defines the composable for a slider with labels.
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    displayValue: String,
    activeColor: Color // Pass the new color
) {
    // Arranges the label row and slider vertically.
    Column {
        // Arranges the label and the current value text horizontally.
        Row(
            modifier = Modifier.fillMaxWidth(),
            // Pushes the label and value to opposite ends.
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(text = displayValue, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
        // The actual draggable slider component.
        Slider(
            value = value,
            // Calls the lambda with the new float value as it's dragged.
            onValueChange = onValueChange,
            valueRange = range,
            steps = 0, // Allows for continuous sliding (no discrete steps).
            // Configures the slider's colors.
            colors = SliderDefaults.colors(
                thumbColor = activeColor, // Sets the color of the draggable circle.
                activeTrackColor = activeColor.copy(alpha = 0.7f), // Sets the color of the track *before* the thumb.
                inactiveTrackColor = Color.White.copy(alpha = 0.7f) // Sets the color of the track *after* the thumb.
            )
        )
    }
}

@Composable
fun ResultCard(
    // Defines the composable for the results text area.
    modifier: Modifier = Modifier,
    bmi: Float?,
    category: String,
    suggestedWeight: String,
    activeColor: Color // Pass the new color
) {
    // Creates a formatter to show one decimal place.
    val df = DecimalFormat("#.0")
    // Gets the appropriate color for the result category.
    val categoryColor = getCategoryColor(category)
    // Selects descriptive text based on whether a result exists.
    val description = if (bmi != null) getCategoryDescription(category) else "Enter your details to see results."

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // The "Your Results" title text.
        Text(
            text = "Your Results",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = activeColor,
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                // Applies a subtle background tint using the active color.
                .background(activeColor.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )

        // Conditionally displays results only if 'bmi' is not null.
        if (bmi != null) {
            // Shows the formatted BMI number.
            ResultRow("Your BMI", bmi.let { df.format(it) } ?: "N/A")
            // Shows the suggested weight range.
            ResultRow("Suggested Weight", suggestedWeight)
            // Shows the category, applying the specific result color.
            ResultRow("Your Category", category, categoryColor)
        }

        // Displays the helpful description text.
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ResultRow(label: String, value: String, valueColor: Color = Color.Black) {
    // Defines a reusable composable for a single "Label: Value" row.
    Row(
        modifier = Modifier.fillMaxWidth(),
        // Pushes the label and value to opposite ends.
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
fun BmiCategoryTable() {
    // Defines the composable for the static info table at the bottom.
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryDarkBlue), // Use theme color
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // The table's title.
            Text(
                text = "BMI Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
            // Calls the reusable row composable.
            CategoryRow("Underweight", "Below 18.5")
            // Draws a thin line between rows.
            HorizontalDivider(
                Modifier,
                DividerDefaults.Thickness,
                color = Color.White.copy(alpha = 0.2f)
            )
            CategoryRow("Healthy", "18.5 - 24.9")
            HorizontalDivider(
                Modifier,
                DividerDefaults.Thickness,
                color = Color.White.copy(alpha = 0.2f)
            )
            CategoryRow("Overweight", "25.0 - 29.9")
            HorizontalDivider(
                Modifier,
                DividerDefaults.Thickness,
                color = Color.White.copy(alpha = 0.2f)
            )
            CategoryRow("Obesity", "30.0 or above")
        }
    }
}

@Composable
fun CategoryRow(category: String, range: String) {
    // Defines the composable for a single row in the info table.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = category, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
        Text(text = range, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Medium)
    }
}

// --- Helper Functions ---
// Defines private functions used for calculations and formatting.

// Converts a float of total inches (e.g., 70.0f) to a "ft / in" string.
private fun formatInchesToFtIn(totalInches: Float): String {
    // Calculates the whole number of feet by dividing by 12.
    val feet = (totalInches / 12).toInt()
    // Calculates the remaining inches using the modulo operator.
    val inches = (totalInches % 12).toInt()
    // Returns the combined, formatted string.
    return "$feet ft / $inches in"
}

// Determines the correct string category for a given BMI value.
private fun getBmiCategory(bmi: Float): String {
    return when {
        bmi < 18.5 -> "Underweight"
        bmi < 25 -> "Healthy" // Changed from "Normal" to match image
        bmi < 30 -> "Overweight"
        else -> "Obese"
    }
}

// Returns a specific color based on the category string.
private fun getCategoryColor(category: String): Color {
    // --- New non-theme colors ---
    return when (category) {
        "Underweight" -> Color(0xFF03A9F4) // Light Blue
        "Healthy" -> Color(0xFF4CAF50) // Green
        "Overweight" -> Color(0xFF0800FF) // Deep Blue (Original comment was 'Orange', but value is Blue)
        "Obese" -> Color(0xFFF44336) // Red
        else -> Color.Gray
    }
}

// Calculates the healthy weight range (lbs) for a given height (inches).
private fun getSuggestedWeight(heightInches: Float): String {
    // Ideal BMI range: 18.5 to 24.9
    // Formula: weight = BMI * (heightInches^2) / 703
    // Calculates the lower bound of healthy weight.
    val minWeight = (18.5 * (heightInches * heightInches)) / 703
    // Calculates the upper bound of healthy weight.
    val maxWeight = (24.9 * (heightInches * heightInches)) / 703
    // Returns the range as a formatted string.
    return "${minWeight.toInt()} - ${maxWeight.toInt()} lbs"
}

// Returns a helpful description for each BMI category.
private fun getCategoryDescription(category: String): String {
    return when (category) {
        "Underweight" -> "You may be at risk of nutritional deficiencies. Consider consulting a healthcare provider."
        "Healthy" -> "You are in a healthy weight range. Keep up the good work with a balanced."
        "Overweight" -> "You are at an increased risk of developing health problems. A healthier diet and exercise are recommended."
        "Obese" -> "You are at a high risk for health conditions. Please consult a healthcare provider for guidance."
        else -> "Please enter your details to see your results."
    }
}

@Preview(showBackground = true)
@Composable
fun BmiScreenPreview() {
    PersonalHealthBuddyTheme {
        BmiScreen(navController = rememberNavController())
    }
}
