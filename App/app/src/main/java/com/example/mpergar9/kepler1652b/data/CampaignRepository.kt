package com.example.mpergar9.kepler1652b.data

import com.example.mpergar9.kepler1652b.model.Campaign
import com.example.mpergar9.kepler1652b.model.Character
import com.example.mpergar9.kepler1652b.model.CharacterFaction
import com.example.mpergar9.kepler1652b.model.InventoryItem
import com.example.mpergar9.kepler1652b.model.SpecialStats
import com.example.mpergar9.kepler1652b.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object CampaignRepository {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (_: Exception) {
            null
        }
    }

    private val _currentUserProfile = MutableStateFlow(
        UserProfile(id = "usr-player-1", displayName = "Explorador")
    )
    val currentUserProfile: StateFlow<UserProfile> = _currentUserProfile.asStateFlow()

    fun updateProfileName(newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isNotEmpty()) {
            _currentUserProfile.value = _currentUserProfile.value.copy(displayName = trimmed)
        }
    }

    private val _campaigns = MutableStateFlow<List<Campaign>>(emptyList())
    val campaigns: StateFlow<List<Campaign>> = _campaigns.asStateFlow()

    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    val characters: StateFlow<List<Character>> = _characters.asStateFlow()

    init {
        startFirestoreListeners()
    }

    private fun startFirestoreListeners() {
        val db = firestore ?: return
        db.collection("campaigns").addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            val currentUserId = _currentUserProfile.value.id
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

                // Auto-migración si estaba en formato plano antiguo
                if (doc.get("master") == null) {
                    db.collection("campaigns").document(id).set(
                        mapOf(
                            "id" to id,
                            "name" to name,
                            "description" to description,
                            "inviteCode" to inviteCode,
                            "master" to mapOf(
                                "masterId" to masterId,
                                "masterName" to masterName
                            ),
                            "uiState" to mapOf(
                                "imageBannerId" to imageBannerId
                            )
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
                }

                Campaign(
                    id = id,
                    name = name,
                    description = description,
                    masterName = masterName,
                    inviteCode = inviteCode,
                    imageBannerId = imageBannerId,
                    isCurrentUserMaster = masterId == currentUserId || masterName.equals(_currentUserProfile.value.displayName, true)
                )
            }
            _campaigns.value = fetchedCampaigns

            fetchedCampaigns.forEach { camp ->
                db.collection("campaigns").document(camp.id).collection("characters")
                    .addSnapshotListener { charSnapshot, charError ->
                        if (charError == null && charSnapshot != null) {
                            val fetchedChars = charSnapshot.documents.mapNotNull { charDoc ->
                                parseCharacter(charDoc, camp.id)
                            }
                            val existingOtherChars = _characters.value.filter { it.campaignId != camp.id }
                            _characters.value = existingOtherChars + fetchedChars
                        }
                    }
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
        val effectiveMaster = masterName.ifBlank { _currentUserProfile.value.displayName }
        val newCampaign = Campaign(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            masterName = effectiveMaster,
            inviteCode = "KPL-" + (1000..9999).random(),
            imageBannerId = "kepler_orbit",
            isCurrentUserMaster = true
        )
        _campaigns.value = _campaigns.value + newCampaign
        
        firestore?.collection("campaigns")?.document(newCampaign.id)?.set(
            mapOf(
                "id" to newCampaign.id,
                "name" to newCampaign.name,
                "description" to newCampaign.description,
                "inviteCode" to newCampaign.inviteCode,
                "master" to mapOf(
                    "masterId" to _currentUserProfile.value.id,
                    "masterName" to newCampaign.masterName
                ),
                "uiState" to mapOf(
                    "imageBannerId" to newCampaign.imageBannerId
                )
            )
        )
        return newCampaign
    }

    fun deleteCampaign(campaignId: String) {
        _campaigns.value = _campaigns.value.filter { it.id != campaignId }
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
        val trimmed = code.trim()
        val found = _campaigns.value.find { it.inviteCode.equals(trimmed, ignoreCase = true) }
        return if (found != null) {
            val playerCampaign = found.copy(isCurrentUserMaster = false)
            _campaigns.value = _campaigns.value.map { if (it.id == found.id) playerCampaign else it }
            playerCampaign
        } else null
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
        syncCharacterToFirestore(newChar)
    }

    private fun syncCharacterToFirestore(character: Character) {
        val db = firestore ?: return
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
