package com.example.mpergar9.kepler1652b

import com.example.mpergar9.kepler1652b.data.CampaignRepository
import com.example.mpergar9.kepler1652b.model.Character
import com.example.mpergar9.kepler1652b.model.CharacterFaction
import com.example.mpergar9.kepler1652b.model.InventoryItem
import com.example.mpergar9.kepler1652b.model.SpecialStats
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CampaignRepositoryTest {

    @Before
    fun setUp() {
        // Limpiar campañas y personajes existentes para pruebas aisladas
        val currentCampaigns = CampaignRepository.campaigns.value
        currentCampaigns.forEach { CampaignRepository.deleteCampaign(it.id) }
    }

    @Test
    fun testUpdateProfileName() {
        CampaignRepository.updateProfileName("Cmdte. Elena Vance")
        assertEquals("Cmdte. Elena Vance", CampaignRepository.currentUserProfile.value.displayName)
    }

    @Test
    fun testCreateCampaign_AndJoinByCode() {
        val campaign = CampaignRepository.createCampaign("Misión Kepler", "Exploración de cráter", "Cmdte. Vance")
        assertNotNull(campaign.id)
        assertTrue(campaign.isCurrentUserMaster)
        assertEquals("Misión Kepler", campaign.name)

        // Cambiar a un segundo usuario para simular la unión desde otro teléfono
        CampaignRepository.initLocalProfile("usr-player-2", "Capitán Miller")

        // Probar unirse por código de invitación como segundo usuario
        val joined = CampaignRepository.joinCampaignByCode(campaign.inviteCode)
        assertNotNull(joined)
        assertEquals(campaign.id, joined?.id)
        assertFalse(joined!!.isCurrentUserMaster)
    }

    @Test
    fun testAddCharacter_AndDeleteCharacter() {
        val campaign = CampaignRepository.createCampaign("Campaña Alfa", "Desc", "Master 1")
        val newChar = Character(
            id = "char-101",
            campaignId = campaign.id,
            characterName = "Dr. Hans",
            faction = CharacterFaction.UNION_HUMANA,
            characterClass = "Médico",
            subclass = "Criogenia",
            stats = SpecialStats(int = 8, end = 4),
            inventory = listOf(InventoryItem(id = "medkit-1", name = "Botiquín Médico", size = 1))
        )

        CampaignRepository.addCharacter(newChar)
        assertEquals(1, CampaignRepository.characters.value.count { it.campaignId == campaign.id })

        val found = CampaignRepository.characters.value.first { it.id == "char-101" }
        assertEquals("Dr. Hans", found.characterName)

        // Probar eliminación de personaje
        CampaignRepository.deleteCharacter(newChar.id)
        assertEquals(0, CampaignRepository.characters.value.count { it.campaignId == campaign.id })
    }

    @Test
    fun testGlobalEnvironmentalDeltas() {
        val campaign = CampaignRepository.createCampaign("Campaña Nube", "Desc", "Master 2")
        val char1 = Character(
            id = "char-102",
            campaignId = campaign.id,
            characterName = "Colono 1",
            faction = CharacterFaction.UNION_HUMANA,
            characterClass = "Ingeniero",
            subclass = "Soporte"
        )
        CampaignRepository.addCharacter(char1)

        // Aplicar variaciones globales
        CampaignRepository.applyGlobalRadiationDelta(campaign.id, 25)
        CampaignRepository.applyGlobalToxicityDelta(campaign.id, 15)
        CampaignRepository.applyGlobalSoporteVitalDelta(campaign.id, -20)
        CampaignRepository.applyGlobalSanityDelta(campaign.id, -10)

        val updatedChar = CampaignRepository.characters.value.first { it.id == char1.id }
        assertEquals(25, updatedChar.radiationLevel)
        assertEquals(15, updatedChar.toxicityPoison)
        assertEquals(80, updatedChar.soporteVitalLevel)
        assertEquals(90, updatedChar.currentSanity)
    }

    @Test
    fun testGlobalStatusEffects_ApplyAndClear() {
        val campaign = CampaignRepository.createCampaign("Campaña Alarma", "Desc", "Master 3")
        val char1 = Character(
            id = "char-103",
            campaignId = campaign.id,
            characterName = "Colono 2",
            faction = CharacterFaction.UNION_HUMANA,
            characterClass = "Piloto",
            subclass = "Navegación"
        )
        CampaignRepository.addCharacter(char1)

        CampaignRepository.applyGlobalStatusEffect(campaign.id, "Asfixia", applyToAll = true)
        val charWithEffect = CampaignRepository.characters.value.first { it.id == char1.id }
        assertTrue(charWithEffect.statusEffects.contains("Asfixia"))

        CampaignRepository.clearAllGlobalStatusEffects(campaign.id)
        val charCleared = CampaignRepository.characters.value.first { it.id == char1.id }
        assertTrue(charCleared.statusEffects.isEmpty())
    }

    @Test
    fun testInventoryManagement() {
        val campaign = CampaignRepository.createCampaign("Campaña Inventario", "Desc", "Master 4")
        val char1 = Character(
            id = "char-104",
            campaignId = campaign.id,
            characterName = "Colono 3",
            faction = CharacterFaction.UNION_HUMANA,
            characterClass = "Soldado",
            subclass = "Asalto"
        )
        CampaignRepository.addCharacter(char1)

        CampaignRepository.addItemToInventory(
            charId = char1.id,
            name = "Rifle de Plasma",
            description = "Arma energética",
            size = 3,
            statBonusStat = "FUE",
            statBonusValue = 1,
            credits = 150
        )

        val charWithItem = CampaignRepository.characters.value.first { it.id == char1.id }
        val itemAdded = charWithItem.inventory.find { it.name == "Rifle de Plasma" }
        assertNotNull(itemAdded)

        CampaignRepository.removeItemFromInventory(char1.id, itemAdded!!.id)
        val charWithoutItem = CampaignRepository.characters.value.first { it.id == char1.id }
        assertFalse(charWithoutItem.inventory.any { it.name == "Rifle de Plasma" })
    }

    @Test
    fun testDeleteCampaign_DeletesCampaignAndCharacters() {
        val campaign = CampaignRepository.createCampaign("Campaña a Borrar", "Desc", "Master 5")
        val char1 = Character(
            id = "char-105",
            campaignId = campaign.id,
            characterName = "Tripulante Borrable"
        )
        CampaignRepository.addCharacter(char1)

        CampaignRepository.deleteCampaign(campaign.id)

        assertNull(CampaignRepository.campaigns.value.find { it.id == campaign.id })
        assertEquals(0, CampaignRepository.characters.value.count { it.campaignId == campaign.id })
    }
}
