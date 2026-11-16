package com.example.mpergar9.kepler1652b

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mpergar9.kepler1652b.ui.components.AliveStatusBadge
import com.example.mpergar9.kepler1652b.ui.components.DeleteConfirmationDialog
import com.example.mpergar9.kepler1652b.ui.components.HealthProgressBar
import com.example.mpergar9.kepler1652b.ui.components.VitalStatsRow
import com.example.mpergar9.kepler1652b.ui.theme.Kepler1652bTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UiComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAliveStatusBadge_DisplaysCorrectText() {
        composeTestRule.setContent {
            Kepler1652bTheme {
                AliveStatusBadge(isAlive = true)
            }
        }
        composeTestRule.onNodeWithText("💚 VIVO").assertIsDisplayed()
    }

    @Test
    fun testDeadStatusBadge_DisplaysCorrectText() {
        composeTestRule.setContent {
            Kepler1652bTheme {
                AliveStatusBadge(isAlive = false)
            }
        }
        composeTestRule.onNodeWithText("💀 FALLECIDO").assertIsDisplayed()
    }

    @Test
    fun testHealthProgressBar_DisplaysHpText() {
        composeTestRule.setContent {
            Kepler1652bTheme {
                HealthProgressBar(currentHp = 30, maxHp = 50)
            }
        }
        composeTestRule.onNodeWithText("30 / 50 PV").assertIsDisplayed()
    }

    @Test
    fun testVitalStatsRow_DisplaysEnvironmentalPercentages() {
        composeTestRule.setContent {
            Kepler1652bTheme {
                VitalStatsRow(
                    radiationLevel = 20,
                    toxicityLevel = 10,
                    soporteVitalLevel = 85,
                    sanityLevel = 95
                )
            }
        }
        composeTestRule.onNodeWithText("☢️ 20%").assertIsDisplayed()
        composeTestRule.onNodeWithText("☣️ 10%").assertIsDisplayed()
        composeTestRule.onNodeWithText("🛡️ 85%").assertIsDisplayed()
        composeTestRule.onNodeWithText("🧠 95%").assertIsDisplayed()
    }

    @Test
    fun testDeleteConfirmationDialog_TriggersCallbacksOnButtonClicks() {
        var confirmed = false
        var dismissed = false

        composeTestRule.setContent {
            Kepler1652bTheme {
                DeleteConfirmationDialog(
                    title = "¿Eliminar?",
                    message = "¿Confirmas borrado?",
                    confirmText = "Sí",
                    cancelText = "Cancelar",
                    onConfirm = { confirmed = true },
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("¿Eliminar?").assertIsDisplayed()
        composeTestRule.onNodeWithText("¿Confirmas borrado?").assertIsDisplayed()

        // Pulsar botón Cancelar
        composeTestRule.onNodeWithText("Cancelar").performClick()
        assertTrue(dismissed)

        // Pulsar botón Confirmar
        composeTestRule.onNodeWithText("Sí").performClick()
        assertTrue(confirmed)
    }
}
