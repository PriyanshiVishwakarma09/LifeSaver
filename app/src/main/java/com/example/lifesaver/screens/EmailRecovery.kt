package com.example.lifesaver.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lifesaver.R

@Composable
fun EmailRecovery(navController: NavController){
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier.fillMaxSize()
            .paint(painter = painterResource(id = R.drawable.background),
                contentScale = ContentScale.Crop )
    ){
//        Image(painter = painterResource(id = R.drawable.background),
//            contentDescription = "LogIn" ,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )
        Column(modifier = Modifier
            .verticalScroll(scrollState)  // to make screen scrollable
            .fillMaxSize(), //Adds padding around the screen
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .padding(top = 80.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(shape = RoundedCornerShape(65.dp))
                    .size(130.dp),
                contentScale = ContentScale.Fit
            )

            Card(modifier = Modifier
                //  .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp , bottom = 10.dp)
                .defaultMinSize(minHeight = 700.dp)
            .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Reset Password",
                        color = Color.Black,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.
                        padding(start = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    "Email",
                    color = Color.Black,
                    modifier = Modifier.padding(start = 24.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                var user by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it },
                    label = { Text("Enter your registered email", color = Color.Red) },
                    modifier = Modifier.padding(start = 16.dp)
                        .padding(end = 16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        cursorColor = Color.Red
                    ),
                    textStyle = TextStyle(color = Color.Black)
                )

                Spacer(modifier = Modifier.height(22.dp))
                Card(
                    modifier = Modifier.padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Red),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
//
                    Button(
                        onClick = { /* Handle sign up */ },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Send Reset Link")
                    }
                }
                Spacer(modifier = Modifier.height(26.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 22.dp, end = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TextButton(
                        onClick = { navController.navigate("LogIn") },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Back to Login",
                            color = Color.Red,
                            fontSize = 18.sp
                        )
                    }
                }

            }

        }

    }
}