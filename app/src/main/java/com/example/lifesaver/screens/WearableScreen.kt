package com.example.lifesaver.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lifesaver.viewmodel.WearableViewModel

@Composable
fun WearableScreen(
    navController: NavController,
    viewModel: WearableViewModel = hiltViewModel()
) {
    // Collect the state flow from the view model
    val wearableData by viewModel.wearableData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
                modifier = Modifier.weight(1f)
            ) {
                Text("Start Tracking")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { viewModel.stopTracking() },
                enabled = wearableData.isTracking,
                modifier = Modifier.weight(1f)
            ) {
                Text("Stop Tracking")
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