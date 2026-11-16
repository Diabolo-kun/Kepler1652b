/**
 * ====================================================================================
 * MasterCharacterDetailScreen.kt
 * ====================================================================================
 * Pantalla completa de detalle y edición total para el Máster (Director de Juego),
 * con estado vital de 3 estados (Vivo / Incapacitado / Fallecido) en un único botón,
 * sin botón Volver, y desglose completo de perks y habilidades.
 * ====================================================================================
 */

package com.example.mpergar9.kepler1652b.ui.screens.master

import androidx.compose.foundation.border
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
import com.example.mpergar9.kepler1652b.data.PerkDefinition
import com.example.mpergar9.kepler1652b.model.Campaign
import com.example.mpergar9.kepler1652b.model.Character
import com.example.mpergar9.kepler1652b.model.InventoryItem
import com.example.mpergar9.kepler1652b.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterCharacterDetailScreen(
    campaign: Campaign,
    character: Character,
    onBack: () -> Unit
) {
    val allCharacters by CampaignRepository.characters.collectAsState()
    val liveChar = allCharacters.find { it.id == character.id } ?: character

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Vitals & Nivel, 1: SPECIAL, 2: Habilidades, 3: Perks, 4: Inventario
    var itemToDelete by remember { mutableStateOf<InventoryItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("⚙️ MÁSTER: ${liveChar.characterName}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                        Text("Jugador: ${liveChar.playerName} • ${liveChar.characterClass}", fontSize = 12.sp, color = TextSecondary)
                    }
                },
                navigationIcon = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepSpaceBlue)
            )
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
            // Pestañas con desplazamiento horizontal (ScrollableTabRow clásico con flechas)
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = HullGray,
                edgePadding = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Vitals & Nivel", color = if (selectedTab == 0) VoidBlack else TextPrimary) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("SPECIAL", color = if (selectedTab == 1) VoidBlack else TextPrimary) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Habilidades", color = if (selectedTab == 2) VoidBlack else TextPrimary) })
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Perks", color = if (selectedTab == 3) VoidBlack else TextPrimary) })
                Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text("Inventario", color = if (selectedTab == 4) VoidBlack else TextPrimary) })
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when (selectedTab) {
                    0 -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder, RoundedCornerShape(10.dp)),
                                    colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text("Estado Vital:", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                            
                                            // Botón cíclico de 3 estados: VIVO -> INCAPACITADO -> FALLECIDO
                                            val isIncapacitated = liveChar.statusEffects.contains("Incapacitado")
                                            val statusText = when {
                                                !liveChar.isAlive -> "💀 FALLECIDO"
                                                isIncapacitated -> "⚠️ INCAPACITADO"
                                                else -> "💚 VIVO"
                                            }
                                            val statusColor = when {
                                                !liveChar.isAlive -> DangerRed
                                                isIncapacitated -> WarningAmber
                                                else -> RadiationGreen
                                            }
                                            Button(
                                                onClick = {
                                                    when {
                                                        liveChar.isAlive && !isIncapacitated -> {
                                                            // VIVO ➔ INCAPACITADO
                                                            CampaignRepository.setCharacterLifeAndStatus(liveChar.id, isAlive = true, incapacitated = true)
                                                        }
                                                        liveChar.isAlive && isIncapacitated -> {
                                                            // INCAPACITADO ➔ FALLECIDO
                                                            CampaignRepository.setCharacterLifeAndStatus(liveChar.id, isAlive = false, incapacitated = false)
                                                        }
                                                        else -> {
                                                            // FALLECIDO ➔ VIVO
                                                            CampaignRepository.setCharacterLifeAndStatus(liveChar.id, isAlive = true, incapacitated = false)
                                                        }
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = statusColor.copy(alpha = 0.2f), contentColor = statusColor),
                                                contentPadding = PaddingValues(0.dp),
                                                modifier = Modifier.width(155.dp).height(32.dp)
                                            ) {
                                                Text(statusText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text("Nivel (${liveChar.level})", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                FilledTonalButton(onClick = { CampaignRepository.changeCharacterLevel(liveChar.id, -1) }, enabled = liveChar.level > 1) { Text("-1 Nv") }
                                                FilledTonalButton(onClick = { CampaignRepository.levelUpCharacter(liveChar.id) }, colors = ButtonDefaults.filledTonalButtonColors(containerColor = WarningAmber, contentColor = VoidBlack)) { Text("+1 Nv (Notif. PJ)") }
                                            }
                                        }

                                        // Fila de PV
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("PV", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp, modifier = Modifier.width(35.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                FilledTonalButton(
                                                    onClick = { CampaignRepository.updateCharacterHp(liveChar.id, liveChar.currentHp - 5) },
                                                    contentPadding = PaddingValues(2.dp),
                                                    modifier = Modifier.height(28.dp).width(36.dp)
                                                ) { Text("-5", fontSize = 10.sp) }

                                                FilledTonalButton(
                                                    onClick = { CampaignRepository.updateCharacterHp(liveChar.id, liveChar.currentHp - 1) },
                                                    contentPadding = PaddingValues(2.dp),
                                                    modifier = Modifier.height(26.dp).width(36.dp)
                                                ) { Text("-1", fontSize = 10.sp) }

                                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                                    Surface(color = HullGray, shape = RoundedCornerShape(4.dp)) {
                                                        Text(
                                                            text = "${liveChar.currentHp} / ${liveChar.stats.maxHp}",
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (liveChar.currentHp < liveChar.stats.maxHp / 2) DangerRed else CyanCurvature,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                            fontFamily = FontFamily.Monospace
                                                        )
                                                    }
                                                }

                                                FilledTonalButton(
                                                    onClick = { CampaignRepository.updateCharacterHp(liveChar.id, liveChar.currentHp + 1) },
                                                    contentPadding = PaddingValues(2.dp),
                                                    modifier = Modifier.height(26.dp).width(36.dp)
                                                ) { Text("+1", fontSize = 10.sp) }

                                                FilledTonalButton(
                                                    onClick = { CampaignRepository.updateCharacterHp(liveChar.id, liveChar.currentHp + 5) },
                                                    contentPadding = PaddingValues(2.dp),
                                                    modifier = Modifier.height(28.dp).width(36.dp)
                                                ) { Text("+5", fontSize = 10.sp) }
                                            }
                                        }
                                    }
                                }
                            }
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder, RoundedCornerShape(10.dp)),
                                    colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Text("Variables Ambientales & Salud", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 13.sp)
                                        MasterVitalsControlRow(
                                            title = "☢️ Radiación",
                                            currentVal = liveChar.radiationLevel,
                                            color = WarningAmber,
                                            onDelta = { CampaignRepository.updateCharacterRadiation(liveChar.id, liveChar.radiationLevel + it) },
                                            onSliderChange = { CampaignRepository.updateCharacterRadiation(liveChar.id, it) }
                                        )
                                        MasterVitalsControlRow(
                                            title = "☣️ Toxicidad",
                                            currentVal = liveChar.toxicityPoison,
                                            color = DangerRed,
                                            onDelta = { CampaignRepository.updateCharacterPoison(liveChar.id, liveChar.toxicityPoison + it) },
                                            onSliderChange = { CampaignRepository.updateCharacterPoison(liveChar.id, it) }
                                        )
                                        MasterVitalsControlRow(
                                            title = "🛡️ Soporte Vital",
                                            currentVal = liveChar.soporteVitalLevel,
                                            color = CyanCurvature,
                                            onDelta = { CampaignRepository.updateCharacterSoporteVital(liveChar.id, liveChar.soporteVitalLevel + it) },
                                            onSliderChange = { CampaignRepository.updateCharacterSoporteVital(liveChar.id, it) }
                                        )
                                        MasterVitalsControlRow(
                                            title = "🧠 Cordura",
                                            currentVal = liveChar.currentSanity,
                                            color = PlasmaPurple,
                                            onDelta = { CampaignRepository.updateCharacterSanity(liveChar.id, liveChar.currentSanity + it) },
                                            onSliderChange = { CampaignRepository.updateCharacterSanity(liveChar.id, it) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        val stats = liveChar.stats
                        Card(
                            modifier = Modifier.fillMaxSize().border(1.dp, PanelBorder, RoundedCornerShape(10.dp)),
                            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize().padding(14.dp)) {
                                Text("Modificar Atributos SPECIAL (1 - 10):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                MasterEditableStatRow("FUE (Fuerza)", stats.fue) { v -> CampaignRepository.updateStats(liveChar.id, stats.copy(fue = v)) }
                                MasterEditableStatRow("PER (Percepción)", stats.per) { v -> CampaignRepository.updateStats(liveChar.id, stats.copy(per = v)) }
                                MasterEditableStatRow("END (Resistencia)", stats.end) { v -> CampaignRepository.updateStats(liveChar.id, stats.copy(end = v)) }
                                MasterEditableStatRow("CAR (Carisma)", stats.car) { v -> CampaignRepository.updateStats(liveChar.id, stats.copy(car = v)) }
                                MasterEditableStatRow("AGI (Agilidad)", stats.agi) { v -> CampaignRepository.updateStats(liveChar.id, stats.copy(agi = v)) }
                                MasterEditableStatRow("INT (Intelecto)", stats.int) { v -> CampaignRepository.updateStats(liveChar.id, stats.copy(int = v)) }
                                MasterEditableStatRow("SUE (Suerte)", stats.sue) { v -> CampaignRepository.updateStats(liveChar.id, stats.copy(sue = v)) }
                            }
                        }
                    }
                    2 -> {
                        Card(
                            modifier = Modifier.fillMaxSize().border(1.dp, PanelBorder, RoundedCornerShape(10.dp)),
                            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
                        ) {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize().padding(14.dp)) {
                                item {
                                    Text("Habilidades de Campo (0 - 100)", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 13.sp)
                                }
                                items(KeplerGameData.ALL_SKILLS) { skill ->
                                    val cur = liveChar.skills[skill] ?: 0
                                    val statKey = KeplerGameData.SKILL_ASSOCIATED_STAT[skill] ?: "INT"
                                    val cap = liveChar.stats.getSkillCap(statKey)

                                    Surface(
                                        color = VoidBlack,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(skill, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                                Text("($statKey)", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = CyanCurvature, fontWeight = FontWeight.Bold)
                                            }
                                            // Botones idénticos y centralizados debajo de la habilidad
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                FilledTonalButton(onClick = { CampaignRepository.updateSkill(liveChar.id, skill, -5) }, contentPadding = PaddingValues(0.dp), modifier = Modifier.height(28.dp).width(36.dp)) { Text("-5", fontSize = 10.sp) }
                                                Spacer(modifier = Modifier.width(4.dp))
                                                FilledTonalButton(onClick = { CampaignRepository.updateSkill(liveChar.id, skill, -1) }, contentPadding = PaddingValues(0.dp), modifier = Modifier.height(28.dp).width(36.dp)) { Text("-1", fontSize = 10.sp) }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Surface(color = HullGray, shape = RoundedCornerShape(4.dp)) {
                                                    Text("$cur / $cap", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = WarningAmber, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                FilledTonalButton(onClick = { CampaignRepository.updateSkill(liveChar.id, skill, 1) }, enabled = cur < cap && cur < 100, contentPadding = PaddingValues(0.dp), modifier = Modifier.height(28.dp).width(36.dp)) { Text("+1", fontSize = 10.sp) }
                                                Spacer(modifier = Modifier.width(4.dp))
                                                FilledTonalButton(onClick = { CampaignRepository.updateSkill(liveChar.id, skill, 5) }, enabled = cur + 5 <= cap && cur + 5 <= 100, contentPadding = PaddingValues(0.dp), modifier = Modifier.height(28.dp).width(36.dp)) { Text("+5", fontSize = 10.sp) }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        var showAllPerks by remember { mutableStateOf(false) }
                        Card(
                            modifier = Modifier.fillMaxSize().border(1.dp, PanelBorder, RoundedCornerShape(10.dp)),
                            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
                        ) {
                            Column(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Perks y Capacidades", fontWeight = FontWeight.Bold, color = WarningAmber, fontSize = 13.sp)
                                    OutlinedButton(
                                        onClick = { showAllPerks = !showAllPerks },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text(if (showAllPerks) "Ver Filtrados" else "Mostrar Todo", fontSize = 10.sp)
                                    }
                                }

                                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                                    if (showAllPerks) {
                                        item { Text("Catálogo Completo:", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold) }
                                        items(KeplerGameData.UNLOCKABLE_PERKS) { perk ->
                                            MasterPerkRowDetail(liveChar, perk)
                                        }
                                    } else {
                                        val availablePerks = KeplerGameData.UNLOCKABLE_PERKS.filter { perk ->
                                            !liveChar.unlockedPerks.contains(perk.name) &&
                                            perk.requirements.all { (sk, req) -> (liveChar.skills[sk] ?: 0) >= req }
                                        }
                                        val activePerks = KeplerGameData.UNLOCKABLE_PERKS.filter { liveChar.unlockedPerks.contains(it.name) }
                                        val closePerks = KeplerGameData.UNLOCKABLE_PERKS.filter { perk ->
                                            !liveChar.unlockedPerks.contains(perk.name) &&
                                            !availablePerks.contains(perk) &&
                                            perk.requirements.all { (sk, req) -> ((liveChar.skills[sk] ?: 0) + 15) >= req }
                                        }

                                        item { Text("Activos en el Personaje (${activePerks.size}):", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold) }
                                        items(activePerks) { perk -> MasterPerkRowDetail(liveChar, perk) }

                                        item { Spacer(modifier = Modifier.height(4.dp)) }
                                        item { Text("Disponibles (${availablePerks.size}):", fontSize = 11.sp, color = RadiationGreen, fontWeight = FontWeight.Bold) }
                                        if (availablePerks.isEmpty()) {
                                            item { Text("Ningún perk disponible con las estadísticas actuales.", fontSize = 11.sp, color = TextMuted) }
                                        }
                                        items(availablePerks) { perk -> MasterPerkRowDetail(liveChar, perk) }

                                        item { Spacer(modifier = Modifier.height(4.dp)) }
                                        item { Text("Próximos a desbloquear (${closePerks.size}):", fontSize = 11.sp, color = WarningAmber, fontWeight = FontWeight.Bold) }
                                        if (closePerks.isEmpty()) {
                                            item { Text("No hay perks cercanos a desbloquear.", fontSize = 11.sp, color = TextMuted) }
                                        }
                                        items(closePerks) { perk -> MasterPerkRowDetail(liveChar, perk) }
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
                        var statBonusVal by remember { mutableStateOf("1") }
                        var itemCredits by remember { mutableStateOf("0") }

                        val maxSlots = liveChar.maxInventorySlots
                        val currentCount = liveChar.currentInventorySlotsUsed

                        Card(
                            modifier = Modifier.fillMaxSize().border(1.dp, PanelBorder, RoundedCornerShape(10.dp)),
                            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                Text("Inventario ($currentCount / $maxSlots huecos):", fontSize = 13.sp, color = CyanCurvature, fontWeight = FontWeight.Bold)
                                
                                LazyColumn(modifier = Modifier.height(130.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    items(liveChar.inventory) { item ->
                                        Surface(color = VoidBlack, shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth()) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp).fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${item.name} | Huecos: ${item.size} ${if (!item.statBonusStat.isNullOrBlank()) "| ${if(item.statBonusValue >= 0) "+" else ""}${item.statBonusValue} ${item.statBonusStat}" else ""} | ${item.credits} Cr",
                                                    color = TextPrimary,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.weight(1f),
                                                    maxLines = 1
                                                )
                                                IconButton(
                                                    onClick = { itemToDelete = item },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Text("🗑️", fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = PanelBorder.copy(alpha = 0.5f))

                                Text("Añadir objeto:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                OutlinedTextField(
                                    value = itemName,
                                    onValueChange = { itemName = it },
                                    label = { Text("Nombre") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                                )
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    OutlinedTextField(
                                        value = itemSize,
                                        onValueChange = { itemSize = it },
                                        label = { Text("Huecos") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).height(50.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                                    )
                                    OutlinedTextField(
                                        value = statBonusStat,
                                        onValueChange = { statBonusStat = it.uppercase() },
                                        label = { Text("Stat") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).height(50.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                                    )
                                    OutlinedTextField(
                                        value = statBonusVal,
                                        onValueChange = { statBonusVal = it },
                                        label = { Text("Val") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).height(50.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                                    )
                                    OutlinedTextField(
                                        value = itemCredits,
                                        onValueChange = { itemCredits = it },
                                        label = { Text("Créd.") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).height(50.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                                    )
                                }
                                Button(
                                    onClick = {
                                        if (itemName.isNotBlank()) {
                                            val sz = itemSize.toIntOrNull() ?: 1
                                            val bVal = statBonusVal.toIntOrNull() ?: 0
                                            val creds = itemCredits.toIntOrNull() ?: 0
                                            CampaignRepository.addItemToInventory(
                                                charId = liveChar.id,
                                                name = itemName.trim(),
                                                description = itemDesc.trim(),
                                                size = sz,
                                                statBonusStat = statBonusStat.ifBlank { null },
                                                statBonusValue = bVal,
                                                credits = creds
                                            )
                                            itemName = ""
                                            itemDesc = ""
                                            itemSize = "1"
                                            statBonusStat = ""
                                            statBonusVal = "1"
                                            itemCredits = "0"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanCurvature, contentColor = VoidBlack),
                                    modifier = Modifier.fillMaxWidth().height(36.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Añadir al Inventario", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("¿Eliminar Objeto?", fontWeight = FontWeight.Bold, color = DangerRed) },
            text = { Text("¿Estás seguro de retirar '${item.name}' del inventario?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        CampaignRepository.removeItemFromInventory(liveChar.id, item.id)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = TextPrimary)
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Cancelar", color = TextSecondary) }
            },
            containerColor = DeepSpaceBlue
        )
    }
}

@Composable
fun MasterPerkRowDetail(liveChar: Character, perk: PerkDefinition) {
    val hasPerk = liveChar.unlockedPerks.contains(perk.name)
    val canUnlock = perk.requirements.all { (sk, req) -> (liveChar.skills[sk] ?: 0) >= req }
    Surface(color = VoidBlack, shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(perk.name, fontSize = 12.sp, color = if (hasPerk) NeonCyan else TextPrimary, fontWeight = FontWeight.Bold)
                    Text(perk.description, fontSize = 10.sp, color = TextSecondary)
                }
                Button(
                    onClick = {
                        if (hasPerk) {
                            val newPerks = liveChar.unlockedPerks.filter { it != perk.name }
                            CampaignRepository.claimLevelUp(liveChar.id, liveChar.stats, liveChar.skills, newPerks)
                        } else {
                            CampaignRepository.unlockPerk(liveChar.id, perk.name)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (hasPerk) DangerRed else if (canUnlock) CyanCurvature else WarningAmber, contentColor = VoidBlack),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Text(if (hasPerk) "Quitar" else if (canUnlock) "Añadir" else "Forzar", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            perk.requirements.forEach { (sk, req) ->
                val currentVal = liveChar.skills[sk] ?: 0
                val diff = req - currentVal
                val statusText = if (currentVal >= req) "✅ $sk: $currentVal/$req" else "❌ Faltan $diff pts en $sk (Actual: $currentVal / Req: $req)"
                Text(statusText, fontSize = 10.sp, color = if (currentVal >= req) RadiationGreen else DangerRed)
            }
        }
    }
}

@Composable
fun MasterVitalsControlRow(
    title: String,
    currentVal: Int,
    color: Color,
    onDelta: (Int) -> Unit,
    onSliderChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$currentVal%",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilledTonalButton(
                onClick = { onDelta(-5) },
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier.height(26.dp).width(36.dp)
            ) { Text("-5", fontSize = 10.sp) }

            FilledTonalButton(
                onClick = { onDelta(-1) },
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier.height(26.dp).width(36.dp)
            ) { Text("-1", fontSize = 10.sp) }

            Slider(
                value = currentVal.toFloat(),
                onValueChange = { onSliderChange(it.toInt()) },
                valueRange = 0f..100f,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
            )

            FilledTonalButton(
                onClick = { onDelta(1) },
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier.height(26.dp).width(36.dp)
            ) { Text("+1", fontSize = 10.sp) }

            FilledTonalButton(
                onClick = { onDelta(5) },
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier.height(26.dp).width(36.dp)
            ) { Text("+5", fontSize = 10.sp) }
        }
    }
}

@Composable
fun MasterEditableStatRow(name: String, value: Int, onValueChange: (Int) -> Unit) {
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
