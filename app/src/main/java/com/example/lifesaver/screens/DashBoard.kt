package com.example.lifesaver.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import com.example.lifesaver.R


@Composable
fun DashBoard(navController: NavController){

    //temporary user name
    val username : String = "Priyanshi"
    Column(
        modifier = Modifier.fillMaxSize()
    ){
        Row(modifier = Modifier.fillMaxWidth()
            .background(color = Color(0xFFCC4633))
            .height(80.dp)
            .padding(12.dp)
            .padding(start = 5.dp)
            ,
            //    horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .padding( end = 16.dp)
//                    .align(Alignment.TopCenter)
                    .clip(shape = RoundedCornerShape(70.dp))
                    .size(50.dp),
                contentScale = ContentScale.Fit
            )
            Text(text = "LifeSaver" , fontSize = 30.sp , fontWeight = FontWeight.Bold , color = Color.White)

            Spacer(modifier = Modifier.padding(start = 28.dp))
        }
        Column(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
//            .height(800.dp)
            .background(color = Color.White)) {
            Column(modifier = Modifier.height(1.dp)
                .fillMaxWidth()
                .background(color = Color.Gray)) {  }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(180.dp),
//                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)){
                    Text(
                        text = "Are you in an emergency?",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Press the SOS button, your live location will be shared with the nearest help centre and you emergency contacts",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                }


                Image(
                    painter = painterResource(R.drawable.emergencyvehicle),
                    contentDescription = "Ambulance",
                    modifier = Modifier
                        .size(140.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Card(
                onClick = { },
                modifier = Modifier
                    .padding(8.dp)
                    .size(250.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sos),
                    contentDescription = "SOS symbol",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Card(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)){
                Text(text = "What's your emergency?" , modifier = Modifier
                    .padding(8.dp) , fontSize = 20.sp , fontWeight = FontWeight.SemiBold)


                val buttonModifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .height(50.dp)

                Column(modifier = Modifier.padding(4.dp)) {

                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly) {
                        EmergencyButton("Medical", R.drawable.medical, Color(0xFFE8F5E9), buttonModifier)
                        EmergencyButton("fire", R.drawable.fire, Color(0xFFFFEBEE), buttonModifier)
                        EmergencyButton("Disaster", R.drawable.disaster, Color(0xFFE0F7FA), buttonModifier)
                    }

                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly) {
                        EmergencyButton("Accident", R.drawable.accident, Color(0xFFEDE7F6), buttonModifier)
                        EmergencyButton("Violence", R.drawable.violence, Color(0xFFFCE4EC), buttonModifier)
                        EmergencyButton("Rescue", R.drawable.rescue, Color(0xFFFFF9C4), buttonModifier)
                    }
                }
            }



        }

    }

}


@Composable
fun EmergencyButton(text: String, iconResId: Int, bgColor: Color, modifier: Modifier) {
    Button(
        onClick = {  },
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = text,
            modifier = Modifier.size(18.dp),
            tint = Color.Black
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = Color.Black,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}




