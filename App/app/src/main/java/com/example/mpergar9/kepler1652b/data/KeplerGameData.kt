package com.example.mpergar9.kepler1652b.data

data class ClassDefinition(
    val name: String,
    val classBonusSkill: String?,
    val classBonusValue: Int,
    val requiredStats: Map<String, Int> = emptyMap(),
    val subclasses: List<SubclassDefinition>
)

data class SubclassDefinition(
    val name: String,
    val initialPerkName: String,
    val perkDefinition: String,
    val requiredSkills: Map<String, Int>, // Skill -> Valor necesario
    val requiredStats: Map<String, Int> = emptyMap(), // Requisitos mínimos SPECIAL
    val statusPrivilege: String? = null,
    val statusPrivilegeDesc: String? = null
) {
    val totalCost: Int get() = requiredSkills.values.sum()
}

data class PerkDefinition(
    val name: String,
    val category: String,
    val requirements: Map<String, Int>,
    val actionName: String,
    val description: String
)

object KeplerGameData {
    val ALL_SKILLS = listOf(
        "Manitas",
        "Reparación Estructural y de Sistemas",
        "Informática y Protocolos de Red",
        "Logística y Carga",
        "Negociación y Reasignación de Recursos",
        "Medicina y Criogenia",
        "Armamento de Precisión y Balística",
        "Contundencia y Combate Cerrado (CQC)",
        "Demoliciones y Trampas Industriales",
        "Sigilo e Infiltración en Conductos",
        "Psicología, Mando y Subterfugio",
        "Supervivencia Espacial y Bioquímica"
    )

    val SKILL_ASSOCIATED_STAT = mapOf(
        "Manitas" to "AGI",
        "Reparación Estructural y de Sistemas" to "INT",
        "Informática y Protocolos de Red" to "INT",
        "Logística y Carga" to "FUE",
        "Negociación y Reasignación de Recursos" to "CAR",
        "Medicina y Criogenia" to "INT",
        "Armamento de Precisión y Balística" to "PER",
        "Contundencia y Combate Cerrado (CQC)" to "FUE",
        "Demoliciones y Trampas Industriales" to "AGI",
        "Sigilo e Infiltración en Conductos" to "AGI",
        "Psicología, Mando y Subterfugio" to "CAR",
        "Supervivencia Espacial y Bioquímica" to "END"
    )

