package com.unh.personal_health_buddy.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.unh.personal_health_buddy.R
import com.unh.personal_health_buddy.ui.theme.ButtonBlue
import com.unh.personal_health_buddy.ui.theme.MediumGray
import com.unh.personal_health_buddy.ui.theme.PrimaryDarkBlue
import com.unh.personal_health_buddy.ui.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@SuppressLint("MissingPermission") // We guard location usage with runtime permission
@Composable
fun GoogleMapScreen(navController: NavController) {
    val context = LocalContext.current

    // Initial camera position
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(41.29, -72.9615), 15f)
    }

    // DEFAULT QUERY: health services near me
    val defaultQuery = "Health services near me"
    var searchQuery by remember { mutableStateOf(defaultQuery) }
    var searchedLocation by remember { mutableStateOf<LatLng?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Light blue -> white background
    val backgroundGradient = Brush.verticalGradient(
        listOf(
            Color(0xFFE3F2FD), // light blue
            Color.White        // white
        )
    )

    // Auto-run the default search once when screen opens
    LaunchedEffect(Unit) {
        if (searchQuery.isNotBlank()) {
            val latLng = geocodeLocation(context, searchQuery)
            latLng?.let {
                searchedLocation = it
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(it, 15f),
                    durationMs = 1000
                )
            }
        }
    }

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

            // ---------- Top Bar (Custom, light) ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.navigate("home") {
                            popUpTo("home") {
                                inclusive = false
                            }
                            launchSingleTop = true
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

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
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

                Image(
                    painter = painterResource(id = R.drawable.google_map),
                    contentDescription = "Google Map",
                    modifier = Modifier
                        .height(40.dp)
                        .padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ---------- Search Card ----------
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
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
                        placeholder = {
                            Text(
                                "Search location...",
                                color = MediumGray,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MediumGray
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent, RoundedCornerShape(8.dp)),
                        textStyle = LocalTextStyle.current.copy(
                            color = PrimaryDarkBlue,
                            textAlign = TextAlign.Start,
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
                                coroutineScope.launch {
                                    val latLng = geocodeLocation(context, searchQuery)
                                    latLng?.let {
                                        searchedLocation = it
                                        cameraPositionState.animate(
                                            update = CameraUpdateFactory.newLatLngZoom(it, 15f),
                                            durationMs = 1000
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        Text(
                            "Search",
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ---------- Map Card ----------
            val hasLocationPermission = RequestLocationPermission()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
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
                            // Soft highlight circle around searched location
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

                    // Little hint overlay at top of map
                    if (searchedLocation == null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 12.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Tip: Use the search above or tap my-location to center on you.",
                                color = MediumGray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    Log.d("GoogleMapScreen", "Google Map screen displayed")
}

/**
 * Requests location permission and returns true when either fine or coarse location is granted.
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

            if (permissionGranted) Log.d("Permissions", "Location permission granted")
            else Log.e("Permissions", "Location permission denied")
        }
    )

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
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
 * Geocode helper
 */
suspend fun geocodeLocation(context: android.content.Context, locationName: String): LatLng? {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(locationName, 1)
            if (!addresses.isNullOrEmpty()) {
                LatLng(addresses[0].latitude, addresses[0].longitude)
            } else null
        } catch (e: Exception) {
            Log.e("Geocode", "Error: ${e.message}")
            null
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewGoogleMapScreen() {
    val navController = rememberNavController()
    GoogleMapScreen(navController = navController)
}
