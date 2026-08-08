package com.example.shareplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.shareplate.ui.buyer.home.BuyerHomeScreen
import com.example.shareplate.ui.seller.home.SellerHomeScreen
import com.example.shareplate.ui.theme.SharePlateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SharePlateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    BuyerHomeScreen()
                }
            }
        }
    }
}