package com.example.lifesaver.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lifesaver.screens.ContactScreen
import com.example.lifesaver.screens.DashBoard
import com.example.lifesaver.screens.EmailRecovery
import com.example.lifesaver.screens.LogIn
import com.example.lifesaver.screens.MapScreen
import com.example.lifesaver.screens.SignUp
import com.example.lifesaver.screens.SosScreen
import com.example.lifesaver.screens.SplashScreen
import com.example.lifesaver.viewmodel.SOSViewModel

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "SplashScreen"){
        composable("SplashScreen"){
            SplashScreen(navController = navController)
        }
        composable("SignUp"){
            SignUp(navController = navController)
        }
        composable("LogIn"){
            LogIn(navController = navController)

        }
        composable("EmailRecovery"){
            EmailRecovery(navController = navController)

        }
        composable("DashBoardContainer") {
            DashBoardContainer()// Scaffold + BottomBar + Internal NavHost
        }
        composable("SOSScreen"){
            SosScreen(navController = navController)
        }
        composable("MapScreen"){
            MapScreen(navController = navController)
        }
        composable("SettingScreen"){
            ContactScreen(navController = navController)
        }
    }
}