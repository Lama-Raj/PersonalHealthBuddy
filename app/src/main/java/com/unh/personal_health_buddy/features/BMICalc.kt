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
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import com.unh.personal_health_buddy.ui.theme.PrimaryDarkBlue
import java.text.DecimalFormat

enum class Gender { MALE, FEMALE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiScreen(navController: NavController) {
    // State for inputs
    var selectedGender by remember { mutableStateOf(Gender.MALE) }
    // Sliders use Float. Let's represent height in total inches (e.g., 5'10" = 70f)
    var heightInches by remember { mutableStateOf(65f) }
    // Weight in pounds
    var weightLbs by remember { mutableStateOf(150f) }

    // State for calculated BMI and its category
    var bmiResult by remember { mutableStateOf<Float?>(null) }
    var bmiCategory by remember { mutableStateOf("") }
    var suggestedWeight by remember { mutableStateOf("") }

    // --- Dynamic Vibrant Color Scheme based on Gender ---
    val (newActiveColor, newGradientStart, newGradientEnd) = if (selectedGender == Gender.MALE) {
        // Blue theme for Male
        Triple(Color(0xFF0288D1), Color(0xFFB3E5FC), Color(0xFF81D4FA))
    } else {
        // Pink theme for Female
        Triple(Color(0xFFC2185B), Color(0xFFF8BBD0), Color(0xFFF48FB1))
    }

    // Define a vibrant gradient
    val vibrantGradient = Brush.verticalGradient(
        colors = listOf(
            newGradientStart,
            newGradientEnd
        )
    )

    // Wrap the Scaffold in a Box to apply the gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(vibrantGradient)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Calculate Your BMI") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // Make TopAppBar transparent
                        titleContentColor = newActiveColor,
                        navigationIconContentColor = newActiveColor
                    )
                )
            },
            containerColor = Color.Transparent // Make Scaffold background transparent
        ) { paddingValues ->
            // Create a scroll state
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    // Add the verticalScroll modifier
                    .verticalScroll(scrollState)
                    // Apply padding *after* the scroll modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Input Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    // Make card semi-transparent white to pop against gradient
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Gender Selector
                        GenderSelector(
                            selectedGender = selectedGender,
                            onGenderSelect = { selectedGender = it },
                            activeColor = newActiveColor
                        )

                        // Height Slider
                        SliderInput(
                            label = "Height (ft / in)",
                            value = heightInches,
                            onValueChange = { heightInches = it },
                            range = 48f..84f, // 4ft to 7ft
                            displayValue = formatInchesToFtIn(heightInches),
                            activeColor = newActiveColor
                        )

                        // Weight Slider
                        SliderInput(
                            label = "Weight (lbs)",
                            value = weightLbs,
                            onValueChange = { weightLbs = it },
                            range = 80f..350f, // 80lbs to 350lbs
                            displayValue = "${weightLbs.toInt()} lbs",
                            activeColor = newActiveColor
                        )

                        // Calculate Button
                        Button(
                            onClick = {
                                val bmi = (weightLbs / (heightInches * heightInches)) * 703
                                bmiResult = bmi
                                bmiCategory = getBmiCategory(bmi)
                                suggestedWeight = getSuggestedWeight(heightInches)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = newActiveColor)
                        ) {
                            Text(text = "Calculate", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }

                // --- Result Area ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Left Side: Result Text
                    // Show the result card even if empty, to maintain layout
                    ResultCard(
                        modifier = Modifier.weight(1f),
                        bmi = bmiResult,
                        category = bmiCategory,
                        suggestedWeight = suggestedWeight,
                        activeColor = newActiveColor
                    )


                    // Right Side: Silhouette Image
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        val imageRes = if (selectedGender == Gender.FEMALE) {
                            R.drawable.ladyicon
                        } else {
                            R.drawable.profile
                        }
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = "Body Silhouette",
                            modifier = Modifier
                                .fillMaxHeight(0.5f)
                                .padding(top = 20.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Removed the Spacer(modifier = Modifier.weight(1f)) to allow scrolling

                // --- BMI Categories Table ---
                BmiCategoryTable()
            }
        }
    }
}

@Composable
fun GenderSelector(
    selectedGender: Gender,
    onGenderSelect: (Gender) -> Unit,
    activeColor: Color // Pass the new color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GenderButton(
            text = "Male",
            icon = Icons.Default.Male,
            isSelected = selectedGender == Gender.MALE,
            onClick = { onGenderSelect(Gender.MALE) },
            modifier = Modifier.weight(1f),
            activeColor = activeColor
        )
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
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color // Pass the new color
) {
    val backgroundColor = if (isSelected) activeColor else Color.White.copy(alpha = 0.5f)
    val textColor = if (isSelected) Color.White else Color.Black.copy(alpha = 0.7f)
    val iconColor = if (isSelected) Color.White else activeColor

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = text, tint = iconColor)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = textColor, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun SliderInput(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    displayValue: String,
    activeColor: Color // Pass the new color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Text(text = displayValue, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = 0,
            colors = SliderDefaults.colors(
                thumbColor = activeColor,
                activeTrackColor = activeColor.copy(alpha = 0.7f),
                inactiveTrackColor = Color.White.copy(alpha = 0.7f)
            )
        )
    }
}

@Composable
fun ResultCard(
    modifier: Modifier = Modifier,
    bmi: Float?,
    category: String,
    suggestedWeight: String,
    activeColor: Color // Pass the new color
) {
    val df = DecimalFormat("#.0")
    val categoryColor = getCategoryColor(category) // Use new category colors
    val description = if (bmi != null) getCategoryDescription(category) else "Enter your details to see results."

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Your Results",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = activeColor,
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(activeColor.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )

        // Only show results if BMI is calculated
        if (bmi != null) {
            ResultRow("Your BMI", bmi.let { df.format(it) } ?: "N/A")
            ResultRow("Suggested Weight", suggestedWeight)
            ResultRow("Your Category", category, categoryColor)
        }

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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
fun BmiCategoryTable() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryDarkBlue), // Use theme color (still looks good)
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
            CategoryRow("Underweight", "Below 18.5")
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

private fun formatInchesToFtIn(totalInches: Float): String {
    val feet = (totalInches / 12).toInt()
    val inches = (totalInches % 12).toInt()
    return "$feet ft / $inches in"
}

private fun getBmiCategory(bmi: Float): String {
    return when {
        bmi < 18.5 -> "Underweight"
        bmi < 25 -> "Healthy" // Changed from "Normal" to match image
        bmi < 30 -> "Overweight"
        else -> "Obese"
    }
}

private fun getCategoryColor(category: String): Color {
    // --- New non-theme colors ---
    return when (category) {
        "Underweight" -> Color(0xFF03A9F4) // Light Blue
        "Healthy" -> Color(0xFF4CAF50) // Green
        "Overweight" -> Color(0xFF0800FF) // Orange
        "Obese" -> Color(0xFFF44336) // Red
        else -> Color.Gray
    }
}

private fun getSuggestedWeight(heightInches: Float): String {
    // Ideal BMI range: 18.5 to 24.9
    // Formula: weight = BMI * (heightInches^2) / 703
    val minWeight = (18.5 * (heightInches * heightInches)) / 703
    val maxWeight = (24.9 * (heightInches * heightInches)) / 703
    return "${minWeight.toInt()} - ${maxWeight.toInt()} lbs"
}

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
