package com.unh.personal_health_buddy.features

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unh.personal_health_buddy.ui.theme.LightBlueBackground
import com.unh.personal_health_buddy.ui.theme.PersonalHealthBuddyTheme
import com.unh.personal_health_buddy.ui.theme.PrimaryDarkBlue


// --- NEW DATA CLASS ---
// A simple data class for our medication list
private data class Medication(val name: String)

@OptIn(ExperimentalMaterial3Api::class) // Got it from auto import
@Composable
fun MedicateScreen(navController: NavController){
    // --- STYLING ---
    // Using the theme's LightBlueBackground for a consistent feel
    val newGradientStart = LightBlueBackground
    val newGradientEnd = Color(0xFFFFFFFF)   // White
    val vibrantGradient = Brush.verticalGradient(colors = listOf(newGradientStart, newGradientEnd))
    val activeColor = PrimaryDarkBlue // Using the theme's dark blue for text

    // --- STATE FOR SEARCH ---
    var searchQuery by remember { mutableStateOf("") }

    // For Floating Action Button
    var isMenuExpanded by remember { mutableStateOf(false) }

    var medications by remember { mutableStateOf(emptyList<Medication>()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(vibrantGradient)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.padding(top = 20.dp),
                    title = {
                        Text(
                            text = "Medication",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        // Invisible button to balance the title
                        IconButton(onClick = { }, enabled = false) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.Transparent)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = activeColor,
                        navigationIconContentColor = activeColor
                    )
                )
            },
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Animated menu options
                    AnimatedVisibility(visible = isMenuExpanded) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MiniFabWithText(
                                icon = Icons.Default.Description,
                                text = "Add Prescription",
                                onClick = { /* TODO: Handle prescription add */ }
                            )
                            MiniFabWithText(
                                icon = Icons.Default.Delete,
                                text = "Delete Prescription",
                                onClick = { /* TODO: Handle prescription Delete */ }
                            )

                        }

                    }

                    // Main FAB
                    FloatingActionButton(
                        onClick = { isMenuExpanded = !isMenuExpanded },
                        containerColor = activeColor,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Medication"
                        )
                    }
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp), // Add horizontal padding for content
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- ADD SEARCH BAR ---
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
                // --- END SEARCH BAR ---
                Spacer(modifier = Modifier.height(32.dp))
                if (medications.isEmpty() && searchQuery.isEmpty()) {
                    EmptyMedicationState(activeColor = activeColor)
                } else {
                    // TODO: Show medication list (LazyColumn)
                    Text("Medication list would appear here...")
                }
            }
        }
    }

}
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 10.dp),
        shape = RoundedCornerShape(50), // Fully rounded corners
        colors = CardDefaults.cardColors(containerColor = Color.White),

        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Start typing medication name",
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(30.dp)

                )
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}
@Composable
private fun MiniFabWithText(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Text Label
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = PrimaryDarkBlue,
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )

        // Mini FAB
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = Color.White,
            contentColor = PrimaryDarkBlue
        ) {
            Icon(imageVector = icon, contentDescription = text)
        }
    }
}
@Composable
private fun EmptyMedicationState(activeColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Medication, // Using a standard icon
            contentDescription = "Medication",
            tint = activeColor.copy(alpha = 0.7f),
            modifier = Modifier.size(80.dp)
        )

        // Dotted border box
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .dashedBorder(color = activeColor.copy(alpha = 0.5f), cornerRadius = 16.dp)
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No Prescriptions Found",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = activeColor
            )
            Text(
                text = "Your medications will appear here once prescribed by a doctor.",
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Helper modifier for the dotted border
fun Modifier.dashedBorder(color: Color, cornerRadius: Dp, strokeWidth: Dp = 1.dp, dashWidth: Dp = 8.dp, gapWidth: Dp = 4.dp) = this.drawBehind {
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth.toPx(), gapWidth.toPx()), 0f)
    )
    drawRoundRect(
        color = color,
        style = stroke,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx())
    )
}


@Preview(showBackground = true)
@Composable
fun MedicateScreenPreview() {
    PersonalHealthBuddyTheme {
        MedicateScreen(navController = rememberNavController())
    }
}