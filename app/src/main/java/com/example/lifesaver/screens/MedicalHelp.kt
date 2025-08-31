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
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.MedicalServices
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
fun MedicalEmergencyScreen(
    emergencyNumber: String = "112",
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.MedicalServices,
                contentDescription = null,
                tint = Color.Red
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Medical Emergency",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        SectionHeader("Steps to Follow" )
        StepItem(
            number = "1",
            title = "Call Emergency Services",
            details = listOf(
                "Tap the SOS button to dial $emergencyNumber immediately.",
                "Clearly state: your location, what happened, patient’s condition."
            ),
            leadingIcon = Icons.Outlined.Phone
        )
        StepItem(
            number = "2",
            title = "Apply First Aid (only if safe)",
            details = listOf(
                "Check for heavy bleeding, burns or visible injuries.",
                "For bleeding: press firmly with a clean cloth or bandage.",
                "For burns: cool the area with clean running water for 10–20 minutes."
            ),
            leadingIcon = Icons.Outlined.MedicalServices
        )
        StepItem(
            number = "3",
            title = "Check Breathing & Pulse",
            details = listOf(
                "If unconscious, tilt head back gently and look for chest movement.",
                "Feel for pulse on the neck (carotid) for 10 seconds.",
                "If no breathing/pulse: start CPR if trained (30 compressions, 2 breaths)."
            ),
            leadingIcon = Icons.Outlined.Favorite
        )

        SectionHeader("Safety Tips")
        TipItem(
            title = "Keep a First Aid Kit",
            points = listOf(
                "Include bandages, antiseptic, gloves, scissors, pain reliever.",
                "Check expiry dates every 6 months and restock."
            )
        )
        TipItem(
            title = "Learn CPR Basics",
            points = listOf(
                "Aim for 100–120 chest compressions per minute, depth ~5–6 cm.",
                "Prefer a certified short course; even basic CPR saves lives."
            ),
            icon = Icons.Outlined.TipsAndUpdates
        )
        TipItem(
            title = "Store Emergency Contacts",
            points = listOf(
                "Save family, doctor and nearest hospital numbers on speed dial.",
                "Enable Medical ID / emergency info on the phone lock screen."
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { callEmergencyNumber(context, emergencyNumber) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Call, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Call SOS")
            }
            Button(
                onClick = { shareLocationText(context) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Share Location")
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            text = "Note: Only perform first aid you are comfortable with. Wait for professionals once help is on the way.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}


@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp),
        color = Color.Black
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
                Text(title,  color = Color.Black , fontWeight = FontWeight.SemiBold ,)
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
                Text(title,   color = Color.Black ,fontWeight = FontWeight.SemiBold)
            }
            points.forEach { Bullet(it) }
        }
    }
}

@Composable
private fun Bullet(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text("• ", color = Color.Black)
        Text(text, modifier = Modifier.weight(1f) , color = Color.Black)
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


private fun callEmergencyNumber(context: android.content.Context, number: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$number")
    }
    context.startActivity(intent)
}


private fun shareLocationText(context: android.content.Context) {
    val message =
        "I need medical help. This is my location: https://maps.google.com (sharing from LifeSaver app)."
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    val chooser = Intent.createChooser(sendIntent, "Share location")
    context.startActivity(chooser)
}


@Preview(showBackground = true)
@Composable
fun previewClass(){
    MedicalEmergencyScreen(navController = rememberNavController())
}