package com.unh.personal_health_buddy.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme

enum class Gender { MALE, FEMALE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiScreen(navController: NavController) {
    var selectedGender by remember { mutableStateOf(Gender.MALE) }
    var heightInches by remember { mutableStateOf(65f) }
    var weightLbs by remember { mutableStateOf(150f) }

    val gradient = Brush.verticalGradient(listOf(Color.White, Color(0xFFE3F2FD)))
    val activeColor = Color(0xFF0277BD)

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
                    containerColor = Color.Transparent,
                    titleContentColor = activeColor,
                    navigationIconContentColor = activeColor
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .background(gradient)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Select Gender", color = activeColor)
            GenderSelector(selectedGender, { selectedGender = it }, activeColor)

            Spacer(modifier = Modifier.height(24.dp))

            SliderInput(
                label = "Height (ft / in)",
                value = heightInches,
                onValueChange = { heightInches = it },
                range = 48f..84f,
                displayValue = formatInchesToFtIn(heightInches),
                activeColor = activeColor
            )

            SliderInput(
                label = "Weight (lbs)",
                value = weightLbs,
                onValueChange = { weightLbs = it },
                range = 80f..350f,
                displayValue = "${weightLbs.toInt()} lbs",
                activeColor = activeColor
            )
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
    activeColor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.Gray)
            Text(displayValue, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = activeColor,
                activeTrackColor = activeColor.copy(alpha = 0.7f),
                inactiveTrackColor = Color.White.copy(alpha = 0.7f)
            )
        )
    }
}

private fun formatInchesToFtIn(totalInches: Float): String {
    val feet = (totalInches / 12).toInt()
    val inches = (totalInches % 12).toInt()
    return "$feet ft / $inches in"
}

// Gender selector remains same as Commit 2
@Composable
fun GenderSelector(
    selectedGender: Gender,
    onGenderSelect: (Gender) -> Unit,
    activeColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GenderButton("Male", Icons.Default.Male, selectedGender == Gender.MALE, { onGenderSelect(Gender.MALE) }, activeColor)
        GenderButton("Female", Icons.Default.Female, selectedGender == Gender.FEMALE, { onGenderSelect(Gender.FEMALE) }, activeColor)
    }
}

@Composable
fun GenderButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    activeColor: Color
) {
    val background = if (isSelected) activeColor else Color.White.copy(alpha = 0.5f)
    val textColor = if (isSelected) Color.White else Color.Black.copy(alpha = 0.7f)
    val iconColor = if (isSelected) Color.White else activeColor

    Card(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = text, tint = iconColor)
            Spacer(Modifier.width(8.dp))
            Text(text, color = textColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BmiScreenPreview() {
    PersonalHealthBuddyTheme {
        BmiScreen(navController = rememberNavController())
    }
}