    val CLASSES: List<ClassDefinition> = listOf(
        ClassDefinition(
            name = "Ingeniero / Técnico",
            classBonusSkill = "Reparación Estructural y de Sistemas",
            classBonusValue = 20,
            requiredStats = mapOf("INT" to 2),
            subclasses = listOf(
                SubclassDefinition(
                    name = "Soporte Vital y Termodinámica",
                    initialPerkName = "Soldadura en Caliente",
                    perkDefinition = "Habilita 'Fundir Acceso' para sellar permanentemente escotillas o conductos en 1 turno con soplete/térmica.",
                    requiredSkills = mapOf("Reparación Estructural y de Sistemas" to 55, "Demoliciones y Trampas Industriales" to 25),
                    requiredStats = mapOf("INT" to 3, "END" to 2)
                ),
                SubclassDefinition(
                    name = "Núcleo de Plasma y Propulsión",
                    initialPerkName = "Vampiro Energético",
                    perkDefinition = "Habilita 'Sifón de Plasma' para recargar baterías directamente de luces, consolas o núcleos de bots destruidos.",
                    requiredSkills = mapOf("Reparación Estructural y de Sistemas" to 40, "Manitas" to 30),
                    requiredStats = mapOf("INT" to 3)
                ),
                SubclassDefinition(
                    name = "Automatización y Robótica",
                    initialPerkName = "Apagado de Emergencia (Shutdown)",
                    perkDefinition = "Permite 'Desconexión Furtiva' por la espalda a bots de seguridad/mantenimiento sin ruido ni entrar en combate.",
                    requiredSkills = mapOf("Informática y Protocolos de Red" to 45, "Sigilo e Infiltración en Conductos" to 35),
                    requiredStats = mapOf("INT" to 3, "AGI" to 2)
                ),
                SubclassDefinition(
                    name = "Mantenimiento y Control de Daños",
                    initialPerkName = "Armero de Fortuna",
                    perkDefinition = "Desbloquea 'Ensamblar Armamento' con chatarra (armas electrificadas, lanzas térmicas, escudos improvisados).",
                    requiredSkills = mapOf("Reparación Estructural y de Sistemas" to 30, "Manitas" to 25),
                    requiredStats = mapOf("INT" to 2, "FUE" to 2)
                )
            )
        ),
        ClassDefinition(
            name = "Científico / Médico",
            classBonusSkill = "Informática y Protocolos de Red",
            classBonusValue = 15,
            requiredStats = mapOf("INT" to 2),
            subclasses = listOf(
                SubclassDefinition(
                    name = "Analista de Datos e IA",
                    initialPerkName = "Bypass de Consola Nivel 1",
                    perkDefinition = "Habilita 'Hackear Terminal' en consolas de camarotes y puertas residenciales simples.",
                    requiredSkills = mapOf("Informática y Protocolos de Red" to 25),
                    requiredStats = mapOf("INT" to 3)
                ),
                SubclassDefinition(
                    name = "Astrofísico y Navegante",
                    initialPerkName = "Bucle de Señal",
                    perkDefinition = "Permite poner cámaras y sensores en bucle para engañar a la IA y evitar que envíe patrullas a la zona.",
                    requiredSkills = mapOf("Informática y Protocolos de Red" to 40, "Manitas" to 30),
                    requiredStats = mapOf("INT" to 3, "PER" to 2)
                ),
                SubclassDefinition(
                    name = "Médico de a bordo y Criogenia",
                    initialPerkName = "Cirujano de Campaña",
                    perkDefinition = "Habilita 'Tratar Herida Grave' para curar huesos rotos, quemaduras de plasma o hemorragias en cero-G.",
                    requiredSkills = mapOf("Medicina y Criogenia" to 45),
                    requiredStats = mapOf("INT" to 3, "END" to 2)
                ),
                SubclassDefinition(
                    name = "Exobiólogo y Cuarentena",
                    initialPerkName = "Examen Forense Nivel 1",
                    perkDefinition = "Habilita 'Inspección de Cadáver' para determinar causa de muerte y registrar bolsillos sin peligro de trampas.",
                    requiredSkills = mapOf("Medicina y Criogenia" to 20),
                    requiredStats = mapOf("INT" to 2, "END" to 2)
                )
            )
        ),
        ClassDefinition(
            name = "Piloto / Comunicaciones",
            classBonusSkill = "Supervivencia Espacial y Bioquímica",
            classBonusValue = 20,
            requiredStats = mapOf("AGI" to 2),
            subclasses = listOf(
                SubclassDefinition(
                    name = "Vuelo de Curvatura",
                    initialPerkName = "Compensación de Inercia",
                    perkDefinition = "Habilita 'Disparo Anclado' para disparar armas pesadas o cortadores en cero-G sin perder estabilidad.",
                    requiredSkills = mapOf("Supervivencia Espacial y Bioquímica" to 35, "Armamento de Precisión y Balística" to 25),
                    requiredStats = mapOf("PER" to 3, "AGI" to 2)
                ),
                SubclassDefinition(
                    name = "Maniobras Subluminales",
                    initialPerkName = "Lanzador en Gravedad Cero",
                    perkDefinition = "Habilita 'Impulso Cinético' usando el retroceso de armas, extintores o enemigos para impulsarse.",
                    requiredSkills = mapOf("Supervivencia Espacial y Bioquímica" to 40, "Contundencia y Combate Cerrado (CQC)" to 30),
                    requiredStats = mapOf("AGI" to 3, "FUE" to 2)
                ),
                SubclassDefinition(
                    name = "Vehículos Auxiliares y EVAs",
                    initialPerkName = "Mochilero Espacial",
                    perkDefinition = "Pasiva: permite transportar equipo pesado de supervivencia EVA sin penalizaciones de movimiento.",
                    requiredSkills = mapOf("Supervivencia Espacial y Bioquímica" to 30, "Logística y Carga" to 30),
                    requiredStats = mapOf("END" to 3, "AGI" to 2)
                ),
                SubclassDefinition(
                    name = "Telecomunicaciones y Criptografía",
                    initialPerkName = "Bypass de Consola Nivel 2",
                    perkDefinition = "Habilita hackear terminales médicas, registros clasificados, cámaras y esclusas de seguridad media.",
                    requiredSkills = mapOf("Informática y Protocolos de Red" to 50),
                    requiredStats = mapOf("INT" to 3, "PER" to 2)
                )
            )
        ),
        ClassDefinition(
            name = "Seguridad / Militar",
            classBonusSkill = "Contundencia y Combate Cerrado (CQC)",
            classBonusValue = 20,
            requiredStats = mapOf("FUE" to 2),
            subclasses = listOf(
                SubclassDefinition(
                    name = "Contención de Disturbios y CQC",
                    initialPerkName = "Formar Barricada",
                    perkDefinition = "Habilita 'Plantar Barricada' usando planchas o puertas arrancadas como parapeto móvil de cobertura total.",
                    requiredSkills = mapOf("Contundencia y Combate Cerrado (CQC)" to 45, "Logística y Carga" to 35),
                    requiredStats = mapOf("FUE" to 3, "END" to 2)
                ),
                SubclassDefinition(
                    name = "Armamento Pesado y Brechas",
                    initialPerkName = "Sobrecarga de Herramienta",
                    perkDefinition = "Desbloquea 'Puenteo de Potencia' en herramientas industriales para infligir triple daño y fundir blindajes.",
                    requiredSkills = mapOf("Armamento de Precisión y Balística" to 35, "Reparación Estructural y de Sistemas" to 35),
                    requiredStats = mapOf("FUE" to 3, "PER" to 2)
                ),
                SubclassDefinition(
                    name = "Inteligencia y Contrainteligencia",
                    initialPerkName = "Interrogador Implacable",
                    perkDefinition = "Habilita 'Presión Psicológica' en diálogos tensos para detectar mentiras o cómplices de los puristas sin tortura.",
                    requiredSkills = mapOf("Psicología, Mando y Subterfugio" to 55),
                    requiredStats = mapOf("PER" to 3, "CAR" to 2)
                )
            )
        ),
        ClassDefinition(
            name = "Agrónomo / Especialista en Biosecha",
            classBonusSkill = "Supervivencia Espacial y Bioquímica",
            classBonusValue = 20,
            requiredStats = mapOf("END" to 2),
            subclasses = listOf(
                SubclassDefinition(
                    name = "Hidroponía y Nutrición Sintética",
                    initialPerkName = "Regateador de Raciones",
                    perkDefinition = "Habilita 'Trueque de Supervivencia' consiguiendo raciones extra, tratos ventajosos y prioridad en dispensadores.",
                    requiredSkills = mapOf("Negociación y Reasignación de Recursos" to 35, "Supervivencia Espacial y Bioquímica" to 25),
                    requiredStats = mapOf("CAR" to 2, "END" to 2)
                ),
                SubclassDefinition(
                    name = "Ecólogo de Cúpulas y Atmósferas",
                    initialPerkName = "Reciclador de Biomasa",
                    perkDefinition = "Habilita 'Reciclar Biomasa' para potabilizar agua de circuitos y extraer alimento de restos orgánicos dañados.",
                    requiredSkills = mapOf("Supervivencia Espacial y Bioquímica" to 40, "Medicina y Criogenia" to 20),
                    requiredStats = mapOf("END" to 3, "INT" to 2)
                ),
                SubclassDefinition(
                    name = "Genetista de Cultivos de Supertierra",
                    initialPerkName = "Destilador de Emergencia",
                    perkDefinition = "Habilita 'Purificar y Sintetizar' para crear antídotos contra gases, desinfectantes inflamables y parches de O2.",
                    requiredSkills = mapOf("Supervivencia Espacial y Bioquímica" to 45, "Medicina y Criogenia" to 25),
                    requiredStats = mapOf("INT" to 3, "END" to 2)
                )
            )
        ),
        ClassDefinition(
            name = "Tripulante Acompañante",
            classBonusSkill = null,
            classBonusValue = 0,
            requiredStats = mapOf("CAR" to 2),
            subclasses = listOf(
                SubclassDefinition(
                    name = "Enlace Político o Diplomático",
                    initialPerkName = "Credenciales Falsificadas",
                    perkDefinition = "Habilita 'Suplantar Autoridad' ante sistemas de voz y guardias fingiendo órdenes clasificadas.",
                    requiredSkills = mapOf("Psicología, Mando y Subterfugio" to 40, "Negociación y Reasignación de Recursos" to 30),
                    requiredStats = mapOf("CAR" to 3),
                    statusPrivilege = "Camarote de Alto Rango (Suite Privada)",
                    statusPrivilegeDesc = "Suite blindada con filtro de aire autónomo (inmune a humo tóxico), terminal institucional y dispensador selecto."
                ),
                SubclassDefinition(
                    name = "Representante del Sector Privado",
                    initialPerkName = "Liquidador de Activos",
                    perkDefinition = "Habilita 'Oferta Corporativa' para comprar favores o recursos a cambio de pagarés o terrenos futuros.",
                    requiredSkills = mapOf("Negociación y Reasignación de Recursos" to 45, "Psicología, Mando y Subterfugio" to 25),
                    requiredStats = mapOf("CAR" to 3, "INT" to 2),
                    statusPrivilege = "Línea de Crédito Corporativa",
                    statusPrivilegeDesc = "1 éxito automático de persuasión/soborno por sesión con PNJs no fanáticos; abre taquillas de empresas privadas sin forzarlas."
                ),
                SubclassDefinition(
                    name = "Familiar / Heredero de la Élite",
                    initialPerkName = "Contrabandista de Esclusa",
                    perkDefinition = "Permite ejecutar 'Ocultar Objeto' para burlar escáneres con armas cortas, tarjetas o viales.",
                    requiredSkills = mapOf("Sigilo e Infiltración en Conductos" to 35, "Manitas" to 25),
                    requiredStats = mapOf("CAR" to 2, "AGI" to 2),
                    statusPrivilege = "Caja Fuerte Personal y Menaje VIP",
                    statusPrivilegeDesc = "Alijo inicial propio (estimulante de lujo, licor destilado y microherramientas finas); bots de seguridad no letales priorizan atacar a otros."
                )
            )
        ),
        ClassDefinition(
            name = "Polizón",
            classBonusSkill = null,
            classBonusValue = 0,
            requiredStats = emptyMap(),
            subclasses = listOf(
                SubclassDefinition(
                    name = "Clandestino no registrado",
                    initialPerkName = "Superviviente de Conductos",
                    perkDefinition = "Comienza con 30 fijos en Sigilo e Infiltración. 140 puntos completamente libres para gastar.",
                    requiredSkills = mapOf("Sigilo e Infiltración en Conductos" to 30),
                    requiredStats = emptyMap()
                )
            )
        )
    )

