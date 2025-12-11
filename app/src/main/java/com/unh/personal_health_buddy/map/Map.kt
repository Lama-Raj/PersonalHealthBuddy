package com.unh.personal_health_buddy.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.unh.personal_health_buddy.R
import com.unh.personal_health_buddy.ui.theme.ButtonBlue
import com.unh.personal_health_buddy.ui.theme.MediumGray
import com.unh.personal_health_buddy.ui.theme.PrimaryDarkBlue
import com.unh.personal_health_buddy.ui.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@SuppressLint("MissingPermission")
@Composable
fun GoogleMapScreen(navController: NavController) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current // To hide keyboard on search

    // Initial camera position (New Haven/West Haven area)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(41.29, -72.9615), 15f)
    }

    val defaultQuery = "Health services near me"
    var searchQuery by remember { mutableStateOf(defaultQuery) }
    var searchedLocation by remember { mutableStateOf<LatLng?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val backgroundGradient = Brush.verticalGradient(
        listOf(Color(0xFFE3F2FD), Color.White)
    )

    // Request permissions
    val hasLocationPermission = RequestLocationPermission()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            // ---------- Top Bar ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        // Use popBackStack for simpler back navigation if valid
                        if (!navController.popBackStack()) {
                            navController.navigate("home")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = ButtonBlue
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Nearby Map",
                        fontSize = 22.sp,
                        color = PrimaryDarkBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Search a place or explore around you",
                        fontSize = 13.sp,
                        color = MediumGray
                    )
                }

                // Ensure this resource exists in your drawable folder
                // Image(painter = painterResource(id = R.drawable.google_map), ...)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ---------- Search Card ----------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search location...", color = MediumGray, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, "Search", tint = MediumGray) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(
                            color = PrimaryDarkBlue,
                            fontSize = 14.sp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ButtonBlue,
                            unfocusedBorderColor = MediumGray.copy(alpha = 0.5f),
                            cursorColor = ButtonBlue,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonBlue,
                            contentColor = White
                        ),
                        onClick = {
                            if (searchQuery.isNotBlank()) {
                                keyboardController?.hide() // Hide keyboard
                                Toast.makeText(context, "Searching...", Toast.LENGTH_SHORT).show()

                                coroutineScope.launch {
                                    val latLng = geocodeLocation(context, searchQuery)
                                    if (latLng != null) {
                                        searchedLocation = latLng
                                        cameraPositionState.animate(
                                            update = CameraUpdateFactory.newLatLngZoom(latLng, 15f),
                                            durationMs = 1000
                                        )
                                        Toast.makeText(context, "Found location!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Search", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ---------- Map Card ----------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            isMyLocationEnabled = hasLocationPermission
                        ),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = true,
                            compassEnabled = true,
                            myLocationButtonEnabled = true
                        )
                    ) {
                        searchedLocation?.let { location ->
                            Circle(
                                center = location,
                                radius = 500.0,
                                fillColor = Color(0x3343D8F3),
                                strokeColor = ButtonBlue,
                                strokeWidth = 4f
                            )
                            Marker(
                                state = MarkerState(position = location),
                                title = "Searched Location",
                                snippet = searchQuery
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Handles permission request state
 */
@Composable
fun RequestLocationPermission(): Boolean {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            permissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            permissionGranted = true
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    return permissionGranted
}

/**
 * Geocoding helper with error logging
 */
suspend fun geocodeLocation(context: Context, locationName: String): LatLng? {
    return withContext(Dispatchers.IO) {
        try {
            // NOTE: Geocoder requires a backend service. It might fail on some Emulators.
            // It works best on physical devices with Google Play Services.
            val geocoder = Geocoder(context, Locale.getDefault())

            @Suppress("DEPRECATION") // Keep simple for now, though API 33+ has a listener approach
            val addresses = geocoder.getFromLocationName(locationName, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                Log.d("Geocode", "Found: ${address.latitude}, ${address.longitude}")
                LatLng(address.latitude, address.longitude)
            } else {
                Log.e("Geocode", "No address found for $locationName")
                null
            }
        } catch (e: Exception) {
            Log.e("Geocode", "Geocode Exception: ${e.message}")
            null
        }
    }
}