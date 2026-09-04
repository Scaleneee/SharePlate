package com.example.shareplate.ui.NGO

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NGOEditProfileScreen(
    username: String = "NGO User",
    email: String = "ngo@email.com",
    phone: String = "",
    organisation: String = "",
    address: String = "",
    onBackClick: () -> Unit = {},
    onSaveClick: (name: String, phone: String, organisation: String, address: String) -> Unit = { _, _, _, _ -> }
) {
    var name by remember { mutableStateOf(username) }
    var phoneText by remember { mutableStateOf(phone) }
    var orgText by remember { mutableStateOf(organisation) }
    var addressText by remember { mutableStateOf(address) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back"
            )
            Text("Edit Profile", fontSize = 21.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        EditField("Name", name, onValueChange = { name = it })
        Spacer(modifier = Modifier.height(14.dp))
        EditField("Email", email, enabled = false)
        Spacer(modifier = Modifier.height(14.dp))
        EditField("Phone Number", phoneText, onValueChange = { phoneText = it })
        Spacer(modifier = Modifier.height(14.dp))
        EditField("Organisation", orgText, onValueChange = { orgText = it })
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = addressText,
            onValueChange = { addressText = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = { onSaveClick(name.trim(), phoneText.trim(), orgText.trim(), addressText.trim()) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Save Changes", color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun EditField(label: String, value: String, onValueChange: (String) -> Unit = {}, enabled: Boolean = true) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NGOEditProfileScreenPreview() {
    NGOEditProfileScreen(username = "Hope Orphanage", organisation = "Hope Orphanage", address = "Georgetown, Penang")
}