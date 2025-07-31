package com.example.lifesaver

import android.window.SplashScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun LifeSaverNavGraph(
    navController: NavHostController = rememberNavController()
){
  NavHost(navController = navController, startDestination = "splash"){
      composable("splash") { SplashScreen(navController)}
  }
}

