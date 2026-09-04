package com.example.shareplate

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.shareplate.navigation.AppNavGraph
import com.example.shareplate.navigation.AppRoutes
import com.example.shareplate.data.supabase.SupabaseClient
import com.example.shareplate.ui.theme.SharePlateTheme
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.handleDeeplinks
import com.example.shareplate.data.remote.SupabaseProvider

class MainActivity : ComponentActivity() {

    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SharePlateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val controller = rememberNavController()
                    navController = controller
                    AppNavGraph(navController = controller)
                }
            }
        }
        window.decorView.post { handleDeepLink(intent) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    @OptIn(SupabaseInternal::class)
    private fun handleDeepLink(intent: Intent?) {
        val data = intent?.data
        if (data?.scheme == "shareplate" && data.host == "reset") {
            SupabaseProvider.client.handleDeeplinks(intent, onSessionSuccess = {
                navController?.navigate(AppRoutes.NEW_PASSWORD)
            })
        }
    }
}
