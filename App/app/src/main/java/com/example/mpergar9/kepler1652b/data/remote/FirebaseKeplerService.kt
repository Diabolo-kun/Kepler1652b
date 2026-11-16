package com.example.mpergar9.kepler1652b.data.remote

/**
 * ====================================================================================
 * GUÍA DE INTEGRACIÓN DE FIREBASE FIRESTORE - KEPLER-1652b
 * ====================================================================================
 * 
 * INSTRUCCIONES PARA EL ASISTENTE IA DE ANDROID STUDIO (Gemini / Studio Bot):
 * ------------------------------------------------------------------------------------
 * Este archivo implementa el servicio desacoplado para sincronizar las campañas y personajes
 * de la app Kepler-1652b con Firebase Cloud Firestore en tiempo real.
 * 
 * PASOS PREVIOS EN ANDROID STUDIO:
 * 1. Abrir la consola de Firebase (https://console.firebase.google.com) y crear un proyecto "Kepler-1652b".
 * 2. Registrar la app Android con el applicationId: "com.example.mpergar9.kepler1652b".
 * 3. Descargar el archivo `google-services.json` y colocarlo dentro de:
 *    app/google-services.json
 * 4. En el `build.gradle.kts` raíz (del proyecto), añadir el plugin de Google Services:
 *    plugins {
 *        id("com.google.gms.google-services") version "4.4.2" apply false
 *    }
 * 5. En `app/build.gradle.kts`, aplicar el plugin y añadir las dependencias de Firebase BOM:
 *    plugins {
 *        id("com.google.gms.google-services")
 *    }
 *    dependencies {
 *        implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
 *        implementation("com.google.firebase:firebase-firestore-ktx")
 *        implementation("com.google.firebase:firebase-auth-ktx") // Opcional para auth anónima
 *    }
 * 
 * ESTRUCTURA DE COLECCIONES EN FIRESTORE:
 * ------------------------------------------------------------------------------------
 * - /campaigns/{campaignId}
 *     - id: String
 *     - name: String
 *     - description: String
 *     - masterName: String
 *     - masterId: String
 *     - inviteCode: String (Ej: "KPL-8421")
 *     - imageBannerId: String
 *     - createdAt: Long
 * 
 * - /campaigns/{campaignId}/characters/{characterId}
 *     - id: String
 *     - campaignId: String
 *     - playerId: String (ID del dispositivo/perfil del jugador)
 *     - playerName: String
 *     - characterName: String
 *     - faction: String
 *     - characterClass: String
 *     - subclass: String
 *     - level: Int
 *     - currentHp: Int
 *     - maxHp: Int
 *     - radiationLevel: Int (0..100)
 *     - toxicityPoison: Int (0..100)
 *     - soporteVitalLevel: Int (0..100)
 *     - currentSanity: Int (0..100)
 *     - isAlive: Boolean
 *     - statusEffects: List<String>
 *     - stats: Map<String, Int> (FUE, PER, END, CAR, AGI, INT, SUE)
 *     - skills: Map<String, Int>
 *     - unlockedPerks: List<String>
 * ====================================================================================
 */

import com.example.mpergar9.kepler1652b.model.Campaign
import com.example.mpergar9.kepler1652b.model.Character
import com.example.mpergar9.kepler1652b.model.CharacterFaction
import com.example.mpergar9.kepler1652b.model.SpecialStats

/**
 * Servicio de sincronización en tiempo real con Firebase Firestore.
 * Si las dependencias de Firebase aún no están compiladas, esta clase sirve como
 * plantilla de referencia lista para descomentar y enlazar con FirebaseFirestore.getInstance().
 */
class FirebaseKeplerService {

    // NOTA PARA LA IA DE ANDROID STUDIO:
    // Una vez añadidas las dependencias de Firebase en build.gradle.kts, descomentar:
    // private val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()

    /**
     * DTO de Campaña para Firestore
     */
    data class CampaignDto(
        val id: String = "",
        val name: String = "",
        val description: String = "",
        val masterName: String = "",
        val masterId: String = "",
        val inviteCode: String = "",
        val imageBannerId: String = "kepler_orbit",
        val createdAt: Long = System.currentTimeMillis()
    ) {
        fun toDomain(currentUserId: String): Campaign = Campaign(
            id = id,
            name = name,
            description = description,
            masterName = masterName,
            inviteCode = inviteCode,
            imageBannerId = imageBannerId,
            isCurrentUserMaster = masterId == currentUserId
        )
    }

