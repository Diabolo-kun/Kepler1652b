/**
 * ====================================================================================
 * MasterGlobalEnvironmentPanel.kt
 * ====================================================================================
 * Panel de control ambiental global de la nave para alterar radiación, toxicidad,
 * soporte vital y cordura de toda la tripulación simultáneamente.
 * ====================================================================================
 */

package com.example.mpergar9.kepler1652b.ui.screens.master

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpergar9.kepler1652b.data.CampaignRepository
import com.example.mpergar9.kepler1652b.model.Campaign
import com.example.mpergar9.kepler1652b.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MasterGlobalEnvironmentPanel(
    campaign: Campaign,
    totalCrewSize: Int
) {
    var globalRadDelta by remember { mutableFloatStateOf(0f) }
    var globalToxDelta by remember { mutableFloatStateOf(0f) }
    var globalSoporteDelta by remember { mutableFloatStateOf(0f) }
    var globalSanityDelta by remember { mutableFloatStateOf(0f) }

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
                    text = "Consola de emergencias • Afecta a toda la tripulación ($totalCrewSize personas)",
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

@Composable
fun GlobalDeltaAdjustmentBar(
    title: String,
    delta: Float,
    color: Color,
    onDeltaChange: (Float) -> Unit,
    onApply: () -> Unit
) {
    val deltaInt = delta.toInt()
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            val textDelta = when {
                deltaInt > 0 -> "+$deltaInt uds (Subir)"
                deltaInt < 0 -> "$deltaInt uds (Bajar)"
                else -> "0 uds (Equilibrio)"
            }
            Text(
                text = textDelta,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (deltaInt != 0) color else TextMuted
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilledTonalIconButton(
                onClick = { onDeltaChange((delta - 1f).coerceAtLeast(-30f)) },
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = HullGray)
            ) {
                Text("-1", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Slider(
                value = delta,
                onValueChange = { onDeltaChange(kotlin.math.round(it)) },
                valueRange = -30f..30f,
                steps = 59,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
            )

            FilledTonalIconButton(
                onClick = { onDeltaChange((delta + 1f).coerceAtMost(30f)) },
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = HullGray)
            ) {
                Text("+1", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Button(
                onClick = onApply,
                enabled = delta.toInt() != 0,
                colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.85f), contentColor = VoidBlack),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Aplicar", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
