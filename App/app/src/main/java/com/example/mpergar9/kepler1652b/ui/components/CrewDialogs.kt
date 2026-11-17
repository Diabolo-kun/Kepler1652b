package com.example.mpergar9.kepler1652b.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpergar9.kepler1652b.data.CampaignRepository
import com.example.mpergar9.kepler1652b.model.Campaign
import com.example.mpergar9.kepler1652b.model.Character
import com.example.mpergar9.kepler1652b.ui.theme.*

/**
 * Diálogo modal reutilizable para mostrar la tripulación conectada a una campaña.
 * Si el usuario activo es el Máster, permite expulsar/eliminar a un usuario invitado mediante el botón de papelera.
 */
@Composable
fun PlayerListDialog(
    campaign: Campaign,
    characters: List<Character>,
    onDismiss: () -> Unit
) {
    var userToKick by remember { mutableStateOf<String?>(null) }
    val joinedUsers = campaign.joinedPlayerIds.filter { it.isNotBlank() }.ifEmpty {
        characters.map { it.playerId }.filter { it.isNotBlank() }.distinct().ifEmpty {
            listOf(campaign.masterId).filter { it.isNotBlank() }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "👥 Colonos Conectados",
                fontWeight = FontWeight.Bold,
                color = CyanCurvature
            )
        },
        text = {
            if (joinedUsers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay usuarios registrados en esta campaña.", color = TextMuted, fontSize = 12.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.height(220.dp)
                ) {
                    items(joinedUsers, key = { it }) { uid ->
                        val assignedChar = characters.find { it.playerId == uid }
                        val userName = assignedChar?.playerName?.ifBlank { null } 
                            ?: if (uid == campaign.masterId) campaign.masterName else "Colono ($uid)"
                        val isMasterUser = uid == campaign.masterId

                        Surface(
                            color = VoidBlack,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = userName,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontSize = 13.sp
                                        )
                                        if (isMasterUser) {
                                            Text("(Máster)", fontSize = 10.sp, color = WarningAmber, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Text(
                                        text = if (assignedChar != null) "PJ: ${assignedChar.characterName} (${assignedChar.characterClass})" else "Sin personaje asignado",
                                        color = if (assignedChar != null) CyanCurvature else TextMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                if (campaign.isCurrentUserMaster && !isMasterUser) {
                                    IconButton(
                                        onClick = { userToKick = uid },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("🗑️", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyanCurvature, contentColor = VoidBlack)
            ) {
                Text("Cerrar", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DeepSpaceBlue
    )

    userToKick?.let { uid ->
        val charToKick = characters.find { it.playerId == uid }
        DeleteConfirmationDialog(
            title = "¿Expulsar Usuario?",
            message = "¿Estás seguro de expulsar a este usuario de la campaña?",
            confirmText = "Expulsar",
            cancelText = "Cancelar",
            onConfirm = {
                CampaignRepository.removePlayerFromCampaign(campaign.id, uid, charToKick?.id)
                userToKick = null
            },
            onDismiss = { userToKick = null }
        )
    }
}

/**
 * Diálogo genérico de confirmación de eliminación con botón de Cancelar a la izquierda y Confirmar a la derecha.
 */
@Composable
fun DeleteConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "Eliminar",
    cancelText: String = "Cancelar",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = DangerRed
            )
        },
        text = {
            Text(
                text = message,
                color = TextSecondary
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelText, color = TextSecondary)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = TextPrimary)
            ) {
                Text(confirmText, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DeepSpaceBlue
    )
}
