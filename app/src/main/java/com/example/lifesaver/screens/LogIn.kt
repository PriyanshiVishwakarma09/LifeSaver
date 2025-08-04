package com.example.lifesaver.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lifesaver.R

@Composable
fun LogIn(navController: NavController){
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Image(painter = painterResource(id = R.drawable.background),
            contentDescription = "LogIn" ,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .padding(top = 30.dp)
                .align(Alignment.TopCenter)
                .clip(shape = RoundedCornerShape(65.dp))
                .size(130.dp),
            contentScale = ContentScale.Fit
        )

        Card(modifier = Modifier.padding(top = 32.dp)
            //  .fillMaxWidth()
            .padding(top = 150.dp, start = 16.dp, end = 16.dp , bottom = 10.dp)
            .fillMaxHeight()
            .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Log In", color = Color.Black , fontSize = 38.sp , fontWeight = FontWeight.Bold , modifier = Modifier.padding(16.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("User Name", color = Color.Black , modifier = Modifier.padding(start = 24.dp) , fontSize = 18.sp , fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(10.dp))
            var user by remember { mutableStateOf("") }
            OutlinedTextField(
                value = user,
                onValueChange = { user = it },
                label = { Text("User Name" , color = Color.Red) },
                modifier = Modifier.padding(start = 16.dp)
                    .padding(end = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    cursorColor = Color.Red
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text("Password", color = Color.Black , modifier = Modifier.padding(start = 24.dp) , fontSize = 18.sp , fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(10.dp))


            var password by remember { mutableStateOf("") }


            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password" , color = Color.Red) },
                modifier = Modifier.padding(start = 16.dp)
                    .padding(end = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    cursorColor = Color.Red
                )
            )
            Spacer(modifier = Modifier.height(22.dp))
            Card(modifier = Modifier.padding(16.dp)
                .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Red),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
//
                Button(
                    onClick = { navController.navigate("DashBoardContainer") {
                        popUpTo("SignUp") { inclusive = true }
                        launchSingleTop = true
                    } },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Log In")
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp , end = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Forgot Password? ")

                TextButton(
                    onClick = { navController.navigate("EmailRecovery")},
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Register",
                        color = Color.Red
                    )
                }
            }



        }

    }
}