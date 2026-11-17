package com.example.mpergar9.kepler1652b

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mpergar9.kepler1652b.data.CampaignRepository
import com.example.mpergar9.kepler1652b.ui.navigation.KeplerNavHost
import com.example.mpergar9.kepler1652b.ui.theme.Kepler1652bTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CampaignRepository.restoreSessionIfSignedIn()

        enableEdgeToEdge()
        setContent {
            Kepler1652bTheme {
                KeplerNavHost()
            }
        }
    }
}
