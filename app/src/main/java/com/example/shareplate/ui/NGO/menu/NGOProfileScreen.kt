package com.example.shareplate.ui.NGO

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NGOProfileScreen(
    username: String = "NGO User",
    email: String = "ngo@email.com",
    phone: String = "",
    organisation: String = "",
    address: String = "",
    onEditProfile: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("My Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "NGO Profile",
            modifier = Modifier.size(105.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(username.ifBlank { "NGO User" }, fontSize = 21.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("NGO", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(24.dp))

        ProfileRow("Name", username)
        ProfileRow("Email", email)
        ProfileRow("Phone Number", phone.ifBlank { "—" })
        ProfileRow("Organisation", organisation.ifBlank { "—" })
        ProfileRow("Address", address.ifBlank { "—" })
        ProfileRow("Role", "NGO")

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onEditProfile,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Edit Profile", color = Color.White)
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Log Out")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp)) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NGOProfileScreenPreview() {
    NGOProfileScreen(username = "Hope Orphanage", organisation = "Hope Orphanage", address = "Georgetown, Penang")
}