package com.example.mpergar9.kepler1652b.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpergar9.kepler1652b.data.CampaignRepository
import com.example.mpergar9.kepler1652b.data.KeplerGameData
import com.example.mpergar9.kepler1652b.model.Campaign
import com.example.mpergar9.kepler1652b.model.Character
import com.example.mpergar9.kepler1652b.model.SpecialStats
import com.example.mpergar9.kepler1652b.ui.components.*
import com.example.mpergar9.kepler1652b.ui.screens.master.*
import com.example.mpergar9.kepler1652b.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MasterDashboardScreen(
    campaign: Campaign,
    onSelectCharacterForDetail: (Character) -> Unit,
    onBack: () -> Unit,
    onDeleteCampaign: () -> Unit = onBack
) {
    val allCharacters by CampaignRepository.characters.collectAsState()
    val campaignChars = allCharacters.filter { it.campaignId == campaign.id }
    val sortedChars = campaignChars.sortedBy { it.characterName.lowercase() }

    var characterToDelete by remember { mutableStateOf<Character?>(null) }
    var showDeleteCampaignDialog by remember { mutableStateOf(false) }

    // Estados para control global de la nave (Equilibrio en 0)
    var globalRadDelta by remember { mutableFloatStateOf(0f) }
    var globalToxDelta by remember { mutableFloatStateOf(0f) }
    var globalSoporteDelta by remember { mutableFloatStateOf(0f) }
    var globalSanityDelta by remember { mutableFloatStateOf(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("PANEL DEL MÁSTER / DIRECTOR DE JUEGO", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                        Text("${campaign.name} • Cód. Invitación: ${campaign.inviteCode}", fontSize = 12.sp, color = TextSecondary)
                    }
                },
                navigationIcon = {},
                actions = {
                    var showPlayerList by remember { mutableStateOf(false) }
                    IconButton(onClick = { showPlayerList = true }) {
                        Text("👥", fontSize = 16.sp)
                    }
                    if (showPlayerList) {
                        PlayerListDialog(campaign = campaign, characters = campaignChars, onDismiss = { showPlayerList = false })
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StatusBarColor)
            )
        },
        containerColor = VoidBlack,
        modifier = Modifier.statusBarsPadding()
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // 1. PANEL AMBIENTAL GLOBAL DE LA NAVE (AFECTA A TODA LA SALA)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, WarningAmber.copy(alpha = 0.7f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "🛸 CONTROL AMBIENTAL GLOBAL DE LA NAVE",
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Consola de emergencias • Afecta a toda la tripulación (${campaignChars.size} personas)",
                                fontSize = 12.sp,
                                color = NeonCyan,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Text(
                            text = "Variables en equilibrio (0). Ajusta con [-1] y [+1] y pulsa Aplicar para subir o bajar unidades a todos los tripulantes:",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        GlobalDeltaAdjustmentBar(
                            title = "☢️ Radiación Colectiva",
                            delta = globalRadDelta,
                            color = WarningAmber,
                            onDeltaChange = { globalRadDelta = it },
                            onApply = {
                                CampaignRepository.applyGlobalRadiationDelta(campaign.id, globalRadDelta.toInt())
                                globalRadDelta = 0f
                            }
                        )

                        GlobalDeltaAdjustmentBar(
                            title = "☣️ Toxicidad",
                            delta = globalToxDelta,
                            color = DangerRed,
                            onDeltaChange = { globalToxDelta = it },
                            onApply = {
                                CampaignRepository.applyGlobalToxicityDelta(campaign.id, globalToxDelta.toInt())
                                globalToxDelta = 0f
                            }
                        )

                        GlobalDeltaAdjustmentBar(
                            title = "🛡️ Soporte Vital de la Nave",
                            delta = globalSoporteDelta,
                            color = CyanCurvature,
                            onDeltaChange = { globalSoporteDelta = it },
                            onApply = {
                                CampaignRepository.applyGlobalSoporteVitalDelta(campaign.id, globalSoporteDelta.toInt())
                                globalSoporteDelta = 0f
                            }
                        )

                        GlobalDeltaAdjustmentBar(
                            title = "🧠 Cordura Colectiva",
                            delta = globalSanityDelta,
                            color = PlasmaPurple,
                            onDeltaChange = { globalSanityDelta = it },
                            onApply = {
                                CampaignRepository.applyGlobalSanityDelta(campaign.id, globalSanityDelta.toInt())
                                globalSanityDelta = 0f
                            }
                        )

                        HorizontalDivider(color = PanelBorder.copy(alpha = 0.5f))

                        Text("Activar / Desactivar Estados de Alarma a toda la tripulación:", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        val globalAlarmEffects = listOf("Asfixia", "Pánico", "Estrés Criogénico", "Fuga Tóxica", "Descompresión")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            globalAlarmEffects.forEach { effect ->
                                Button(
                                    onClick = {
                                        CampaignRepository.applyGlobalStatusEffect(campaign.id, effect, applyToAll = true)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = HullGray),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("+ $effect", fontSize = 11.sp, color = TextPrimary)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    CampaignRepository.clearAllGlobalStatusEffects(campaign.id)
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RadiationGreen),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Limpiar Estados a Todos", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // 2. LISTADO ALFABÉTICO DE PERSONAJES
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRIPULACIÓN REGISTRADA (${sortedChars.size}) • ORDEN ALFABÉTICO",
                        fontWeight = FontWeight.Bold,
                        color = WarningAmber,
                        fontSize = 13.sp
                    )
                }
            }

            if (sortedChars.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay ningún personaje en esta campaña todavía.", color = TextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                items(sortedChars) { char ->
                    MasterCharacterSummaryCard(
                        character = char,
                        onClick = { onSelectCharacterForDetail(char) }
                    )
                }
            }

            // 3. ELIMINAR CAMPAÑA (SOLO SI ES MASTER)
            if (campaign.isCurrentUserMaster) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Eliminar campaña",
                            color = DangerRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { showDeleteCampaignDialog = true }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }

    characterToDelete?.let { campaignChar ->
        DeleteConfirmationDialog(
            title = "¿Eliminar Personaje?",
            message = "¿Estás seguro de eliminar a '${campaignChar.characterName}' de la campaña?",
            confirmText = "Eliminar",
            cancelText = "Cancelar",
            onConfirm = {
                CampaignRepository.deleteCharacter(campaignChar.id)
                characterToDelete = null
            },
            onDismiss = { characterToDelete = null }
        )
    }

    if (showDeleteCampaignDialog) {
        DeleteConfirmationDialog(
            title = "¿Eliminar Campaña?",
            message = "¿Seguro que quieres eliminarla?",
            confirmText = "Sí",
            cancelText = "Cancelar",
            onConfirm = {
                showDeleteCampaignDialog = false
                CampaignRepository.deleteCampaign(campaign.id)
                onDeleteCampaign()
            },
            onDismiss = { showDeleteCampaignDialog = false }
        )
    }
}

@Composable
fun MasterCharacterSummaryCard(
    character: Character,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (character.isAlive) PanelBorder else DangerRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            color = if (character.isAlive) TextPrimary else TextMuted
                        )
                        AliveStatusBadge(isAlive = character.isAlive)
                    }
                    Text(
                        text = "${character.characterClass} • Jugador: ${character.playerName} (Nv.${character.level})",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            // Barra de vida
            HealthProgressBar(currentHp = character.currentHp, maxHp = character.stats.maxHp)

            // 4 variables ambientales/vitales
            VitalStatsRow(
                radiationLevel = character.radiationLevel,
                toxicityLevel = character.toxicityPoison,
                soporteVitalLevel = character.soporteVitalLevel,
                sanityLevel = character.currentSanity
            )
        }
    }
}
