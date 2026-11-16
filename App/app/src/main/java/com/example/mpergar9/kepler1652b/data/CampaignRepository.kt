package com.example.mpergar9.kepler1652b.data

import com.example.mpergar9.kepler1652b.model.Campaign
import com.example.mpergar9.kepler1652b.model.Character
import com.example.mpergar9.kepler1652b.model.CharacterFaction
import com.example.mpergar9.kepler1652b.model.InventoryItem
import com.example.mpergar9.kepler1652b.model.SpecialStats
import com.example.mpergar9.kepler1652b.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.PersistentCacheSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object CampaignRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val syncJobs = ConcurrentHashMap<String, Job>()
    private val activeCharListeners = ConcurrentHashMap<String, ListenerRegistration>()

    private val firestore: FirebaseFirestore? by lazy {
        try {
            val instance = FirebaseFirestore.getInstance()
            try {
                val settings = FirebaseFirestoreSettings.Builder()
                    .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                    .build()
                instance.firestoreSettings = settings
            } catch (_: Exception) {}
            instance
        } catch (_: Exception) {
            null
        }
    }

    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth? by lazy {
        try {
            com.google.firebase.auth.FirebaseAuth.getInstance()
        } catch (_: Exception) {
            null
        }
    }

    private val _currentUserProfile = MutableStateFlow(
        UserProfile(id = "", displayName = "Explorador")
    )
    val currentUserProfile: StateFlow<UserProfile> = _currentUserProfile.asStateFlow()

    private val _localJoinedCampaignIds = MutableStateFlow<Set<String>>(emptySet())
    private val _allFetchedCampaigns = MutableStateFlow<List<Campaign>>(emptyList())

    fun registerUserWithEmail(
        email: String,
        password: String,
        displayName: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val auth = firebaseAuth
        if (auth == null) {
            onResult(false, "Servicio de autenticación no disponible.")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                if (uid.isNotBlank()) {
                    val profile = UserProfile(id = uid, displayName = displayName, email = email)
                    _currentUserProfile.value = profile

                    firestore?.collection("users")?.document(uid)?.set(
                        mapOf(
                            "uid" to uid,
                            "displayName" to displayName,
                            "email" to email,
                            "createdAt" to System.currentTimeMillis()
                        )
                    )

                    filterUserVisibleCampaigns()
                    onResult(true, null)
                } else {
                    onResult(false, "No se pudo obtener el identificador de usuario.")
                }
            }
            .addOnFailureListener { exception ->
                val msg = exception.localizedMessage ?: ""
                if (msg.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true)) {
                    onResult(false, "Debes habilitar el proveedor 'Correo electrónico/Contraseña' en la consola de Firebase (Authentication > Sign-in method > Correo electrónico/Contraseña > Habilitar).")
                } else {
                    onResult(false, msg.ifBlank { "Error al registrar usuario." })
                }
            }
    }

    fun loginUserWithEmail(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val auth = firebaseAuth
        if (auth == null) {
            onResult(false, "Servicio de autenticación no disponible.")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                if (uid.isNotBlank()) {
                    fetchUserProfileFromFirestore(uid) { fetchedProfile ->
                        _currentUserProfile.value = fetchedProfile ?: UserProfile(id = uid, displayName = email.substringBefore("@"), email = email)
                        filterUserVisibleCampaigns()
                        onResult(true, null)
                    }
                } else {
                    onResult(false, "Identificador no válido.")
                }
            }
            .addOnFailureListener { exception ->
                val msg = exception.localizedMessage ?: ""
                if (msg.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true)) {
                    onResult(false, "Debes habilitar el proveedor 'Correo electrónico/Contraseña' en la consola de Firebase (Authentication > Sign-in method > Correo electrónico/Contraseña > Habilitar).")
                } else {
                    onResult(false, msg.ifBlank { "Error de autenticación." })
                }
            }
    }

    private fun fetchUserProfileFromFirestore(uid: String, onComplete: (UserProfile?) -> Unit) {
        val db = firestore
        if (db == null) {
            onComplete(null)
            return
        }
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val name = doc.getString("displayName") ?: "Explorador"
                    val email = doc.getString("email") ?: ""
                    onComplete(UserProfile(id = uid, displayName = name, email = email))
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    fun signOut() {
        firebaseAuth?.signOut()
        _currentUserProfile.value = UserProfile(id = "", displayName = "Explorador", email = "")
        _campaigns.value = emptyList()
        _characters.value = emptyList()
    }

    fun initLocalProfile(userId: String, displayName: String, joinedCampaignIds: Set<String> = emptySet()) {
        val currentAuth = firebaseAuth?.currentUser
        val activeUid = currentAuth?.uid ?: userId
        val activeEmail = currentAuth?.email ?: ""
        
        if (currentAuth != null) {
            fetchUserProfileFromFirestore(activeUid) { profile ->
                _currentUserProfile.value = profile ?: UserProfile(id = activeUid, displayName = displayName, email = activeEmail)
                _localJoinedCampaignIds.value = joinedCampaignIds
                filterUserVisibleCampaigns()
            }
        } else {
            _currentUserProfile.value = UserProfile(id = activeUid, displayName = displayName, email = activeEmail)
            _localJoinedCampaignIds.value = joinedCampaignIds
            filterUserVisibleCampaigns()
        }
    }

    fun updateProfileName(newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isNotEmpty()) {
            _currentUserProfile.value = _currentUserProfile.value.copy(displayName = trimmed)
            val uid = _currentUserProfile.value.id
            if (uid.isNotBlank()) {
                firestore?.collection("users")?.document(uid)?.update("displayName", trimmed)
            }
            filterUserVisibleCampaigns()
        }
    }

    private val _campaigns = MutableStateFlow<List<Campaign>>(emptyList())
    val campaigns: StateFlow<List<Campaign>> = _campaigns.asStateFlow()

    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    val characters: StateFlow<List<Character>> = _characters.asStateFlow()

    init {
        startFirestoreListeners()
    }

    private fun filterUserVisibleCampaigns() {
        val currentUserId = _currentUserProfile.value.id
        val localJoined = _localJoinedCampaignIds.value

        val visible = _allFetchedCampaigns.value.filter { camp ->
            val isMaster = camp.masterId == currentUserId && currentUserId.isNotBlank()
            val isJoinedInFirestore = camp.joinedPlayerIds.contains(currentUserId)
            val isJoinedLocally = localJoined.contains(camp.id)
            val hasCharacterInCampaign = _characters.value.any { it.campaignId == camp.id && it.playerId == currentUserId }

            isMaster || isJoinedInFirestore || isJoinedLocally || hasCharacterInCampaign
        }.map { camp ->
            val isMaster = camp.masterId == currentUserId && currentUserId.isNotBlank()
            camp.copy(isCurrentUserMaster = isMaster)
        }

        _campaigns.value = visible
        updateCharacterListenersForVisibleCampaigns(visible)
    }

    private fun updateCharacterListenersForVisibleCampaigns(visibleCampaigns: List<Campaign>) {
        val db = firestore ?: return
        val visibleIds = visibleCampaigns.map { it.id }.toSet()

        // Unsubscribe from campaigns that are no longer visible or joined
        val toRemove = activeCharListeners.keys.filter { !visibleIds.contains(it) }
        toRemove.forEach { id ->
            activeCharListeners[id]?.remove()
            activeCharListeners.remove(id)
        }

        // Subscribe ONLY to visible campaigns
        visibleCampaigns.forEach { camp ->
            if (!activeCharListeners.containsKey(camp.id)) {
                val listener = db.collection("campaigns").document(camp.id)
                    .collection("characters")
                    .addSnapshotListener { charSnapshot, charError ->
                        if (charError == null && charSnapshot != null) {
                            val fetchedChars = charSnapshot.documents.mapNotNull { charDoc ->
                                parseCharacter(charDoc, camp.id)
                            }
                            val existingOtherChars = _characters.value.filter { it.campaignId != camp.id }
                            _characters.value = existingOtherChars + fetchedChars
                        }
                    }
                activeCharListeners[camp.id] = listener
            }
        }
    }

    private fun startFirestoreListeners() {
        val db = firestore ?: return
        db.collection("campaigns").addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            val fetchedCampaigns = snapshot.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: doc.id
                val name = doc.getString("name") ?: ""
                val description = doc.getString("description") ?: ""
                val inviteCode = doc.getString("inviteCode") ?: ""
                
                val masterMap = doc.get("master") as? Map<*, *>
                val masterId = masterMap?.get("masterId")?.toString() ?: doc.getString("masterId") ?: ""
                val masterName = masterMap?.get("masterName")?.toString() ?: doc.getString("masterName") ?: "Director de Juego"

                val uiStateMap = doc.get("uiState") as? Map<*, *>
                val imageBannerId = uiStateMap?.get("imageBannerId")?.toString() ?: doc.getString("imageBannerId") ?: "kepler_orbit"

                val joinedList = (doc.get("joinedPlayerIds") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()

                Campaign(
                    id = id,
                    name = name,
                    description = description,
                    masterName = masterName,
                    masterId = masterId,
                    inviteCode = inviteCode,
                    imageBannerId = imageBannerId,
                    isCurrentUserMaster = false,
                    joinedPlayerIds = joinedList
                )
            }
            _allFetchedCampaigns.value = fetchedCampaigns
            filterUserVisibleCampaigns()
        }
    }

    private fun parseCharacter(doc: com.google.firebase.firestore.DocumentSnapshot, campaignId: String): Character {
        val id = doc.getString("id") ?: doc.id
        val playerId = doc.getString("playerId") ?: "usr-player-1"
        val playerName = doc.getString("playerName") ?: ""

        val identityMap = doc.get("identity") as? Map<*, *>
        val characterName = identityMap?.get("characterName")?.toString() ?: doc.getString("characterName") ?: ""
        val characterClass = identityMap?.get("characterClass")?.toString() ?: doc.getString("characterClass") ?: ""
        val subclass = identityMap?.get("subclass")?.toString() ?: doc.getString("subclass") ?: ""
        val factionStr = identityMap?.get("faction")?.toString() ?: identityMap?.get("faction")?.toString() ?: doc.getString("faction") ?: "UNION_HUMANA"
        val faction = try { CharacterFaction.valueOf(factionStr) } catch (e: Exception) { CharacterFaction.UNION_HUMANA }

        val progressionMap = doc.get("progression") as? Map<*, *>
        val level = (progressionMap?.get("level") as? Number)?.toInt() ?: doc.getLong("level")?.toInt() ?: 1
        val pendingLevelUps = (progressionMap?.get("pendingLevelUps") as? Number)?.toInt() ?: doc.getLong("pendingLevelUps")?.toInt() ?: 0

        val vitalsMap = doc.get("vitals") as? Map<*, *>
        val isAlive = (vitalsMap?.get("isAlive") as? Boolean) ?: doc.getBoolean("isAlive") ?: true
        val currentHp = (vitalsMap?.get("currentHp") as? Number)?.toInt() ?: doc.getLong("currentHp")?.toInt() ?: 30
        val currentSanity = (vitalsMap?.get("currentSanity") as? Number)?.toInt() ?: doc.getLong("currentSanity")?.toInt() ?: 100
        val radiationLevel = (vitalsMap?.get("radiationLevel") as? Number)?.toInt() ?: doc.getLong("radiationLevel")?.toInt() ?: 0
        val toxicityPoison = (vitalsMap?.get("toxicityPoison") as? Number)?.toInt() ?: doc.getLong("toxicityPoison")?.toInt() ?: 0
        val soporteVitalLevel = (vitalsMap?.get("soporteVitalLevel") as? Number)?.toInt() ?: doc.getLong("soporteVitalLevel")?.toInt() ?: 100
        val statusEffects = (vitalsMap?.get("statusEffects") as? List<*>)?.mapNotNull { it?.toString() } 
            ?: (doc.get("statusEffects") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()

        val unlockedPerks = (doc.get("unlockedPerks") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()

        val inventoryList = (doc.get("inventory") as? List<*>)?.mapNotNull { itemObj ->
            val map = itemObj as? Map<*, *> ?: return@mapNotNull null
            InventoryItem(
                id = map["id"]?.toString() ?: UUID.randomUUID().toString(),
                name = map["name"]?.toString() ?: "",
                description = map["description"]?.toString() ?: "",
                size = (map["size"] as? Number)?.toInt() ?: 1,
                statBonusStat = map["statBonusStat"]?.toString()?.takeIf { it.isNotBlank() },
                statBonusValue = (map["statBonusValue"] as? Number)?.toInt() ?: 0,
                credits = (map["credits"] as? Number)?.toInt() ?: 0
            )
        } ?: emptyList()

        val statsMap = doc.get("stats") as? Map<*, *>
        val stats = SpecialStats(
            fue = (statsMap?.get("fue") as? Long)?.toInt() ?: (statsMap?.get("FUE") as? Long)?.toInt() ?: 3,
            per = (statsMap?.get("per") as? Long)?.toInt() ?: (statsMap?.get("PER") as? Long)?.toInt() ?: 3,
            end = (statsMap?.get("end") as? Long)?.toInt() ?: (statsMap?.get("END") as? Long)?.toInt() ?: 3,
            car = (statsMap?.get("car") as? Long)?.toInt() ?: (statsMap?.get("CAR") as? Long)?.toInt() ?: 3,
            agi = (statsMap?.get("agi") as? Long)?.toInt() ?: (statsMap?.get("AGI") as? Long)?.toInt() ?: 3,
            int = (statsMap?.get("int") as? Long)?.toInt() ?: (statsMap?.get("INT") as? Long)?.toInt() ?: 3,
            sue = (statsMap?.get("sue") as? Long)?.toInt() ?: (statsMap?.get("SUE") as? Long)?.toInt() ?: 3
        )

        val skillsMap = mutableMapOf<String, Int>()
        val rawSkills = doc.get("skills") as? Map<*, *>
        rawSkills?.forEach { (k, v) ->
            if (k != null && v != null) {
                val intVal = when (v) {
                    is Number -> v.toInt()
                    else -> v.toString().toIntOrNull() ?: 0
                }
                skillsMap[k.toString()] = intVal
            }
        }

        val parsedChar = Character(
            id = id,
            campaignId = campaignId,
            playerId = playerId,
            playerName = playerName,
            characterName = characterName,
            faction = faction,
            characterClass = characterClass,
            subclass = subclass,
            level = level,
            stats = stats,
            currentHp = currentHp,
            radiationLevel = radiationLevel,
            toxicityPoison = toxicityPoison,
            soporteVitalLevel = soporteVitalLevel,
            currentSanity = currentSanity,
            statusEffects = statusEffects,
            skills = skillsMap,
            unlockedPerks = unlockedPerks,
            isAlive = isAlive,
            pendingLevelUps = pendingLevelUps,
            inventory = inventoryList
        )

        if (doc.get("identity") == null) {
            syncCharacterToFirestore(parsedChar)
        }

        return parsedChar
    }

    fun createCampaign(name: String, description: String, masterName: String): Campaign {
        val currentUserId = _currentUserProfile.value.id
        val effectiveMaster = masterName.ifBlank { _currentUserProfile.value.displayName }
        val newCampaign = Campaign(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            masterName = effectiveMaster,
            masterId = currentUserId,
            inviteCode = "KPL-" + (1000..9999).random(),
            imageBannerId = "kepler_orbit",
            isCurrentUserMaster = true,
            joinedPlayerIds = listOf(currentUserId)
        )
        _allFetchedCampaigns.value = _allFetchedCampaigns.value + newCampaign
        _localJoinedCampaignIds.value = _localJoinedCampaignIds.value + newCampaign.id
        filterUserVisibleCampaigns()

        firestore?.collection("campaigns")?.document(newCampaign.id)?.set(
            mapOf(
                "id" to newCampaign.id,
                "name" to newCampaign.name,
                "description" to newCampaign.description,
                "inviteCode" to newCampaign.inviteCode,
                "master" to mapOf(
                    "masterId" to currentUserId,
                    "masterName" to newCampaign.masterName
                ),
                "joinedPlayerIds" to listOf(currentUserId),
                "uiState" to mapOf(
                    "imageBannerId" to newCampaign.imageBannerId
                )
            )
        )
        return newCampaign
    }

    fun deleteCampaign(campaignId: String) {
        _allFetchedCampaigns.value = _allFetchedCampaigns.value.filter { it.id != campaignId }
        _localJoinedCampaignIds.value = _localJoinedCampaignIds.value - campaignId
        filterUserVisibleCampaigns()
        _characters.value = _characters.value.filter { it.campaignId != campaignId }

        val db = firestore ?: return
        val campaignRef = db.collection("campaigns").document(campaignId)
        campaignRef.collection("characters").get().addOnSuccessListener { snapshot ->
            val batch = db.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.delete(campaignRef)
            batch.commit()
        }.addOnFailureListener {
            campaignRef.delete()
        }
    }

    fun joinCampaignByCode(code: String): Campaign? {
        val trimmed = code.trim().uppercase()
        val currentUserId = _currentUserProfile.value.id

        val found = _allFetchedCampaigns.value.find { it.inviteCode.trim().equals(trimmed, ignoreCase = true) }
        if (found != null) {
            _localJoinedCampaignIds.value = _localJoinedCampaignIds.value + found.id
            firestore?.collection("campaigns")?.document(found.id)?.update(
                "joinedPlayerIds", com.google.firebase.firestore.FieldValue.arrayUnion(currentUserId)
            )
            filterUserVisibleCampaigns()
            val isMaster = found.masterId == currentUserId && currentUserId.isNotBlank()
            return found.copy(isCurrentUserMaster = isMaster)
        }
        return null
    }

    fun removePlayerFromCampaign(campaignId: String, playerId: String, charId: String? = null) {
        val db = firestore
        if (db != null) {
            db.collection("campaigns").document(campaignId).update(
                "joinedPlayerIds", com.google.firebase.firestore.FieldValue.arrayRemove(playerId)
            )
        }
        if (!charId.isNullOrBlank()) {
            deleteCharacter(charId)
        } else {
            _characters.value = _characters.value.filter { !(it.campaignId == campaignId && it.playerId == playerId) }
        }
        filterUserVisibleCampaigns()
    }

    fun addCharacter(character: Character) {
        val profile = _currentUserProfile.value
        val assignedPlayerId = if (character.playerId.isBlank()) profile.id else character.playerId
        val assignedPlayerName = if (character.playerName.isBlank()) profile.displayName else character.playerName
        val newChar = character.copy(
            id = if (character.id.isEmpty()) UUID.randomUUID().toString() else character.id,
            playerId = assignedPlayerId,
            playerName = assignedPlayerName
        )
        _characters.value = _characters.value + newChar
        syncCharacterToFirestore(newChar, debounceMillis = 0L)
    }

    private fun syncCharacterToFirestore(character: Character, debounceMillis: Long = 400L) {
        val db = firestore ?: return

        syncJobs[character.id]?.cancel()

        val task = Runnable {
            val data = mapOf(
                "id" to character.id,
                "campaignId" to character.campaignId,
                "playerId" to character.playerId,
                "playerName" to character.playerName,
                "identity" to mapOf(
                    "characterName" to character.characterName,
                    "characterClass" to character.characterClass,
                    "subclass" to character.subclass,
                    "faction" to character.faction.name
                ),
                "progression" to mapOf(
                    "level" to character.level,
                    "pendingLevelUps" to character.pendingLevelUps
                ),
                "vitals" to mapOf(
                    "isAlive" to character.isAlive,
                    "currentHp" to character.currentHp,
                    "currentSanity" to character.currentSanity,
                    "radiationLevel" to character.radiationLevel,
                    "toxicityPoison" to character.toxicityPoison,
                    "soporteVitalLevel" to character.soporteVitalLevel,
                    "statusEffects" to character.statusEffects
                ),
                "stats" to mapOf(
                    "FUE" to character.stats.fue,
                    "PER" to character.stats.per,
                    "END" to character.stats.end,
                    "CAR" to character.stats.car,
                    "AGI" to character.stats.agi,
                    "INT" to character.stats.int,
                    "SUE" to character.stats.sue
                ),
                "skills" to character.skills,
                "unlockedPerks" to character.unlockedPerks,
                "inventory" to character.inventory.map { item ->
                    mapOf(
                        "id" to item.id,
                        "name" to item.name,
                        "description" to item.description,
                        "size" to item.size,
                        "statBonusStat" to (item.statBonusStat ?: ""),
                        "statBonusValue" to item.statBonusValue,
                        "credits" to item.credits
                    )
                }
            )
            db.collection("campaigns").document(character.campaignId)
                .collection("characters").document(character.id)
                .set(data)
        }

        if (debounceMillis <= 0L) {
            task.run()
        } else {
            val job = repositoryScope.launch {
                delay(debounceMillis)
                task.run()
            }
            syncJobs[character.id] = job
        }
    }

    fun addItemToInventory(charId: String, name: String, description: String, size: Int, statBonusStat: String?, statBonusValue: Int, credits: Int = 0) {
        _characters.value = _characters.value.map {
            if (it.id == charId) {
                val newItem = InventoryItem(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    description = description,
                    size = size,
                    statBonusStat = statBonusStat?.ifBlank { null },
                    statBonusValue = statBonusValue,
                    credits = credits
                )
                val updated = it.copy(inventory = it.inventory + newItem)
                val overEncumbered = updated.currentInventorySlotsUsed > updated.maxInventorySlots
                val effects = updated.statusEffects.toMutableList()
                if (overEncumbered) {
                    if (!effects.contains("Movimiento Lento")) effects.add("Movimiento Lento")
                } else {
                    effects.remove("Movimiento Lento")
                }
                val finalUpdated = updated.copy(statusEffects = effects)
                syncCharacterToFirestore(finalUpdated)
                finalUpdated
            } else it
        }
    }

    fun removeItemFromInventory(charId: String, itemId: String) {
        _characters.value = _characters.value.map {
            if (it.id == charId) {
                val updated = it.copy(inventory = it.inventory.filter { item -> item.id != itemId })
                val overEncumbered = updated.currentInventorySlotsUsed > updated.maxInventorySlots
                val effects = updated.statusEffects.toMutableList()
                if (!overEncumbered) {
                    effects.remove("Movimiento Lento")
                }
                val finalUpdated = updated.copy(statusEffects = effects)
                syncCharacterToFirestore(finalUpdated)
                finalUpdated
            } else it
        }
    }

    fun updateCharacterHp(charId: String, newHp: Int) {
        _characters.value = _characters.value.map {
            if (it.id == charId) {
                val clamped = newHp.coerceIn(0, it.stats.maxHp)
                val updated = it.copy(currentHp = clamped)
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun updateCharacterRadiation(charId: String, newRad: Int) {
        _characters.value = _characters.value.map {
            if (it.id == charId) {
                val updated = it.copy(radiationLevel = newRad.coerceIn(0, 100))
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun updateCharacterPoison(charId: String, newTox: Int) {
        _characters.value = _characters.value.map {
            if (it.id == charId) {
                val updated = it.copy(toxicityPoison = newTox.coerceIn(0, 100))
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun updateCharacterSanity(charId: String, newSanity: Int) {
        _characters.value = _characters.value.map {
            if (it.id == charId) {
                val updated = it.copy(currentSanity = newSanity.coerceIn(0, 100))
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun updateCharacterSoporteVital(charId: String, newLevel: Int) {
        _characters.value = _characters.value.map {
            if (it.id == charId) {
                val updated = it.copy(soporteVitalLevel = newLevel.coerceIn(0, 100))
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun updateCharacterOxygen(charId: String, newO2: Int) = updateCharacterSoporteVital(charId, newO2)

    fun toggleStatusEffect(charId: String, effect: String) {
        _characters.value = _characters.value.map {
            if (it.id == charId) {
                val current = it.statusEffects.toMutableList()
                if (current.contains(effect)) current.remove(effect) else current.add(effect)
                val updated = it.copy(statusEffects = current)
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun applyGlobalRadiationDelta(campaignId: String, delta: Int) {
        _characters.value = _characters.value.map {
            if (it.campaignId == campaignId) {
                val updated = it.copy(radiationLevel = (it.radiationLevel + delta).coerceIn(0, 100))
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun applyGlobalToxicityDelta(campaignId: String, delta: Int) {
        _characters.value = _characters.value.map {
            if (it.campaignId == campaignId) {
                val updated = it.copy(toxicityPoison = (it.toxicityPoison + delta).coerceIn(0, 100))
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun applyGlobalSoporteVitalDelta(campaignId: String, delta: Int) {
        _characters.value = _characters.value.map {
            if (it.campaignId == campaignId) {
                val updated = it.copy(soporteVitalLevel = (it.soporteVitalLevel + delta).coerceIn(0, 100))
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun applyGlobalSanityDelta(campaignId: String, delta: Int) {
        _characters.value = _characters.value.map {
            if (it.campaignId == campaignId) {
                val updated = it.copy(currentSanity = (it.currentSanity + delta).coerceIn(0, 100))
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun setGlobalEnvironment(campaignId: String, rad: Int?, tox: Int?, soporteVital: Int?) {
        _characters.value = _characters.value.map {
            if (it.campaignId == campaignId) {
                var updated = it
                if (rad != null) updated = updated.copy(radiationLevel = rad.coerceIn(0, 100))
                if (tox != null) updated = updated.copy(toxicityPoison = tox.coerceIn(0, 100))
                if (soporteVital != null) updated = updated.copy(soporteVitalLevel = soporteVital.coerceIn(0, 100))
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun applyGlobalStatusEffect(campaignId: String, effect: String, applyToAll: Boolean) {
        _characters.value = _characters.value.map {
            if (it.campaignId == campaignId) {
                val current = it.statusEffects.toMutableList()
                if (applyToAll) {
                    if (!current.contains(effect)) current.add(effect)
                } else {
                    current.remove(effect)
                }
                val updated = it.copy(statusEffects = current)
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun clearAllGlobalStatusEffects(campaignId: String) {
        _characters.value = _characters.value.map {
            if (it.campaignId == campaignId) {
                val updated = it.copy(statusEffects = emptyList())
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun changeCharacterLevel(charId: String, delta: Int) {
        _characters.value = _characters.value.map {
            if (it.id == charId) {
                val newLevel = (it.level + delta).coerceAtLeast(1)
                val newPending = if (delta > 0) it.pendingLevelUps + delta else it.pendingLevelUps
                val updated = it.copy(level = newLevel, pendingLevelUps = newPending)
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun levelUpCharacter(charId: String) = changeCharacterLevel(charId, 1)

    fun claimLevelUp(charId: String, newStats: SpecialStats, newSkills: Map<String, Int>, newPerks: List<String>) {
        _characters.value = _characters.value.map {
            if (it.id == charId) {
                val updated = it.copy(
                    stats = newStats,
                    skills = newSkills,
                    unlockedPerks = newPerks,
                    pendingLevelUps = (it.pendingLevelUps - 1).coerceAtLeast(0)
                )
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun updateStats(charId: String, newStats: SpecialStats) {
        _characters.value = _characters.value.map {
            if (it.id == charId) {
                val updated = it.copy(stats = newStats)
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun updateSkill(charId: String, skillName: String, deltaPoints: Int) {
        _characters.value = _characters.value.map {
            if (it.id == charId) {
                val currentSkills = it.skills.toMutableMap()
                val currentVal = currentSkills[skillName] ?: 0
                val newVal = (currentVal + deltaPoints).coerceIn(0, 100)
                currentSkills[skillName] = newVal
                val updated = it.copy(skills = currentSkills)
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun unlockPerk(charId: String, perkName: String) {
        _characters.value = _characters.value.map {
            if (it.id == charId && !it.unlockedPerks.contains(perkName)) {
                val updated = it.copy(unlockedPerks = it.unlockedPerks + perkName)
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun toggleCharacterLifeStatus(charId: String) {
        _characters.value = _characters.value.map {
            if (it.id == charId) {
                val updated = it.copy(isAlive = !it.isAlive)
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun setCharacterLifeAndStatus(charId: String, isAlive: Boolean, incapacitated: Boolean) {
        _characters.value = _characters.value.map {
            if (it.id == charId) {
                val effects = it.statusEffects.toMutableList()
                if (incapacitated) {
                    if (!effects.contains("Incapacitado")) effects.add("Incapacitado")
                } else {
                    effects.remove("Incapacitado")
                }
                val updated = it.copy(isAlive = isAlive, statusEffects = effects)
                syncCharacterToFirestore(updated)
                updated
            } else it
        }
    }

    fun deleteCharacter(charId: String) {
        val charToDelete = _characters.value.find { it.id == charId }
        _characters.value = _characters.value.filter { it.id != charId }
        if (charToDelete != null) {
            firestore?.collection("campaigns")?.document(charToDelete.campaignId)
                ?.collection("characters")?.document(charToDelete.id)?.delete()
        }
    }
}
