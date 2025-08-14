package com.example.lifesaver.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.lifesaver.R
import com.example.lifesaver.data.FirebaseHelper
import com.example.lifesaver.viewmodel.SOSViewModel

@Composable
fun DashBoard(navController: NavController, viewModel: SOSViewModel){
    val context = LocalContext.current
   // val firebaseHelper = remember { FirebaseHelper(context) }
    val scrollState  = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ){
        Row(modifier = Modifier
            .fillMaxWidth()
           .background(color = Color.White)
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
                    .padding(end = 16.dp)
//                    .align(Alignment.TopCenter)
                    .border(width = 0.7.dp , color = Color.LightGray , shape = RoundedCornerShape(70.dp))
                    .clip(shape = RoundedCornerShape(70.dp))
                    .size(50.dp),
                contentScale = ContentScale.Fit
            )
            Text(text = "LifeSaver" , fontSize = 30.sp , fontWeight = FontWeight.Bold , color = Color.Black)

                      Spacer(modifier = Modifier.weight(1f))

            Icon(imageVector = Icons.Default.Notifications , contentDescription = "About" ,tint = Color.Black , modifier = Modifier.padding(end = 8.dp)
                .clickable {  }) //can make this icon clickable (use it as about for our app)
        }


        Column(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
//            .height(800.dp)
            .background(color = Color.White)) {


            Column(modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(color = Color.LightGray)) {  }


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
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        color = Color.Black
                    )
                    Text(
                        text = "Press the SOS button, your live location will be shared with the nearest help centre and you emergency contacts",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                }


                Image(
                    painter = painterResource(R.drawable.emergency),
                    contentDescription = "Ambulance",
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(140.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Card(
                onClick = {viewModel.sendEmergency("SOS")
                    navController.navigate("sos_screen")
                          },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .size(300.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    SOSGif()
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Card(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)){
                Text(text = "What's your emergency?" , modifier = Modifier
                    .padding(8.dp) , fontSize = 20.sp , fontWeight = FontWeight.SemiBold , color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))


                val buttonModifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .height(50.dp)

                Column(modifier = Modifier.padding(4.dp)) {

                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly) {
                        EmergencyButton("Medical", R.drawable.medical, Color(0xFFE8F5E9), buttonModifier){
                           viewModel.sendEmergency("Medical")
                            navController.navigate("sos_screen")
                        }
                        EmergencyButton("fire", R.drawable.fire, Color(0xFFFFEBEE), buttonModifier){
                            viewModel.sendEmergency("Fire")
                            navController.navigate("sos_screen")
                        }
                        EmergencyButton("Disaster", R.drawable.disaster, Color(0xFFE0F7FA), buttonModifier){
                            viewModel.sendEmergency("Disaster")
                            navController.navigate("sos_screen")
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly) {
                        EmergencyButton("Accident", R.drawable.accident, Color(0xFFEDE7F6), buttonModifier){
                            viewModel.sendEmergency("Accident")
                            navController.navigate("sos_screen")
                        }
                        EmergencyButton("Violence", R.drawable.violence, Color(0xFFFCE4EC), buttonModifier){
                            viewModel.sendEmergency("Violence")
                            navController.navigate("sos_screen")
                        }
                        EmergencyButton("Rescue", R.drawable.rescue, Color(0xFFFFF9C4), buttonModifier){
                            viewModel.sendEmergency("Rescue")
                            navController.navigate("sos_screen")
                        }
                    }
                }
            }



        }

    }

}


@Composable
fun EmergencyButton(text: String, iconResId: Int, bgColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
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


@Composable
fun SOSGif() {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(R.drawable.sosgif) // Reference to sos.gif in drawable
            .decoderFactory(
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    ImageDecoderDecoder.Factory()
                } else {
                    GifDecoder.Factory()
                }
            )
            .build(),
        contentDescription = "Animated SOS Button",
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.FillWidth
//        size(200.dp)
    )
}




