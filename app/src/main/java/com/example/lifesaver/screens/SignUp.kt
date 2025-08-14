package com.example.lifesaver.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lifesaver.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun SignUp(navController: NavController){
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val auth = remember { FirebaseAuth.getInstance() }
    val database = remember { FirebaseDatabase.getInstance().reference }
    var isLoading by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Image(painter = painterResource(id = R.drawable.background),
            contentDescription = "SignUp" ,
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
            .fillMaxWidth()
            .verticalScroll(scrollState),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Sign Up", color = Color.Black , fontSize = 38.sp , fontWeight = FontWeight.Bold , modifier = Modifier.padding(16.dp))
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
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    cursorColor = Color.Red
                )
            )
            Spacer(modifier = Modifier.height(18.dp))


            Text("Email", color = Color.Black , modifier = Modifier.padding(start = 24.dp) , fontSize = 18.sp , fontWeight = FontWeight.SemiBold)


            Spacer(modifier = Modifier.height(10.dp))


            var email by remember { mutableStateOf("") }
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email" , color = Color.Red) },
                modifier = Modifier.padding(start = 16.dp)
                    .padding(end = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
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
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
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
                    onClick = {
                        isLoading = true
                        auth.createUserWithEmailAndPassword(email.trim(), password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if(task.isSuccessful) {
                                    val userData = mapOf("username" to user, "email" to email)
                                    if (user != null) {
                                        database.child("users").child(user).setValue(userData)
                                            .addOnCompleteListener {
                                                Toast.makeText(
                                                    context,
                                                    "Registration Successful",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                navController.navigate("DashBoardContainer") {
                                                    popUpTo("SignUp") { inclusive = true }
                                                    launchSingleTop = true
                                                }
                                            }
                                    }
                                }else {
                                    Toast.makeText(context, "Signup Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                    } },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = if(isLoading) "Registering..." else "Sign Up")
                }
            }
            Spacer(modifier = Modifier.height(65.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Already have an account? ")

                TextButton(
                    onClick = {navController.navigate("LogIn") },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Sign In",
                        color = Color.Red
                    )
                }
            }



        }

    }
}