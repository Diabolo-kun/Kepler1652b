package com.example.mpergar9.kepler1652b.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpergar9.kepler1652b.model.Campaign
import com.example.mpergar9.kepler1652b.model.Character
import com.example.mpergar9.kepler1652b.ui.theme.*

/**
 * Diálogo modal reutilizable para mostrar la tripulación conectada a una campaña.
 */
@Composable
fun PlayerListDialog(
    campaign: Campaign,
    characters: List<Character>,
    onDismiss: () -> Unit
) {
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.height(200.dp)
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
                            Column {
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
                            Text(if (ch.isAlive) "💚" else "💀", fontSize = 11.sp)
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
