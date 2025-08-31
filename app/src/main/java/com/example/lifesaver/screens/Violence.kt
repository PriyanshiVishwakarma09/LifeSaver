package com.example.lifesaver.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.Icon
import androidx.compose.material.icons.outlined.Warning
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun ViolenceEmergencyScreen(emergencyNumber: String = "100" ,
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
            Icon(imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                tint = Color.Red)
            Spacer(Modifier.width(8.dp))
            Text("Violence Emergency", color = Color.Black , fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }

        // Steps
        SectionHeader("Steps to Follow")
        StepItem("1", "Find a Safe Spot",
            listOf("Move to a secure area away from the threat.", "Avoid confrontation if possible."),
            leadingIcon = Icons.Filled.Warning
        )
        StepItem("2", "Call Police Immediately",
            listOf("Dial $emergencyNumber to report the incident.", "Provide details like location, description of threat, and number of people involved."),
            leadingIcon = Icons.Outlined.Phone
        )
        StepItem("3", "Seek Help from Nearby People",
            listOf("Alert trusted neighbors or bystanders if safe to do so.", "Avoid escalating the situation."),
            leadingIcon = Icons.Outlined.LocalHospital
        )

        // Tips
        SectionHeader("Safety Tips")
        TipItem("Stay Alert", listOf("Be aware of your surroundings.", "Avoid secluded areas late at night."))
        TipItem("Keep Emergency Numbers Handy",
            listOf("Police: 100", "Women's Helpline: 1091"),
            icon = Icons.Outlined.TipsAndUpdates
        )

        ActionButtons(context, emergencyNumber, "Violence")

        Spacer(Modifier.height(12.dp))
        Text("Note: Do not confront attackers directly unless absolutely necessary.",
            style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
                Text(title, fontWeight = FontWeight.SemiBold)
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
        Text(text , color = Color.Black, modifier = Modifier.weight(1f))
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
fun PreviewViolenc(){
    ViolenceEmergencyScreen(navController = rememberNavController())
}
