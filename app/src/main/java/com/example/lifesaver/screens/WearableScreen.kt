package com.example.lifesaver.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lifesaver.R
import com.example.lifesaver.viewmodel.WearableViewModel
import kotlin.random.Random

@Composable
fun WearableScreen(
    navController: NavController,
    viewModel: WearableViewModel = hiltViewModel()
) {
    // Collect the state flow from the view model
    val context = LocalContext.current
    val wearableData by viewModel.wearableData.collectAsState()
    var hasBluetoothPermissions by remember {
        mutableStateOf(false
        )
    }

    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasBluetoothPermissions = permissions.entries.all { it.value }
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
        } else {
            arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
        }
        val arePermissionsGranted = permissionsToRequest.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (arePermissionsGranted) {
            hasBluetoothPermissions = true
        } else {
            bluetoothPermissionLauncher.launch(permissionsToRequest)
        }
    }
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(color = Color.White)
//            .height(80.dp)
//            .padding(12.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.logo),
//            contentDescription = "App Logo",
//            modifier = Modifier
//                .padding(end = 16.dp)
//                .border(width = 0.7.dp, color = Color.LightGray, shape = RoundedCornerShape(70.dp))
//                .clip(shape = RoundedCornerShape(70.dp))
//                .size(50.dp),
//            contentScale = ContentScale.Fit
//        )
//        Text(text = "Profile", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.Black)
//        Spacer(modifier = Modifier.weight(1f))
//        Icon(
//            imageVector = Icons.Default.Notifications,
//            contentDescription = "Notifications",
//            tint = Color.Black,
//            modifier = Modifier.padding(end = 8.dp)
//        )
//
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if(hasBluetoothPermissions){
        // Status Text
        Text(
            text = if (wearableData.isTracking) "Tracking..." else "Stopped",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (wearableData.isTracking) Color.Green else Color.Red
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Data Display Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            DataCard("Heart Rate", "${wearableData.heartRate} bpm")
            DataCard("Steps", "${wearableData.steps}")
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Control Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = { viewModel.startTracking() },
                enabled = !wearableData.isTracking,
                modifier = Modifier.weight(1f),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.Red,
//                    contentColor = Color.White
//                )

            ) {
                Text("Start Tracking")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { viewModel.stopTracking() },
                enabled = wearableData.isTracking,
                modifier = Modifier.weight(1f),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.Red,
//                    contentColor = Color.White
//                )
            ) {
                Text("Stop Tracking")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { viewModel.addSteps(Random.nextInt(5, 15)) },
                modifier = Modifier.weight(1f),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.Red,
//                    contentColor = Color.White
//                )
            ) {
                Text("Add Steps")
            }
        }
    } else {
            Text(
                "Bluetooth permissions are required to access wearable data.",
                modifier = Modifier.padding(16.dp),
                color = Color.Red,
                fontSize = 18.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        arrayOf(
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN
                        )
                    } else {
                        arrayOf(
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN
                        )
                    }
                    bluetoothPermissionLauncher.launch(permissionsToRequest)
                }
            ) {
                Text("Allow Bluetooth Access")
            }
        }
        }
}

@Composable
fun DataCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = label, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

