package com.example.lifesaver.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun AccidentEmergencyScreen(
    emergencyNumber: String = "108" ,
    navController: NavController
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.LocalHospital,
                contentDescription = null,
                tint = Color.Red
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Accident Emergency",
                color = Color.Black,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Steps Section
        SectionHeader("Steps to Follow")
        StepItem(
            number = "1",
            title = "Ensure Scene Safety",
            details = listOf(
                "Check surroundings for danger before helping.",
                "Do not move injured persons unless necessary."
            ),
            leadingIcon = Icons.Filled.Warning
        )
        StepItem(
            number = "2",
            title = "Call Emergency Services",
            details = listOf(
                "Dial $emergencyNumber and report location, number of injured, and condition.",
                "If in doubt, provide maximum details to help responders."
            ),
            leadingIcon = Icons.Outlined.Phone
        )
        StepItem(
            number = "3",
            title = "Provide First Aid (if trained)",
            details = listOf(
                "Stop bleeding by applying pressure with a clean cloth.",
                "Keep injured person calm and warm until help arrives."
            ),
            leadingIcon = Icons.Outlined.LocalHospital
        )

        // Safety Tips
        SectionHeader("Safety Tips")
        TipItem(
            title = "Keep First Aid Kit",
            points = listOf(
                "Include bandages, antiseptic, scissors, gloves, and CPR mask.",
                "Regularly check expiry dates of items."
            )
        )
        TipItem(
            title = "Learn Basic First Aid & CPR",
            points = listOf(
                "Consider attending certified first aid and CPR training.",
                "It can save lives in critical situations."
            ),
            icon = Icons.Outlined.TipsAndUpdates
        )

        // Action Buttons
        ActionButtons(context, emergencyNumber, "Accident")

        Spacer(Modifier.height(12.dp))
        Text(
            text = "Note: Do not move accident victims unless necessary (e.g., fire risk).",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
//
//@Composable
//fun ActionButtons(context: android.content.Context, number: String, emergencyType: String) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        OutlinedButton(
//            onClick = { callEmergencyNumber(context, number) },
//            modifier = Modifier.weight(1f),
//            shape = RoundedCornerShape(16.dp)
//        ) {
//            Icon(Icons.Filled.Call, contentDescription = null)
//            Spacer(Modifier.width(8.dp))
//            Text("Call SOS")
//        }
//        Button(
//            onClick = { shareLocationText(context, emergencyType) },
//            modifier = Modifier.weight(1f),
//            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
//            shape = RoundedCornerShape(16.dp)
//        ) {
//            Icon(Icons.Filled.LocationOn, contentDescription = null)
//            Spacer(Modifier.width(8.dp))
//            Text("Share Location")
//        }
//    }
//}

private fun callEmergencyNumber(context: android.content.Context, number: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$number")
    }
    context.startActivity(intent)
}

private fun shareLocationText(context: android.content.Context, emergencyType: String) {
    val message = "I need $emergencyType help. This is my location: https://maps.google.com (sharing from LifeSaver app)."
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    val chooser = Intent.createChooser(sendIntent, "Share location")
    context.startActivity(chooser)
}



@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp)
    )
}


@Composable
private fun StepItem(
    number: String,
    title: String,
    details: List<String>,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AssistChip(number)
                Spacer(Modifier.width(8.dp))
                if (leadingIcon != null) {
                    Icon(leadingIcon, contentDescription = null, tint = Color.Red)
                    Spacer(Modifier.width(6.dp))
                }
                Text(title,color = Color.Black, fontWeight = FontWeight.SemiBold)
            }
            details.forEach {
                Bullet(it)
            }
        }
    }
}


@Composable
private fun TipItem(
    title: String,
    points: List<String>,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7F7)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = Color.Red)
                    Spacer(Modifier.width(6.dp))
                }
                Text(title,color = Color.Black, fontWeight = FontWeight.SemiBold)
            }
            points.forEach { Bullet(it) }
        }
    }
}

@Composable
private fun Bullet(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text("• ", color = Color.Black)
        Text(text, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun AssistChip(text: String) {
    Surface(
        color = Color(0xFFFFE7E7),
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color = Color.Red,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun PreviewAccident(){
    AccidentEmergencyScreen(navController = rememberNavController())
}
