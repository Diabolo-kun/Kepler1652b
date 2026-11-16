/**
 * ====================================================================================
 * MasterCharacterDetailDialog.kt
 * ====================================================================================
 * Diálogo de edición total del Máster sobre un personaje específico de la tripulación,
 * permitiendo modificar Vitals, Nivel, Atributos SPECIAL, Habilidades, Perks e Inventario
 * (con soporte para tamaños y bonificadores/penalizadores de estadísticas).
 * ====================================================================================
 */

package com.example.mpergar9.kepler1652b.ui.screens.master

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.mpergar9.kepler1652b.data.KeplerGameData
import com.example.mpergar9.kepler1652b.model.Character
import com.example.mpergar9.kepler1652b.ui.theme.*

@Composable
fun MasterCharacterDetailDialog(
    character: Character,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Vitals & Nivel, 1: SPECIAL, 2: Habilidades, 3: Perks, 4: Inventario

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("⚙️ EDICIÓN MÁSTER: ${character.characterName}", color = WarningAmber, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Jugador: ${character.playerName} • ${character.characterClass}", fontSize = 12.sp, color = TextSecondary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth().height(420.dp)) {
                ScrollableTabRow(selectedTabIndex = selectedTab, containerColor = HullGray) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Vitals & Nivel") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("SPECIAL") })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Habilidades") })
                    Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Perks") })
                    Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text("Inventario") })
                }

                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    when (selectedTab) {
                        0 -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                                item {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("Estado Vital:", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                                        FilterChip(
                                            selected = character.isAlive,
                                            onClick = { CampaignRepository.toggleCharacterLifeStatus(character.id) },
                                            label = { Text(if (character.isAlive) "💚 VIVO" else "💀 FALLECIDO", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = RadiationGreen.copy(alpha = 0.2f),
                                                selectedLabelColor = RadiationGreen,
                                                containerColor = DangerRed.copy(alpha = 0.3f),
                                                labelColor = DangerRed
                                            )
                                        )
                                    }
                                }
                                item {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("Nivel (${character.level})", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            FilledTonalButton(onClick = { CampaignRepository.changeCharacterLevel(character.id, -1) }, enabled = character.level > 1) { Text("-1 Nv") }
                                            FilledTonalButton(onClick = { CampaignRepository.levelUpCharacter(character.id) }, colors = ButtonDefaults.filledTonalButtonColors(containerColor = WarningAmber, contentColor = VoidBlack)) { Text("+1 Nv") }
                                        }
                                    }
                                }
                                item {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("Puntos de Vida (${character.currentHp}/${character.stats.maxHp}):", fontSize = 12.sp, color = TextSecondary)
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            FilledTonalButton(onClick = { CampaignRepository.updateCharacterHp(character.id, character.currentHp - 5) }) { Text("-5") }
                                            FilledTonalButton(onClick = { CampaignRepository.updateCharacterHp(character.id, character.currentHp - 1) }) { Text("-1") }
                                            FilledTonalButton(onClick = { CampaignRepository.updateCharacterHp(character.id, character.currentHp + 1) }) { Text("+1") }
                                            FilledTonalButton(onClick = { CampaignRepository.updateCharacterHp(character.id, character.currentHp + 5) }) { Text("+5") }
                                        }
                                    }
                                }
                                item {
                                    IndividualVitalsControlRow(
                                        title = "☢️ Radiación",
                                        currentVal = character.radiationLevel,
                                        color = WarningAmber,
                                        onDelta = { CampaignRepository.updateCharacterRadiation(character.id, character.radiationLevel + it) },
                                        onSliderChange = { CampaignRepository.updateCharacterRadiation(character.id, it) }
                                    )
                                }
                                item {
                                    IndividualVitalsControlRow(
                                        title = "☣️ Toxicidad",
                                        currentVal = character.toxicityPoison,
                                        color = DangerRed,
                                        onDelta = { CampaignRepository.updateCharacterPoison(character.id, character.toxicityPoison + it) },
                                        onSliderChange = { CampaignRepository.updateCharacterPoison(character.id, it) }
                                    )
                                }
                                item {
                                    IndividualVitalsControlRow(
                                        title = "🛡️ Soporte Vital",
                                        currentVal = character.soporteVitalLevel,
                                        color = CyanCurvature,
                                        onDelta = { CampaignRepository.updateCharacterSoporteVital(character.id, character.soporteVitalLevel + it) },
                                        onSliderChange = { CampaignRepository.updateCharacterSoporteVital(character.id, it) }
                                    )
                                }
                                item {
                                    IndividualVitalsControlRow(
                                        title = "🧠 Cordura",
                                        currentVal = character.currentSanity,
                                        color = PlasmaPurple,
                                        onDelta = { CampaignRepository.updateCharacterSanity(character.id, character.currentSanity + it) },
                                        onSliderChange = { CampaignRepository.updateCharacterSanity(character.id, it) }
                                    )
                                }
                            }
                        }
                        1 -> {
                            val stats = character.stats
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                                Text("Modificar Atributos SPECIAL (1 - 10):", fontSize = 12.sp, color = TextSecondary)
                                EditableStatRow("FUE (Fuerza)", stats.fue) { v -> CampaignRepository.updateStats(character.id, stats.copy(fue = v)) }
                                EditableStatRow("PER (Percepción)", stats.per) { v -> CampaignRepository.updateStats(character.id, stats.copy(per = v)) }
                                EditableStatRow("END (Resistencia)", stats.end) { v -> CampaignRepository.updateStats(character.id, stats.copy(end = v)) }
                                EditableStatRow("CAR (Carisma)", stats.car) { v -> CampaignRepository.updateStats(character.id, stats.copy(car = v)) }
                                EditableStatRow("AGI (Agilidad)", stats.agi) { v -> CampaignRepository.updateStats(character.id, stats.copy(agi = v)) }
                                EditableStatRow("INT (Intelecto)", stats.int) { v -> CampaignRepository.updateStats(character.id, stats.copy(int = v)) }
                                EditableStatRow("SUE (Suerte)", stats.sue) { v -> CampaignRepository.updateStats(character.id, stats.copy(sue = v)) }
                            }
                        }
                        2 -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxSize()) {
                                items(KeplerGameData.ALL_SKILLS) { skill ->
                                    val cur = character.skills[skill] ?: 0
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(skill, fontSize = 11.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            FilledTonalButton(onClick = { CampaignRepository.updateSkill(character.id, skill, -5) }, contentPadding = PaddingValues(horizontal = 6.dp), modifier = Modifier.height(26.dp)) { Text("-5") }
                                            Text("$cur", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = CyanCurvature, modifier = Modifier.width(32.dp))
                                            FilledTonalButton(onClick = { CampaignRepository.updateSkill(character.id, skill, 5) }, contentPadding = PaddingValues(horizontal = 6.dp), modifier = Modifier.height(26.dp)) { Text("+5") }
                                        }
                                    }
                                }
                            }
                        }
                        3 -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxSize()) {
                                items(KeplerGameData.UNLOCKABLE_PERKS) { perk ->
                                    val hasPerk = character.unlockedPerks.contains(perk.name)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(perk.name, fontSize = 12.sp, color = if (hasPerk) NeonCyan else TextPrimary, modifier = Modifier.weight(1f))
                                        Button(
                                            onClick = {
                                                if (hasPerk) {
                                                    val newPerks = character.unlockedPerks.filter { it != perk.name }
                                                    CampaignRepository.claimLevelUp(character.id, character.stats, character.skills, newPerks)
                                                } else {
                                                    CampaignRepository.unlockPerk(character.id, perk.name)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (hasPerk) DangerRed else CyanCurvature, contentColor = VoidBlack),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text(if (hasPerk) "Quitar" else "Añadir", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                        4 -> {
                            var itemName by remember { mutableStateOf("") }
                            var itemDesc by remember { mutableStateOf("") }
                            var itemSize by remember { mutableStateOf("1") }
                            var statBonusStat by remember { mutableStateOf("") }
                            var statBonusVal by remember { mutableStateOf("1") } // Admite positivos y negativos (ej: -1 AGI)

                            val maxSlots = character.maxInventorySlots
                            val currentCount = character.currentInventorySlotsUsed

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                                Text("Inventario ($currentCount / $maxSlots huecos):", fontSize = 12.sp, color = CyanCurvature, fontWeight = FontWeight.Bold)
                                LazyColumn(modifier = Modifier.height(100.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    items(character.inventory) { item ->
                                        Surface(color = VoidBlack, shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth()) {
                                            Row(
                                                modifier = Modifier.padding(6.dp).fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("${item.name} (Huecos: ${item.size})", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    if (!item.statBonusStat.isNullOrBlank()) {
                                                        val sign = if (item.statBonusValue >= 0) "+" else ""
                                                        Text("Bonus: $sign${item.statBonusValue} ${item.statBonusStat}", color = if (item.statBonusValue >= 0) NeonCyan else DangerRed, fontSize = 10.sp)
                                                    }
                                                }
                                                IconButton(
                                                    onClick = { CampaignRepository.removeItemFromInventory(character.id, item.id) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Text("❌", fontSize = 10.sp)
                                                }
                                            }
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = itemName,
                                    onValueChange = { itemName = it },
                                    label = { Text("Nombre (Ej: Gafas / Armadura Pesada)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                                )
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedTextField(
                                        value = itemSize,
                                        onValueChange = { itemSize = it },
                                        label = { Text("Huecos") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                                    )
                                    OutlinedTextField(
                                        value = statBonusStat,
                                        onValueChange = { statBonusStat = it.uppercase() },
                                        label = { Text("Stat (INT/AGI/SLOTS)") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1.3f),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                                    )
                                    OutlinedTextField(
                                        value = statBonusVal,
                                        onValueChange = { statBonusVal = it },
                                        label = { Text("Val (+/-)") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                                    )
                                }
                                Button(
                                    onClick = {
                                        if (itemName.isNotBlank()) {
                                            val sz = itemSize.toIntOrNull() ?: 1
                                            val bVal = statBonusVal.toIntOrNull() ?: 0
                                            CampaignRepository.addItemToInventory(
                                                charId = character.id,
                                                name = itemName.trim(),
                                                description = itemDesc.trim(),
                                                size = sz,
                                                statBonusStat = statBonusStat.ifBlank { null },
                                                statBonusValue = bVal
                                            )
                                            itemName = ""
                                            itemDesc = ""
                                            itemSize = "1"
                                            statBonusStat = ""
                                            statBonusVal = "1"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanCurvature, contentColor = VoidBlack),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Añadir al Inventario", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                colors = ButtonDefaults.buttonColors(containerColor = WarningAmber, contentColor = VoidBlack)
            ) {
                Text("Cerrar Panel", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DeepSpaceBlue
    )
}

@Composable
fun IndividualVitalsControlRow(
    title: String,
    currentVal: Int,
    color: Color,
    onDelta: (Int) -> Unit,
    onSliderChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "$title: $currentVal%",
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(135.dp)
        )
        FilledTonalButton(
            onClick = { onDelta(-5) },
            contentPadding = PaddingValues(horizontal = 4.dp),
            modifier = Modifier.height(26.dp)
        ) { Text("-5", fontSize = 10.sp) }
        FilledTonalButton(
            onClick = { onDelta(5) },
            contentPadding = PaddingValues(horizontal = 4.dp),
            modifier = Modifier.height(26.dp)
        ) { Text("+5", fontSize = 10.sp) }
        Slider(
            value = currentVal.toFloat(),
            onValueChange = { onSliderChange(it.toInt()) },
            valueRange = 0f..100f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
        )
    }
}

@Composable
fun EditableStatRow(name: String, value: Int, onValueChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$name:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledTonalIconButton(
                onClick = { if (value > 1) onValueChange(value - 1) },
                enabled = value > 1,
                modifier = Modifier.size(28.dp)
            ) { Text("-", fontWeight = FontWeight.Bold) }
            Text(value.toString(), fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyanCurvature)
            FilledTonalIconButton(
                onClick = { if (value < 10) onValueChange(value + 1) },
                enabled = value < 10,
                modifier = Modifier.size(28.dp)
            ) { Text("+", fontWeight = FontWeight.Bold) }
        }
    }
}
