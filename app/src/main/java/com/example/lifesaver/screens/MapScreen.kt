package com.example.lifesaver.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.lifesaver.R
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

// Data classes to parse the JSON response from Overpass API
data class OverpassResponse(
    @SerializedName("elements") val elements: List<Element>
)

data class Element(
    @SerializedName("type") val type: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("tags") val tags: Map<String, String>?
)

@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    var location by remember { mutableStateOf<GeoPoint?>(null) }
    var emergencyServices by remember { mutableStateOf<List<Element>>(emptyList()) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // State to track if permission is granted
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Callback for location updates
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { newLocation ->
                    location = GeoPoint(newLocation.latitude, newLocation.longitude)
                }
            }
        }
    }

    // Request permissions launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasLocationPermission = isGranted
        if (isGranted) {
            // Permission granted, start location updates
        } else {
            // Permission denied, handle accordingly
            Log.e("MapScreen", "Location permission denied.")
        }
    }

    // Start location updates when the composable enters the screen and permission is granted
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val locationRequest = LocationRequest.create().apply {
                interval = 5000 // 5 seconds
                fastestInterval = 2000 // 2 seconds
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    // Stop location updates when the composable leaves the screen
    DisposableEffect(Unit) {
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // Load OSMDroid configuration once
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE)
        )
        // Request permission if not already granted
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Fetch emergency services once the location is available
    LaunchedEffect(location) {
        location?.let { geoPoint ->
            val services = findNearbyEmergencyServices(geoPoint.latitude, geoPoint.longitude)
            emergencyServices = services
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (location != null && hasLocationPermission) {
            AndroidView(
                factory = {
                    MapView(it).apply {
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        controller.setCenter(location)

                        // Add user location marker
                        val userMarker = Marker(this)
                        userMarker.position = location
                        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        userMarker.title = "You are here"
                        userMarker.icon = ContextCompat.getDrawable(context, R.drawable.ic_user_marker)
                        overlays.add(userMarker)

                        // Add emergency service markers
                        emergencyServices.forEach { element ->
                            val servicePoint = GeoPoint(element.lat, element.lon)
                            val serviceMarker = Marker(this)
                            serviceMarker.position = servicePoint
                            serviceMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                            val name = element.tags?.get("name") ?: "Unnamed"
                            when {
                                element.tags?.containsKey("amenity") == true && element.tags["amenity"] == "hospital" -> {
                                    serviceMarker.title = "Hospital: $name"
                                    serviceMarker.icon = ContextCompat.getDrawable(context, R.drawable.ic_hospital_marker)
                                }
                                element.tags?.containsKey("amenity") == true && element.tags["amenity"] == "police" -> {
                                    serviceMarker.title = "Police Station: $name"
                                    serviceMarker.icon = ContextCompat.getDrawable(context, R.drawable.ic_police_marker)
                                }
                                element.tags?.containsKey("amenity") == true && element.tags["amenity"] == "fire_station" -> {
                                    serviceMarker.title = "Fire Station: $name"
                                    serviceMarker.icon = ContextCompat.getDrawable(context, R.drawable.ic_fire_marker)
                                }
                            }
                            overlays.add(serviceMarker)
                        }
                    }
                },
                update = { mapView ->
                    location?.let { newLocation ->
                        mapView.controller.setCenter(newLocation)
                        val userMarker = mapView.overlays.firstOrNull { it is Marker && it.title == "You are here" } as? Marker
                        userMarker?.position = newLocation
                        mapView.invalidate()
                    }
                },
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = { navController.navigate("dashboard") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text("Send Help")
            }
        } else {
            Text(
                "Fetching location... Please enable location",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

private suspend fun findNearbyEmergencyServices(latitude: Double, longitude: Double): List<Element> {
    return withContext(Dispatchers.IO) {
        try {
            val query = """
                [out:json];
                (
                  node["amenity"="hospital"](around:2000, $latitude, $longitude);
                  node["amenity"="police"](around:2000, $latitude, $longitude);
                  node["amenity"="fire_station"](around:2000, $latitude, $longitude);
                );
                out center;
            """.trimIndent()
            val url = URL("https://overpass-api.de/api/interpreter?data=$query")
            val connection = url.openConnection()
            val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
            val response = reader.use { it.readText() }
            val gson = Gson()
            val overpassResponse = gson.fromJson(response, OverpassResponse::class.java)
            overpassResponse.elements
        } catch (e: Exception) {
            Log.e("MapScreen", "Error fetching nearby services", e)
            emptyList()
        }
    }
}

