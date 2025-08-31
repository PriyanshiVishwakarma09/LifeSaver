package com.example.lifesaver.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun RescueEmergencyScreen(emergencyNumber: String = "112",
                          navController: NavController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.LocalHospital,
                contentDescription = null,
                tint = Color(0xFF00796B)
            )
            Spacer(Modifier.width(8.dp))
            Text("Rescue Emergency",  fontSize = 28.sp, color = Color.Red, fontWeight = FontWeight.Bold)
        }

        // Steps
        SectionHeader("Steps to Follow")
        StepItem(
            "1", "Assess the Situation",
            listOf(
                "Check if it's safe to approach.",
                "Identify number of injured or trapped individuals."
            ),
            leadingIcon = Icons.Outlined.Warning
        )
        StepItem(
            "2", "Call Rescue Services",
            listOf(
                "Dial $emergencyNumber immediately for professional assistance.",
                "Provide location, type of emergency, and number of people involved."
            ),
            leadingIcon = Icons.Outlined.Phone
        )
        StepItem(
            "3", "Assist Safely",
            listOf(
                "Give first aid if trained.",
                "Avoid putting yourself in danger while helping others."
            ),
            leadingIcon = Icons.Outlined.LocalHospital
        )

        // Tips
        SectionHeader("Safety Tips")
        TipItem(
            "Stay Prepared",
            listOf(
                "Keep a first aid kit and emergency supplies.",
                "Learn basic rescue and CPR techniques."
            )
        )
        TipItem(
            "Keep Emergency Contacts",
            listOf("Rescue Services: 112", "Local Disaster Helpline: 108"),
            icon = Icons.Outlined.TipsAndUpdates
        )

        ActionButtons(context, emergencyNumber, "Rescue")

        Spacer(Modifier.height(12.dp))
        Text(
            "Note: Only attempt rescue if you are trained or the situation allows safe assistance.",
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
                    androidx.compose.material3.Icon(leadingIcon, contentDescription = null, tint = Color.Red)
                    Spacer(Modifier.width(6.dp))
                }
                Text(title, color = Color.Black,fontWeight = FontWeight.SemiBold )
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
                    androidx.compose.material3.Icon(icon, contentDescription = null, tint = Color.Red)
                    Spacer(Modifier.width(6.dp))
                }
                Text(title, color = Color.Black, fontWeight = FontWeight.SemiBold)
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
fun PreviewRescue(){
    RescueEmergencyScreen(navController = rememberNavController())
}
