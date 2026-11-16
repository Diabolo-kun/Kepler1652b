package com.example.mpergar9.kepler1652b.model

data class InventoryItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val size: Int = 1, // Tamaño o huecos que ocupa en el inventario
    val statBonusStat: String? = null, // Ej: "INT", "FUE", "SLOTS", "PER", "END", "CAR", "AGI", "SUE"
    val statBonusValue: Int = 0, // Ej: 1 para +1 INT, 5 para +5 huecos de mochila
    val credits: Int = 0 // Valor en créditos
)

data class Campaign(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val masterName: String = "Director de Juego",
    val inviteCode: String = "",
    val imageBannerId: String = "kepler_orbit", // Referencia a fondo sci-fi
    val isCurrentUserMaster: Boolean = false
)

data class SpecialStats(
    val fue: Int = 1,
    val per: Int = 1,
    val end: Int = 1,
    val car: Int = 1,
    val agi: Int = 1,
    val int: Int = 1,
    val sue: Int = 1
) {
    // Escala SPECIAL: 1 a 10
    // Escala de Habilidades: TODAS van de 0 a 100
    fun getSkillCap(statName: String): Int {
        val statVal = getStat(statName)
        return (28 + (statVal * 7.2f).toInt()).coerceIn(35, 100)
    }

    fun getStat(statName: String): Int = when (statName.uppercase()) {
        "FUE" -> fue
        "PER" -> per
        "END" -> end
        "CAR" -> car
        "AGI" -> agi
        "INT" -> int
        "SUE" -> sue
        else -> 0
    }

    fun meetsRequirements(reqs: Map<String, Int>): Boolean {
        return reqs.all { (stat, minVal) -> getStat(stat) >= minVal }
    }

    // PV Máximos = 20 + (END * 5) + (FUE * 2)
    val maxHp: Int get() = 20 + (end * 5) + (fue * 2)

    // Cordura = Escala percentil 0 a 100% (inicia en 100%)
    val maxSanity: Int get() = 100
}

enum class CharacterFaction(val label: String) {
    UNION_HUMANA("Unión Humana"),
    PURISTAS("Puristas"),
    NEUTRAL("No alineado")
}

data class UserProfile(
    val id: String = "usr-player-1",
    val displayName: String = "Explorador"
)

data class Character(
    val id: String = "",
    val campaignId: String = "",
    val playerId: String = "usr-player-1", // Identificador único del jugador propietario
    val playerName: String = "",
    val characterName: String = "",
    val faction: CharacterFaction = CharacterFaction.UNION_HUMANA,
    val characterClass: String = "",
    val subclass: String = "",
    val level: Int = 1,
    val stats: SpecialStats = SpecialStats(),
    val currentHp: Int = 27,
    val radiationLevel: Int = 0, // 0 - 100 rads
    val toxicityPoison: Int = 0,  // 0 - 100 veneno
    val soporteVitalLevel: Int = 100, // 0 - 100% Soporte Vital
    val currentSanity: Int = 100, // 0 - 100% Cordura (inicia en 100)
    val statusEffects: List<String> = emptyList(), // "Pánico", "Fallo de Soporte Vital", "Hemorragia", etc.
    val skills: Map<String, Int> = emptyMap(),
    val unlockedPerks: List<String> = emptyList(),
    val statusPrivilege: String? = null, // Privilegio de estatus (para Acompañante)
    val bodyModifications: String = "", // Estético / lore
    val isAlive: Boolean = true, // Identifica si el personaje está vivo o fallecido
    val pendingLevelUps: Int = 0, // Cantidad de subidas de nivel pendientes por reclamar por el jugador
    val inventory: List<InventoryItem> = emptyList() // Inventario / equipo del personaje
) {
    val oxygenLevel: Int get() = soporteVitalLevel

    // Estadísticas efectivas sumando los bonificadores de los objetos del inventario (ej: gafas +1 INT)
    val effectiveStats: SpecialStats get() {
        val bFue = inventory.filter { it.statBonusStat?.uppercase() == "FUE" }.sumOf { it.statBonusValue }
        val bPer = inventory.filter { it.statBonusStat?.uppercase() == "PER" }.sumOf { it.statBonusValue }
        val bEnd = inventory.filter { it.statBonusStat?.uppercase() == "END" }.sumOf { it.statBonusValue }
        val bCar = inventory.filter { it.statBonusStat?.uppercase() == "CAR" }.sumOf { it.statBonusValue }
        val bAgi = inventory.filter { it.statBonusStat?.uppercase() == "AGI" }.sumOf { it.statBonusValue }
        val bInt = inventory.filter { it.statBonusStat?.uppercase() == "INT" }.sumOf { it.statBonusValue }
        val bSue = inventory.filter { it.statBonusStat?.uppercase() == "SUE" }.sumOf { it.statBonusValue }
        return SpecialStats(
            fue = (stats.fue + bFue).coerceAtMost(10),
            per = (stats.per + bPer).coerceAtMost(10),
            end = (stats.end + bEnd).coerceAtMost(10),
            car = (stats.car + bCar).coerceAtMost(10),
            agi = (stats.agi + bAgi).coerceAtMost(10),
            int = (stats.int + bInt).coerceAtMost(10),
            sue = (stats.sue + bSue).coerceAtMost(10)
        )
    }

    // Capacidad máxima de almacenamiento (FUE + END + 5 + bonificadores de objetos como mochilas)
    val maxInventorySlots: Int get() {
        val base = stats.fue + stats.end + 5
        val bonusSlots = inventory.filter { it.statBonusStat?.uppercase() == "SLOTS" }.sumOf { it.statBonusValue }
        return base + bonusSlots
    }

    // Espacio de inventario actualmente ocupado (suma de tamaño de objetos)
    val currentInventorySlotsUsed: Int get() = inventory.sumOf { it.size }
}
