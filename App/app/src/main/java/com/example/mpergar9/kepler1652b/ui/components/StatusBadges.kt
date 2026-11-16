package com.example.mpergar9.kepler1652b.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mpergar9.kepler1652b.ui.theme.*

/**
 * Insignia reutilizable que indica si un personaje está Vivo (💚) o Fallecido (💀).
 */
@Composable
fun AliveStatusBadge(
    isAlive: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isAlive) RadiationGreen.copy(alpha = 0.2f) else DangerRed.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Text(
            text = if (isAlive) "💚 VIVO" else "💀 FALLECIDO",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isAlive) RadiationGreen else DangerRed,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

/**
 * Insignia reutilizable para destacar el personaje perteneciente al usuario actual ("TU PJ").
 */
@Composable
fun UserCharacterBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        color = NeonCyan.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Text(
            text = "TU PJ",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

/**
 * Chip reutilizable para mostrar un efecto de estado activo en el personaje.
 */
@Composable
fun StatusEffectChip(
    effectName: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = WarningAmber.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Text(
            text = "⚠️ $effectName",
            fontSize = 9.sp,
            color = WarningAmber,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090D14)
@Composable
fun PreviewStatusBadges() {
    Kepler1652bTheme {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AliveStatusBadge(isAlive = true)
            AliveStatusBadge(isAlive = false)
            UserCharacterBadge()
            StatusEffectChip(effectName = "Asfixia")
        }
    }
}