    /**
     * DTO de Personaje para Firestore
     */
    data class CharacterDto(
        val id: String = "",
        val campaignId: String = "",
        val playerId: String = "",
        val playerName: String = "",
        val characterName: String = "",
        val faction: String = "UNION_HUMANA",
        val characterClass: String = "",
        val subclass: String = "",
        val level: Int = 1,
        val currentHp: Int = 27,
        val radiationLevel: Int = 0,
        val toxicityPoison: Int = 0,
        val soporteVitalLevel: Int = 100,
        val currentSanity: Int = 100,
        val isAlive: Boolean = true,
        val statusEffects: List<String> = emptyList(),
        val stats: Map<String, Int> = emptyMap(),
        val skills: Map<String, Int> = emptyMap(),
        val unlockedPerks: List<String> = emptyList()
    ) {
        fun toDomain(): Character {
            val special = SpecialStats(
                fue = stats["FUE"] ?: 1,
                per = stats["PER"] ?: 1,
                end = stats["END"] ?: 1,
                car = stats["CAR"] ?: 1,
                agi = stats["AGI"] ?: 1,
                int = stats["INT"] ?: 1,
                sue = stats["SUE"] ?: 1
            )
            val mappedFaction = try {
                CharacterFaction.valueOf(faction)
            } catch (e: Exception) {
                CharacterFaction.UNION_HUMANA
            }
            return Character(
                id = id,
                campaignId = campaignId,
                playerId = playerId,
                playerName = playerName,
                characterName = characterName,
                faction = mappedFaction,
                characterClass = characterClass,
                subclass = subclass,
                level = level,
                stats = special,
                currentHp = currentHp,
                radiationLevel = radiationLevel,
                toxicityPoison = toxicityPoison,
                soporteVitalLevel = soporteVitalLevel,
                currentSanity = currentSanity,
                isAlive = isAlive,
                statusEffects = statusEffects,
                skills = skills,
                unlockedPerks = unlockedPerks
            )
        }
    }

    /**
     * Ejemplo de escucha en tiempo real de todas las campañas:
     * 
     * fun listenCampaigns(currentUserId: String, onUpdate: (List<Campaign>) -> Unit): ListenerRegistration {
     *     return firestore.collection("campaigns")
     *         .addSnapshotListener { snapshot, error ->
     *             if (error != null || snapshot == null) return@addSnapshotListener
     *             val campaigns = snapshot.documents.mapNotNull { doc ->
     *                 doc.toObject(CampaignDto::class.java)?.toDomain(currentUserId)
     *             }
     *             onUpdate(campaigns)
     *         }
     * }
     */

    /**
     * Ejemplo de escucha en tiempo real de los personajes de una campaña:
     * 
     * fun listenCharacters(campaignId: String, onUpdate: (List<Character>) -> Unit): ListenerRegistration {
     *     return firestore.collection("campaigns")
     *         .document(campaignId)
     *         .collection("characters")
     *         .addSnapshotListener { snapshot, error ->
     *             if (error != null || snapshot == null) return@addSnapshotListener
     *             val characters = snapshot.documents.mapNotNull { doc ->
     *                 doc.toObject(CharacterDto::class.java)?.toDomain()
     *             }
     *             onUpdate(characters)
     *         }
     * }
     */

    /**
     * Ejemplo de creación o guardado de campaña en Firestore:
     * 
     * fun saveCampaign(campaign: Campaign, masterUserId: String) {
     *     val dto = CampaignDto(
     *         id = campaign.id,
     *         name = campaign.name,
     *         description = campaign.description,
     *         masterName = campaign.masterName,
     *         masterId = masterUserId,
     *         inviteCode = campaign.inviteCode,
     *         imageBannerId = campaign.imageBannerId
     *     )
     *     firestore.collection("campaigns").document(campaign.id).set(dto)
     * }
     */

    /**
     * Ejemplo de borrado de campaña en Firestore:
     * 
     * fun deleteCampaign(campaignId: String) {
     *     firestore.collection("campaigns").document(campaignId).delete()
     * }
     */

    /**
     * Ejemplo de sincronización de personaje en Firestore:
     * 
     * fun saveCharacter(character: Character) {
     *     val dto = CharacterDto(
     *         id = character.id,
     *         campaignId = character.campaignId,
     *         playerId = character.playerId,
     *         playerName = character.playerName,
     *         characterName = character.characterName,
     *         faction = character.faction.name,
     *         characterClass = character.characterClass,
     *         subclass = character.subclass,
     *         level = character.level,
     *         currentHp = character.currentHp,
     *         radiationLevel = character.radiationLevel,
     *         toxicityPoison = character.toxicityPoison,
     *         soporteVitalLevel = character.soporteVitalLevel,
     *         currentSanity = character.currentSanity,
     *         isAlive = character.isAlive,
     *         statusEffects = character.statusEffects,
     *         stats = mapOf(
     *             "FUE" to character.stats.fue,
     *             "PER" to character.stats.per,
     *             "END" to character.stats.end,
     *             "CAR" to character.stats.car,
     *             "AGI" to character.stats.agi,
     *             "INT" to character.stats.int,
     *             "SUE" to character.stats.sue
     *         ),
     *         skills = character.skills,
     *         unlockedPerks = character.unlockedPerks
     *     )
     *     firestore.collection("campaigns")
     *         .document(character.campaignId)
     *         .collection("characters")
     *         .document(character.id)
     *         .set(dto)
     * }
     */
}
