package com.example.lifesaver.navigation


import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
//import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lifesaver.data.Contact
import com.example.lifesaver.screens.ContactScreen
import com.example.lifesaver.screens.DashBoard
import com.example.lifesaver.screens.ProfileScreen
import com.example.lifesaver.screens.VoiceTriggerScreen
import com.example.lifesaver.screens.MapScreen
import com.example.lifesaver.screens.SosScreen
import com.example.lifesaver.viewmodel.SOSViewModel

@Composable
fun DashBoardContainer() {
    val navController = rememberNavController()
    val viewModel: SOSViewModel = hiltViewModel()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") {
                DashBoard(navController = navController, viewModel = viewModel)
            }
            composable("sos_screen") {
                SosScreen(navController = navController)
            }
            composable("profile") {
                ProfileScreen(navController = navController)
            }
            composable("voicetrigger"){
                VoiceTriggerScreen(navController = navController)
            }
            composable("map_screen"){
                MapScreen(navController = navController)
            }
            composable("setting"){
                ContactScreen(navController = navController)
            }
        }
    }
}


@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Location" , "map_screen" , icon = Icons.Default.LocationOn),
        BottomNavItem("Voice Help" , "voicetrigger" , icon = Icons.Filled.Mic),

        BottomNavItem("Dashboard", "dashboard", icon = Icons.Default.Home),
        BottomNavItem("Contacts", "setting", icon = Icons.Default.Call),
        BottomNavItem("Profile", "profile", icon = Icons.Default.Person)

    )

    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("dashboard") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

    }
}

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

