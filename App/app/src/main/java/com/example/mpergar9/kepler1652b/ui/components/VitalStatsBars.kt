package com.example.mpergar9.kepler1652b.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpergar9.kepler1652b.ui.theme.*

/**
 * Barra de Integridad Física / Vida (PV) reutilizable con animación fluida de transición y color dinámico.
 */
@Composable
fun HealthProgressBar(
    currentHp: Int,
    maxHp: Int,
    modifier: Modifier = Modifier
) {
    val targetRatio = if (maxHp > 0) (currentHp.toFloat() / maxHp).coerceIn(0f, 1f) else 0f
    val animatedRatio by animateFloatAsState(
        targetValue = targetRatio,
        animationSpec = tween(durationMillis = 400),
        label = "hp_animation"
    )

    val hpColor = when {
        targetRatio < 0.3f -> DangerRed
        targetRatio < 0.6f -> WarningAmber
        else -> RadiationGreen
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Integridad Física", fontSize = 10.sp, color = TextMuted)
            Text(
                text = "$currentHp / $maxHp PV",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = hpColor
            )
        }
        LinearProgressIndicator(
            progress = { animatedRatio },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = hpColor,
            trackColor = HullGray
        )
    }
}

/**
 * Fila unificada de constantes ambientales y vitales (Radiación, Toxicidad, Soporte Vital y Cordura).
 */
@Composable
fun VitalStatsRow(
    radiationLevel: Int,
    toxicityLevel: Int,
    soporteVitalLevel: Int,
    sanityLevel: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("☢️ $radiationLevel%", fontSize = 10.sp, color = WarningAmber)
        Text("☣️ $toxicityLevel%", fontSize = 10.sp, color = DangerRed)
        Text(
            text = "🛡️ $soporteVitalLevel%",
            fontSize = 10.sp,
            color = if (soporteVitalLevel < 30) DangerRed else CyanCurvature
        )
        Text("🧠 $sanityLevel%", fontSize = 10.sp, color = PlasmaPurple)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
fun PreviewVitalStatsComponents() {
    Kepler1652bTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            HealthProgressBar(currentHp = 25, maxHp = 45)
            VitalStatsRow(radiationLevel = 15, toxicityLevel = 5, soporteVitalLevel = 90, sanityLevel = 100)
        }
    }
}
