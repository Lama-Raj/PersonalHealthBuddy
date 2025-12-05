import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import com.unh.personal_health_buddy.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.unh.personal_health_buddy.ui.theme.AppSurfaceLight
import com.unh.personal_health_buddy.ui.theme.ButtonBlue
import com.unh.personal_health_buddy.ui.theme.ChatGreen
import com.unh.personal_health_buddy.ui.theme.MediumGray
import com.unh.personal_health_buddy.ui.theme.PrimaryDarkBlue
import com.unh.personal_health_buddy.ui.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun GoogleMapScreen(navController: NavController) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(41.29, -72.9615), 15f)
    }

    var searchQuery by remember { mutableStateOf("") }
    var searchedLocation by remember { mutableStateOf<LatLng?>(null) }
    val coroutineScope = rememberCoroutineScope()



    // Root background fills entire screen including behind system bars
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(ChatGreen)

    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Back button and Search Section with horizontal padding
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)

            ) {
                // Back button with IconButton (better UX)
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
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back to Home",
                        tint = AppSurfaceLight,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Google Map Icon
                Image(
                    painter = painterResource(id = R.drawable.google_map),
                    contentDescription = "Google Map",
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Search Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 30.dp)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search location...", color = MediumGray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MediumGray
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(4.dp)),
                        textStyle = LocalTextStyle.current.copy(
                            color = PrimaryDarkBlue,
                            textAlign = TextAlign.Start
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ButtonBlue,
                            unfocusedBorderColor = MediumGray,
                            cursorColor = ButtonBlue,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonBlue,
                            contentColor = White
                        ),
                        onClick = {
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
                    ) {
                        Text("Search")
                    }
                }
            }


            Spacer(modifier = Modifier.height(19.dp))

            // Map - fills remaining space with NO horizontal padding
            val hasLocationPermission = RequestLocationPermission()

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .offset(y = 5.dp)
                    .padding(top = 8.dp, bottom = 0.dp)

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
                        Circle(
                            center = location,
                            radius = 500.0,
                            fillColor = Color(0x5543D8F3),
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

    Log.d("GoogleMapScreen", "Google Map screen displayed")
}


/**
 * Requests location permission and returns true when either fine or coarse location is granted.
 * Behavior unchanged from your original implementation.
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
 * Geocode helper (kept same as your original)
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