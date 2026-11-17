/**
 * ====================================================================================
 * CharacterListScreen.kt
 * ====================================================================================
 * Pantalla de listado de la tripulación con botón de Máster a la izquierda del icono
 * de jugadores conectados, y sin botón de "Volver" (se usa la flecha del sistema).
 * ====================================================================================
 */

package com.example.mpergar9.kepler1652b.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpergar9.kepler1652b.data.CampaignRepository
import com.example.mpergar9.kepler1652b.model.Campaign
import com.example.mpergar9.kepler1652b.model.Character
import com.example.mpergar9.kepler1652b.ui.components.*
import com.example.mpergar9.kepler1652b.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    campaign: Campaign,
    onSelectCharacter: (Character) -> Unit,
    onCreateNewCharacter: () -> Unit,
    onOpenMasterDashboard: (() -> Unit)? = null,
    onBack: () -> Unit
) {
    val allCharacters by CampaignRepository.characters.collectAsState()
    val savedVault by CampaignRepository.savedUserCharacters.collectAsState()
    val currentUserProfile by CampaignRepository.currentUserProfile.collectAsState()
    val campaignChars = allCharacters.filter { it.campaignId == campaign.id }

    var showPlayerListDialog by remember { mutableStateOf(false) }
    var showSelectSavedCharDialog by remember { mutableStateOf(false) }

    // Personajes disponibles en el baúl personal que no están asignados a ninguna campaña activa
    val availableSavedChars = savedVault.filter { it.activeCampaignId.isNullOrBlank() }

    // Filtro: 0 = Toda la Tripulación, 1 = Mis Personajes
    var filterMode by remember { mutableIntStateOf(0) }
    val myCharacters = campaignChars.filter {
        it.playerId == currentUserProfile.id || it.playerName.equals(currentUserProfile.displayName, ignoreCase = true)
    }
    val displayedChars = if (filterMode == 1) myCharacters else campaignChars

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("TRIPULACIÓN & FICHAS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CyanCurvature)
                        Text("${campaign.name} • Cód: ${campaign.inviteCode}", fontSize = 12.sp, color = TextSecondary)
                    }
                },
                navigationIcon = {},
                actions = {
                    if (campaign.isCurrentUserMaster && onOpenMasterDashboard != null) {
                        Button(
                            onClick = onOpenMasterDashboard,
                            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.padding(end = 4.dp).height(32.dp)
                        ) {
                            Text("👑 Máster", color = VoidBlack, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    IconButton(onClick = { showPlayerListDialog = true }) {
                        Text("👥", fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StatusBarColor)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (availableSavedChars.isNotEmpty()) {
                        showSelectSavedCharDialog = true
                    } else {
                        onCreateNewCharacter()
                    }
                },
                containerColor = CyanCurvature,
                contentColor = VoidBlack
            ) {
                Text("+ Nuevo Personaje", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = VoidBlack,
        modifier = Modifier.statusBarsPadding()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pestañas / Filtro de visualización
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterMode == 0,
                    onClick = { filterMode = 0 },
                    label = { Text("🌐 Todos (${campaignChars.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanCurvature,
                        selectedLabelColor = VoidBlack
                    )
                )
                FilterChip(
                    selected = filterMode == 1,
                    onClick = { filterMode = 1 },
                    label = { Text("👤 Mis PJs (${myCharacters.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanCurvature,
                        selectedLabelColor = VoidBlack
                    )
                )
            }

            Text(
                text = if (filterMode == 1) "TUS PERSONAJES ASIGNADOS (${currentUserProfile.displayName})" else "SELECCIONA UN PERSONAJE PARA ABRIR SU FICHA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace
            )

            if (displayedChars.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (filterMode == 1) "No tienes ningún personaje asignado en esta campaña." else "No hay personajes registrados todavía.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedChars, key = { it.id }) { char ->
                        val isMine = char.playerId == currentUserProfile.id || char.playerName.equals(currentUserProfile.displayName, ignoreCase = true)
                        CharacterRowCard(
                            character = char,
                            isMine = isMine,
                            onClick = { onSelectCharacter(char) }
                        )
                    }
                }
            }
        }
    }

    if (showPlayerListDialog) {
        PlayerListDialog(
            campaign = campaign,
            characters = campaignChars,
            onDismiss = { showPlayerListDialog = false }
        )
    }

    if (showSelectSavedCharDialog) {
        AlertDialog(
            onDismissRequest = { showSelectSavedCharDialog = false },
            title = { Text("Asignar Personaje de tu Baúl", fontWeight = FontWeight.Bold, color = CyanCurvature, fontSize = 15.sp) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Selecciona un personaje disponible de tu baúl para unirte a '${campaign.name}' o crea uno nuevo:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(availableSavedChars, key = { it.id }) { savedChar ->
                            OutlinedButton(
                                onClick = {
                                    showSelectSavedCharDialog = false
                                    CampaignRepository.assignSavedCharacterToCampaign(campaign.id, savedChar)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PanelBorder)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(savedChar.characterName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Nivel ${savedChar.level} • ${savedChar.characterClass}", fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = PanelBorder.copy(alpha = 0.5f))

                    Button(
                        onClick = {
                            showSelectSavedCharDialog = false
                            onCreateNewCharacter()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanCurvature, contentColor = VoidBlack)
                    ) {
                        Text("+ Crear Nuevo Personaje desde Cero", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSelectSavedCharDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = DeepSpaceBlue
        )
    }
}

@Composable
fun CharacterRowCard(
    character: Character,
    isMine: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isMine) NeonCyan.copy(alpha = 0.7f) else PanelBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = character.characterName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        if (isMine) {
                            UserCharacterBadge()
                        }
                    }
                    Text(
                        text = "${character.characterClass} • Jugador: ${character.playerName} (Nv.${character.level})",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                AliveStatusBadge(isAlive = character.isAlive)
            }

            HorizontalDivider(color = PanelBorder.copy(alpha = 0.4f))

            VitalStatsRow(
                radiationLevel = character.radiationLevel,
                toxicityLevel = character.toxicityPoison,
                soporteVitalLevel = character.soporteVitalLevel,
                sanityLevel = character.currentSanity
            )

            if (character.statusEffects.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    character.statusEffects.forEach { effect ->
                        StatusEffectChip(effectName = effect)
                    }
                }
            }
        }
    }
}
