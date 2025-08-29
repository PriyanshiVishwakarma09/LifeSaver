package com.example.lifesaver.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lifesaver.R
import com.example.lifesaver.viewmodel.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val dbRef = FirebaseDatabase.getInstance().getReference("Users")
    val storageRef = FirebaseStorage.getInstance().getReference("ProfileImages")

    val user = auth.currentUser
    val uid = user?.uid ?: return

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var bloodGroup by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    var isEditing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        selectedImageUri = it
    }

    LaunchedEffect(uid) {
        dbRef.child(uid).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                name = snapshot.child("name").value?.toString() ?: ""
                bloodGroup = snapshot.child("bloodGroup").value?.toString() ?: ""
                age = snapshot.child("age").value?.toString() ?: ""
                height = snapshot.child("height").value?.toString() ?: ""
                weight = snapshot.child("weight").value?.toString() ?: ""
                address = snapshot.child("address").value?.toString() ?: ""
                phone = snapshot.child("phone").value?.toString() ?: ""
                profileImageUrl = snapshot.child("profileImage").value?.toString()
            } else {
                name = user?.displayName ?: ""
                email = user?.email ?: ""
                val seed = mapOf(
                    "name" to name,
                    "email" to email,
                    "bloodGroup" to bloodGroup,
                    "age" to age,
                    "height" to height,
                    "weight" to weight,
                    "address" to address,
                    "phone" to phone,
                    "profileImage" to profileImageUrl
                )
                dbRef.child(uid).setValue(seed)
            }
        }
    }

    val scrollState = rememberScrollState()

    // Separate function to handle the database save
    fun saveProfileToDb(finalImageUrl: String?) {
        val updatedData = mapOf(
            "name" to name,
            "email" to email,
            "bloodGroup" to bloodGroup,
            "age" to age,
            "height" to height,
            "weight" to weight,
            "address" to address,
            "phone" to phone,
            "profileImage" to finalImageUrl
        )
        dbRef.child(uid).setValue(updatedData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    profileImageUrl = finalImageUrl // This is critical: Update the local state
                    selectedImageUri = null // Clear the URI to prevent re-upload
                } else {
                    // Handle database write failure
                }
                isSaving = false
                isEditing = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .height(80.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .padding(end = 16.dp)
                    .border(width = 0.7.dp, color = Color.LightGray, shape = RoundedCornerShape(70.dp))
                    .clip(shape = RoundedCornerShape(70.dp))
                    .size(50.dp),
                contentScale = ContentScale.Fit
            )

            Text(text = "Profile", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.Black)

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color.Black,
                modifier = Modifier.padding(end = 8.dp)
            )

            Icon(
                imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                contentDescription = if (isEditing) "Save Profile" else "Edit Profile",
                tint = Color.Red,
                modifier = Modifier
                    .size(28.dp)
                    .clickable(enabled = !isSaving) {
                        if (isEditing) {
                            isSaving = true

                            if (selectedImageUri != null) {
                                val imageRef = storageRef.child("$uid.jpg")
                                imageRef.putFile(selectedImageUri!!)
                                    .addOnSuccessListener {
                                        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                            saveProfileToDb(downloadUri.toString())
                                        }
                                    }
                                    .addOnFailureListener {
                                        saveProfileToDb(profileImageUrl)
                                    }
                            } else {
                                saveProfileToDb(profileImageUrl)
                            }
                        } else {
                            isEditing = true
                        }
                    }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
        ) {
            // Profile Image
            Image(
                painter = when {
                    selectedImageUri != null -> rememberAsyncImagePainter(selectedImageUri)
                    !profileImageUrl.isNullOrEmpty() -> rememberAsyncImagePainter(profileImageUrl)
                    else -> painterResource(id = R.drawable.usericon)
                },
                contentDescription = "User Image",
                modifier = Modifier
                    .padding(top = 40.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(shape = RoundedCornerShape(65.dp))
                    .size(130.dp)
                    .clickable { if (isEditing) imagePicker.launch("image/*") },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Text(
                    text = name,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 24.sp,
                    fontStyle = FontStyle.Normal
                )
            }

            EditableCards(isEditing, bloodGroup, { bloodGroup = it }, age, { age = it }, height, { height = it }, weight, { weight = it })
            EditableFieldCard("Address", address, { address = it }, Icons.Default.LocationOn, isEditing)
            EditableFieldCard("Email", email, { }, Icons.Default.Mail, false)
            EditableFieldCard("Number", phone, { phone = it }, Icons.Default.Call, isEditing)

            Spacer(modifier = Modifier.height(16.dp))
            AllergiesCard()
        }
    }
}

