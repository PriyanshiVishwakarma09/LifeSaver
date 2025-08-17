package com.example.lifesaver.screens


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract.Contacts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lifesaver.R
import com.example.lifesaver.data.Contact
import com.example.lifesaver.viewmodel.ContactViewModel
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button


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
        Contact(name = "Police", role = "Emergency Service", phone = "100", email = ""),
        Contact(name = "Ambulance", role = "Emergency Service", phone = "102", email = ""),
        Contact(name = "Fire Brigade", role = "Emergency Service", phone = "101", email = "")
    )

    val allContacts = defaultsContacts + contacts

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
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
        Column(modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(color = Color.LightGray)) {  }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .height(80.dp)
                .padding(12.dp)
                .padding(start = 5.dp)
                .clickable {  }, //for add contacts
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = "Add Contact",
                modifier = Modifier
                    .padding(end = 16.dp)
                    .border(
                        width = 0.7.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(70.dp)
                    )
                    .clip(shape = RoundedCornerShape(70.dp))
                    .size(50.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Add Contacts",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
        }

        if (allContacts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No contacts found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(allContacts) { contact ->
                    ContactCard(contact) { phone ->
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
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
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
                        Text(text = "Name: ${contact.name}", fontWeight = FontWeight.Bold , color = Color.Black)
                        IconButton(onClick = { onCallClick(contact.phone) }) {
                            Icon(Icons.Default.Call, contentDescription = "Call Contact" , tint = Color.Red)
                        }
                    }

                        Text(text = "Role: ${contact.role}" ,  color = Color.Black)
                        Text(text = "Phone: ${contact.phone}" , color = Color.Black)
                    if(contact.email.isNotEmpty()){
                        Text(text = "Email: ${contact.email}" , color = Color.Black)
                    }
                }
            }
        }

