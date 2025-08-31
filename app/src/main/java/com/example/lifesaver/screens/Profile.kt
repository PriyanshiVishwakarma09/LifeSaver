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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.lifesaver.R
import com.example.lifesaver.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.storage.FirebaseStorage

// Main Composable for the Profile Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val dbRef = FirebaseDatabase.getInstance().getReference("Users")
   // val storageRef = FirebaseStorage.getInstance().getReference("ProfileImages")

    val user = auth.currentUser
    val uid = user?.uid ?: return

    // State variables to hold user data
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var bloodGroup by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var allergies by remember { mutableStateOf(listOf<String>()) }
    var newAllergy by remember { mutableStateOf("") }

    var isEditing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Image picker for profile picture
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        selectedImageUri = it
    }

    // Fetch user data from Firebase when the screen is launched
    LaunchedEffect(uid) {
        dbRef.child(uid).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                name = snapshot.child("name").value?.toString() ?: user?.displayName ?: ""
                bloodGroup = snapshot.child("bloodGroup").value?.toString() ?: ""
                age = snapshot.child("age").value?.toString() ?: ""
                height = snapshot.child("height").value?.toString() ?: ""
                weight = snapshot.child("weight").value?.toString() ?: ""
                address = snapshot.child("address").value?.toString() ?: ""
                phone = snapshot.child("phone").value?.toString() ?: ""
                profileImageUrl = snapshot.child("profileImage").value?.toString()
                allergies = snapshot.child("allergies").children.mapNotNull { it.getValue(String::class.java) }
            } else {
                // If user data doesn't exist, create a new entry with basic info
                val seed = mapOf(
                    "name" to user?.displayName,
                    "email" to user?.email,
                    "allergies" to emptyList<String>()
                )
                dbRef.child(uid).setValue(seed)
            }
        }
    }

    // Function to save profile data to the database
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
            "profileImage" to finalImageUrl,
            "allergies" to allergies
        )
        dbRef.child(uid).setValue(updatedData)
            .addOnCompleteListener {
                isSaving = false
                if (it.isSuccessful) {
                    profileImageUrl = finalImageUrl // Update the local state
                    selectedImageUri = null // Clear the URI to prevent re-upload
                    isEditing = false
                } else {
                    // Handle database write failure
                }
            }
    }

    val scrollState = rememberScrollState()

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
                            saveProfileToDb(null)
                        } else {
                            isEditing = true
                        }
                    }
            )
        }

        // Profile details section
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
            // Name
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

            // Editable cards for other details
            EditableCards(isEditing, bloodGroup, { bloodGroup = it }, age, { age = it }, height, { height = it }, weight, { weight = it })
            EditableFieldCard("Address", address, { address = it }, Icons.Default.LocationOn, isEditing)
            EditableFieldCard("Email", email, { }, Icons.Default.Mail, false)
            EditableFieldCard("Number", phone, { phone = it }, Icons.Default.Call, isEditing)
            AllergiesCard(allergies, { allergies = it }, newAllergy, { newAllergy = it })
            Spacer(modifier = Modifier.height(16.dp))
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
        ProfileCard("BLOOD GROUP", bloodGroup, onBloodChange, Icons.Default.Bloodtype, Color.Red, isEditing)
        ProfileCard("AGE", age, onAgeChange, Icons.Default.CalendarToday, Color.Green, isEditing)
        ProfileCard("HEIGHT", height, onHeightChange, Icons.Default.Height, Color.Blue, isEditing)
        ProfileCard("WEIGHT", weight, onWeightChange, Icons.Default.MonitorWeight, Color.Yellow, isEditing)
    }
}

@Composable
fun ProfileCard(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isEditing: Boolean,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
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
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(modifier = Modifier
                .padding(top = 8.dp, start = 16.dp)
                .fillMaxWidth()) {
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
                    modifier = Modifier
                        .padding(16.dp)
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
fun AllergiesCard(
    allergies: List<String>,
    onAllergiesChange: (List<String>) -> Unit,
    newAllergy: String,
    onNewAllergyChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(modifier = Modifier
                .padding(top = 8.dp, start = 16.dp)
                .fillMaxWidth()) {
                Image(imageVector = Icons.Default.Medication, contentDescription = "allergies", colorFilter = ColorFilter.tint(color = Color.Red))
                Text(
                    text = "Allergies and Reactions",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold,
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
                onValueChange = onNewAllergyChange,
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
                        onAllergiesChange(allergies + newAllergy)
                        onNewAllergyChange("")
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
