package com.example.mpergar9.kepler1652b

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpergar9.kepler1652b.data.CampaignRepository
import com.example.mpergar9.kepler1652b.ui.navigation.KeplerNavHost
import com.example.mpergar9.kepler1652b.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val prefs = getSharedPreferences("kepler_prefs", MODE_PRIVATE)
        val savedName = prefs.getString("user_display_name", null)
        val isFirstLaunch = savedName.isNullOrBlank() || savedName == "Explorador"
        
        if (!savedName.isNullOrBlank() && savedName != "Explorador") {
            CampaignRepository.updateProfileName(savedName)
        }

        enableEdgeToEdge()
        setContent {
            Kepler1652bTheme {
                var showUsernameDialog by remember { mutableStateOf(isFirstLaunch) }
                var inputName by remember { mutableStateOf("") }

                if (showUsernameDialog) {
                    AlertDialog(
                        onDismissRequest = { /* No dismiss sin nombre */ },
                        title = { Text("IDENTIFICACIÓN DE COLONO", color = CyanCurvature, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Bienvenido a bordo de la misión Kepler-1652b. Introduce tu nombre de usuario o nombre de colono:",
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                )
                                OutlinedTextField(
                                    value = inputName,
                                    onValueChange = { inputName = it },
                                    placeholder = { Text("Ej: Cmdte. Elena Vance", color = TextMuted) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyanCurvature,
                                        unfocusedBorderColor = PanelBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        cursorColor = CyanCurvature
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val finalName = if (inputName.isBlank()) "Explorador" else inputName.trim()
                                    CampaignRepository.updateProfileName(finalName)
                                    prefs.edit().putString("user_display_name", finalName).apply()
                                    showUsernameDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanCurvature)
                            ) {
                                Text("Comenzar Viaje", color = VoidBlack, fontWeight = FontWeight.Bold)
                            }
                        },
                        containerColor = DeepSpaceBlue
                    )
                }

                KeplerNavHost()
            }
        }
    }
}
