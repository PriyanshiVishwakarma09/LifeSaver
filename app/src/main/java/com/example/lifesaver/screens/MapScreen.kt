package com.example.lifesaver.screens

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.navigation.NavController
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.google.maps.android.compose.Marker
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(navController: NavController){
    val context = LocalContext.current
    var location by remember { mutableStateOf<GeoPoint?>(null) }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE)
        )
    }

    LaunchedEffect(Unit) {
        location = getDeviceLastKnownLocation(context)?.let{
            GeoPoint(it.latitude, it.longitude)
        }?: GeoPoint(28.6139, 77.2090)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (location != null){
            AndroidView(
                factory = {
                    MapView(it).apply {
                        setMultiTouchControls(true)
                        controller.setZoom(18.0)
                        controller.setCenter(location)

                        val marker = org.osmdroid.views.overlay.Marker(this)
                        marker.position = location
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = "You are here"
                        overlays.add(marker)
                    }
                },
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    navController.navigate("setting")

                },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.DarkGray
                )

            ) {
                Text("Send Help")
            }
        }else {
            Text(
                "Fetching location...",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

private fun getDeviceLastKnownLocation(context: Context): Location? {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val providers = locationManager.getProviders(true)
    for (provider in providers){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED){
            locationManager.getLastKnownLocation(provider)?.let {
                return it
            }
        }
    }
    return null
}

