package com.example.lifesaver.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lifesaver.screens.EmailRecovery
import com.example.lifesaver.screens.LogIn
import com.example.lifesaver.screens.SignUp
import com.example.lifesaver.screens.SplashScreen

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
            DashBoardContainer() // Scaffold + BottomBar + Internal NavHost
        }
    }
}