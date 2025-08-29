package com.example.lifesaver.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.util.UUID
import com.example.lifesaver.R
import com.example.lifesaver.data.Contact
import com.example.lifesaver.viewmodel.ContactViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(navController: NavController,
                  viewModel: ContactViewModel = hiltViewModel()
) {
    val contacts by viewModel.contacts.collectAsState(initial = emptyList())
    val context = LocalContext.current
    var showAddContact by remember { mutableStateOf(false) }
    // List to hold contacts, managed directly in the Composable
//    val contacts = remember {
//        mutableStateListOf(
//            Contact(name = "Family Member", role = "Primary", phone = "9876543210")
//        )
//    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddContact = !showAddContact
            navController.navigate("add_contact")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Header()
            if (showAddContact) {
                AddContactScreen(
                    onAdd = { name, role, phone, email ->
                        viewModel.addContact(name, role, phone, email)
                        showAddContact = false
                    },
                    onCancel = { showAddContact = false }
                )
            } else {
                if (contacts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No contacts found", fontSize = 18.sp, color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        items(contacts, key = { it.id }) { contact ->
                            ContactCard(
                                //  contact = contact,
                                contact = contact,
                                onCallClick = { phone ->
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:$phone")
                                    }
                                    context.startActivity(intent)
                                },
                                onDeleteClick = { id ->
                                    viewModel.deleteContact(id)
                                    //ontacts.removeIf { it.id == id }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Header() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .height(80.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(70.dp))
                    .border(0.7.dp, Color.LightGray, RoundedCornerShape(70.dp)),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "Emergency Contacts",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
        Box(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(color = Color.LightGray)
        )
    }
}

@Composable
fun ContactCard(
    contact: Contact,
    onCallClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Name: ${contact.name}", fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "Role: ${contact.role}", color = Color.Black)
                Text(text = "Phone: ${contact.phone}", color = Color.Black)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { onCallClick(contact.phone) }) {
                    Icon(Icons.Default.Call, contentDescription = "Call Contact", tint = Color.Red)
                }
                IconButton(onClick = { onDeleteClick(contact.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Contact", tint = Color.Gray)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    onAdd: (String, String, String, String) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = role,
            onValueChange = { role = it },
            label = { Text("Role") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = onCancel) {
                Text("Cancel")
            }
            Button(onClick = {
                if (name.isNotEmpty() && role.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty()) {
                    onAdd(name, role, phone, email)
                }
            }) {
                Text("Add")
            }
        }
    }
}












//package com.example.lifesaver.screens
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.util.Log
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.example.lifesaver.MainActivity
//import com.example.lifesaver.R
//import com.example.lifesaver.data.Contact
//import com.example.lifesaver.viewmodel.ContactViewModel
//import com.google.android.gms.maps.model.LatLng
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ContactScreen(
//    navController: NavController,
//    viewModel: ContactViewModel = hiltViewModel()
//) {
//    val contacts by viewModel.contacts.collectAsState(emptyList())
//    val context = LocalContext.current
//    var showAddContact by remember { mutableStateOf(false) }
//
//    // State to hold the phone number for the SMS action
////    var smsPhoneNumber by remember { mutableStateOf("") }
////
////    // ActivityResultLauncher for the MapsScreen
////    val mapsScreenLauncher = rememberLauncherForActivityResult(
////        contract = ActivityResultContracts.StartActivityForResult()
////    ) { result ->
////        if (result.resultCode == Activity.RESULT_OK) {
////            val latitude = result.data?.getDoubleExtra("latitude", 0.0) ?: 0.0
////            val longitude = result.data?.getDoubleExtra("longitude", 0.0) ?: 0.0
////
////            if (smsPhoneNumber.isNotEmpty()) {
////                sendSms(context, smsPhoneNumber, latitude, longitude)
////            }
////        }
////    }
//
//    Scaffold(
//        floatingActionButton = {
//            FloatingActionButton(onClick = { showAddContact = !showAddContact }) {
//                Icon(Icons.Default.Add, contentDescription = "Add Contact")
//            }
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.White)
//                .padding(paddingValues)
//        ) {
//            Header()
//            if (showAddContact) {
//                AddContactScreen(
//                    onAdd = { name, role, phone, email ->
//                        viewModel.addContact(name, role, phone, email)
//                        showAddContact = false
//                    },
//                    onCancel = { showAddContact = false }
//                )
//            } else {
//                if (contacts.isEmpty()) {
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text("No contacts found", fontSize = 18.sp, color = Color.Gray)
//                    }
//                } else {
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(horizontal = 16.dp),
//                        verticalArrangement = Arrangement.spacedBy(12.dp)
//                    ) {
//                        item {
//                            Spacer(modifier = Modifier.height(16.dp))
//                        }
//                        items(contacts, key = { it.id }) { contact ->
//                            ContactCard(
//                                contact = contact,
//                                onCallClick = { phone ->
//                                    val intent = Intent(Intent.ACTION_DIAL).apply {
//                                        data = Uri.parse("tel:$phone")
//                                    }
//                                    context.startActivity(intent)
//                                },
//                                onSmsClick = { phone ->
//                                    navController.navigate("map_screen/$phone")
//                                    //smsPhoneNumber = phone
//                                    // Start the MapsScreen to get location
//                                   // val intent = Intent(context, MainActivity::class.java) // or whatever your Maps screen is
//                                   // mapsScreenLauncher.launch(intent)
//                                },
//                                onDeleteClick = { id ->
//                                    viewModel.deleteContact(id)
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//// Function to send SMS with location
//fun sendSms(context: Context, phoneNumber: String, latitude: Double, longitude: Double) {
//    val mapsLink = "http://maps.google.com/maps?q=$latitude,$longitude"
//    val message = "Emergency! My current location is here: $mapsLink"
//    val intent = Intent(Intent.ACTION_SENDTO).apply {
//        data = Uri.parse("smsto:$phoneNumber")
//        putExtra("sms_body", message)
//    }
//    context.startActivity(intent)
//}
//
//// The rest of your composables (Header, ContactCard) are the same
//@Composable
//fun Header() {
//    Column {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color.White)
//                .height(80.dp)
//                .padding(horizontal = 12.dp, vertical = 8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.logo),
//                contentDescription = "App Logo",
//                modifier = Modifier
//                    .size(50.dp)
//                    .clip(RoundedCornerShape(70.dp))
//                    .border(0.7.dp, Color.LightGray, RoundedCornerShape(70.dp)),
//                contentScale = ContentScale.Fit
//            )
//            Text(
//                text = "Emergency Contacts",
//                fontSize = 26.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black,
//                modifier = Modifier.padding(start = 12.dp)
//            )
//        }
//        Box(
//            modifier = Modifier
//                .height(1.dp)
//                .fillMaxWidth()
//                .background(color = Color.LightGray)
//        )
//    }
//}
//
//@Composable
//fun ContactCard(
//    contact: Contact,
//    onCallClick: (String) -> Unit,
//    onSmsClick: (String) -> Unit,
//    onDeleteClick: (String) -> Unit
//) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = Color.White
//        )
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(modifier = Modifier.weight(1f)) {
//                Text(text = "Name: ${contact.name}", fontWeight = FontWeight.Bold, color = Color.Black)
//                Text(text = "Role: ${contact.role}", color = Color.Black)
//                Text(text = "Phone: ${contact.phone}", color = Color.Black)
//                if (contact.email.isNotEmpty()) {
//                    Text(text = "Email: ${contact.email}", color = Color.Black)
//                }
//            }
//            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                IconButton(onClick = { onSmsClick(contact.phone) }) {
//                    Icon(Icons.Default.LocationOn, contentDescription = "Send SMS with Location", tint = Color.Blue)
//                }
//                IconButton(onClick = { onCallClick(contact.phone) }) {
//                    Icon(Icons.Default.Call, contentDescription = "Call Contact", tint = Color.Red)
//                }
//                IconButton(onClick = { onDeleteClick(contact.id) }) {
//                    Icon(Icons.Default.Delete, contentDescription = "Delete Contact", tint = Color.Gray)
//                }
//            }
//        }
//    }
//}