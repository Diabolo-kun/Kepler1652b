package com.example.mpergar9.kepler1652b

import com.example.mpergar9.kepler1652b.data.KeplerGameData
import org.junit.Assert.*
import org.junit.Test

class KeplerGameDataTest {

    @Test
    fun testAllSkills_CountAndNonEmpty() {
        assertEquals(12, KeplerGameData.ALL_SKILLS.size)
        assertTrue(KeplerGameData.ALL_SKILLS.contains("Manitas"))
        assertTrue(KeplerGameData.ALL_SKILLS.contains("Medicina y Criogenia"))
    }

    @Test
    fun testSkillAssociatedStat_AllSkillsMapped() {
        KeplerGameData.ALL_SKILLS.forEach { skill ->
            val stat = KeplerGameData.SKILL_ASSOCIATED_STAT[skill]
            assertNotNull("Skill '$skill' must have an associated stat", stat)
            assertTrue(listOf("FUE", "PER", "END", "CAR", "AGI", "INT", "SUE").contains(stat))
        }
    }

    @Test
    fun testClassesDefinitions_CountAndSubclasses() {
        val classes = KeplerGameData.CLASSES
        assertTrue("Classes list must not be empty", classes.isNotEmpty())

        val ingenieroClass = classes.find { it.name.startsWith("Ingeniero") }
        assertNotNull(ingenieroClass)
        assertTrue(ingenieroClass!!.subclasses.isNotEmpty())
    }

    @Test
    fun testSubclassDefinition_TotalCostCalculation() {
        val classDef = KeplerGameData.CLASSES.first()
        val subclass = classDef.subclasses.first()
        val expectedCost = subclass.requiredSkills.values.sum()
        assertEquals(expectedCost, subclass.totalCost)
    }

    @Test
    fun testUnlockablePerks_NotEmptyAndValid() {
        val perks = KeplerGameData.UNLOCKABLE_PERKS
        assertTrue("Perks list must not be empty", perks.isNotEmpty())
        perks.forEach { perk ->
            assertNotNull(perk.name)
            assertNotNull(perk.description)
        }
    }
}
