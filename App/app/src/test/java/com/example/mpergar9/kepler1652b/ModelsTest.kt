package com.example.mpergar9.kepler1652b

import com.example.mpergar9.kepler1652b.model.Character
import com.example.mpergar9.kepler1652b.model.CharacterFaction
import com.example.mpergar9.kepler1652b.model.InventoryItem
import com.example.mpergar9.kepler1652b.model.SpecialStats
import org.junit.Assert.*
import org.junit.Test

class ModelsTest {

    @Test
    fun testSpecialStats_MaxHpCalculation() {
        // PV Máximos = 20 + (END * 5) + (FUE * 2)
        val stats = SpecialStats(fue = 5, end = 4)
        // 20 + (4 * 5) + (5 * 2) = 20 + 20 + 10 = 50
        assertEquals(50, stats.maxHp)
    }

    @Test
    fun testSpecialStats_GetSkillCap() {
        val stats = SpecialStats(int = 10)
        // (28 + 10 * 7.2).toInt() = 28 + 72 = 100
        assertEquals(100, stats.getSkillCap("INT"))

        val statsLow = SpecialStats(fue = 1)
        // (28 + 1 * 7.2).toInt() = 35 -> coerced to 35..100
        assertEquals(35, statsLow.getSkillCap("FUE"))
    }

    @Test
    fun testSpecialStats_GetStatByName() {
        val stats = SpecialStats(fue = 3, per = 4, end = 5, car = 6, agi = 7, int = 8, sue = 9)
        assertEquals(3, stats.getStat("fue"))
        assertEquals(4, stats.getStat("PER"))
        assertEquals(5, stats.getStat("End"))
        assertEquals(6, stats.getStat("car"))
        assertEquals(7, stats.getStat("agi"))
        assertEquals(8, stats.getStat("int"))
        assertEquals(9, stats.getStat("sue"))
        assertEquals(0, stats.getStat("invalid"))
    }

    @Test
    fun testSpecialStats_MeetsRequirements() {
        val stats = SpecialStats(fue = 6, int = 7)
        val reqsSatisfied = mapOf("FUE" to 5, "INT" to 6)
        val reqsUnsatisfied = mapOf("FUE" to 7, "INT" to 6)

        assertTrue(stats.meetsRequirements(reqsSatisfied))
        assertFalse(stats.meetsRequirements(reqsUnsatisfied))
    }

    @Test
    fun testCharacter_EffectiveStatsWithBonusItems() {
        val baseStats = SpecialStats(fue = 4, int = 5)
        val item1 = InventoryItem(id = "1", name = "Gafas de Inteligencia", statBonusStat = "INT", statBonusValue = 2)
        val item2 = InventoryItem(id = "2", name = "Guantes de Fuerza", statBonusStat = "FUE", statBonusValue = 3)

        val character = Character(
            id = "c1",
            characterName = "Elena Vance",
            stats = baseStats,
            inventory = listOf(item1, item2)
        )

        val effective = character.effectiveStats
        assertEquals(7, effective.fue) // 4 + 3
        assertEquals(7, effective.int) // 5 + 2
    }

    @Test
    fun testCharacter_MaxInventorySlotsAndSlotsUsed() {
        val baseStats = SpecialStats(fue = 3, end = 2)
        // Base = fue (3) + end (2) + 5 = 10
        val backpack = InventoryItem(id = "b1", name = "Mochila Táctica", statBonusStat = "SLOTS", statBonusValue = 5)
        val itemStandard = InventoryItem(id = "i1", name = "Ración de Combate", size = 2)

        val character = Character(
            id = "c1",
            stats = baseStats,
            inventory = listOf(backpack, itemStandard)
        )

        assertEquals(15, character.maxInventorySlots) // 10 + 5
        assertEquals(3, character.currentInventorySlotsUsed) // 1 + 2
    }

    @Test
    fun testCharacter_DefaultsAndOxygenLevel() {
        val character = Character(soporteVitalLevel = 85)
        assertEquals(85, character.oxygenLevel)
        assertTrue(character.isAlive)
        assertEquals(CharacterFaction.UNION_HUMANA, character.faction)
    }
}
