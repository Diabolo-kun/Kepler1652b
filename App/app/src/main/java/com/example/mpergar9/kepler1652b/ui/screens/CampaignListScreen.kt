/**
 * ====================================================================================
 * CampaignListScreen.kt
 * ====================================================================================
 * Pantalla principal con Botonera Inferior de 5 elementos y deslizamiento horizontal
 * mediante gestos de arrastre con el dedo (HorizontalPager):
 * Pestaña 0: Campañas (Terminal de navegación)
 * Pestaña 1: Personajes (Baúl de PJs guardados del usuario)
 * Pestaña 2 (Centro +): Crear Campaña o Unirse con Código
 * Pestaña 3: Buscar (Lupa - Exploración de sectores)
 * Pestaña 4: Perfil (Identificación de Colono y Cerrar Sesión)
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.ExperimentalFoundationApi
import com.example.mpergar9.kepler1652b.data.CampaignRepository
import com.example.mpergar9.kepler1652b.model.Campaign
import com.example.mpergar9.kepler1652b.model.Character
import com.example.mpergar9.kepler1652b.ui.components.AliveStatusBadge
import com.example.mpergar9.kepler1652b.ui.components.DeleteConfirmationDialog
import com.example.mpergar9.kepler1652b.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CampaignListScreen(
    onSelectCampaignAsMaster: (Campaign) -> Unit,
    onSelectCampaignAsPlayer: (Campaign) -> Unit,
    onCreateCharacterForCampaign: (Campaign) -> Unit,
    onCreateStandaloneCharacter: () -> Unit = {},
    onSelectSavedCharacter: (Character) -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val campaigns by CampaignRepository.campaigns.collectAsState()
    val savedCharacters by CampaignRepository.savedUserCharacters.collectAsState()
    val currentUserProfile by CampaignRepository.currentUserProfile.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 5 })

    var campaignToDelete by remember { mutableStateOf<Campaign?>(null) }
    var campaignFilterTab by remember { mutableIntStateOf(0) } // 0: Todas, 1: Máster, 2: Jugador

    val filteredCampaigns = when (campaignFilterTab) {
        1 -> campaigns.filter { it.isCurrentUserMaster }
        2 -> campaigns.filter { !it.isCurrentUserMaster }
        else -> campaigns
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "KEPLER-1652b",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = CyanCurvature,
                        letterSpacing = 2.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StatusBarColor)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DeepSpaceBlue,
                contentColor = TextPrimary,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .border(BorderStroke(1.dp, PanelBorder))
                    .navigationBarsPadding()
            ) {
                // 0: Campañas (Far Left)
                NavigationBarItem(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(0) }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Campañas") },
                    label = { Text("Campañas", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyanCurvature,
                        selectedTextColor = CyanCurvature,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = HullGray
                    )
                )

                // 1: Buscar / Lupa (Mid Left)
                NavigationBarItem(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(1) }
                    },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    label = { Text("Buscar", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = HullGray
                    )
                )

                // 2: Botón Central Destacado (+) (Center)
                NavigationBarItem(
                    selected = pagerState.currentPage == 2,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(2) }
                    },
                    icon = {
                        Surface(
                            color = if (pagerState.currentPage == 2) WarningAmber else CyanCurvature,
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Crear o Unirse",
                                    tint = VoidBlack,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    },
                    label = { Text("Crear", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VoidBlack,
                        selectedTextColor = WarningAmber,
                        unselectedIconColor = VoidBlack,
                        unselectedTextColor = TextMuted,
                        indicatorColor = androidx.compose.ui.graphics.Color.Transparent
                    )
                )

                // 3: Personajes Guardados (Mid Right)
                NavigationBarItem(
                    selected = pagerState.currentPage == 3,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(3) }
                    },
                    icon = { Icon(Icons.Default.AccountBox, contentDescription = "Personajes") },
                    label = { Text("Personajes", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyanCurvature,
                        selectedTextColor = CyanCurvature,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = HullGray
                    )
                )

                // 4: Perfil (Far Right)
                NavigationBarItem(
                    selected = pagerState.currentPage == 4,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(4) }
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = HullGray
                    )
                )
            }
        },
        containerColor = VoidBlack,
        modifier = Modifier.statusBarsPadding()
    ) { padding ->
        // Pager con deslizamiento horizontal mediante gestos táctiles
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) { page ->
            when (page) {
                0 -> {
                    // --- PESTAÑA 0: LISTADO DE CAMPAÑAS ---
                    Column(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)) {
                        Text(
                            text = "TERMINAL DE CAMPAÑAS",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = CyanCurvature,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Filtros: Todas / Máster / Jugador
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = campaignFilterTab == 0,
                                onClick = { campaignFilterTab = 0 },
                                label = { Text("Todas (${campaigns.size})") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanCurvature,
                                    selectedLabelColor = VoidBlack,
                                    containerColor = DeepSpaceBlue,
                                    labelColor = TextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = campaignFilterTab == 1,
                                onClick = { campaignFilterTab = 1 },
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
                                selected = campaignFilterTab == 2,
                                onClick = { campaignFilterTab = 2 },
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
                                Text(
                                    text = when (campaignFilterTab) {
                                        1 -> "No has creado ninguna campaña como Máster."
                                        2 -> "No te has unido a ninguna campaña como Jugador."
                                        else -> "No participas en ninguna campaña todavía.\n\nPulsa '+' en la barra inferior para crear una o unirte con un código."
                                    },
                                    color = TextMuted,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(filteredCampaigns, key = { it.id }) { campaign ->
                                    CampaignCard(
                                        campaign = campaign,
                                        onOpenMaster = { onSelectCampaignAsMaster(campaign) },
                                        onOpenPlayer = { onSelectCampaignAsPlayer(campaign) },
                                        onDeleteCampaign = { campaignToDelete = campaign }
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // --- PESTAÑA 1: BUSCAR (LUPA - MID LEFT) ---
                    InlineSearchContent()
                }

                2 -> {
                    // --- PESTAÑA 2: CREAR O UNIRSE A CAMPAÑA (+) ---
                    InlineCampaignCreationContent(
                        defaultMasterName = currentUserProfile.displayName,
                        onCreateConfirm = { name, desc, master ->
                            CampaignRepository.createCampaign(name, desc, master)
                            coroutineScope.launch { pagerState.animateScrollToPage(0) }
                        },
                        onJoinConfirm = { code, onJoinError ->
                            CampaignRepository.joinCampaignByCode(code) { joined ->
                                if (joined != null) {
                                    onCreateCharacterForCampaign(joined)
                                    coroutineScope.launch { pagerState.animateScrollToPage(0) }
                                } else {
                                    onJoinError("No se encontró ninguna campaña con el código '$code'.")
                                }
                            }
                        }
                    )
                }

                3 -> {
                    // --- PESTAÑA 3: PERSONAJES GUARDADOS (MID RIGHT) ---
                    InlineSavedCharactersContent(
                        savedCharacters = savedCharacters,
                        userDisplayName = currentUserProfile.displayName,
                        onCreateNewStandaloneCharacter = onCreateStandaloneCharacter,
                        onSelectSavedCharacter = onSelectSavedCharacter
                    )
                }

                4 -> {
                    // --- PESTAÑA 4: PERFIL DE JUGADOR (FAR RIGHT) ---
                    InlineProfileContent(
                        userProfile = currentUserProfile,
                        campaigns = campaigns,
                        characters = savedCharacters,
                        onSaveName = { newName ->
                            CampaignRepository.updateProfileName(newName)
                        },
                        onSignOut = onSignOut
                    )
                }
            }
        }

    campaignToDelete?.let { campaign ->
        DeleteConfirmationDialog(
            title = "¿Eliminar Campaña?",
            message = "¿Estás seguro de eliminar permanentemente la campaña '${campaign.name}'?\n\nSe eliminarán todos los registros vinculados.",
            confirmText = "Eliminar Campaña",
            cancelText = "Cancelar",
            onConfirm = {
                CampaignRepository.deleteCampaign(campaign.id)
                campaignToDelete = null
            },
            onDismiss = { campaignToDelete = null }
        )
    }
}
}

@Composable
fun InlineSavedCharactersContent(
    savedCharacters: List<Character>,
    userDisplayName: String,
    onCreateNewStandaloneCharacter: () -> Unit,
    onSelectSavedCharacter: (Character) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "REGISTRO DE PERSONAJES",
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = CyanCurvature,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )

            Text(
                text = "Consulta las fichas completas de tus personajes y su disponibilidad de asignación a expediciones.",
                fontSize = 11.sp,
                color = TextMuted,
                lineHeight = 15.sp
            )

            if (savedCharacters.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no tienes ningún personaje guardado en tu registro.\n\nPulsa '+ Nuevo Personaje' para añadir uno.",
                        color = TextMuted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 60.dp)
                ) {
                    items(savedCharacters, key = { it.id }) { char ->
                        val isAvailable = char.activeCampaignId.isNullOrBlank()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, if (isAvailable) PanelBorder else WarningAmber.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable { onSelectSavedCharacter(char) },
                            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // --- COLUMNA IZQUIERDA: Nombre, Clase principal, Nivel y Salud ---
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = char.characterName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "• ${char.characterClass}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // 1. Nivel
                                        Text(
                                            text = "Nv.${char.level}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyanCurvature,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        // 2. Indicador Vivo / Muerto
                                        AliveStatusBadge(isAlive = char.isAlive)

                                        // 3. Salud (solo el porcentaje)
                                        val hpPercent = if (char.effectiveStats.maxHp > 0) ((char.currentHp.toFloat() / char.effectiveStats.maxHp.toFloat()) * 100).toInt().coerceIn(0, 100) else 100
                                        Text(
                                            text = "$hpPercent% Salud",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                }

                                // --- COLUMNA DERECHA: Indicador de Misión Compacto ---
                                Surface(
                                    color = if (isAvailable) CyanCurvature.copy(alpha = 0.12f) else WarningAmber.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, if (isAvailable) CyanCurvature.copy(alpha = 0.6f) else WarningAmber.copy(alpha = 0.6f)),
                                    modifier = Modifier
                                        .width(115.dp)
                                        .wrapContentHeight()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        horizontalAlignment = Alignment.Start,
                                        verticalArrangement = Arrangement.spacedBy(1.dp)
                                    ) {
                                        Text(
                                            text = if (isAvailable) "DISPONIBLE" else "EN MISIÓN",
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isAvailable) CyanCurvature else WarningAmber
                                        )

                                        Text(
                                            text = if (isAvailable) "Sin Campaña" else (char.activeCampaignName ?: "Campaña"),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isAvailable) TextSecondary else TextPrimary,
                                            maxLines = 2,
                                            lineHeight = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Botón Flotante en la esquina inferior derecha (estilo FAB)
        ExtendedFloatingActionButton(
            onClick = onCreateNewStandaloneCharacter,
            containerColor = CyanCurvature,
            contentColor = VoidBlack,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 12.dp, end = 4.dp)
        ) {
            Text("+ Nuevo Personaje", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun InlineSearchContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("EXPLORACIÓN & BÚSQUEDA", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 13.sp)

        Card(
            modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
        ) {
            Column(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(40.dp))
                Text(
                    text = "Buscador de Sectores y Archivos",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 15.sp
                )
                Text(
                    text = "Próximamente podrás buscar frecuencias de radio, sectores estelares y expediciones públicas en el universo de Kepler-1652b.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun InlineCampaignCreationContent(
    defaultMasterName: String,
    onCreateConfirm: (name: String, desc: String, master: String) -> Unit,
    onJoinConfirm: (code: String, onError: (String) -> Unit) -> Unit
) {
    var mode by remember { mutableIntStateOf(0) } // 0: Crear, 1: Unirse

    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    var inviteCode by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Selector de modo con dos botones del mismo tamaño exacto
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    mode = 0
                    errorMsg = null
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (mode == 0) CyanCurvature else HullGray,
                    contentColor = if (mode == 0) VoidBlack else TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(42.dp)
            ) {
                Text("Crear Campaña", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = {
                    mode = 1
                    errorMsg = null
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (mode == 1) CyanCurvature else HullGray,
                    contentColor = if (mode == 1) VoidBlack else TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(42.dp)
            ) {
                Text("Unirse con Código", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        errorMsg?.let { err ->
            Surface(
                color = DangerRed.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, DangerRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(err, color = DangerRed, fontSize = 11.sp, modifier = Modifier.padding(10.dp))
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().border(1.dp, PanelBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (mode == 0) {
                    Text("INFORMACIÓN DE LA NUEVA CAMPAÑA", fontWeight = FontWeight.Bold, color = CyanCurvature, fontSize = 13.sp)

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre de la Campaña") },
                        placeholder = { Text("Ej: Misión Artemisa") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                    )

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Descripción / Ambientación") },
                        placeholder = { Text("Ej: Exploración de ruinas alienígenas en el sector Kepler.") },
                        modifier = Modifier.fillMaxWidth().height(90.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                    )

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMsg = "Por favor, introduce el nombre de la campaña."
                            } else {
                                onCreateConfirm(name.trim(), desc.trim(), defaultMasterName)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanCurvature, contentColor = VoidBlack)
                    ) {
                        Text("Crear Campaña (Máster)", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text("INGRESAR CÓDIGO DE INVITACIÓN", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 13.sp)
                    Text(
                        text = "Introduce el código compartido por tu Director de Juego (ej: KPL-8421) para unirte a la tripulación:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = inviteCode,
                        onValueChange = { inviteCode = CampaignRepository.formatInviteCode(it) },
                        label = { Text("Código de Invitación") },
                        placeholder = { Text("KPL-XXXX") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan)
                    )

                    Button(
                        onClick = {
                            if (inviteCode.isBlank()) {
                                errorMsg = "Introduce un código de invitación válido."
                            } else {
                                onJoinConfirm(inviteCode.trim()) { msg ->
                                    errorMsg = msg
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = VoidBlack)
                    ) {
                        Text("Unirse a la Campaña", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = HullGray,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, PanelBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InlineProfileContent(
    userProfile: com.example.mpergar9.kepler1652b.model.UserProfile,
    campaigns: List<Campaign>,
    characters: List<Character>,
    onSaveName: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showSignOutConfirmDialog by remember { mutableStateOf(false) }

    // Métricas calculadas
    val masterCampaignsCount = campaigns.count { it.isCurrentUserMaster }
    val joinedCampaignsCount = campaigns.count { !it.isCurrentUserMaster }
    val totalCampaignsCount = campaigns.size
    val usedCharactersCount = characters.count { it.playerId == userProfile.id && (it.level > 1 || it.pendingLevelUps > 0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Cabecera con Avatar y Nombre
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PanelBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    color = HullGray,
                    shape = CircleShape,
                    border = BorderStroke(2.dp, CyanCurvature),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = CyanCurvature,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userProfile.displayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )
                    if (userProfile.email.isNotBlank()) {
                        Text(
                            text = userProfile.email,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Sección 2: Estadísticas de Registro
        Text(
            text = "ESTADÍSTICAS DE REGISTRO",
            fontWeight = FontWeight.Bold,
            color = CyanCurvature,
            fontSize = 12.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Máster",
                value = "$masterCampaignsCount",
                color = WarningAmber,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Jugador",
                value = "$joinedCampaignsCount",
                color = NeonCyan,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Total",
                value = "$totalCampaignsCount",
                color = CyanCurvature,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "PJs Usados",
                value = "$usedCharactersCount",
                color = PlasmaPurple,
                modifier = Modifier.weight(1f)
            )
        }

        // Sección 3: Acciones y Ajustes
        Text(
            text = "AJUSTES Y CONFIGURACIÓN",
            fontWeight = FontWeight.Bold,
            color = CyanCurvature,
            fontSize = 12.sp
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PanelBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Opción 1: Editar Nombre (Abre Ventana Modal)
                OutlinedButton(
                    onClick = { showEditNameDialog = true },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = BorderStroke(1.dp, PanelBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Editar Alias / Nombre", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("✏️", fontSize = 12.sp)
                    }
                }

                // Opción 2: Seguridad & Contraseña (Abre Ventana Modal)
                OutlinedButton(
                    onClick = { showSecurityDialog = true },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = BorderStroke(1.dp, PanelBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Seguridad & Contraseña", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Icon(Icons.Default.Lock, contentDescription = null, tint = CyanCurvature, modifier = Modifier.size(16.dp))
                    }
                }

                // Opción 3: Copiar ID Discreto de Registro
                OutlinedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(userProfile.id))
                        Toast.makeText(context, "ID de registro copiado al portapapeles", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    border = BorderStroke(1.dp, PanelBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Copiar ID de Registro", fontSize = 12.sp, fontWeight = FontWeight.Normal)
                        Icon(Icons.Default.Share, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Cerrar Sesión
        OutlinedButton(
            onClick = { showSignOutConfirmDialog = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
            border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth().height(44.dp)
        ) {
            Text("🔒 Cerrar Sesión", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }

    // Modal 1: Editar Nombre
    if (showEditNameDialog) {
        var nameInput by remember { mutableStateOf(userProfile.displayName) }
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Editar Alias de Colono", fontWeight = FontWeight.Bold, color = CyanCurvature, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Introduce tu nuevo nombre. Se actualizará automáticamente en todas tus campañas y personajes de Firebase:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nombre / Alias") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanCurvature)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            onSaveName(nameInput.trim())
                            showEditNameDialog = false
                            Toast.makeText(context, "Nombre actualizado.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanCurvature, contentColor = VoidBlack)
                ) {
                    Text("Guardar Cambios", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = DeepSpaceBlue
        )
    }

    // Modal 2: Seguridad y Restablecer Contraseña
    if (showSecurityDialog) {
        var isSendingReset by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { showSecurityDialog = false },
            title = { Text("Seguridad de la Cuenta", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Correo asociado: ${userProfile.email}",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Puedes enviar un correo oficial de recuperación para cambiar tu contraseña de acceso.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                if (isSendingReset) {
                    CircularProgressIndicator(color = CyanCurvature, modifier = Modifier.size(24.dp))
                } else {
                    Button(
                        onClick = {
                            if (userProfile.email.isNotBlank()) {
                                isSendingReset = true
                                CampaignRepository.sendPasswordResetEmail(userProfile.email) { success, err ->
                                    isSendingReset = false
                                    showSecurityDialog = false
                                    if (success) {
                                        Toast.makeText(context, "Correo enviado a ${userProfile.email}", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, err ?: "Error al enviar correo.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = VoidBlack)
                    ) {
                        Text("Enviar Correo de Cambio", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showSecurityDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = DeepSpaceBlue
        )
    }

    // Modal 3: Confirmación de Cierre de Sesión
    if (showSignOutConfirmDialog) {
        DeleteConfirmationDialog(
            title = "¿Cerrar Sesión?",
            message = "¿Estás seguro de que deseas salir de tu cuenta en Kepler-1652b?",
            confirmText = "Cerrar Sesión",
            cancelText = "Cancelar",
            onConfirm = {
                showSignOutConfirmDialog = false
                onSignOut()
            },
            onDismiss = { showSignOutConfirmDialog = false }
        )
    }
}

@Composable
fun CampaignCard(
    campaign: Campaign,
    onOpenMaster: () -> Unit,
    onOpenPlayer: () -> Unit,
    onDeleteCampaign: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (campaign.isCurrentUserMaster) WarningAmber.copy(alpha = 0.8f) else NeonCyan.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onOpenPlayer() },
        colors = CardDefaults.cardColors(containerColor = DeepSpaceBlue),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
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
                            text = if (campaign.isCurrentUserMaster) "PROPIETARIO" else "JUGADOR / TRIPULANTE",
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
                                Toast.makeText(context, "¡Código '${campaign.inviteCode}' copiado!", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(12.dp))
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
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Máster: ${campaign.masterName}",
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )

                    if (campaign.isCurrentUserMaster) {
                        Text(
                            text = "Entrar como Máster 👑",
                            fontSize = 11.sp,
                            color = WarningAmber,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onOpenMaster() }
                                .padding(4.dp)
                        )
                    } else {
                        Text(
                            text = "Entrar →",
                            fontSize = 11.sp,
                            color = CyanCurvature,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Baúl de Personajes Guardados", showBackground = true)
@Composable
fun InlineSavedCharactersContentPreview() {
    Kepler1652bTheme {
        InlineSavedCharactersContent(
            savedCharacters = listOf(
                Character(
                    id = "1",
                    characterName = "Cmdte. Vance",
                    characterClass = "Médico",
                    level = 3,
                    currentHp = 27,
                    activeCampaignId = null,
                    activeCampaignName = null
                ),
                Character(
                    id = "2",
                    characterName = "Hans Jaeger",
                    characterClass = "Soldado de Asalto",
                    level = 5,
                    currentHp = 42,
                    activeCampaignId = "c1",
                    activeCampaignName = "Misión Artemisa"
                )
            ),
            userDisplayName = "Cmdte. Vance",
            onCreateNewStandaloneCharacter = {},
            onSelectSavedCharacter = {}
        )
    }
}
