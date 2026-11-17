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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object CampaignRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val syncJobs = ConcurrentHashMap<String, Job>()
    private val activeCharListeners = ConcurrentHashMap<String, ListenerRegistration>()
    private var campaignsListener: ListenerRegistration? = null
    private var vaultListener: ListenerRegistration? = null

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
                    startFirestoreListeners()
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
                        startFirestoreListeners()
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

    fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
        val auth = firebaseAuth
        if (auth == null) {
            onResult(false, "Servicio de autenticación no disponible.")
            return
        }
        auth.sendPasswordResetEmail(email.trim())
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { exception ->
                onResult(false, exception.localizedMessage ?: "Error al enviar el correo de recuperación.")
            }
    }

    fun loginWithGoogleToken(idToken: String, onResult: (Boolean, String?) -> Unit) {
        val auth = firebaseAuth
        if (auth == null) {
            onResult(false, "Servicio de autenticación no disponible.")
            return
        }
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                val uid = user?.uid ?: ""
                val displayName = user?.displayName?.takeIf { it.isNotBlank() } ?: "Explorador"
                val email = user?.email ?: ""
                if (uid.isNotBlank()) {
                    val profile = UserProfile(id = uid, displayName = displayName, email = email)
                    _currentUserProfile.value = profile

                    firestore?.collection("users")?.document(uid)?.set(
                        mapOf(
                            "uid" to uid,
                            "displayName" to displayName,
                            "email" to email,
                            "createdAt" to System.currentTimeMillis()
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )

                    filterUserVisibleCampaigns()
                    startFirestoreListeners()
                    onResult(true, null)
                } else {
                    onResult(false, "No se pudo obtener el identificador de usuario.")
                }
            }
            .addOnFailureListener { exception ->
                onResult(false, exception.localizedMessage ?: "Error de autenticación con Google.")
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

    fun restoreSessionIfSignedIn(): Boolean {
        val currentAuth = firebaseAuth?.currentUser ?: return false
        val uid = currentAuth.uid
        if (uid.isBlank()) return false

        val fallbackName = currentAuth.displayName?.takeIf { it.isNotBlank() }
            ?: currentAuth.email?.substringBefore("@")?.takeIf { it.isNotBlank() }
            ?: "Explorador"

        _currentUserProfile.value = UserProfile(
            id = uid,
            displayName = fallbackName,
            email = currentAuth.email ?: ""
        )
        fetchUserProfileFromFirestore(uid) { fetchedProfile ->
            if (fetchedProfile != null) {
                _currentUserProfile.value = fetchedProfile
                filterUserVisibleCampaigns()
            }
        }
        startFirestoreListeners()
        return true
    }

    fun signOut() {
        syncJobs.values.forEach { it.cancel() }
        syncJobs.clear()

        campaignsListener?.remove()
        campaignsListener = null
        vaultListener?.remove()
        vaultListener = null
        activeCharListeners.values.forEach { it.remove() }
        activeCharListeners.clear()

        firebaseAuth?.signOut()
        _currentUserProfile.value = UserProfile(id = "", displayName = "Explorador", email = "")
        _localJoinedCampaignIds.value = emptySet()
        _allFetchedCampaigns.value = emptyList()
        _campaigns.value = emptyList()
        _characters.value = emptyList()
        _savedUserCharacters.value = emptyList()
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
            if (uid.isNotBlank() && firestore != null) {
                val db = firestore!!
                // 1. Actualizar en colección de usuarios
                db.collection("users").document(uid).update("displayName", trimmed)

                // 2. Actualizar en todas las campañas creadas por el usuario como Máster
                _allFetchedCampaigns.value.filter { it.masterId == uid }.forEach { camp ->
                    db.collection("campaigns").document(camp.id).update("master.masterName", trimmed)
                }

                // 3. Actualizar en todos los personajes pertenecientes a este jugador
                _characters.value.filter { it.playerId == uid }.forEach { char ->
                    db.collection("campaigns").document(char.campaignId)
                        .collection("characters").document(char.id)
                        .update("playerName", trimmed)
                }
            }
            filterUserVisibleCampaigns()
        }
    }

    private val _campaigns = MutableStateFlow<List<Campaign>>(emptyList())
    val campaigns: StateFlow<List<Campaign>> = _campaigns.asStateFlow()

    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    val characters: StateFlow<List<Character>> = _characters.asStateFlow()

    init {
        restoreSessionIfSignedIn()
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
                            _characters.update { current ->
                                current.filter { it.campaignId != camp.id } + fetchedChars
                            }
                        }
                    }
                activeCharListeners[camp.id] = listener
            }
        }
    }

    private fun startFirestoreListeners() {
        val db = firestore ?: return
        val uid = _currentUserProfile.value.id.ifBlank { firebaseAuth?.currentUser?.uid ?: "" }
        if (uid.isBlank()) return

        campaignsListener?.remove()
        campaignsListener = db.collection("campaigns").addSnapshotListener { snapshot, error ->
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

        vaultListener?.remove()
        vaultListener = db.collection("users").document(uid)
            .collection("saved_characters")
            .addSnapshotListener { vaultSnapshot, vaultError ->
                if (vaultError == null && vaultSnapshot != null) {
                    val fetchedVault = vaultSnapshot.documents.mapNotNull { doc ->
                        val campId = doc.getString("campaignId") ?: ""
                        parseCharacter(doc, campId)
                    }
                    _savedUserCharacters.value = fetchedVault
                }
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
        val statusPrivilege = identityMap?.get("statusPrivilege")?.toString()?.takeIf { it.isNotBlank() }
            ?: doc.getString("statusPrivilege")?.takeIf { it.isNotBlank() }
        val factionStr = identityMap?.get("faction")?.toString() ?: doc.getString("faction") ?: "UNION_HUMANA"
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

        val activeCampaignId = doc.getString("activeCampaignId")?.takeIf { it.isNotBlank() }
        val activeCampaignName = doc.getString("activeCampaignName")?.takeIf { it.isNotBlank() }

        val parsedChar = Character(
            id = id,
            campaignId = campaignId,
            playerId = playerId,
            playerName = playerName,
            activeCampaignId = activeCampaignId ?: campaignId.takeIf { it.isNotBlank() },
            activeCampaignName = activeCampaignName,
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
            statusPrivilege = statusPrivilege,
            isAlive = isAlive,
            pendingLevelUps = pendingLevelUps,
            inventory = inventoryList
        )

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

        releaseVaultCharacters(campaignId = campaignId)

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

    fun formatInviteCode(raw: String): String {
        val clean = raw.uppercase().replace("[^A-Z0-9]".toRegex(), "")
        return if (clean.length > 3) {
            val prefix = clean.take(3)
            val suffix = clean.drop(3).take(4)
            "$prefix-$suffix"
        } else {
            clean
        }
    }

    fun joinCampaignByCode(code: String): Campaign? {
        val formatted = formatInviteCode(code)
        val currentUserId = _currentUserProfile.value.id

        val found = _allFetchedCampaigns.value.find { formatInviteCode(it.inviteCode) == formatted }
        if (found != null) {
            _localJoinedCampaignIds.update { it + found.id }
            if (currentUserId.isNotBlank()) {
                firestore?.collection("campaigns")?.document(found.id)?.update(
                    "joinedPlayerIds", com.google.firebase.firestore.FieldValue.arrayUnion(currentUserId)
                )
            }
            filterUserVisibleCampaigns()
            val isMaster = found.masterId == currentUserId && currentUserId.isNotBlank()
            return found.copy(isCurrentUserMaster = isMaster)
        }
        return null
    }

    fun joinCampaignByCode(code: String, onResult: (Campaign?) -> Unit) {
        val localMatch = joinCampaignByCode(code)
        if (localMatch != null) {
            onResult(localMatch)
            return
        }
        val db = firestore
        if (db == null) {
            onResult(null)
            return
        }
        val formatted = formatInviteCode(code)
        val currentUserId = _currentUserProfile.value.id
        db.collection("campaigns")
            .whereEqualTo("inviteCode", formatted)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents.firstOrNull()
                if (doc == null) {
                    onResult(null)
                    return@addOnSuccessListener
                }
                val id = doc.getString("id") ?: doc.id
                val masterMap = doc.get("master") as? Map<*, *>
                val masterId = masterMap?.get("masterId")?.toString() ?: doc.getString("masterId") ?: ""
                val masterName = masterMap?.get("masterName")?.toString() ?: doc.getString("masterName") ?: "Director de Juego"
                val uiStateMap = doc.get("uiState") as? Map<*, *>
                val imageBannerId = uiStateMap?.get("imageBannerId")?.toString() ?: doc.getString("imageBannerId") ?: "kepler_orbit"
                val joinedList = (doc.get("joinedPlayerIds") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()

                val campaign = Campaign(
                    id = id,
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    masterName = masterName,
                    masterId = masterId,
                    inviteCode = doc.getString("inviteCode") ?: formatted,
                    imageBannerId = imageBannerId,
                    isCurrentUserMaster = masterId == currentUserId && currentUserId.isNotBlank(),
                    joinedPlayerIds = joinedList
                )
                _localJoinedCampaignIds.update { it + campaign.id }
                if (currentUserId.isNotBlank()) {
                    doc.reference.update("joinedPlayerIds", com.google.firebase.firestore.FieldValue.arrayUnion(currentUserId))
                }
                filterUserVisibleCampaigns()
                onResult(campaign)
            }
            .addOnFailureListener {
                onResult(null)
            }
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
            val matchingIds = _characters.value
                .filter { it.campaignId == campaignId && it.playerId == playerId }
                .map { it.id }
            _characters.update { list -> list.filter { !(it.campaignId == campaignId && it.playerId == playerId) } }
            matchingIds.forEach { releaseVaultCharacters(charId = it) }
        }
        filterUserVisibleCampaigns()
    }

    private val _savedUserCharacters = MutableStateFlow<List<Character>>(emptyList())
    val savedUserCharacters: StateFlow<List<Character>> = _savedUserCharacters.asStateFlow()

    fun addCharacter(character: Character) {
        val profile = _currentUserProfile.value
        val assignedPlayerId = if (character.playerId.isBlank()) profile.id else character.playerId
        val assignedPlayerName = if (character.playerName.isBlank()) profile.displayName else character.playerName
        
        val campName = _allFetchedCampaigns.value.find { it.id == character.campaignId }?.name ?: character.activeCampaignName

        val newChar = character.copy(
            id = if (character.id.isEmpty()) UUID.randomUUID().toString() else character.id,
            playerId = assignedPlayerId,
            playerName = assignedPlayerName,
            activeCampaignId = character.campaignId.ifBlank { character.activeCampaignId },
            activeCampaignName = campName
        )
        
        if (newChar.campaignId.isNotBlank()) {
            _characters.update { current -> current.filter { it.id != newChar.id } + newChar }
            syncCharacterToFirestore(newChar, debounceMillis = 0L)
        }

        if (assignedPlayerId == profile.id && profile.id.isNotBlank()) {
            _savedUserCharacters.update { current ->
                current.filter { it.id != newChar.id } + newChar
            }
            saveCharacterToVaultFirestore(newChar)
        }
    }

    fun assignSavedCharacterToCampaign(campaignId: String, savedChar: Character) {
        val campName = _allFetchedCampaigns.value.find { it.id == campaignId }?.name ?: ""
        val updatedChar = savedChar.copy(
            campaignId = campaignId,
            activeCampaignId = campaignId,
            activeCampaignName = campName
        )
        addCharacter(updatedChar)
    }

    private fun saveCharacterToVaultFirestore(character: Character) {
        val db = firestore ?: return
        val uid = _currentUserProfile.value.id
        if (uid.isBlank()) return

        db.collection("users").document(uid)
            .collection("saved_characters").document(character.id)
            .set(characterToFirestoreMap(character))
    }

    private fun characterToFirestoreMap(character: Character): Map<String, Any> = mapOf(
        "id" to character.id,
        "campaignId" to character.campaignId,
        "playerId" to character.playerId,
        "playerName" to character.playerName,
        "activeCampaignId" to (character.activeCampaignId ?: ""),
        "activeCampaignName" to (character.activeCampaignName ?: ""),
        "identity" to mapOf(
            "characterName" to character.characterName,
            "characterClass" to character.characterClass,
            "subclass" to character.subclass,
            "faction" to character.faction.name,
            "statusPrivilege" to (character.statusPrivilege ?: "")
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

    private fun syncCharacterToFirestore(character: Character, debounceMillis: Long = 400L) {
        val db = firestore ?: return
        if (character.campaignId.isBlank() || character.id.isBlank()) return

        syncJobs.remove(character.id)?.cancel()

        val task = Runnable {
            db.collection("campaigns").document(character.campaignId)
                .collection("characters").document(character.id)
                .set(characterToFirestoreMap(character))
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

    private fun syncCharactersBatch(updatedCharacters: List<Character>) {
        val db = firestore ?: return
        if (updatedCharacters.isEmpty()) return

        updatedCharacters.forEach { ch ->
            syncJobs.remove(ch.id)?.cancel()
        }

        val batch = db.batch()
        updatedCharacters.forEach { ch ->
            if (ch.campaignId.isNotBlank() && ch.id.isNotBlank()) {
                val ref = db.collection("campaigns").document(ch.campaignId)
                    .collection("characters").document(ch.id)
                batch.set(ref, characterToFirestoreMap(ch))
            }
        }
        batch.commit()
    }

    private inline fun updateCharacter(charId: String, crossinline transform: (Character) -> Character) {
        var updatedChar: Character? = null
        _characters.update { list ->
            list.map {
                if (it.id == charId) {
                    val next = transform(it)
                    updatedChar = next
                    next
                } else it
            }
        }
        updatedChar?.let { syncCharacterToFirestore(it) }
    }

    private inline fun updateCampaignCharacters(campaignId: String, crossinline transform: (Character) -> Character) {
        val updatedList = mutableListOf<Character>()
        _characters.update { list ->
            list.map {
                if (it.campaignId == campaignId) {
                    val next = transform(it)
                    updatedList.add(next)
                    next
                } else it
            }
        }
        syncCharactersBatch(updatedList)
    }

    fun addItemToInventory(charId: String, name: String, description: String, size: Int, statBonusStat: String?, statBonusValue: Int, credits: Int = 0) {
        updateCharacter(charId) {
            val newItem = InventoryItem(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                size = size.coerceAtLeast(1),
                statBonusStat = statBonusStat?.ifBlank { null },
                statBonusValue = statBonusValue,
                credits = credits.coerceAtLeast(0)
            )
            val updated = it.copy(inventory = it.inventory + newItem)
            val overEncumbered = updated.currentInventorySlotsUsed > updated.maxInventorySlots
            val effects = updated.statusEffects.toMutableList()
            if (overEncumbered) {
                if (!effects.contains("Movimiento Lento")) effects.add("Movimiento Lento")
            } else {
                effects.remove("Movimiento Lento")
            }
            updated.copy(statusEffects = effects)
        }
    }

    fun removeItemFromInventory(charId: String, itemId: String) {
        updateCharacter(charId) {
            val updated = it.copy(inventory = it.inventory.filter { item -> item.id != itemId })
            val overEncumbered = updated.currentInventorySlotsUsed > updated.maxInventorySlots
            val effects = updated.statusEffects.toMutableList()
            if (!overEncumbered) {
                effects.remove("Movimiento Lento")
            }
            updated.copy(statusEffects = effects)
        }
    }

    fun updateCharacterHp(charId: String, newHp: Int) {
        updateCharacter(charId) {
            val maxHp = it.effectiveStats.maxHp
            it.copy(currentHp = newHp.coerceIn(0, maxHp))
        }
    }

    fun updateCharacterRadiation(charId: String, newRad: Int) {
        updateCharacter(charId) { it.copy(radiationLevel = newRad.coerceIn(0, 100)) }
    }

    fun updateCharacterPoison(charId: String, newTox: Int) {
        updateCharacter(charId) { it.copy(toxicityPoison = newTox.coerceIn(0, 100)) }
    }

    fun updateCharacterSanity(charId: String, newSanity: Int) {
        updateCharacter(charId) { it.copy(currentSanity = newSanity.coerceIn(0, 100)) }
    }

    fun updateCharacterSoporteVital(charId: String, newLevel: Int) {
        updateCharacter(charId) { it.copy(soporteVitalLevel = newLevel.coerceIn(0, 100)) }
    }

    fun updateCharacterOxygen(charId: String, newO2: Int) = updateCharacterSoporteVital(charId, newO2)

    fun toggleStatusEffect(charId: String, effect: String) {
        updateCharacter(charId) {
            val current = it.statusEffects.toMutableList()
            if (current.contains(effect)) current.remove(effect) else current.add(effect)
            it.copy(statusEffects = current)
        }
    }

    fun applyGlobalRadiationDelta(campaignId: String, delta: Int) {
        updateCampaignCharacters(campaignId) {
            it.copy(radiationLevel = (it.radiationLevel + delta).coerceIn(0, 100))
        }
    }

    fun applyGlobalToxicityDelta(campaignId: String, delta: Int) {
        updateCampaignCharacters(campaignId) {
            it.copy(toxicityPoison = (it.toxicityPoison + delta).coerceIn(0, 100))
        }
    }

    fun applyGlobalSoporteVitalDelta(campaignId: String, delta: Int) {
        updateCampaignCharacters(campaignId) {
            it.copy(soporteVitalLevel = (it.soporteVitalLevel + delta).coerceIn(0, 100))
        }
    }

    fun applyGlobalSanityDelta(campaignId: String, delta: Int) {
        updateCampaignCharacters(campaignId) {
            it.copy(currentSanity = (it.currentSanity + delta).coerceIn(0, 100))
        }
    }

    fun setGlobalEnvironment(campaignId: String, rad: Int?, tox: Int?, soporteVital: Int?) {
        updateCampaignCharacters(campaignId) {
            var updated = it
            if (rad != null) updated = updated.copy(radiationLevel = rad.coerceIn(0, 100))
            if (tox != null) updated = updated.copy(toxicityPoison = tox.coerceIn(0, 100))
            if (soporteVital != null) updated = updated.copy(soporteVitalLevel = soporteVital.coerceIn(0, 100))
            updated
        }
    }

    fun applyGlobalStatusEffect(campaignId: String, effect: String, applyToAll: Boolean) {
        updateCampaignCharacters(campaignId) {
            val current = it.statusEffects.toMutableList()
            if (applyToAll) {
                if (!current.contains(effect)) current.add(effect)
            } else {
                current.remove(effect)
            }
            it.copy(statusEffects = current)
        }
    }

    fun clearAllGlobalStatusEffects(campaignId: String) {
        updateCampaignCharacters(campaignId) { it.copy(statusEffects = emptyList()) }
    }

    fun changeCharacterLevel(charId: String, delta: Int) {
        updateCharacter(charId) {
            val newLevel = (it.level + delta).coerceAtLeast(1)
            val newPending = if (delta > 0) it.pendingLevelUps + delta else it.pendingLevelUps
            it.copy(level = newLevel, pendingLevelUps = newPending)
        }
    }

    fun levelUpCharacter(charId: String) = changeCharacterLevel(charId, 1)

    fun claimLevelUp(charId: String, newStats: SpecialStats, newSkills: Map<String, Int>, newPerks: List<String>) {
        updateCharacter(charId) {
            it.copy(
                stats = newStats,
                skills = newSkills,
                unlockedPerks = newPerks,
                pendingLevelUps = (it.pendingLevelUps - 1).coerceAtLeast(0)
            )
        }
    }

    fun updateStats(charId: String, newStats: SpecialStats) {
        updateCharacter(charId) { it.copy(stats = newStats) }
    }

    fun updateSkill(charId: String, skillName: String, deltaPoints: Int) {
        updateCharacter(charId) {
            val currentSkills = it.skills.toMutableMap()
            val currentVal = currentSkills[skillName] ?: 0
            currentSkills[skillName] = (currentVal + deltaPoints).coerceIn(0, 100)
            it.copy(skills = currentSkills)
        }
    }

    fun unlockPerk(charId: String, perkName: String) {
        updateCharacter(charId) {
            if (!it.unlockedPerks.contains(perkName)) {
                it.copy(unlockedPerks = it.unlockedPerks + perkName)
            } else it
        }
    }

    fun removePerk(charId: String, perkName: String) {
        updateCharacter(charId) {
            if (it.unlockedPerks.contains(perkName)) {
                it.copy(unlockedPerks = it.unlockedPerks.filter { p -> p != perkName })
            } else it
        }
    }

    fun toggleCharacterLifeStatus(charId: String) {
        updateCharacter(charId) { it.copy(isAlive = !it.isAlive) }
    }

    fun setCharacterLifeAndStatus(charId: String, isAlive: Boolean, incapacitated: Boolean) {
        updateCharacter(charId) {
            val effects = it.statusEffects.toMutableList()
            if (incapacitated) {
                if (!effects.contains("Incapacitado")) effects.add("Incapacitado")
            } else {
                effects.remove("Incapacitado")
            }
            it.copy(isAlive = isAlive, statusEffects = effects)
        }
    }

    private fun releaseVaultCharacters(charId: String? = null, campaignId: String? = null) {
        val releasedChars = mutableListOf<Character>()
        _savedUserCharacters.update { vault ->
            vault.map { char ->
                val matchesChar = charId != null && char.id == charId
                val matchesCampaign = campaignId != null && (char.activeCampaignId == campaignId || char.campaignId == campaignId)
                if (matchesChar || matchesCampaign) {
                    val freed = char.copy(activeCampaignId = null, activeCampaignName = null, campaignId = "")
                    releasedChars.add(freed)
                    freed
                } else {
                    char
                }
            }
        }
        val db = firestore ?: return
        releasedChars.forEach { freed ->
            val ownerUid = freed.playerId.ifBlank { _currentUserProfile.value.id }
            if (ownerUid.isNotBlank()) {
                db.collection("users").document(ownerUid)
                    .collection("saved_characters").document(freed.id)
                    .update(
                        mapOf(
                            "campaignId" to "",
                            "activeCampaignId" to "",
                            "activeCampaignName" to ""
                        )
                    )
            }
        }
    }

    fun deleteCharacter(charId: String) {
        syncJobs.remove(charId)?.cancel()
        val charToDelete = _characters.value.find { it.id == charId }
        _characters.update { list -> list.filter { it.id != charId } }
        releaseVaultCharacters(charId = charId)
        if (charToDelete != null) {
            firestore?.collection("campaigns")?.document(charToDelete.campaignId)
                ?.collection("characters")?.document(charToDelete.id)?.delete()
        }
    }
}
