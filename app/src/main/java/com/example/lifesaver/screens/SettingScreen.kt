package com.example.lifesaver.screens


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract.Contacts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lifesaver.data.Contact
import com.example.lifesaver.viewmodel.ContactViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    navController: NavController,
    viewModel: ContactViewModel = hiltViewModel()
) {
    val contacts by viewModel.contacts.collectAsState(emptyList())
    val context = LocalContext.current

    val defaultsContacts = listOf(
        Contact(name = "Rishika", role = "Friend", phone = "8840787852", email = ""),
        Contact(name = "Police", role = "Emergency Service", phone = "100",email = ""),
        Contact(name = "Ambulance", role = "Emergency Service", phone = "102", email = ""),
        Contact(name = "Fire Brigade", role = "Emergency Service", phone = "101", email = "")
    )

    val allContacts = defaultsContacts + contacts

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Emergency Contacts") })
        }
    ) { paddingValues ->
        if (allContacts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No contacts found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                items(allContacts) { contact ->
                    ContactCard(contact){
                        phone ->
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$phone")
                        }
                        context.startActivity(intent)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

        @Composable
        fun ContactCard(contact: Contact, onCallClick: (String) -> Unit) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Name: ${contact.name}", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { onCallClick(contact.phone) }) {
                            Icon(Icons.Default.Call, contentDescription = "Call Contact")
                        }
                    }

                        Text(text = "Role: ${contact.role}")
                        Text(text = "Phone: ${contact.phone}")
                    if(contact.email.isNotEmpty()){
                        Text(text = "Email: ${contact.email}")
                    }
                }
                }
            }

