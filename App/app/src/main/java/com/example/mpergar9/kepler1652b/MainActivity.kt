package com.example.mpergar9.kepler1652b

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import com.example.mpergar9.kepler1652b.model.Campaign
import com.example.mpergar9.kepler1652b.model.Character
import com.example.mpergar9.kepler1652b.ui.screens.*
import com.example.mpergar9.kepler1652b.ui.screens.master.MasterCharacterDetailScreen
import com.example.mpergar9.kepler1652b.ui.theme.*

sealed class Screen {
    object CampaignList : Screen()
    data class MasterDashboard(val campaign: Campaign) : Screen()
    data class MasterCharacterDetail(val campaign: Campaign, val character: Character) : Screen()
    data class CharacterList(val campaign: Campaign) : Screen()
    data class PlayerSheet(val campaign: Campaign, val character: Character) : Screen()
    data class CharacterCreation(val campaign: Campaign) : Screen()
}

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
                var currentScreen by remember { mutableStateOf<Screen>(Screen.CampaignList) }
                var showUsernameDialog by remember { mutableStateOf(isFirstLaunch) }
                var inputName by remember { mutableStateOf("") }

                BackHandler(enabled = currentScreen !is Screen.CampaignList) {
                    currentScreen = when (val s = currentScreen) {
                        is Screen.MasterDashboard -> Screen.CharacterList(s.campaign)
                        is Screen.MasterCharacterDetail -> Screen.MasterDashboard(s.campaign)
                        is Screen.CharacterList -> Screen.CampaignList
                        is Screen.CharacterCreation -> Screen.CampaignList
                        is Screen.PlayerSheet -> Screen.CharacterList(s.campaign)
                        else -> Screen.CampaignList
                    }
                }

                if (showUsernameDialog) {
                    AlertDialog(
                        onDismissRequest = { /* No dismiss without name */ },
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

                when (val screen = currentScreen) {
                    is Screen.CampaignList -> {
                        CampaignListScreen(
                            onSelectCampaignAsMaster = { currentScreen = Screen.MasterDashboard(it) },
                            onSelectCampaignAsPlayer = { currentScreen = Screen.CharacterList(it) },
                            onCreateCharacterForCampaign = { currentScreen = Screen.CharacterCreation(it) }
                        )
                    }
                    is Screen.MasterDashboard -> {
                        MasterDashboardScreen(
                            campaign = screen.campaign,
                            onSelectCharacterForDetail = { char ->
                                currentScreen = Screen.MasterCharacterDetail(screen.campaign, char)
                            },
                            onBack = { currentScreen = Screen.CharacterList(screen.campaign) }
                        )
                    }
                    is Screen.MasterCharacterDetail -> {
                        MasterCharacterDetailScreen(
                            campaign = screen.campaign,
                            character = screen.character,
                            onBack = { currentScreen = Screen.MasterDashboard(screen.campaign) }
                        )
                    }
                    is Screen.CharacterList -> {
                        CharacterListScreen(
                            campaign = screen.campaign,
                            onSelectCharacter = { selectedChar ->
                                currentScreen = Screen.PlayerSheet(screen.campaign, selectedChar)
                            },
                            onCreateNewCharacter = {
                                currentScreen = Screen.CharacterCreation(screen.campaign)
                            },
                            onOpenMasterDashboard = {
                                currentScreen = Screen.MasterDashboard(screen.campaign)
                            },
                            onBack = { currentScreen = Screen.CampaignList }
                        )
                    }
                    is Screen.PlayerSheet -> {
                        PlayerCharacterSheetScreen(
                            campaign = screen.campaign,
                            selectedCharacter = screen.character,
                            onBack = { currentScreen = Screen.CharacterList(screen.campaign) }
                        )
                    }
                    is Screen.CharacterCreation -> {
                        CharacterCreationScreen(
                            campaign = screen.campaign,
                            onCharacterCreated = { currentScreen = Screen.CharacterList(screen.campaign) },
                            onBack = { currentScreen = Screen.CampaignList }
                        )
                    }
                }
            }
        }
    }
}
