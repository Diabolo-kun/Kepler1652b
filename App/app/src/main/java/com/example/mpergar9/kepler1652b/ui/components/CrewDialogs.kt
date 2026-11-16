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
    var playerToKick by remember { mutableStateOf<Character?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "👥 Tripulación Conectada",
                fontWeight = FontWeight.Bold,
                color = CyanCurvature
            )
        },
        text = {
            if (characters.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay tripulantes conectados en esta campaña.", color = TextMuted, fontSize = 12.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.height(220.dp)
                ) {
                    items(characters) { ch ->
                        Surface(
                            color = VoidBlack,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ch.characterName,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "${ch.characterClass} • ${ch.playerName}",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(if (ch.isAlive) "💚" else "💀", fontSize = 11.sp)
                                    if (campaign.isCurrentUserMaster) {
                                        IconButton(
                                            onClick = { playerToKick = ch },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Text("🗑️", fontSize = 11.sp)
                                        }
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

    playerToKick?.let { ch ->
        DeleteConfirmationDialog(
            title = "¿Expulsar Tripulante?",
            message = "¿Estás seguro de expulsar a '${ch.characterName}' (${ch.playerName}) de la campaña?",
            confirmText = "Expulsar",
            cancelText = "Cancelar",
            onConfirm = {
                CampaignRepository.removePlayerFromCampaign(campaign.id, ch.playerId, ch.id)
                playerToKick = null
            },
            onDismiss = { playerToKick = null }
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