    val UNLOCKABLE_PERKS: List<PerkDefinition> = listOf(
        PerkDefinition(
            name = "Adaptación Básica",
            category = "General",
            requirements = emptyMap(),
            actionName = "Supervivencia Estándar",
            description = "Perk universal para colonos sin requisitos especiales."
        ),
        PerkDefinition(
            name = "Resistencia al Vacío Leve",
            category = "General",
            requirements = mapOf("Supervivencia Espacial y Bioquímica" to 10),
            actionName = "Respiración Controlada",
            description = "Menor consumo de oxígeno en zonas presurizadas."
        ),
        PerkDefinition(
            name = "Bypass de Consola Nivel 1",
            category = "Interacción Digital",
            requirements = mapOf("Informática y Protocolos de Red" to 25),
            actionName = "Hackear Terminal Nivel 1",
            description = "Consolas de camarotes y puertas simples."
        ),
        PerkDefinition(
            name = "Bypass de Consola Nivel 2",
            category = "Interacción Digital",
            requirements = mapOf("Informática y Protocolos de Red" to 50),
            actionName = "Hackear Terminal Nivel 2",
            description = "Registros médicos, cámaras y esclusas intermedias."
        ),
        PerkDefinition(
            name = "Bypass de Consola Nivel 3",
            category = "Interacción Digital",
            requirements = mapOf("Informática y Protocolos de Red" to 75),
            actionName = "Hackear Terminal Nivel 3",
            description = "Terminales de Seguridad y Computadora Central."
        ),
        PerkDefinition(
            name = "Apagado de Emergencia (Shutdown)",
            category = "Interacción Digital",
            requirements = mapOf("Informática y Protocolos de Red" to 45, "Sigilo e Infiltración en Conductos" to 35),
            actionName = "Desconexión Furtiva",
            description = "Apaga un bot hostil por la espalda sin ruido ni combate."
        ),
        PerkDefinition(
            name = "Síndrome de Amigo/Enemigo",
            category = "Interacción Digital",
            requirements = mapOf("Informática y Protocolos de Red" to 65, "Reparación Estructural y de Sistemas" to 40),
            actionName = "Invertir Directiva",
            description = "Reprograma un bot abatido o torreta para que luche a vuestro favor."
        ),
        PerkDefinition(
            name = "Bucle de Señal",
            category = "Interacción Digital",
            requirements = mapOf("Informática y Protocolos de Red" to 40, "Manitas" to 30),
            actionName = "Cegar a la IA",
            description = "Pone sensores y cámaras en bucle para que la IA no envíe patrullas."
        ),
        PerkDefinition(
            name = "Forzado de Esclusas Nivel 1",
            category = "Acceso Físico",
            requirements = mapOf("Manitas" to 25),
            actionName = "Forzar Cierre Leve",
            description = "Abrir taquillas y maletines sin hacer ruido."
        ),
        PerkDefinition(
            name = "Armero de Fortuna",
            category = "Acceso Físico",
            requirements = mapOf("Reparación Estructural y de Sistemas" to 30, "Manitas" to 25),
            actionName = "Ensamblar Armamento",
            description = "Fabrica armas electrificadas, lanzas o escudos con chatarra."
        ),
        PerkDefinition(
            name = "Químico de Alcantarilla",
            category = "Acceso Físico",
            requirements = mapOf("Demoliciones y Trampas Industriales" to 35, "Supervivencia Espacial y Bioquímica" to 25),
            actionName = "Sintetizar Carga",
            description = "Crea granadas EMP, bombas de humo o cargas corrosivas."
        ),
        PerkDefinition(
            name = "Vampiro Energético",
            category = "Acceso Físico",
            requirements = mapOf("Reparación Estructural y de Sistemas" to 40, "Manitas" to 30),
            actionName = "Sifón de Plasma",
            description = "Recarga baterías agotadas de consolas, luces o núcleos de bots."
        ),
        PerkDefinition(
            name = "Cirujano de Campaña",
            category = "Supervivencia",
            requirements = mapOf("Medicina y Criogenia" to 45),
            actionName = "Tratar Herida Grave",
            description = "Cura huesos rotos, quemaduras de plasma o hemorragias en cero-G."
        ),
        PerkDefinition(
            name = "Despertar en Frío",
            category = "Supervivencia",
            requirements = mapOf("Medicina y Criogenia" to 60, "Informática y Protocolos de Red" to 30),
            actionName = "Protocolo Lázaro",
            description = "Descongelación segura instantánea sin ceguera ni vómitos criogénicos."
        ),
        PerkDefinition(
            name = "Terapia de Choque",
            category = "Social y Mando",
            requirements = mapOf("Psicología, Mando y Subterfugio" to 30),
            actionName = "Control de Pánico",
            description = "Gasta acción menor para sacar inmediatamente a un aliado de histeria o bloqueo."
        ),
        PerkDefinition(
            name = "Interrogador Implacable",
            category = "Social y Mando",
            requirements = mapOf("Psicología, Mando y Subterfugio" to 55),
            actionName = "Presión Psicológica",
            description = "Detecta mentiras o cómplices de los puristas en diálogos tensos."
        )
    )
}
