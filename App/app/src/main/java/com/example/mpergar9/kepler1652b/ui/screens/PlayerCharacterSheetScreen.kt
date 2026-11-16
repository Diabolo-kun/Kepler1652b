package com.example.mpergar9.kepler1652b.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpergar9.kepler1652b.data.CampaignRepository
import com.example.mpergar9.kepler1652b.data.KeplerGameData
import com.example.mpergar9.kepler1652b.model.Campaign
import com.example.mpergar9.kepler1652b.model.Character
import com.example.mpergar9.kepler1652b.model.SpecialStats
import com.example.mpergar9.kepler1652b.ui.components.*
import com.example.mpergar9.kepler1652b.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerCharacterSheetScreen(
    campaign: Campaign,
    selectedCharacter: Character? = null,
    onBack: () -> Unit
) {
    val allCharacters by CampaignRepository.characters.collectAsState()
    val currentUserProfile by CampaignRepository.currentUserProfile.collectAsState()
    val campaignChars = allCharacters.filter { it.campaignId == campaign.id }
    var selectedCharId by remember { mutableStateOf(selectedCharacter?.id ?: campaignChars.firstOrNull()?.id ?: "") }

    val character = campaignChars.find { it.id == selectedCharId } ?: selectedCharacter ?: campaignChars.firstOrNull()

    // Verificación de permisos: solo el propietario o el máster pueden modificar/subir de nivel/borrar el personaje
    val isOwner = character != null && (character.playerId == currentUserProfile.id || character.playerName.equals(currentUserProfile.displayName, true))
    val canModify = isOwner || campaign.isCurrentUserMaster

    var showLevelUpDialog by remember { mutableStateOf(false) }
    var showDeleteCharacterDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Habilidades, 1: Perks, 2: Inventario

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(character?.characterName ?: "FICHA DE COLONO", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CyanCurvature)
                        Text("Campaña: ${campaign.name}", fontSize = 12.sp, color = TextSecondary)
                    }
                },
                navigationIcon = {},
                actions = {
                    if (character != null && canModify) {
                        IconButton(onClick = { showDeleteCharacterDialog = true }) {
                            Text("🗑️", fontSize = 16.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StatusBarColor)
            )
        },
        containerColor = VoidBlack,
        modifier = Modifier.statusBarsPadding()
    ) { padding ->
        if (character == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay ningún personaje registrado en esta campaña.", color = TextMuted)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // Selector si hay varios PJs creados
                if (campaignChars.size > 1) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        campaignChars.forEach { c ->
                            FilterChip(
                                selected = c.id == character.id,
                                onClick = { selectedCharId = c.id },
                                label = { Text(c.characterName) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyanCurvature)
                            )
                        }
                    }
                }

                // --- CABECERA FIJA (VIDA Y ESTADÍSTICAS VITALES SIEMPRE VISIBLES) ---
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder, RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(character.characterClass, fontSize = 13.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                                Text(character.subclass, fontSize = 11.sp, color = TextSecondary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                AliveStatusBadge(isAlive = character.isAlive)
                                
                                // Nivel con indicador de punto rojo si tiene permiso de edición y hay subidas pendientes
                                Box(
                                    modifier = Modifier.clickable(enabled = canModify && character.pendingLevelUps > 0) {
                                        showLevelUpDialog = true
                                    }
                                ) {
                                    Surface(color = HullGray, shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = "Nivel ${character.level}",
                                            fontSize = 11.sp,
                                            color = WarningAmber,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    if (canModify && character.pendingLevelUps > 0) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(DangerRed, CircleShape)
                                                .align(Alignment.TopEnd)
                                        )
                                    }
                                }
                            }
                        }

                        // Barra de Salud
                        val effectiveStats = character.effectiveStats
                        HealthProgressBar(currentHp = character.currentHp, maxHp = effectiveStats.maxHp)

                        // Radiación, Veneno, O2 y Cordura en 2x2 grid
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("☢️ Radiación: ${character.radiationLevel}%", fontSize = 11.sp, color = WarningAmber, modifier = Modifier.weight(1f))
                                Text("☣️ Toxicidad: ${character.toxicityPoison}%", fontSize = 11.sp, color = DangerRed, modifier = Modifier.weight(1f))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("🛡️ Soporte Vital: ${character.soporteVitalLevel}%", fontSize = 11.sp, color = if (character.soporteVitalLevel < 30) DangerRed else CyanCurvature, modifier = Modifier.weight(1f))
                                Text("🧠 Cordura: ${character.currentSanity}%", fontSize = 11.sp, color = PlasmaPurple, modifier = Modifier.weight(1f))
                            }
                        }

                        // Estadísticas SPECIAL efectivas con bonificadores de inventario
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            val stats = effectiveStats
                            Text("FUE:${stats.fue}", fontSize = 10.sp, color = TextMuted)
                            Text("PER:${stats.per}", fontSize = 10.sp, color = TextMuted)
                            Text("END:${stats.end}", fontSize = 10.sp, color = TextMuted)
                            Text("CAR:${stats.car}", fontSize = 10.sp, color = TextMuted)
                            Text("AGI:${stats.agi}", fontSize = 10.sp, color = TextMuted)
                            Text("INT:${stats.int}", fontSize = 10.sp, color = TextMuted)
                            Text("SUE:${stats.sue}", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }

                // --- PESTAÑAS (TABS) PARA HABILIDADES, PERKS E INVENTARIO ---
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DeepSpaceBlue,
                    contentColor = NeonCyan
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Habilidades", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Perks", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Inventario", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                // Contenido de la Pestaña Activa
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    when (selectedTab) {
                        0 -> {
                            Card(
                                modifier = Modifier.fillMaxSize().border(1.dp, PanelBorder, RoundedCornerShape(10.dp)),
                                colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize().padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    item {
                                        Text("HABILIDADES DE CAMPO (0 - 100)", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 13.sp)
                                    }
                                    items(KeplerGameData.ALL_SKILLS) { skill ->
                                        val points = character.skills[skill] ?: 0
                                        val associatedStat = KeplerGameData.SKILL_ASSOCIATED_STAT[skill] ?: "INT"
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(skill, fontSize = 12.sp, color = if (points > 0) TextPrimary else TextMuted, modifier = Modifier.weight(1f))
                                            Text("$points / 100 ($associatedStat)", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = if (points > 0) CyanCurvature else TextMuted)
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            Card(
                                modifier = Modifier.fillMaxSize().border(1.dp, PanelBorder, RoundedCornerShape(10.dp)),
                                colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize().padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    item {
                                        Text("⚡ CAPACIDADES Y ACCIONES DESBLOQUEADAS", fontWeight = FontWeight.Bold, color = WarningAmber, fontSize = 13.sp)
                                    }
                                    
                                    character.statusPrivilege?.let { priv ->
                                        item {
                                            Surface(color = VoidBlack, shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Text("🏛️ $priv", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    Text("Privilegio de estatus pasivo permanente por trasfondo social.", color = TextMuted, fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }

                                    items(character.unlockedPerks) { perkName ->
                                        val perkInfo = KeplerGameData.UNLOCKABLE_PERKS.find { it.name.equals(perkName, ignoreCase = true) }
                                        Surface(color = VoidBlack, shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(perkName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text(perkInfo?.description ?: "Habilidad operativa activa en combate o exploración.", color = TextSecondary, fontSize = 12.sp)
                                                perkInfo?.actionName?.let { act ->
                                                    Text("Habilita acción: '$act'", color = CyanCurvature, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        2 -> {
                            val maxSlots = character.maxInventorySlots
                            val currentItemsCount = character.currentInventorySlotsUsed
                            Card(
                                modifier = Modifier.fillMaxSize().border(1.dp, PanelBorder, RoundedCornerShape(10.dp)),
                                colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("📦 INVENTARIO & EQUIPAMIENTO", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 13.sp)
                                        Surface(color = HullGray, shape = RoundedCornerShape(4.dp)) {
                                            Text(
                                                text = "Huecos: $currentItemsCount / $maxSlots",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (currentItemsCount > maxSlots) DangerRed else CyanCurvature,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }

                                    Text(
                                        text = "Nota: El inventario es gestionado exclusivamente por el Director de Juego (Máster) durante la exploración.",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )

                                    HorizontalDivider(color = PanelBorder.copy(alpha = 0.5f))

                                    if (character.inventory.isEmpty()) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().weight(1f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("El inventario está vacío.", color = TextMuted, fontSize = 13.sp)
                                        }
                                    } else {
                                        LazyColumn(
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth().weight(1f)
                                        ) {
                                            items(character.inventory) { item ->
                                                Surface(
                                                    color = VoidBlack,
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Column(modifier = Modifier.weight(1f)) {
                                                            Text(item.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                                                            if (item.description.isNotBlank()) {
                                                                Text(item.description, color = TextSecondary, fontSize = 11.sp)
                                                            }
                                                            Text("Tamaño (Huecos): ${item.size}", color = TextMuted, fontSize = 10.sp)
                                                            if (!item.statBonusStat.isNullOrBlank()) {
                                                                val sign = if (item.statBonusValue >= 0) "+" else ""
                                                                Text("Bonus: $sign${item.statBonusValue} ${item.statBonusStat}", color = if (item.statBonusValue >= 0) NeonCyan else DangerRed, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showLevelUpDialog && isOwner && character != null) {
        PlayerLevelUpDialog(
            character = character,
            onDismiss = { showLevelUpDialog = false },
            onClaim = { newStats, newSkills, newPerks ->
                CampaignRepository.claimLevelUp(character.id, newStats, newSkills, newPerks)
                showLevelUpDialog = false
            }
        )
    }

    if (showDeleteCharacterDialog && character != null) {
        DeleteConfirmationDialog(
            title = "¿Eliminar Personaje?",
            message = "¿Estás seguro de eliminar a '${character.characterName}' de la campaña?",
            confirmText = "Eliminar",
            cancelText = "Cancelar",
            onConfirm = {
                showDeleteCharacterDialog = false
                CampaignRepository.deleteCharacter(character.id)
                onBack()
            },
            onDismiss = { showDeleteCharacterDialog = false }
        )
    }
}

enum class LevelUpChoiceType { PERK, SPECIAL }

/**
 * Diálogo secuencial ordenado para la Subida de Nivel del Jugador:
 * Paso 1: Reparto de 15 puntos de habilidad.
 * Paso 2: Selección exclusiva entre 1 Perk O la mejora +1 de 1 Atributo SPECIAL.
 */
@Composable
fun PlayerLevelUpDialog(
    character: Character,
    onDismiss: () -> Unit,
    onClaim: (newStats: SpecialStats, newSkills: Map<String, Int>, newPerks: List<String>) -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1: Habilidades, 2: Perk O SPECIAL

    val currentStats = remember { mutableStateOf(character.stats) }
    val currentSkills = remember { mutableStateMapOf<String, Int>().apply { putAll(character.skills) } }
    val currentPerks = remember { mutableStateListOf<String>().apply { addAll(character.unlockedPerks) } }

    var availableSkillPoints by remember { mutableIntStateOf(15) }
    val skillIncrements = remember { mutableStateMapOf<String, Int>() }

    var selectedChoiceType by remember { mutableStateOf<LevelUpChoiceType?>(null) }
    var selectedNewPerk by remember { mutableStateOf<String?>(null) }
    var selectedSpecialStat by remember { mutableStateOf<String?>(null) }
    var showSpecialSelectorDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("⭐ SUBIDA DE NIVEL (Nivel ${character.level})", color = WarningAmber, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    text = if (step == 1) "Paso 1/2: Reparte 15 Puntos de Habilidad" else "Paso 2/2: Selecciona 1 Perk o 1 Atributo SPECIAL (+1)",
                    fontSize = 12.sp,
                    color = NeonCyan
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth().height(380.dp)) {
                when (step) {
                    1 -> {
                        Text("Tienes 15 puntos libres. Puedes sumar y restar libremente para organizarte antes de continuar:", fontSize = 11.sp, color = TextSecondary)
                        Text("Puntos disponibles: $availableSkillPoints pts", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (availableSkillPoints > 0) WarningAmber else RadiationGreen)
                        
                        LazyColumn(modifier = Modifier.height(280.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(KeplerGameData.ALL_SKILLS) { skill ->
                                val baseVal = character.skills[skill] ?: 0
                                val added = skillIncrements[skill] ?: 0
                                val totalVal = baseVal + added
                                val statKey = KeplerGameData.SKILL_ASSOCIATED_STAT[skill] ?: "INT"
                                val cap = currentStats.value.getSkillCap(statKey)

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                        Text(
                                            text = skill,
                                            fontSize = 11.sp,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text("$totalVal / $cap ($statKey)", fontSize = 10.sp, color = TextMuted)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        FilledTonalButton(
                                            onClick = {
                                                if (added >= 1) {
                                                    skillIncrements[skill] = added - 1
                                                    availableSkillPoints += 1
                                                }
                                            },
                                            enabled = added >= 1,
                                            contentPadding = PaddingValues(horizontal = 4.dp),
                                            modifier = Modifier.height(28.dp).width(36.dp)
                                        ) { Text("-1", fontSize = 11.sp, fontWeight = FontWeight.Bold) }

                                        Surface(
                                            color = HullGray,
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.width(32.dp).height(28.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "+$added",
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (added > 0) CyanCurvature else TextMuted
                                                )
                                            }
                                        }

                                        FilledTonalButton(
                                            onClick = {
                                                if (availableSkillPoints >= 1 && totalVal + 1 <= cap && totalVal + 1 <= 100) {
                                                    skillIncrements[skill] = added + 1
                                                    availableSkillPoints -= 1
                                                }
                                            },
                                            enabled = availableSkillPoints >= 1 && totalVal + 1 <= cap && totalVal + 1 <= 100,
                                            contentPadding = PaddingValues(horizontal = 4.dp),
                                            modifier = Modifier.height(28.dp).width(36.dp)
                                        ) { Text("+1", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        val tempSkills = currentSkills.mapValues { (sk, base) -> base + (skillIncrements[sk] ?: 0) }

                        Text("Selecciona solo uno de los dos: un Perk o aumentar +1 a un atributo SPECIAL:", fontSize = 11.sp, color = TextSecondary)

                        LazyColumn(modifier = Modifier.height(280.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Opción 1: Aumento de Atributo SPECIAL
                            item {
                                Surface(
                                    color = if (selectedChoiceType == LevelUpChoiceType.SPECIAL) CyanCurvature.copy(alpha = 0.2f) else VoidBlack,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, if (selectedChoiceType == LevelUpChoiceType.SPECIAL) CyanCurvature else PanelBorder, RoundedCornerShape(6.dp))
                                        .clickable {
                                            showSpecialSelectorDialog = true
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "➕ Subir +1 a un Atributo SPECIAL",
                                                fontWeight = FontWeight.Bold,
                                                color = NeonCyan,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = if (selectedChoiceType == LevelUpChoiceType.SPECIAL && selectedSpecialStat != null)
                                                    "Seleccionado: +1 a $selectedSpecialStat"
                                                else
                                                    "Aumenta 1 punto en FUE, INT, AGI, etc. (en lugar de coger un perk)",
                                                fontSize = 11.sp,
                                                color = if (selectedChoiceType == LevelUpChoiceType.SPECIAL) WarningAmber else TextSecondary
                                            )
                                        }
                                        if (selectedChoiceType == LevelUpChoiceType.SPECIAL && selectedSpecialStat != null) {
                                            Text("✅", fontSize = 14.sp)
                                        }
                                    }
                                }
                            }

                            item {
                                HorizontalDivider(color = PanelBorder.copy(alpha = 0.5f))
                                Text("O elige un Perk Disponible:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                            }

                            items(KeplerGameData.UNLOCKABLE_PERKS) { perk ->
                                val alreadyUnlocked = currentPerks.contains(perk.name)
                                val canUnlockNow = perk.requirements.all { (sk, reqVal) -> (tempSkills[sk] ?: 0) >= reqVal }
                                val isClose = perk.requirements.all { (sk, reqVal) -> ((tempSkills[sk] ?: 0) + 15) >= reqVal }
                                val isSelected = selectedChoiceType == LevelUpChoiceType.PERK && selectedNewPerk == perk.name

                                if (!alreadyUnlocked && (canUnlockNow || isClose)) {
                                    Surface(
                                        color = if (isSelected) CyanCurvature.copy(alpha = 0.2f) else VoidBlack,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, if (isSelected) CyanCurvature else PanelBorder, RoundedCornerShape(6.dp))
                                            .clickable(enabled = canUnlockNow) {
                                                selectedChoiceType = LevelUpChoiceType.PERK
                                                selectedNewPerk = perk.name
                                                selectedSpecialStat = null
                                            }
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text(perk.name, fontWeight = FontWeight.Bold, color = if (canUnlockNow) NeonCyan else WarningAmber, fontSize = 12.sp)
                                                Text(if (canUnlockNow) "✅ Disponible" else "⚠️ Cerca", fontSize = 10.sp, color = if (canUnlockNow) RadiationGreen else WarningAmber)
                                            }
                                            Text(perk.description, fontSize = 11.sp, color = TextSecondary)
                                            perk.requirements.forEach { (sk, req) ->
                                                val currentVal = tempSkills[sk] ?: 0
                                                Text("• Requisito $sk: $currentVal / $req", fontSize = 10.sp, color = if (currentVal >= req) RadiationGreen else DangerRed)
                                            }
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (step > 1) {
                    Button(
                        onClick = { step -= 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = HullGray, contentColor = TextPrimary)
                    ) {
                        Text("← Anterior")
                    }
                }
                if (step == 1) {
                    Button(
                        onClick = { step = 2 },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanCurvature, contentColor = VoidBlack)
                    ) {
                        Text("Siguiente →")
                    }
                } else {
                    Button(
                        onClick = {
                            val finalSkills = currentSkills.mapValues { (sk, base) -> base + (skillIncrements[sk] ?: 0) }
                            val finalPerks = currentPerks.toMutableList()
                            if (selectedChoiceType == LevelUpChoiceType.PERK && selectedNewPerk != null) {
                                if (!finalPerks.contains(selectedNewPerk!!)) finalPerks.add(selectedNewPerk!!)
                            }
                            val finalStats = if (selectedChoiceType == LevelUpChoiceType.SPECIAL && selectedSpecialStat != null) {
                                when (selectedSpecialStat!!.uppercase()) {
                                    "FUE" -> currentStats.value.copy(fue = (currentStats.value.fue + 1).coerceAtMost(10))
                                    "PER" -> currentStats.value.copy(per = (currentStats.value.per + 1).coerceAtMost(10))
                                    "END" -> currentStats.value.copy(end = (currentStats.value.end + 1).coerceAtMost(10))
                                    "CAR" -> currentStats.value.copy(car = (currentStats.value.car + 1).coerceAtMost(10))
                                    "AGI" -> currentStats.value.copy(agi = (currentStats.value.agi + 1).coerceAtMost(10))
                                    "INT" -> currentStats.value.copy(int = (currentStats.value.int + 1).coerceAtMost(10))
                                    "SUE" -> currentStats.value.copy(sue = (currentStats.value.sue + 1).coerceAtMost(10))
                                    else -> currentStats.value
                                }
                            } else {
                                currentStats.value
                            }
                            onClaim(finalStats, finalSkills, finalPerks)
                        },
                        enabled = selectedChoiceType != null && (selectedNewPerk != null || selectedSpecialStat != null),
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber, contentColor = VoidBlack)
                    ) {
                        Text("Reclamar Nivel", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Posponer", color = TextSecondary) }
        },
        containerColor = DeepSpaceBlue
    )

    if (showSpecialSelectorDialog) {
        AlertDialog(
            onDismissRequest = { showSpecialSelectorDialog = false },
            title = { Text("Selecciona Atributo SPECIAL (+1)", color = CyanCurvature, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("FUE", "PER", "END", "CAR", "AGI", "INT", "SUE").forEach { stat ->
                        val curVal = currentStats.value.getStat(stat)
                        OutlinedButton(
                            onClick = {
                                selectedChoiceType = LevelUpChoiceType.SPECIAL
                                selectedSpecialStat = stat
                                selectedNewPerk = null
                                showSpecialSelectorDialog = false
                            },
                            enabled = curVal < 10,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selectedSpecialStat == stat) CyanCurvature else Color.Transparent,
                                contentColor = if (selectedSpecialStat == stat) VoidBlack else TextPrimary
                            )
                        ) {
                            Text("Subir +1 a $stat ($curVal/10)${if (selectedSpecialStat == stat) " (Seleccionado)" else ""}", fontWeight = if (selectedSpecialStat == stat) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSpecialSelectorDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = DeepSpaceBlue
        )
    }
}
