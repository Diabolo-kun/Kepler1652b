/**
 * ====================================================================================
 * CharacterCreationScreen.kt
 * ====================================================================================
 * Pantalla de creación de personajes en 5 pasos (Wizard), sin "Aplomo" en SPECIAL,
 * con perk de subclase compacto ubicado abajo junto a los perks elegidos, y manejo
 * de botón de atrás del sistema.
 * ====================================================================================
 */

package com.example.mpergar9.kepler1652b.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpergar9.kepler1652b.data.CampaignRepository
import com.example.mpergar9.kepler1652b.data.KeplerGameData
import com.example.mpergar9.kepler1652b.data.SubclassDefinition
import com.example.mpergar9.kepler1652b.model.Campaign
import com.example.mpergar9.kepler1652b.model.Character
import com.example.mpergar9.kepler1652b.model.CharacterFaction
import com.example.mpergar9.kepler1652b.model.SpecialStats
import com.example.mpergar9.kepler1652b.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCreationScreen(
    campaign: Campaign,
    onCharacterCreated: () -> Unit,
    onBack: () -> Unit
) {
    val userProfile by CampaignRepository.currentUserProfile.collectAsState()
    var currentStep by remember { mutableIntStateOf(1) }

    BackHandler(enabled = true) {
        if (currentStep > 1) {
            currentStep -= 1
        } else {
            onBack()
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(currentStep) {
        listState.animateScrollToItem(0)
    }

    var charName by remember { mutableStateOf("") }
    var selectedFaction by remember { mutableStateOf(CharacterFaction.UNION_HUMANA) }

    var rollInputText by remember { mutableStateOf("20") }
    val physicalRoll = rollInputText.toIntOrNull() ?: 0
    val isRollValid = physicalRoll in 11..30

    var statFue by remember { mutableIntStateOf(1) }
    var statPer by remember { mutableIntStateOf(1) }
    var statEnd by remember { mutableIntStateOf(1) }
    var statCar by remember { mutableIntStateOf(1) }
    var statAgi by remember { mutableIntStateOf(1) }
    var statInt by remember { mutableIntStateOf(1) }
    var statSue by remember { mutableIntStateOf(1) }

    val allocatedSpecialPoints = (statFue - 1) + (statPer - 1) + (statEnd - 1) +
            (statCar - 1) + (statAgi - 1) + (statInt - 1) + (statSue - 1)
    val remainingSpecialPoints = (if (isRollValid) physicalRoll else 0) - allocatedSpecialPoints
    val currentStats = SpecialStats(statFue, statPer, statEnd, statCar, statAgi, statInt, statSue)

    var selectedClassIndex by remember { mutableIntStateOf(0) }
    var selectedSubclassIndex by remember { mutableIntStateOf(0) }

    val currentClass = KeplerGameData.CLASSES[selectedClassIndex]
    val currentSubclass = currentClass.subclasses.getOrElse(selectedSubclassIndex) { currentClass.subclasses.first() }

    val isClassAllowed = currentStats.meetsRequirements(currentClass.requiredStats)
    val isSubclassAllowed = currentStats.meetsRequirements(currentSubclass.requiredStats)

    val isAcompañante = currentClass.name.contains("Acompañante")
    val isPolizon = currentClass.name.contains("Polizón")
    val baseSkillPool = when {
        isPolizon -> 170
        isAcompañante -> 160
        else -> 150 + currentClass.classBonusValue
    }
    val requiredPerkCost = currentSubclass.totalCost
    val totalFreeSkillPoints = (baseSkillPool - requiredPerkCost).coerceAtLeast(0)

    val customSkillAllocations = remember { mutableStateMapOf<String, Int>() }

    var extraFue by remember { mutableIntStateOf(0) }
    var extraPer by remember { mutableIntStateOf(0) }
    var extraEnd by remember { mutableIntStateOf(0) }
    var extraCar by remember { mutableIntStateOf(0) }
    var extraAgi by remember { mutableIntStateOf(0) }
    var extraInt by remember { mutableIntStateOf(0) }
    var extraSue by remember { mutableIntStateOf(0) }

    val extraSelectedPerks = remember { mutableStateListOf<String>() }

    val finalSkills: Map<String, Int> = remember(customSkillAllocations, currentSubclass) {
        val map = currentSubclass.requiredSkills.toMutableMap()
        customSkillAllocations.forEach { (sk, added) ->
            val cur = map[sk] ?: 0
            map[sk] = cur + added
        }
        map.toMap()
    }

    val sumAssignedSkillPoints = customSkillAllocations.values.sum()
    val availableFreePoints = (totalFreeSkillPoints - sumAssignedSkillPoints).coerceAtLeast(0)

    val maxTotalChoices = 3
    val totalUsedChoices = extraFue + extraPer + extraEnd + extraCar + extraAgi + extraInt + extraSue + extraSelectedPerks.size
    val remainingChoices = (maxTotalChoices - totalUsedChoices).coerceAtLeast(0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("CREACIÓN DE COLONO / TRIPULANTE", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CyanCurvature)
                        Text("Campaña: ${campaign.name} • Paso $currentStep de 5", fontSize = 11.sp, color = TextSecondary)
                    }
                },
                navigationIcon = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepSpaceBlue)
            )
        },
        containerColor = VoidBlack,
        modifier = Modifier.statusBarsPadding()
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            item { WizardStepIndicator(currentStep = currentStep) }

            when (currentStep) {
                1 -> {
                    item {
                        Step1IdentityCard(
                            charName = charName,
                            onNameChange = { charName = it },
                            selectedFaction = selectedFaction,
                            onFactionChange = { selectedFaction = it }
                        )
                    }
                    item {
                        val isStep1Valid = charName.isNotBlank()
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { if (isStep1Valid) currentStep = 2 },
                                enabled = isStep1Valid,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanCurvature, contentColor = VoidBlack),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("CONTINUAR A SPECIAL (PASO 2) →", fontWeight = FontWeight.Bold)
                            }
                            if (!isStep1Valid) {
                                Text("⚠️ Debes introducir un nombre para el colono.", color = WarningAmber, fontSize = 11.sp)
                            }
                        }
                    }
                }

                2 -> {
                    item {
                        Step2SpecialCard(
                            rollInputText = rollInputText,
                            onRollChange = { rollInputText = it },
                            remainingPoints = remainingSpecialPoints,
                            fue = statFue, onFueChange = { statFue = it },
                            per = statPer, onPerChange = { statPer = it },
                            end = statEnd, onEndChange = { statEnd = it },
                            car = statCar, onCarChange = { statCar = it },
                            agi = statAgi, onAgiChange = { statAgi = it },
                            int = statInt, onIntChange = { statInt = it },
                            sue = statSue, onSueChange = { statSue = it }
                        )
                    }
                    item {
                        val isStep2Valid = isRollValid && remainingSpecialPoints == 0
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { currentStep = 3 },
                                enabled = isStep2Valid,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanCurvature, contentColor = VoidBlack),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("CONTINUAR A CLASE (PASO 3) →", fontWeight = FontWeight.Bold)
                            }
                            if (!isStep2Valid) {
                                Text(
                                    text = if (!isRollValid)
                                        "⚠️ Introduce un resultado de tirada física válido entre 11 y 30."
                                    else if (remainingSpecialPoints > 0)
                                        "⚠️ Debes repartir todos tus puntos (${remainingSpecialPoints} restantes) antes de continuar."
                                    else
                                        "⚠️ Has superado los puntos permitidos por ${-remainingSpecialPoints}.",
                                    color = WarningAmber,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                3 -> {
                    item {
                        Step3ClassSelectionCard(
                            currentStats = currentStats,
                            selectedClassIndex = selectedClassIndex,
                            onSelectClass = { idx ->
                                selectedClassIndex = idx
                                val firstAllowed = KeplerGameData.CLASSES[idx].subclasses.indexOfFirst { currentStats.meetsRequirements(it.requiredStats) }
                                selectedSubclassIndex = if (firstAllowed >= 0) firstAllowed else 0
                            },
                            selectedSubclassIndex = selectedSubclassIndex,
                            onSelectSubclass = { idx -> selectedSubclassIndex = idx }
                        )
                    }
                    item {
                        val isSubclassAllowed = currentSubclass.requiredStats.all { (stat, minVal) -> currentStats.getStat(stat) >= minVal }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { currentStep = 4 },
                                enabled = isSubclassAllowed,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanCurvature, contentColor = VoidBlack),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("CONTINUAR A HABILIDADES (PASO 4) →", fontWeight = FontWeight.Bold)
                            }
                            if (!isSubclassAllowed) {
                                val missingReqs = currentSubclass.requiredStats.filter { (stat, minVal) -> currentStats.getStat(stat) < minVal }
                                    .map { "${it.key} ${it.value}+ (tienes ${currentStats.getStat(it.key)})" }
                                    .joinToString(", ")
                                Text(
                                    text = "🔒 La subclase '${currentSubclass.name}' está bloqueada. Te falta: $missingReqs. Elige otra subclase accesible o la clase Polizón.",
                                    color = DangerRed,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                4 -> {
                    item {
                        Step4SkillsCard(
                            currentStats = currentStats,
                            currentSubclass = currentSubclass,
                            availableFreePoints = availableFreePoints,
                            customSkillAllocations = customSkillAllocations,
                            onSetSkillAdded = { skill, newAdded ->
                                customSkillAllocations[skill] = newAdded
                            }
                        )
                    }
                    item {
                        val isStep4Valid = availableFreePoints == 0
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    if (isStep4Valid) {
                                        currentStep = 5
                                    }
                                },
                                enabled = isStep4Valid,
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = VoidBlack),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("CONTINUAR A ELECCIONES LIBRES (PASO 5) →", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            if (!isStep4Valid) {
                                Text(
                                    text = "⚠️ Tienes $availableFreePoints puntos libres sin repartir. Repártelos todos para continuar.",
                                    color = WarningAmber,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                5 -> {
                    item {
                        Step5ChoicesCardWithCycle(
                            baseStats = currentStats,
                            extraFue = extraFue, onFueChange = { extraFue = it },
                            extraPer = extraPer, onPerChange = { extraPer = it },
                            extraEnd = extraEnd, onEndChange = { extraEnd = it },
                            extraCar = extraCar, onCarChange = { extraCar = it },
                            extraAgi = extraAgi, onAgiChange = { extraAgi = it },
                            extraInt = extraInt, onIntChange = { extraInt = it },
                            extraSue = extraSue, onSueChange = { extraSue = it },
                            remainingChoices = remainingChoices,
                            finalSkills = finalSkills,
                            initialPerkName = currentSubclass.initialPerkName,
                            extraSelectedPerks = extraSelectedPerks,
                            onTogglePerk = { perkName ->
                                if (extraSelectedPerks.contains(perkName)) {
                                    extraSelectedPerks.remove(perkName)
                                } else if (remainingChoices > 0) {
                                    extraSelectedPerks.add(perkName)
                                }
                            }
                        )
                    }

                    item {
                        val isStep5Valid = remainingChoices == 0
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    if (isStep5Valid) {
                                        val finalStats = SpecialStats(
                                            fue = statFue + extraFue,
                                            per = statPer + extraPer,
                                            end = statEnd + extraEnd,
                                            car = statCar + extraCar,
                                            agi = statAgi + extraAgi,
                                            int = statInt + extraInt,
                                            sue = statSue + extraSue
                                        )
                                        val allPerks = (listOf(currentSubclass.initialPerkName) + extraSelectedPerks)
                                            .filter { it.isNotBlank() && !it.contains("Ninguno") }

                                        val newChar = Character(
                                            campaignId = campaign.id,
                                            playerId = userProfile.id,
                                            playerName = userProfile.displayName,
                                            characterName = charName,

                                            faction = selectedFaction,
                                            characterClass = currentClass.name,
                                            subclass = currentSubclass.name,
                                            level = 1,
                                            stats = finalStats,
                                            currentHp = finalStats.maxHp,
                                            radiationLevel = 0,
                                            toxicityPoison = 0,
                                            soporteVitalLevel = 100,
                                            currentSanity = 100,
                                            statusEffects = emptyList(),
                                            skills = finalSkills,
                                            unlockedPerks = allPerks,
                                            statusPrivilege = currentSubclass.statusPrivilege
                                        )
                                        CampaignRepository.addCharacter(newChar)
                                        onCharacterCreated()
                                    }
                                },
                                enabled = isStep5Valid,
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = VoidBlack),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("FINALIZAR FICHA & DESPERTAR DE HIPERSUEÑO ✓", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            if (!isStep5Valid) {
                                Text(
                                    text = "⚠️ Te quedan $remainingChoices de las 3 elecciones libres de Nivel 1. Puedes subir atributos SPECIAL o adquirir perks.",
                                    color = WarningAmber,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WizardStepIndicator(currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepBubble(stepNumber = 1, label = "Datos", isCurrent = currentStep == 1, isPassed = currentStep > 1)
        HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 2.dp), color = if (currentStep > 1) CyanCurvature else HullGray)
        StepBubble(stepNumber = 2, label = "SPECIAL", isCurrent = currentStep == 2, isPassed = currentStep > 2)
        HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 2.dp), color = if (currentStep > 2) CyanCurvature else HullGray)
        StepBubble(stepNumber = 3, label = "Clase", isCurrent = currentStep == 3, isPassed = currentStep > 3)
        HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 2.dp), color = if (currentStep > 3) CyanCurvature else HullGray)
        StepBubble(stepNumber = 4, label = "Habilidades", isCurrent = currentStep == 4, isPassed = currentStep > 4)
        HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 2.dp), color = if (currentStep > 4) CyanCurvature else HullGray)
        StepBubble(stepNumber = 5, label = "Cierre", isCurrent = currentStep == 5, isPassed = currentStep > 5)
    }
}

@Composable
fun StepBubble(stepNumber: Int, label: String, isCurrent: Boolean, isPassed: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(50),
            color = when {
                isCurrent -> CyanCurvature
                isPassed -> RadiationGreen
                else -> HullGray
            },
            modifier = Modifier.size(26.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = if (isPassed) "✓" else "$stepNumber",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrent || isPassed) VoidBlack else TextSecondary
                )
            }
        }
        Text(text = label, fontSize = 9.sp, color = if (isCurrent) CyanCurvature else TextMuted, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun Step1IdentityCard(
    charName: String,
    onNameChange: (String) -> Unit,
    selectedFaction: CharacterFaction,
    onFactionChange: (CharacterFaction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "PASO 1: DATOS DE IDENTIDAD",
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                fontSize = 13.sp
            )
            OutlinedTextField(
                value = charName,
                onValueChange = onNameChange,
                label = { Text("Nombre del Colono / Tripulante") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
            )

            Text("Facción de Origen:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            CharacterFaction.entries.forEach { faction ->
                Surface(
                    color = if (selectedFaction == faction) CyanCurvature.copy(alpha = 0.2f) else VoidBlack,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (selectedFaction == faction) CyanCurvature else PanelBorder, RoundedCornerShape(6.dp))
                        .clickable { onFactionChange(faction) }
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(faction.label, fontWeight = FontWeight.Bold, color = if (selectedFaction == faction) NeonCyan else TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun Step2SpecialCard(
    rollInputText: String,
    onRollChange: (String) -> Unit,
    remainingPoints: Int,
    fue: Int, onFueChange: (Int) -> Unit,
    per: Int, onPerChange: (Int) -> Unit,
    end: Int, onEndChange: (Int) -> Unit,
    car: Int, onCarChange: (Int) -> Unit,
    agi: Int, onAgiChange: (Int) -> Unit,
    int: Int, onIntChange: (Int) -> Unit,
    sue: Int, onSueChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "PASO 2: ATRIBUTOS SPECIAL",
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                fontSize = 13.sp
            )
            Text(
                text = "Introduce el resultado de tu tirada física real (1d20 + 10, entre 11 y 30). Este es tu fondo de puntos para repartir en SPECIAL (cada atributo empieza en 1):",
                fontSize = 11.sp,
                color = TextSecondary
            )

            OutlinedTextField(
                value = rollInputText,
                onValueChange = onRollChange,
                label = { Text("Resultado de Tirada (11 - 30)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
            )

            Surface(
                color = HullGray,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Puntos SPECIAL Disponibles:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        text = "$remainingPoints",
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (remainingPoints == 0) RadiationGreen else WarningAmber
                    )
                }
            }

            HorizontalDivider(color = PanelBorder.copy(alpha = 0.5f))

            SpecialAttrRow("FUE (Fuerza)", fue) { onFueChange(it) }
            SpecialAttrRow("PER (Percepción)", per) { onPerChange(it) }
            SpecialAttrRow("END (Resistencia)", end) { onEndChange(it) }
            SpecialAttrRow("CAR (Carisma)", car) { onCarChange(it) }
            SpecialAttrRow("AGI (Agilidad)", agi) { onAgiChange(it) }
            SpecialAttrRow("INT (Intelecto)", int) { onIntChange(it) }
            SpecialAttrRow("SUE (Suerte)", sue) { onSueChange(it) }
        }
    }
}

@Composable
fun SpecialAttrRow(name: String, value: Int, onChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledTonalIconButton(
                onClick = { if (value > 1) onChange(value - 1) },
                enabled = value > 1,
                modifier = Modifier.size(28.dp)
            ) { Text("-", fontWeight = FontWeight.Bold) }
            Text(
                text = "$value",
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = CyanCurvature,
                modifier = Modifier.width(24.dp)
            )
            FilledTonalIconButton(
                onClick = { if (value < 10) onChange(value + 1) },
                enabled = value < 10,
                modifier = Modifier.size(28.dp)
            ) { Text("+", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun Step3ClassSelectionCard(
    currentStats: SpecialStats,
    selectedClassIndex: Int,
    onSelectClass: (Int) -> Unit,
    selectedSubclassIndex: Int,
    onSelectSubclass: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "PASO 3: CLASE Y SUBCLASE",
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                fontSize = 13.sp
            )
            Text("Selecciona tu Clase de Tripulación:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)

            KeplerGameData.CLASSES.forEachIndexed { idx, cls ->
                val allowed = currentStats.meetsRequirements(cls.requiredStats)
                val isSelected = selectedClassIndex == idx
                Surface(
                    color = if (isSelected) CyanCurvature.copy(alpha = 0.2f) else VoidBlack,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (isSelected) CyanCurvature else PanelBorder, RoundedCornerShape(6.dp))
                        .clickable { onSelectClass(idx) }
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(cls.name, fontWeight = FontWeight.Bold, color = if (isSelected) NeonCyan else TextPrimary, fontSize = 12.sp)
                            Text(if (allowed) "✓ Accesible" else "🔒 Requisitos no cumplidos", fontSize = 10.sp, color = if (allowed) RadiationGreen else DangerRed)
                        }
                        Text("Bono: ${cls.classBonusSkill ?: "Ninguno"} (+${cls.classBonusValue})", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }

            HorizontalDivider(color = PanelBorder.copy(alpha = 0.5f))

            Text("Selecciona tu Subclase Especializada:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            val currentClass = KeplerGameData.CLASSES[selectedClassIndex]
            currentClass.subclasses.forEachIndexed { subIdx, sub ->
                val allowed = currentStats.meetsRequirements(sub.requiredStats)
                val isSelected = selectedSubclassIndex == subIdx
                Surface(
                    color = if (isSelected) WarningAmber.copy(alpha = 0.2f) else VoidBlack,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (isSelected) WarningAmber else PanelBorder, RoundedCornerShape(6.dp))
                        .clickable { onSelectSubclass(subIdx) }
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(sub.name, fontWeight = FontWeight.Bold, color = if (isSelected) WarningAmber else TextPrimary, fontSize = 12.sp)
                            Text(if (allowed) "✓ Accesible" else "🔒 Bloqueada", fontSize = 10.sp, color = if (allowed) RadiationGreen else DangerRed)
                        }
                        Text(sub.perkDefinition, fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun Step4SkillsCard(
    currentStats: SpecialStats,
    currentSubclass: SubclassDefinition,
    availableFreePoints: Int,
    customSkillAllocations: Map<String, Int>,
    onSetSkillAdded: (String, Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "PASO 4: REPARTO DE HABILIDADES",
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                fontSize = 13.sp
            )
            Text(
                text = "Puntos Libres Disponibles: $availableFreePoints",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (availableFreePoints == 0) RadiationGreen else WarningAmber
            )

            HorizontalDivider(color = PanelBorder.copy(alpha = 0.5f))

            KeplerGameData.ALL_SKILLS.forEach { skill ->
                val baseRequired = currentSubclass.requiredSkills[skill] ?: 0
                val userAdded = customSkillAllocations[skill] ?: 0
                val totalSkillValue = baseRequired + userAdded
                val statKey = KeplerGameData.SKILL_ASSOCIATED_STAT[skill] ?: "INT"
                val skillCap = currentStats.getSkillCap(statKey)

                SkillAllocationRow(
                    skillName = skill,
                    baseValue = baseRequired,
                    addedValue = userAdded,
                    totalValue = totalSkillValue,
                    statKey = statKey,
                    statVal = currentStats.getStat(statKey),
                    skillCap = skillCap,
                    availableFreePoints = availableFreePoints,
                    onSetAdded = { newAdded -> onSetSkillAdded(skill, newAdded) }
                )
            }
        }
    }
}

@Composable
fun SkillAllocationRow(
    skillName: String,
    baseValue: Int,
    addedValue: Int,
    totalValue: Int,
    statKey: String,
    statVal: Int,
    skillCap: Int,
    availableFreePoints: Int,
    onSetAdded: (Int) -> Unit
) {
    val maxPossibleAdd = skillCap - baseValue
    val maxAddable = minOf(maxPossibleAdd, addedValue + availableFreePoints)

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = skillName,
                fontSize = 12.sp,
                color = if (totalValue > 0) TextPrimary else TextMuted,
                fontWeight = if (totalValue > 0) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$totalValue / $skillCap (Tope $statKey: $statVal)",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = if (totalValue >= skillCap) WarningAmber else CyanCurvature,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                modifier = Modifier.width(160.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilledTonalIconButton(
                onClick = { if (addedValue > 0) onSetAdded(addedValue - 1) },
                enabled = addedValue > 0,
                modifier = Modifier.size(26.dp)
            ) { Text("-", fontWeight = FontWeight.Bold) }

            Slider(
                value = addedValue.toFloat(),
                onValueChange = { onSetAdded(it.toInt().coerceIn(0, maxAddable)) },
                valueRange = 0f..maxOf(maxPossibleAdd.toFloat(), 1f),
                steps = maxOf(maxPossibleAdd - 1, 0),
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(thumbColor = CyanCurvature, activeTrackColor = CyanCurvature)
            )

            FilledTonalIconButton(
                onClick = { if (addedValue < maxAddable) onSetAdded(addedValue + 1) },
                enabled = addedValue < maxAddable,
                modifier = Modifier.size(26.dp)
            ) { Text("+", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun Step5ChoicesCardWithCycle(
    baseStats: SpecialStats,
    extraFue: Int, onFueChange: (Int) -> Unit,
    extraPer: Int, onPerChange: (Int) -> Unit,
    extraEnd: Int, onEndChange: (Int) -> Unit,
    extraCar: Int, onCarChange: (Int) -> Unit,
    extraAgi: Int, onAgiChange: (Int) -> Unit,
    extraInt: Int, onIntChange: (Int) -> Unit,
    extraSue: Int, onSueChange: (Int) -> Unit,
    remainingChoices: Int,
    finalSkills: Map<String, Int>,
    initialPerkName: String,
    extraSelectedPerks: List<String>,
    onTogglePerk: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "PASO 5: ELECCIONES LIBRES (NIVEL 1)",
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                fontSize = 13.sp
            )
            Text(
                text = "Todo personaje en Nivel 1 dispone de 3 elecciones libres. Cada punto puede invertirse en subir +1 a un Atributo SPECIAL (máximo 10) o en adquirir 1 Perk del catálogo si cumples sus requisitos de habilidad:",
                fontSize = 11.sp,
                color = TextSecondary
            )

            HorizontalDivider(color = PanelBorder.copy(alpha = 0.5f))

            Text("Opción A: Subir +1 Atributo SPECIAL (Coste: 1 elección)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = WarningAmber)

            StatChoiceRow("FUE (Fuerza)", baseStats.fue, extraFue, remainingChoices > 0 && (baseStats.fue + extraFue) < 10, extraFue > 0, { onFueChange(extraFue + 1) }, { onFueChange(extraFue - 1) })
            StatChoiceRow("PER (Percepción)", baseStats.per, extraPer, remainingChoices > 0 && (baseStats.per + extraPer) < 10, extraPer > 0, { onPerChange(extraPer + 1) }, { onPerChange(extraPer - 1) })
            StatChoiceRow("END (Resistencia)", baseStats.end, extraEnd, remainingChoices > 0 && (baseStats.end + extraEnd) < 10, extraEnd > 0, { onEndChange(extraEnd + 1) }, { onEndChange(extraEnd - 1) })
            StatChoiceRow("CAR (Carisma)", baseStats.car, extraCar, remainingChoices > 0 && (baseStats.car + extraCar) < 10, extraCar > 0, { onCarChange(extraCar + 1) }, { onCarChange(extraCar - 1) })
            StatChoiceRow("AGI (Agilidad)", baseStats.agi, extraAgi, remainingChoices > 0 && (baseStats.agi + extraAgi) < 10, extraAgi > 0, { onAgiChange(extraAgi + 1) }, { onAgiChange(extraAgi - 1) })
            StatChoiceRow("INT (Intelecto)", baseStats.int, extraInt, remainingChoices > 0 && (baseStats.int + extraInt) < 10, extraInt > 0, { onIntChange(extraInt + 1) }, { onIntChange(extraInt - 1) })
            StatChoiceRow("SUE (Suerte)", baseStats.sue, extraSue, remainingChoices > 0 && (baseStats.sue + extraSue) < 10, extraSue > 0, { onSueChange(extraSue + 1) }, { onSueChange(extraSue - 1) })

            HorizontalDivider(color = PanelBorder.copy(alpha = 0.5f))

            Text("Opción B: Desbloquear Perks del Catálogo (Coste: 1 elección)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NeonCyan)

            var perkFilterMode by remember { mutableIntStateOf(0) }
            val filterButtonText = when (perkFilterMode) {
                0 -> "🟢 Filtro: Disponibles para ti"
                1 -> "🟡 Filtro: Próximos a desbloquear"
                else -> "🟣 Filtro: Ver Todas del Catálogo"
            }

            OutlinedButton(
                onClick = { perkFilterMode = (perkFilterMode + 1) % 3 },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningAmber),
                border = BorderStroke(1.dp, WarningAmber.copy(alpha = 0.7f))
            ) {
                Text(filterButtonText, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            val allCatalogPerks = KeplerGameData.UNLOCKABLE_PERKS.filter { perk ->
                !perk.name.equals(initialPerkName, ignoreCase = true) &&
                !extraSelectedPerks.contains(perk.name)
            }

            val displayedPerks = when (perkFilterMode) {
                0 -> allCatalogPerks.filter { perk ->
                    perk.requirements.all { (sk, req) -> (finalSkills[sk] ?: 0) >= req }
                }
                1 -> allCatalogPerks.filter { perk ->
                    val canUnlock = perk.requirements.all { (sk, req) -> (finalSkills[sk] ?: 0) >= req }
                    val close = perk.requirements.all { (sk, req) -> ((finalSkills[sk] ?: 0) + 15) >= req }
                    !canUnlock && close
                }
                else -> allCatalogPerks
            }

            if (displayedPerks.isEmpty()) {
                Surface(
                    color = VoidBlack,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder, RoundedCornerShape(6.dp))
                ) {
                    Text(
                        text = "ℹ️ No hay perks en esta categoría con el filtro actual.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    displayedPerks.forEach { perk ->
                        val canUnlock = perk.requirements.all { (sk, req) -> (finalSkills[sk] ?: 0) >= req }
                        Surface(
                            color = VoidBlack,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().border(1.dp, if (canUnlock) CyanCurvature.copy(alpha = 0.6f) else PanelBorder, RoundedCornerShape(8.dp))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(perk.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (canUnlock) NeonCyan else TextPrimary)
                                        Text("Acción: ${perk.actionName} • ${perk.category}", fontSize = 10.sp, color = WarningAmber)
                                    }
                                    if (canUnlock) {
                                        Button(
                                            onClick = {
                                                if (remainingChoices > 0) {
                                                    onTogglePerk(perk.name)
                                                }
                                            },
                                            enabled = remainingChoices > 0,
                                            colors = ButtonDefaults.buttonColors(containerColor = CyanCurvature, contentColor = VoidBlack),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("+ Desbloquear", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Surface(color = HullGray, shape = RoundedCornerShape(4.dp)) {
                                            Text("Bloqueado", fontSize = 10.sp, color = DangerRed, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                Text(perk.description, fontSize = 11.sp, color = TextSecondary)
                                perk.requirements.forEach { (sk, req) ->
                                    val currentVal = finalSkills[sk] ?: 0
                                    val diff = req - currentVal
                                    val statusText = if (currentVal >= req) "✅ $sk: $currentVal/$req" else "❌ Faltan $diff pts en $sk (Actual: $currentVal / Req: $req)"
                                    Text(statusText, fontSize = 10.sp, color = if (currentVal >= req) RadiationGreen else DangerRed)
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = PanelBorder.copy(alpha = 0.5f))

            // Perks Desbloqueados (incluyendo el inicial de subclase abajo en tamaño compacto)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Perks Desbloqueados / Asignados:", fontSize = 12.sp, color = RadiationGreen, fontWeight = FontWeight.SemiBold)

                if (initialPerkName.isNotBlank() && !initialPerkName.contains("Ninguno")) {
                    val initialDef = KeplerGameData.UNLOCKABLE_PERKS.find { it.name.equals(initialPerkName, ignoreCase = true) }
                    Surface(
                        color = VoidBlack,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, WarningAmber.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text("🔒 (Subclase) $initialPerkName", fontWeight = FontWeight.Bold, color = WarningAmber, fontSize = 11.sp)
                                if (initialDef != null) Text(initialDef.description, fontSize = 10.sp, color = TextSecondary)
                            }
                            Surface(color = HullGray, shape = RoundedCornerShape(4.dp)) {
                                Text("Fijo", fontSize = 9.sp, color = WarningAmber, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                extraSelectedPerks.forEach { perkName ->
                    val def = KeplerGameData.UNLOCKABLE_PERKS.find { it.name == perkName }
                    Surface(
                        color = VoidBlack,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, RadiationGreen.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text("✓ $perkName", fontWeight = FontWeight.Bold, color = RadiationGreen, fontSize = 11.sp)
                                if (def != null) Text(def.description, fontSize = 10.sp, color = TextSecondary)
                            }
                            Button(
                                onClick = { onTogglePerk(perkName) },
                                colors = ButtonDefaults.buttonColors(containerColor = DangerRed.copy(alpha = 0.8f)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text("Quitar", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatChoiceRow(
    name: String,
    baseVal: Int,
    extraVal: Int,
    canInc: Boolean,
    canDec: Boolean,
    onInc: () -> Unit,
    onDec: () -> Unit
) {
    val total = baseVal + extraVal
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$name: $total (Base $baseVal ${if (extraVal > 0) "+$extraVal" else ""})",
            fontSize = 12.sp,
            color = if (extraVal > 0) WarningAmber else TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilledTonalIconButton(
                onClick = onDec,
                enabled = canDec,
                modifier = Modifier.size(26.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = HullGray)
            ) { Text("-", fontSize = 11.sp, fontWeight = FontWeight.Bold) }

            Text(
                text = "+$extraVal",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (extraVal > 0) WarningAmber else TextMuted,
                modifier = Modifier.width(24.dp)
            )

            FilledTonalIconButton(
                onClick = onInc,
                enabled = canInc,
                modifier = Modifier.size(26.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = HullGray)
            ) { Text("+", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
        }
    }
}