@Composable
fun EditableCards(
    isEditing: Boolean,
    bloodGroup: String, onBloodChange: (String) -> Unit,
    age: String, onAgeChange: (String) -> Unit,
    height: String, onHeightChange: (String) -> Unit,
    weight: String, onWeightChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileCard(
            "BLOOD GROUP",
            value = bloodGroup,
            onValueChange = onBloodChange,
            icon = Icons.Default.Bloodtype,
            color = Color.Red,
            isEditing = isEditing,
            modifier = Modifier.fillMaxWidth()
        )
        ProfileCard("AGE",
            value = age,
            onValueChange = onAgeChange,
            icon = Icons.Default.CalendarToday,
            color = Color.Green,
            isEditing = isEditing,
            modifier = Modifier.fillMaxWidth()
        )
        ProfileCard("HEIGHT",
            value = height,
            onValueChange = onHeightChange,
            icon = Icons.Default.Height,
            color = Color.Blue,
            isEditing = isEditing,
            modifier = Modifier.fillMaxWidth()
        )
        ProfileCard("WEIGHT",
            value = weight,
            onValueChange = onWeightChange,
            icon = Icons.Default.MonitorWeight,
            color = Color.Yellow,
            isEditing = isEditing,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ProfileCard(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color : Color,
    isEditing: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(end = 4.dp))
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    singleLine = true
                )
            } else {
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
fun EditableFieldCard(title: String, value: String, onValueChange: (String) -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector, isEditing: Boolean) {
    Card(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(modifier = Modifier.padding(top = 8.dp , start = 16.dp).fillMaxWidth()) {
                Icon(icon, contentDescription = title, tint = Color.Red)
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (isEditing) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    singleLine = true
                )
            } else {
                Box(
                    modifier = Modifier.padding(16.dp)
                        .fillMaxWidth()
                        .border(width = 1.dp, color = Color.LightGray)
                ) {
                    Text(value, modifier = Modifier.padding(8.dp), color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun AllergiesCard() {
    var allergies by remember { mutableStateOf(listOf<String>()) }
    var newAllergy by remember { mutableStateOf("") }

    Card(modifier = Modifier.padding(8.dp)
        .fillMaxWidth() ,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(modifier = Modifier.padding(top = 8.dp , start = 16.dp)
                .fillMaxWidth()) {
                Image(imageVector = Icons.Default.Medication , contentDescription = "allergies" , colorFilter = ColorFilter.tint(color = Color.Red))
                Text(
                    text = "Allergies and Reactions",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold ,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            allergies.forEachIndexed { index, allergy ->
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                        ) {
                            append("${index + 1} ")
                        }
                        withStyle(style = SpanStyle(color = Color.Black)) {
                            append("-> $allergy")
                        }
                    },
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newAllergy,
                onValueChange = { newAllergy = it },
                label = { Text("Enter new allergy") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (newAllergy.isNotBlank()) {
                        allergies = allergies + newAllergy
                        newAllergy = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text("Add Allergy")
            }
        }
    }
}









//package com.example.lifesaver.screens
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Bloodtype
//import androidx.compose.material.icons.filled.CalendarToday
//import androidx.compose.material.icons.filled.Call
//import androidx.compose.material.icons.filled.Height
//import androidx.compose.material.icons.filled.LocationOn
//import androidx.compose.material.icons.filled.Mail
//import androidx.compose.material.icons.filled.Medication
//import androidx.compose.material.icons.filled.MonitorWeight
//import androidx.compose.material.icons.filled.Notifications
//import androidx.compose.material.icons.filled.Numbers
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.paint
//import androidx.compose.ui.focus.focusModifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.ColorFilter
//import androidx.compose.ui.input.pointer.motionEventSpy
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.SpanStyle
//import androidx.compose.ui.text.buildAnnotatedString
//import androidx.compose.ui.text.font.FontStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.withStyle
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import androidx.navigation.compose.rememberNavController
//import com.example.lifesaver.R
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.OutlinedTextFieldDefaults
//import androidx.compose.material3.Text
//import android.net.Uri
//import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.ui.platform.LocalContext
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.lifesaver.viewmodel.ProfileViewModel
//
//@Composable
//fun ProfileScreen(
//    navController: NavController,
//    viewModel: ProfileViewModel = viewModel()
//) {
//    val context = LocalContext.current
//    val user = viewModel.userProfile.value
//
//    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
//    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
//        selectedImageUri = it
//    }
//        val scrollState = rememberScrollState()
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(scrollState)
//        ){
//            Row(modifier = Modifier
//                .fillMaxWidth()
//                .background(color = Color.White)
//                .height(80.dp)
//                .padding(12.dp)
//                .padding(start = 5.dp)
//                ,
//                //    horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.logo),
//                    contentDescription = "App Logo",
//                    modifier = Modifier
//                        .padding(end = 16.dp)
////                    .align(Alignment.TopCenter)
//                        .border(width = 0.7.dp , color = Color.LightGray , shape = RoundedCornerShape(70.dp))
//                        .clip(shape = RoundedCornerShape(70.dp))
//                        .size(50.dp),
//                    contentScale = ContentScale.Fit
//                )
//                Text(text = "Profile" , fontSize = 30.sp , fontWeight = FontWeight.Bold , color = Color.Black)
//
//                Spacer(modifier = Modifier.weight(1f))
//
//                Icon(imageVector = Icons.Default.Notifications , contentDescription = "About" ,tint = Color.Black , modifier = Modifier.padding(end = 8.dp)
//                    .clickable {  }) //can make this icon clickable (use it as about for our app)
//            }
//
//            Column(modifier = Modifier.fillMaxWidth()
//                .fillMaxHeight()
//                .background(color = Color.White)) {
//                Column(modifier = Modifier
//                    .height(1.dp)
//                    .fillMaxWidth()
//                    .background(color = Color.LightGray)) {  }
//
//                Image(
//                    painter = painterResource(id = R.drawable.usericon),
//                    contentDescription = "UserImage",
//                    modifier = Modifier
//                        .padding(top = 40.dp)
//                        .align(Alignment.CenterHorizontally)
//                        .clip(shape = RoundedCornerShape(65.dp))
//                        .size(130.dp),
//                    contentScale = ContentScale.Fit
//                )
//                Spacer(modifier = Modifier.height(12.dp))
//                var userName = "Priyanshi Vishwakarma"
//                Text(
//                    text = userName,
//                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier.align(Alignment.CenterHorizontally),
//                    fontSize = 24.sp,
//                    fontStyle = FontStyle.Normal
//                )
//
//                Column(
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxWidth(),
//
//                    ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 8.dp, vertical = 8.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//
//                        Card(
//                            modifier = Modifier
//                                .weight(1f)
//                                .padding(4.dp),
//                            colors = CardDefaults.cardColors(containerColor = Color.White),
//                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//                            shape = RoundedCornerShape(12.dp)
//                        ) {
//                            Column(
//                                modifier = Modifier
//                                    .padding(vertical = 12.dp)
//                                    .fillMaxWidth(),
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Row(horizontalArrangement = Arrangement.SpaceBetween) {
//                                    Text(
//                                        "BLOOD GROUP",
//                                        fontSize = 12.sp,
//                                        color = Color.Gray,
//                                        modifier = Modifier.padding(end = 4.dp)
//                                    )
//                                    Image(
//                                        imageVector = Icons.Default.Bloodtype,
//                                        colorFilter = ColorFilter.tint(color = Color.Red),
//                                        modifier = Modifier.size(16.dp),
//                                        contentDescription = "BloodGroup"
//                                    )
//
//                                }
//                                Spacer(modifier = Modifier.height(8.dp))
//                                Text(
//                                    "O+",
//                                    fontSize = 20.sp,
//                                    fontWeight = FontWeight.Bold,
//                                    color = Color.Black
//                                )
//
//                            }
//                        }
//
//                        Card(
//                            modifier = Modifier
//                                .weight(1f)
//                                .padding(4.dp),
//                            colors = CardDefaults.cardColors(containerColor = Color.White),
//                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//                            shape = RoundedCornerShape(12.dp)
//                        ) {
//                            Column(
//                                modifier = Modifier
//                                    .padding(vertical = 12.dp)
//                                    .fillMaxWidth(),
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Row(horizontalArrangement = Arrangement.SpaceBetween) {
//                                    Text(
//                                        "AGE",
//                                        fontSize = 12.sp,
//                                        color = Color.Gray,
//                                        modifier = Modifier.padding(end = 8.dp)
//                                    )
//                                    Image(
//                                        imageVector = Icons.Default.CalendarToday,
//                                        colorFilter = ColorFilter.tint(color = Color.Green),
//                                        contentDescription = "Age",
//                                        modifier = Modifier.size(14.dp)
//                                    )
//                                }
//                                Spacer(modifier = Modifier.height(8.dp))
//
//                                Text(
//                                    "18",
//                                    fontSize = 20.sp,
//                                    fontWeight = FontWeight.Bold,
//                                    color = Color.Black
//                                )
//
//                            }
//                        }
//                    }
//
//
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 8.dp, vertical = 8.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//
//                        Card(
//                            modifier = Modifier
//                                .weight(1f)
//                                .padding(4.dp),
//                            colors = CardDefaults.cardColors(containerColor = Color.White),
//                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//                            shape = RoundedCornerShape(12.dp)
//                        ) {
//                            Column(
//                                modifier = Modifier
//                                    .padding(vertical = 12.dp)
//                                    .fillMaxWidth(),
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Row(horizontalArrangement = Arrangement.SpaceBetween) {
//                                    Text(
//                                        "HEIGHT",
//                                        fontSize = 12.sp,
//                                        color = Color.Gray,
//                                        modifier = Modifier.padding(end = 4.dp)
//                                    )
//                                    Image(
//                                        imageVector = Icons.Default.Height,
//                                        colorFilter = ColorFilter.tint(color = Color.Blue),
//                                        modifier = Modifier.size(16.dp),
//                                        contentDescription = "BloodGroup"
//                                    )
//
//                                }
//                                Spacer(modifier = Modifier.height(8.dp))
//                                Text(
//                                    "159cm",
//                                    fontSize = 20.sp,
//                                    fontWeight = FontWeight.Bold,
//                                    color = Color.Black
//                                )
//
//                            }
//                        }
//
//                        Card(
//                            modifier = Modifier
//                                .weight(1f)
//                                .padding(4.dp),
//                            colors = CardDefaults.cardColors(containerColor = Color.White),
//                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//                            shape = RoundedCornerShape(12.dp)
//                        ) {
//                            Column(
//                                modifier = Modifier
//                                    .padding(vertical = 12.dp)
//                                    .fillMaxWidth(),
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Row(horizontalArrangement = Arrangement.SpaceBetween) {
//                                    Text(
//                                        "WEIGHT",
//                                        fontSize = 12.sp,
//                                        color = Color.Gray,
//                                        modifier = Modifier.padding(end = 8.dp)
//                                    )
//                                    Image(
//                                        imageVector = Icons.Default.MonitorWeight,
//                                        colorFilter = ColorFilter.tint(color = Color.Yellow),
//                                        contentDescription = "Age",
//                                        modifier = Modifier.size(16.dp)
//                                    )
//                                }
//                                Spacer(modifier = Modifier.height(8.dp))
//
//                                Text(
//                                    "50kg",
//                                    fontSize = 20.sp,
//                                    fontWeight = FontWeight.Bold,
//                                    color = Color.Black
//                                )
//
//                            }
//                        }
//                    }
//
//                }
//                Column(modifier = Modifier.fillMaxWidth()
//                    .fillMaxHeight()) {
//                    Card(modifier = Modifier.padding(8.dp)
//                        .fillMaxWidth() ,
//                        colors = CardDefaults.cardColors(containerColor = Color.White),
//                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//                        shape = RoundedCornerShape(12.dp) ) {
//                        Row(modifier = Modifier.padding(top = 8.dp , start = 16.dp)
//                            .fillMaxWidth()) {
//                            Image(imageVector = Icons.Default.LocationOn , contentDescription = "location" , colorFilter = ColorFilter.tint(color = Color.Red))
//                            Text(
//                                text = "Address",
//                                style = MaterialTheme.typography.bodyLarge,
//                                modifier = Modifier.padding(start = 8.dp),
//                                fontSize = 18.sp,
//                                fontStyle = FontStyle.Normal,
//                                fontWeight = FontWeight.Bold ,
//                                color = Color.Black
//                            )
//                        }
//                        Spacer(modifier = Modifier.height(4.dp))
//                        Box(modifier = Modifier.padding(16.dp)
//                            .fillMaxWidth()
//                            .border(
//                                width = 1.dp,
//                                color = Color.LightGray,
//                            )
//                        ){
//                            Text("Mho.Kalyan Singh (Delhi)" , modifier = Modifier.padding(8.dp) , color = Color.Black)
//                        }
//
//
//                    }
//                    Card(modifier = Modifier.padding(8.dp)
//                        .fillMaxWidth() ,
//                        colors = CardDefaults.cardColors(containerColor = Color.White),
//                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//                        shape = RoundedCornerShape(12.dp) ) {
//                        Row(modifier = Modifier.padding(top = 8.dp , start = 16.dp)
//                            .fillMaxWidth()) {
//                            Image(imageVector = Icons.Default.Mail , contentDescription = "location" , colorFilter = ColorFilter.tint(color = Color.Red))
//                            Text(
//                                text = "Email",
//                                style = MaterialTheme.typography.bodyLarge,
//                                modifier = Modifier.padding(start = 8.dp),
//                                fontSize = 18.sp,
//                                fontStyle = FontStyle.Normal,
//                                fontWeight = FontWeight.Bold ,
//                                color = Color.Black
//                            )
//                        }
//                        Spacer(modifier = Modifier.height(4.dp))
//                        Box(modifier = Modifier.padding(16.dp)
//                            .fillMaxWidth()
//                            .border(
//                                width = 1.dp,
//                                color = Color.LightGray,
//                            )
//                        ){
//                            Text("priyanshi56963@gmail.com" , modifier = Modifier.padding(8.dp) , color = Color.Black)
//                        }
//                    }
//
//                    Card(modifier = Modifier.padding(8.dp)
//                        .fillMaxWidth() ,
//                        colors = CardDefaults.cardColors(containerColor = Color.White),
//                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//                        shape = RoundedCornerShape(12.dp) ) {
//                        Row(modifier = Modifier.padding(top = 8.dp , start = 16.dp)
//                            .fillMaxWidth()) {
//                            Image(imageVector = Icons.Default.Call , contentDescription = "number" , colorFilter = ColorFilter.tint(color = Color.Red))
//                            Text(
//                                text = "Number",
//                                style = MaterialTheme.typography.bodyLarge,
//                                modifier = Modifier.padding(start = 8.dp),
//                                fontSize = 18.sp,
//                                fontStyle = FontStyle.Normal,
//                                fontWeight = FontWeight.Bold ,
//                                color = Color.Black
//                            )
//                        }
//                        Spacer(modifier = Modifier.height(4.dp))
//                        Box(modifier = Modifier.padding(16.dp)
//                            .fillMaxWidth()
//                            .border(
//                                width = 1.dp,
//                                color = Color.LightGray,
//                            )
//                        ){
//                            Text("748519346538" , modifier = Modifier.padding(8.dp) , color = Color.Black)
//                        }
//                    }
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    AllergiesCard()
//
//
//
//                }
//
//            }
//
//        }
//    }
//
//
//@Composable
//fun AllergiesCard() {
//    var allergies by remember { mutableStateOf(listOf<String>()) }
//    var newAllergy by remember { mutableStateOf("") }
//
//    Card(modifier = Modifier.padding(8.dp)
//        .fillMaxWidth() ,
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = RoundedCornerShape(12.dp)) {
//        Column(modifier = Modifier.padding(8.dp)) {
//            Row(modifier = Modifier.padding(top = 8.dp , start = 16.dp)
//                .fillMaxWidth()) {
//                Image(imageVector = Icons.Default.Medication , contentDescription = "allergies" , colorFilter = ColorFilter.tint(color = Color.Red))
//                Text(
//                    text = "Allergies and Reactions",
//                    style = MaterialTheme.typography.bodyLarge,
//                    modifier = Modifier.padding(start = 8.dp),
//                    fontSize = 18.sp,
//                    fontStyle = FontStyle.Normal,
//                    fontWeight = FontWeight.Bold ,
//                    color = Color.Black
//                )
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            allergies.forEachIndexed { index, allergy ->
//                Text(
//                    buildAnnotatedString {
//                        withStyle(
//                            style = SpanStyle(
//                                fontWeight = FontWeight.Bold,
//                                color = Color.Red
//                            )
//                        ) {
//                            append("${index + 1} ")
//                        }
//                        withStyle(style = SpanStyle(color = Color.Black)) {
//                            append("-> $allergy")
//                        }
//                    },
//                    fontSize = 16.sp
//                )
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            OutlinedTextField(
//                value = newAllergy,
//                onValueChange = { newAllergy = it },
//                label = { Text("Enter new allergy") },
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(4.dp),
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedBorderColor = Color.LightGray,
//                    unfocusedBorderColor = Color.LightGray,
//                    focusedTextColor = Color.Black,
//                    unfocusedTextColor = Color.Black
//                )
//            )
//
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Button(
//                onClick = {
//                    if (newAllergy.isNotBlank()) {
//                        allergies = allergies + newAllergy
//                        newAllergy = ""
//                    }
//                },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.Red,
//                    contentColor = Color.White
//                )
//            ) {
//                Text("Add Allergy")
//            }
//        }
//    }
//}
//
//
//
//@Preview
//@Composable
//fun PreviewProfileScreen(){
//    ProfileScreen(navController = rememberNavController())
//}
//
//
//
