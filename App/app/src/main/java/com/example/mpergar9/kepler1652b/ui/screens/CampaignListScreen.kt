/**
 * ====================================================================================
 * CampaignListScreen.kt
 * ====================================================================================
 * Pantalla principal del listado de campañas con botón unificado "+ Campaña" para
 * crear o unirse en una ventana de tamaño fijo, y tarjetas clicables de campaña.
 * ====================================================================================
 */

package com.example.mpergar9.kepler1652b.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpergar9.kepler1652b.data.CampaignRepository
import com.example.mpergar9.kepler1652b.model.Campaign
import com.example.mpergar9.kepler1652b.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignListScreen(
    onSelectCampaignAsMaster: (Campaign) -> Unit,
    onSelectCampaignAsPlayer: (Campaign) -> Unit,
    onCreateCharacterForCampaign: (Campaign) -> Unit
) {
    val campaigns by CampaignRepository.campaigns.collectAsState()
    val currentUserProfile by CampaignRepository.currentUserProfile.collectAsState()

    var showActionDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var campaignToDelete by remember { mutableStateOf<Campaign?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Todas, 1: Máster, 2: Jugador

    val filteredCampaigns = when (selectedTab) {
        1 -> campaigns.filter { it.isCurrentUserMaster }
        2 -> campaigns.filter { !it.isCurrentUserMaster }
        else -> campaigns
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "KEPLER-1652b",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyanCurvature,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Terminal de Campañas",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                },
                actions = {
                    Surface(
                        onClick = { showProfileDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        color = HullGray,
                        border = BorderStroke(1.dp, PanelBorder),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("👤", fontSize = 12.sp)
                            Text(
                                text = currentUserProfile.displayName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StatusBarColor)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showActionDialog = true },
                containerColor = CyanCurvature,
                contentColor = VoidBlack
            ) {
                Text("+ Campaña", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = VoidBlack,
        modifier = Modifier.statusBarsPadding()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Filtros de Campañas: Todas / Máster / Jugador
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Todas") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanCurvature,
                        selectedLabelColor = VoidBlack,
                        containerColor = DeepSpaceBlue,
                        labelColor = TextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Máster") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WarningAmber,
                        selectedLabelColor = VoidBlack,
                        containerColor = DeepSpaceBlue,
                        labelColor = TextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("Jugador") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonCyan,
                        selectedLabelColor = VoidBlack,
                        containerColor = DeepSpaceBlue,
                        labelColor = TextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredCampaigns.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay campañas disponibles en esta sección.", color = TextMuted, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredCampaigns) { campaign ->
                        CampaignCard(
                            campaign = campaign,
                            onOpenMaster = { onSelectCampaignAsMaster(campaign) },
                            onOpenPlayer = { onSelectCampaignAsPlayer(campaign) },
                            onCreateChar = { onCreateCharacterForCampaign(campaign) },
                            onDeleteCampaign = { campaignToDelete = campaign }
                        )
                    }
                }
            }
        }
    }

    if (showActionDialog) {
        CampaignActionDialog(
            defaultMasterName = currentUserProfile.displayName,
            onDismiss = { showActionDialog = false },
            onCreateConfirm = { name, desc, master ->
                CampaignRepository.createCampaign(name, desc, master)
                showActionDialog = false
            },
            onJoinSuccess = { campaign ->
                showActionDialog = false
                onCreateCharacterForCampaign(campaign)
            }
        )
    }

    if (showProfileDialog) {
        EditProfileDialog(
            currentName = currentUserProfile.displayName,
            profileId = currentUserProfile.id,
            onDismiss = { showProfileDialog = false },
            onSave = { newName ->
                CampaignRepository.updateProfileName(newName)
                showProfileDialog = false
            }
        )
    }

    campaignToDelete?.let { campaign ->
        AlertDialog(
            onDismissRequest = { campaignToDelete = null },
            title = { Text("¿Eliminar Campaña?", fontWeight = FontWeight.Bold, color = DangerRed) },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas eliminar permanentemente la campaña '${campaign.name}'?\n\nSe eliminarán todos los personajes, registros y estados asociados a ella.",
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        CampaignRepository.deleteCampaign(campaign.id)
                        campaignToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = TextPrimary)
                ) {
                    Text("Eliminar Campaña", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { campaignToDelete = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = DeepSpaceBlue
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CampaignActionDialog(
    defaultMasterName: String = "Director de Juego",
    onDismiss: () -> Unit,
    onCreateConfirm: (name: String, desc: String, master: String) -> Unit,
    onJoinSuccess: (Campaign) -> Unit
) {
    var mode by remember { mutableIntStateOf(0) } // 0: Crear, 1: Unirse

    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var master by remember { mutableStateOf(defaultMasterName) }

    var inviteCode by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("⚙️ Gestión de Campaña", fontWeight = FontWeight.Bold, color = CyanCurvature)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = mode == 0,
                        onClick = { mode = 0 },
                        label = { Text("Crear Campaña", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyanCurvature, selectedLabelColor = VoidBlack)
                    )
                    FilterChip(
                        selected = mode == 1,
                        onClick = { mode = 1 },
                        label = { Text("Unirse con Código", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonCyan, selectedLabelColor = VoidBlack)
                    )
                }
            }
        },
        text = {
            // Contenedor de tamaño fijo para evitar saltos al cambiar entre Crear y Unirse
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(vertical = 4.dp)
            ) {
                if (mode == 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nombre de la Campaña") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                        )
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = { Text("Resumen / Descripción") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                        )
                        OutlinedTextField(
                            value = master,
                            onValueChange = { master = it },
                            label = { Text("Nombre del Máster") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                        Text("Introduce el código de invitación proporcionado por el Máster:", fontSize = 12.sp, color = TextSecondary)
                        OutlinedTextField(
                            value = inviteCode,
                            onValueChange = {
                                inviteCode = it.uppercase()
                                errorMsg = null
                            },
                            label = { Text("Código (Ej: KEPLER1652)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan)
                        )
                        errorMsg?.let {
                            Text(it, color = DangerRed, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = TextSecondary, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = {
                        if (mode == 0) {
                            if (name.isNotBlank()) onCreateConfirm(name, desc, master)
                        } else {
                            val joined = CampaignRepository.joinCampaignByCode(inviteCode.trim())
                            if (joined != null) {
                                onJoinSuccess(joined)
                            } else {
                                errorMsg = "Código no encontrado."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (mode == 0) CyanCurvature else NeonCyan, contentColor = VoidBlack)
                ) {
                    Text(if (mode == 0) "Crear Campaña" else "Unirse", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {},
        containerColor = DeepSpaceBlue
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CampaignCard(
    campaign: Campaign,
    onOpenMaster: () -> Unit,
    onOpenPlayer: () -> Unit,
    onCreateChar: () -> Unit,
    onDeleteCampaign: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PanelBorder, RoundedCornerShape(12.dp))
            .clickable { onOpenPlayer() },
        colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Banner Sci-Fi con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                DeepSpaceBlue,
                                HullGray,
                                PanelBorder
                            )
                        )
                    )
                    .padding(12.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = if (campaign.isCurrentUserMaster) "👑 CAMPAÑA PROPIA (MÁSTER)" else "🚀 CAMPAÑA UNIDA",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (campaign.isCurrentUserMaster) WarningAmber else NeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = campaign.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Surface(
                        color = VoidBlack.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .clickable {
                                clipboardManager.setText(AnnotatedString(campaign.inviteCode))
                                Toast.makeText(context, "¡Código '${campaign.inviteCode}' copiado al portapapeles!", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("📋", fontSize = 10.sp)
                            Text(
                                text = "Cód: ${campaign.inviteCode}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = WarningAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = campaign.description,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Director: ${campaign.masterName}",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "Toca para ver tripulación →",
                        fontSize = 11.sp,
                        color = CyanCurvature,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    profileId: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var nameInput by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Perfil de Jugador Local", fontWeight = FontWeight.Bold, color = NeonCyan) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Este nombre se asignará automáticamente a cualquier personaje que crees, y servirá para que el Director de Juego te identifique en la tripulación.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Tu Nombre / Alias de Jugador") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                )
                Surface(
                    color = VoidBlack.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Identificador de Dispositivo: $profileId",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameInput.isNotBlank()) onSave(nameInput)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanCurvature, contentColor = VoidBlack)
            ) {
                Text("Guardar Perfil", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextSecondary) }
        },
        containerColor = DeepSpaceBlue
    )
}
