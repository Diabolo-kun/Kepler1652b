# Sistema de Creación de Personajes — Kepler-1652b

## 🧬 1. Reglas Base

- **Raza Única:** 100% Humano.
- **Modificaciones Corporales:** Se permiten implantes oculares, extremidades sintéticas, marcas genéticas o chips dérmicos como parte del trasfondo o detalles estéticos. No otorgan bonificadores mecánicos ni ventajas sobrehumanas (por lore de supervivencia humana).
- **Seleccionar Bando / Ideología:**
  - **Unión Humana:** Partidarios de la expansión planetaria, el avance tecnológico y la supervivencia de la especie como civilización multiplanetaria.
  - **Puristas:** Faccionarios radicales que abogan por preservar la genética humana original y asegurar por la fuerza que la especie no abandone la Tierra ni se corrompa en el cosmos.
  - **No alineado (Neutral):** Colonos civiles, mercenarios, parias y polizones que priorizan su supervivencia individual o la de sus seres queridos por encima de las doctrinas políticas.
- **Perfil de Jugador Integrado:** Cada jugador posee un perfil en su dispositivo con un identificador único (`playerId`) y un alias que se asigna automáticamente a cualquier ficha creada, vinculando al personaje directamente con su propietario en el manifiesto de la nave.

---

## 🎲 2. Atributos Base (SPECIAL)

**Escala:** Del 1 al 10 (donde 1 es deficiencia crítica, 5 es el promedio humano sano, y 10 es el pico máximo).

| Sigla | Atributo | Descripción |
|---|---|---|
| **FUE** | Fuerza | Potencia física, levantar escombros, golpear con armas improvisadas. |
| **PER** | Percepción | Agudeza sensorial, detectar fugas, escuchar ruidos en conductos o notar mentiras. |
| **END** | Resistencia | Salud general, aguante ante la falta de oxígeno, tolerancia al estrés criogénico y radiación. |
| **CAR** | Carisma | Fuerza de voluntad psicológica, cordura ante el terror del vacío y autoridad social/política. |
| **AGI** | Agilidad | Coordinación, reflejos, movimiento en microgravedad (Cero-G) y pulso fino. |
| **INT** | Intelecto | Razonamiento técnico, cálculo de trayectorias, comprensión de redes y lógica de la IA. |
| **SUE** | Suerte | Suerte. |

### Sistema de Creación de Atributos
- Todas las estadísticas empiezan por defecto en **1**.
- Para determinar la reserva total de puntos libres que se pueden repartir entre los 7 atributos, el jugador tira **1d20 en la mesa y le suma 10** (generando una reserva entre **11 y 30 puntos** para distribuir, con un promedio estándar de 20 puntos).
- **Diferenciación de Escalas:** Los 7 atributos SPECIAL van del **1 al 10**, mientras que **todas las habilidades van de 0 a 100**. No existe ninguna habilidad que vaya de 0 a 10.
- **Tope Biológico:** El valor de un atributo SPECIAL define el techo de aprendizaje máximo de sus habilidades vinculadas (desde 35 en atributo 1 hasta 100 en atributo 10).


---

## 🛠️ 3. Clases y Subclases

> (Ver el documento detallado de clases y subclases para la descripción completa)

| Clase | Requisito Orientativo | Subclases y Requisitos Mínimos |
|---|:---:|---|
| **Ingeniero / Técnico** | INT 2+ | Soporte Vital (INT 3, END 2) · Núcleo de Plasma (INT 3) · Automatización/Robótica (INT 3, AGI 2) · Mantenimiento (INT 2, FUE 2) |
| **Científico / Médico** | INT 2+ | Analista de Datos/IA (INT 3) · Astrofísico/Navegante (INT 3, PER 2) · Médico de a bordo (INT 3, END 2) · Exobiólogo (INT 2, END 2) |
| **Piloto / Comunicaciones** | AGI 2+ | Vuelo de Curvatura (PER 3, AGI 2) · Maniobras Subluminales (AGI 3, FUE 2) · Vehículos Auxiliares/EVAs (END 3, AGI 2) · Telecomunicaciones (INT 3, PER 2) |
| **Seguridad / Militar** | FUE 2+ | Contención de Disturbios/CQC (FUE 3, END 2) · Armamento Pesado/Brechas (FUE 3, PER 2) · Inteligencia (PER 3, CAR 2) |
| **Agrónomo / Especialista en Biosecha** | END 2+ | Hidroponía (CAR 2, END 2) · Ecólogo de Cúpulas (END 3, INT 2) · Genetista (INT 3, END 2) |
| **Tripulante Acompañante** | CAR 2+ | Enlace Político (CAR 3) · Representante Privado (CAR 3, INT 2) · Familiar VIP (CAR 2, AGI 2) |
| **Polizón** | *Sin requisitos* | Clandestino no registrado (sin requisitos de atributos, accesible para cualquier personaje) |

> **Acceso Libre al Menú de Clases:** En la aplicación se puede acceder y consultar libremente todas las clases para comprobar qué requisitos exige cada una de sus subclases antes de decidir.

---

## 📊 4. Las 12 Habilidades y Especializaciones (Perks)

- **Escala de Habilidad (0 a 100 para TODAS las habilidades):** 0 = sin formación, 30 = promedio profesional, 60 = experto consumado, 100 = referente o genio. Todas las 12 habilidades se miden del 0 al 100 (tiradas en 1d100) y ninguna tiene un tope de 10.
- **Especializaciones (Perks):** No se desbloquean automáticamente al alcanzar el número; alcanzar el umbral de habilidad es el requisito indispensable para poder adquirir el perk, habilitando una acción mecánica o ventaja concreta en partida.

### Las 12 Habilidades
1. **Manitas** (Técnica de Campo, Cerrajería y Desguace)
2. **Reparación Estructural y de Sistemas**
3. **Informática y Protocolos de Red**
4. **Logística y Carga** (Gestión de Inventario)
5. **Negociación y Reasignación de Recursos**
6. **Medicina y Criogenia**
7. **Armamento de Precisión y Balística**
8. **Contundencia y Combate Cerrado (CQC)**
9. **Demoliciones y Trampas Industriales**
10. **Sigilo e Infiltración en Conductos**
11. **Psicología, Mando y Subterfugio**
12. **Supervivencia Espacial y Bioquímica** (EVAs y Entorno)

---

## 🛠️ 5. Elección de Subclase y Reparto de Puntos

El reparto de puntos en Nivel 1 se realiza en 4 pasos:

1. **Bolsa Total de Puntos:**
   - **Clases Operativas:** Cuentan con **150 puntos base** + **15 a 20 puntos** de bono de clase en su especialidad principal (reserva total de **165 a 170 puntos**).
   - **Tripulante Acompañante:** Cuenta con **160 puntos base** (sin bono de clase por lore de versatilidad).
   - **Polizón:** Cuenta con **170 puntos de habilidad** en total. No tiene bono de clase ni asignaciones predefinidas, salvo **30 puntos obligatorios en Sigilo**. No recibe perk inicial, quedándole **140 puntos libres** para gastar libremente.
2. **Perk Inicial Obligatorio por Subclase:**
   - Al elegir subclase, se desbloquea su perk temático inicial. Los puntos necesarios para cumplir sus requisitos se asignan automáticamente de la bolsa a esas habilidades. *(El Polizón no tiene perk inicial).*
3. **Puntos Libres Restantes:**
   - El remanente (`Bolsa Total − Coste del Perk Inicial`) queda disponible para repartir libremente entre las 12 habilidades.
4. **Cierre de Creación (3 Elecciones Libres de Nivel 1):**
   - El jugador dispone de **3 elecciones libres** de nivel 1. Cada una puede ser:
     - **Desbloquear 1 perk** de especialización (la aplicación muestra un **menú desplegable filtrado exclusivamente con los perks a los que califica** según sus habilidades distribuidas, ocultando los bloqueados para evitar confusión), **o**
     - **Subir +1 punto un atributo SPECIAL** (máximo 10).
     - *Combinaciones posibles:* 3 perks · 2 perks + 1 atributo · 1 perk + 2 atributos · 3 atributos.

---

### 💻 El Asistente Digital de Creación (Wizard en 5 Pasos)
En la app de acompañamiento, la creación se realiza mediante un asistente guiado que valida cada fase antes de avanzar:
1. **Paso 1 (Datos):** Nombre del personaje y bando/ideología (el nombre y código de jugador se cargan automáticamente desde tu perfil activo local).
2. **Paso 2 (SPECIAL):** Introducción del resultado de la tirada física de dados (1d20+10, entre 11 y 30) y asignación táctica de puntos a los 7 atributos.
3. **Paso 3 (Clase y Subclase):** Exploración libre de las 7 clases y todas sus ramas, con indicadores claros de qué requisitos cumple o le faltan al personaje.
4. **Paso 4 (Habilidades):** Distribución de la bolsa de puntos con ajuste fino de 1 en 1 (`-1` / `+1`), saltos rápidos (`-5` / `+5`) y control deslizante (Slider), respetando en todo momento el **Tope Biológico** impuesto por el atributo afín.
5. **Paso 5 (Cierre):** Las 3 elecciones libres (perks elegibles en desplegable o +1 SPECIAL) y activación final para despertar del hipersueño.


---

### Tablas de Subclases, Perks Iniciales y Puntos Libres

#### 🛠️ Ingeniero / Técnico — Bolsa Total: 170 puntos (150 base + 20 en Reparación Estructural)
| Subclase | Perk inicial | Definición / Acción que Habilita | Requisito (Coste) | Puntos libres restantes |
|---|---|---|---|---|
| Soporte Vital y Termodinámica | **Soldadura en Caliente** | Habilita "Fundir Acceso" para sellar permanentemente escotillas o conductos en 1 turno con soplete/térmica. | Reparación 55 + Demoliciones 25 (**80 pts**) | **90 puntos** |
| Núcleo de Plasma y Propulsión | **Vampiro Energético** | Habilita "Sifón de Plasma" para recargar baterías directamente de luces, consolas o núcleos de bots destruidos. | Reparación 40 + Manitas 30 (**70 pts**) | **100 puntos** |
| Automatización y Robótica | **Apagado de Emergencia (Shutdown)** | Permite "Desconexión Furtiva" por la espalda a bots de seguridad/mantenimiento sin ruido ni entrar en combate. | Informática 45 + Sigilo 35 (**80 pts**) | **70 puntos** |
| Mantenimiento y Control de Daños | **Armero de Fortuna** | Desbloquea "Ensamblar Armamento" con chatarra (armas electrificadas, lanzas térmicas, escudos improvisados). | Reparación 30 + Manitas 25 (**55 pts**) | **115 puntos** |

#### 🔬 Científico / Médico — Bolsa Total: 165 puntos (150 base + 15 en Informática)
| Subclase | Perk inicial | Definición / Acción que Habilita | Requisito (Coste) | Puntos libres restantes |
|---|---|---|---|---|
| Analista de Datos e IA | **Bypass de Consola Nivel 1** | Habilita "Hackear Terminal" en consolas de camarotes y puertas residenciales simples. | Informática 25 (**25 pts**) | **140 puntos** |
| Astrofísico y Navegante | **Bucle de Señal** | Permite poner cámaras y sensores en bucle para engañar a la IA y evitar que envíe patrullas a la zona. | Informática 40 + Manitas 30 (**70 pts**) | **95 puntos** |
| Médico de a bordo y Criogenia | **Cirujano de Campaña** | Habilita "Tratar Herida Grave" para curar huesos rotos, quemaduras de plasma o hemorragias en cero-G. | Medicina 45 (**45 pts**) | **105 puntos** |
| Exobiólogo y Cuarentena | **Examen Forense Nivel 1** | Habilita "Inspección de Cadáver" para determinar causa de muerte y registrar bolsillos sin peligro de trampas. | Medicina 20 (**20 pts**) | **130 puntos** |

#### 🚀 Piloto / Comunicaciones — Bolsa Total: 170 puntos (150 base + 20 en Supervivencia Espacial)
| Subclase | Perk inicial | Definición / Acción que Habilita | Requisito (Coste) | Puntos libres restantes |
|---|---|---|---|---|
| Vuelo de Curvatura | **Compensación de Inercia**\* | Habilita "Disparo Anclado" para disparar armas pesadas o cortadores en cero-G sin perder estabilidad ni salir despedido. | Supervivencia 35 + Armamento 25 (**60 pts**) | **110 puntos** |
| Maniobras Subluminales | **Lanzador en Gravedad Cero** | Habilita "Impulso Cinético" usando el retroceso de armas, extintores o enemigos para impulsarse en el aire. | Supervivencia 40 + CQC 30 (**70 pts**) | **100 puntos** |
| Vehículos Auxiliares y EVAs | **Mochilero Espacial**\* | Pasiva: permite transportar equipo pesado de supervivencia EVA sin penalizaciones de movimiento. | Supervivencia 30 + Logística 30 (**60 pts**) | **110 puntos** |
| Telecomunicaciones y Criptografía | **Bypass de Consola Nivel 2** | Habilita hackear terminales médicas, registros clasificados, cámaras y esclusas de seguridad media. | Informática 50 (**50 pts**) | **100 puntos** |

#### 🛡️ Seguridad / Militar — Bolsa Total: 170 puntos (150 base + 20 en Contundencia/CQC)
| Subclase | Perk inicial | Definición / Acción que Habilita | Requisito (Coste) | Puntos libres restantes |
|---|---|---|---|---|
| Contención de Disturbios y CQC | **Formar Barricada** | Habilita "Plantar Barricada" usando planchas o puertas arrancadas como parapeto móvil de cobertura total. | CQC 45 + Logística 35 (**80 pts**) | **90 puntos** |
| Armamento Pesado y Brechas | **Sobrecarga de Herramienta** | Desbloquea "Puenteo de Potencia" en herramientas industriales para infligir triple daño y fundir blindajes. | Armamento 35 + Reparación 35 (**70 pts**) | **80 puntos** |
| Inteligencia y Contrainteligencia | **Interrogador Implacable** | Habilita "Presión Psicológica" en diálogos tensos para detectar mentiras o cómplices de los puristas sin tortura. | Psicología/Mando 55 (**55 pts**) | **95 puntos** |

#### 🌱 Agrónomo / Biosecha — Bolsa Total: 170 puntos (150 base + 20 en Supervivencia Espacial)
| Subclase | Perk inicial | Definición / Acción que Habilita | Requisito (Coste) | Puntos libres restantes |
|---|---|---|---|---|
| Hidroponía y Nutrición Sintética | **Regateador de Raciones**\* | Habilita "Trueque de Supervivencia" consiguiendo raciones extra, tratos ventajosos y prioridad en dispensadores. | Negociación 35 + Supervivencia 25 (**60 pts**) | **110 puntos** |
| Ecólogo de Cúpulas y Atmósferas | **Reciclador de Biomasa**\* | Habilita "Reciclar Biomasa" para potabilizar agua de circuitos y extraer alimento de restos orgánicos dañados. | Supervivencia 40 + Medicina 20 (**60 pts**) | **110 puntos** |
| Genetista de Cultivos de Supertierra | **Destilador de Emergencia** | Habilita "Purificar y Sintetizar" para crear antídotos contra gases, desinfectantes inflamables y parches de O2. | Supervivencia 45 + Medicina 25 (**70 pts**) | **100 puntos** |

#### 🎫 Tripulante Acompañante — Bolsa Total: 160 puntos (Sin bono de clase operativo)
> *Compensación de Trasfondo:* Al no poseer entrenamiento operativo de campo, cada subclase recibe además un **Privilegio de Estatus** pasivo exclusivo (ver detalles en la sección de Especializaciones).

| Subclase | Perk inicial | Definición de Acción | Privilegio de Estatus | Definición de Estatus | Requisito (Coste) | Puntos libres restantes |
|---|---|---|---|---|---|---|
| Enlace Político o Diplomático | **Credenciales Falsificadas** | Habilita "Suplantar Autoridad" ante sistemas de voz y guardias fingiendo órdenes clasificadas. | **Camarote de Alto Rango (Suite Privada)** | Suite ejecutiva blindada con filtro de aire autónomo (inmune a humo tóxico del pasillo), terminal institucional propia y dispensador selecto. | Psicología 40 + Negociación 30 (**70 pts**) | **90 puntos** |
| Representante del Sector Privado | **Liquidador de Activos**\* | Habilita "Oferta Corporativa" para comprar favores o recursos a cambio de pagarés o terrenos futuros. | **Línea de Crédito Corporativa** | 1 éxito automático de persuasión/soborno por sesión con PNJs no fanáticos; abre taquillas logísticas de empresas privadas sin forzarlas. | Negociación 45 + Psicología 25 (**70 pts**) | **90 puntos** |
| Familiar / Heredero de la Élite | **Contrabandista de Esclusa** | Permite ejecutar "Ocultar Objeto" para burlar escáneres con armas cortas, tarjetas o viales. | **Caja Fuerte Personal y Menaje VIP** | Alijo inicial propio (1 estimulante médico de lujo, 1 licor destilado y microherramientas finas); bots de seguridad no letales priorizan atacar a otros antes que a él. | Sigilo 35 + Manitas 25 (**60 pts**) | **100 puntos** |

#### 🫥 Polizón — Bolsa Total: 170 puntos (Clandestino no registrado)
> *Superviviente de conductos:* Viaja de forma ilegal escondido en zonas de mantenimiento o bodegas de carga. No cuenta con privilegios oficiales, respaldo de facción ni perks iniciales de profesión, pero posee la mayor libertad de puntos iniciales gracias a su capacidad de adaptación.

| Arquetipo | Perk inicial | Definición | Inversión Obligatoria de Trasfondo | Puntos libres para otras habilidades |
|---|---|---|---|---|
| **Polizón / Clandestino** | *Ninguno al inicio* | Comienza sin perks iniciales predefinidos. Deberá desbloquear sus primeros perks mediante las 3 elecciones de cierre de creación. | **30 puntos fijos en Sigilo e Infiltración** | **140 puntos** |

---

## 📚 6. Catálogo de Especializaciones (Perks Desbloqueables)

Las siguientes especializaciones no se obtienen automáticamente al subir una habilidad; alcanzar el umbral numérico de habilidad es el requisito previo para poder elegir y adquirir la Especialización, la cual desbloquea una acción mecánica concreta en la partida:

### 💻 Interacción Digital y Robótica (Contra la IA y los Bots)

#### Bypass de Consola (Niveles 1, 2 y 3)
- **Requisito:** Nivel 1 (Informática 25) / Nivel 2 (Informática 50) / Nivel 3 (Informática 75).
- **Acción que habilita:** Sin este perk, un personaje solo puede usar terminales desbloqueadas. Tenerlo permite ejecutar la acción **"Hackear Terminal"**:
  - Nivel 1: consolas de camarotes y puertas simples.
  - Nivel 2: registros médicos, cámaras y esclusas.
  - Nivel 3: terminales de Seguridad y nodos de la Computadora Central.

#### Apagado de Emergencia (Shutdown)
- **Requisito:** Informática 45 + Sigilo 35.
- **Acción que habilita:** Permite ejecutar la acción **"Desconexión Furtiva"** al acercarte por detrás a un bot de mantenimiento o seguridad hostil. En lugar de golpearlo, abres su panel trasero y lo apagas al instante sin hacer ruido ni entrar en combate.

#### Síndrome de Amigo/Enemigo (Reprogramador)
- **Requisito:** Informática 65 + Reparación Estructural 40.
- **Acción que habilita:** Permite usar la acción **"Invertir Directiva"** sobre un bot abatido (pero no destruido) o una torreta desactivada para que luche a favor de los tripulantes contra otros bots de la IA corrupta.

#### Bucle de Señal (Cegar a la IA)
- **Requisito:** Informática 40 + Manitas 30.
- **Acción que habilita:** Permite interactuar con cualquier cámara o sensor de movimiento de un pasillo para ponerlo en bucle. La IA creerá que el pasillo está despejado y no enviará patrullas de bots a esa zona.

### 🛠️ Acceso Físico, Ingeniería y Entorno

#### Forzado de Esclusas y Cierres (Niveles 1, 2 y 3)
- **Requisito:** Nivel 1 (Manitas 25) / Nivel 2 (Manitas 50) / Nivel 3 (Manitas 75).
- **Acción que habilita:** Habilita la acción **"Forzar Cierre"**. Sin este perk, solo puedes intentar romper puertas a golpes (haciendo muchísimo ruido). Con él:
  - Nvl 1: abrir taquillas y maletines.
  - Nvl 2: puentear puertas neumáticas sin energía.
  - Nvl 3: abrir armeros de seguridad y cámaras acorazadas.

#### Armero de Fortuna (Crafting de Armas)
- **Requisito:** Reparación 30 + Manitas 25.
- **Acción que habilita:** Desbloquea la acción **"Ensamblar Armamento"** en plena partida. Permite unir chatarra del escenario (ej. una tubería de plomo + un cable de alta tensión o una batería de litio) para crear armas electrificadas, lanzas térmicas o escudos pesados.

#### Químico de Alcantarilla (Crafting de Explosivos/EMP)
- **Requisito:** Demoliciones 35 + Supervivencia Espacial 25.
- **Acción que habilita:** Desbloquea la acción **"Sintetizar Carga"**. Permite mezclar productos de limpieza de los camarotes, fertilizantes de agronomía o baterías rotas para crear granadas de pulso electromagnético (EMP) contra los bots, bombas de humo o cargas corrosivas para fundir cerraduras.

#### Despresurización Táctica
- **Requisito:** Reparación 50 + Supervivencia Espacial 40.
- **Acción que habilita:** Te permite usar los paneles de ventilación de una sala para ejecutar **"Purga de Aire Localizada"** o ventilar humo tóxico/incendios hacia otro sector sin asfixiar a tu grupo, usando las corrientes de presión a tu favor.

#### Caníbal de Piezas (Desguace Avanzado)
- **Requisito:** Manitas 35.
- **Acción que habilita:** Al registrar un bot destruido o una máquina rota, habilita la acción **"Extraer Componente Intacto"**, permitiéndote sacar baterías de plasma, lentes ópticas o servomotores útiles que cualquier otro personaje rompería al intentar arrancarlos.

#### Vampiro Energético (Drenaje de Red)
- **Requisito:** Reparación Estructural 40 + Manitas 30.
- **Acción que habilita:** Desbloquea la acción **"Sifón de Plasma"**. Permite recargar baterías agotadas de trajes EVA, linternas o herramientas conectándolas directamente con cables pelados al alumbrado de un pasillo, a una consola activa o al núcleo de un bot recién destruido.

#### Soldadura en Caliente (Sellado Instantáneo)
- **Requisito:** Reparación Estructural 55 + Demoliciones 25.
- **Acción que habilita:** Habilita la acción **"Fundir Acceso"**. Permite usar una carga térmica rápida o un soldador sobrecargado para sellar permanentemente una escotilla o conducto de ventilación en un solo turno, impidiendo que una horda de bots o un incendio os alcance.

#### Trampa de Arco Voltaico (Electrificar Suelo)
- **Requisito:** Reparación Estructural 45 + Demoliciones 40.
- **Acción que habilita:** Habilita la acción **"Tender Red de Alta Tensión"**. Permite conectar el cableado de la pared a las rejillas metálicas del suelo o a una fuga de agua/refrigerante, creando una zona electrificada que fríe los circuitos de cualquier bot que la pise.

### 🎒 Inventario, Cuerpo y Supervivencia

#### Espalda de Titanio (Niveles 1 y 2)
- **Requisito:** Logística y Carga 35 (Nvl 1) / Logística y Carga 65 (Nvl 2).
- **Acción que habilita:** Aumenta directamente tus ranuras de inventario máximas (+25% / +50%) y te habilita la acción **"Acarrear Objeto Pesado"** (llevar una celda de combustible industrial, una puerta arrancada como escudo móvil o a un compañero inconsciente al hombro pudiendo seguir usando un arma a una mano).

#### Anatomía Mecánica (Cazador de Bots)
- **Requisito:** Armamento 40 (o CQC 40) + Reparación 30.
- **Acción que habilita:** Desbloquea la acción de combate **"Ataque Dirigido a Chasis"**. Te permite declarar antes de atacar que apuntas a las ruedas/piernas de un bot (para inmovilizarlo), a su visor (para cegarlo) o a su brazo de sierra (para desarmarlo).

#### Formar Barricada
- **Requisito:** Contundencia/CQC 45 + Logística y Carga 35.
- **Acción que habilita:** Habilita la acción **"Plantar Barricada"** en pasillos estrechos. Usando una puerta de taquilla arrancada, restos de mobiliario o una plancha metálica pesada, el personaje levanta un parapeto fijo o móvil que sirve de cobertura total para él y los compañeros situados detrás, absorbiendo el fuego de torretas o las embestidas de bots mientras el grupo avanza o dispara desde atrás.

#### Balística de Rebote (Geometría de Pasillo)
- **Requisito:** Armamento de Precisión 65.
- **Acción que habilita:** Habilita la acción **"Disparo en Ángulo"**. Permite disparar armas cinéticas o cortadores industriales contra las paredes metálicas reforzadas de la nave para acertar a enemigos o cierres de puertas que estén doblando una esquina o tras cobertura total, sin exponerte al fuego enemigo.

#### Lanzador en Gravedad Cero (Vector Humano)
- **Requisito:** Supervivencia Espacial 40 + Contundencia/CQC 30.
- **Acción que habilita:** En sectores sin gravedad o en el exterior del casco, habilita la acción **"Impulso Cinético"**. Permite usar el retroceso de un arma, un extintor de CO2 o el propio cuerpo de un enemigo como trampolín para propulsarte a gran velocidad de un extremo a otro de una sala sin gastar combustible del traje EVA.

#### Sobrecarga de Herramienta (Modo Letal)
- **Requisito:** Armamento 35 + Reparación Estructural 35.
- **Acción que habilita:** Habilita la acción **"Puenteo de Potencia"** en herramientas industriales (cortadores de plasma, soldadores, pistolas de clavos). Anula los seguros de fábrica para que el próximo disparo haga el triple de daño y funda blindajes pesados, a cambio de recalentar o inutilizar la batería de la herramienta tras el uso.

#### Cirujano de Campaña (Extracción y Sutura)
- **Requisito:** Medicina 45.
- **Acción que habilita:** Un botiquín normal solo recupera puntos de vida leves. Este perk habilita la acción **"Tratar Herida Grave"**, indispensable para curar estados alterados permanentes como huesos rotos, quemaduras de tercer grado por plasma o hemorragias internas en gravedad cero.

#### Despertar en Frío (Protocolo Lázaro)
- **Requisito:** Medicina 60 + Informática 30.
- **Acción que habilita:** Permite ejecutar **"Descongelación Segura de Emergencia"** en cualquier cápsula de hipersueño. Permite despertar a PNJs clave (o a jugadores que se unan a mitad de sesión) al instante y al 100% de sus capacidades, sin que sufran el penalizador de ceguera y vómitos post-criogénicos.

#### Farmacología de Combate (Cóctel de Adrenalina)
- **Requisito:** Medicina y Criogenia 40 + Supervivencia Espacial 30.
- **Acción que habilita:** Habilita la acción **"Inyección de Sobrecarga"**. Permite mezclar fármacos de emergencia para inyectar a un aliado un estimulante que ignora todo el dolor y las penalizaciones por heridas graves durante un combate entero (aunque sufre fatiga extrema al terminar el efecto).

#### Examen Forense (Niveles 1 y 2: Inspección de Cadáver / Autopsia Forense)
- **Requisito:**
  - Nivel 1 — Inspección de Cadáver: Medicina y Criogenia 20.
  - Nivel 2 — Autopsia Forense y Biopsia de Sabotaje: Medicina y Criogenia 45 + Psicología/Mando 25.
  - *Nota: Si un personaje adquiere directamente el Nivel 2 al cumplir sus requisitos, obtiene automáticamente todas las capacidades del Nivel 1 sin necesidad de haberlo comprado antes.*
- **Acción que habilita:**
  - **Nivel 1 ("Inspección de Cadáver"):** Permite realizar un examen preliminar rápido de un cuerpo para determinar la causa evidente de la muerte (impacto, asfixia, descarga), estimar cuánto tiempo lleva fallecido y registrar sus bolsillos con seguridad sin activar trampas físicas simples.
  - **Nivel 2 ("Autopsia Forense"):** Permite realizar una autopsia médica y técnica exhaustiva del cadáver (como el del técnico purista muerto en la Sesión 1), descubriendo venenos o fármacos en sangre, micro-implantes ocultos bajo la piel, modificaciones genéticas o pistas forenses exactas del arma o herramienta homicida.

#### Destilador de Emergencia (Bio-Alquimia)
- **Requisito:** Supervivencia Espacial 45 + Medicina 25.
- **Acción que habilita:** Habilita la acción **"Purificar y Sintetizar"**. Usando muestras de los domos agrónomos o fluidos orgánicos, puedes fabricar antídotos contra gases tóxicos, alcohol médico desinfectante de alta graduación (útil también como inflamable) o parches de oxígeno químico a partir de algas.

#### Compensación de Inercia (Tirador Estabilizado) \*
- **Requisito:** Supervivencia Espacial 35 + Armamento 25. *(propuesta)*
- **Acción que habilita:** Habilita la acción **"Disparo Anclado"**. Permite disparar armas pesadas o cortadores industriales en gravedad cero o gravedad inestable sin perder el equilibrio ni salir despedido por el retroceso.

#### Mochilero Espacial (Carga Distribuida) \*
- **Requisito:** Supervivencia Espacial 30 + Logística y Carga 30. *(propuesta)*
- **Acción que habilita:** Pasiva. Permite llevar equipo adicional de supervivencia (tanques de O2, kits de sellado, herramientas EVA) sin sufrir penalizaciones de movimiento.

#### Reciclador de Biomasa \*
- **Requisito:** Supervivencia Espacial 40 + Medicina 20. *(propuesta)*
- **Acción que habilita:** Habilita la acción **"Reciclar Biomasa"**. Permite potabilizar agua de circuitos y extraer nutrientes comestibles de plantas dañadas o raciones caducadas.

### 🗣️ Sociales, de Mando y Subterfugio

#### Credenciales Falsificadas / Jerga de Alto Mando
- **Requisito:** Psicología/Mando 40 + Negociación 30.
- **Acción que habilita:** Habilita la acción **"Suplantar Autoridad"** ante sistemas de voz de la nave o frente a guardias de seguridad y políticos del Gran Consejo, haciéndoles creer que tienes órdenes clasificadas de rango superior.

#### Contrabandista de Esclusa (Bolsillos Ocultos)
- **Requisito:** Sigilo 35 + Manitas 25.
- **Acción que habilita:** Permite ejecutar **"Ocultar Objeto"**. Si la seguridad de la nave, facciones rivales o escáneres automáticos os registran, puedes esconder armas pequeñas, tarjetas de datos o viales médicos sin que aparezcan en el registro.

#### Interrogador Implacable (Cazador de Puristas)
- **Requisito:** Psicología/Mando 55.
- **Acción que habilita:** Habilita la acción **"Presión Psicológica"** durante un diálogo tenso. Obliga a un PNJ sospechoso a revelar si está mintiendo o a soltar información sobre posibles cómplices del saboteador purista sin necesidad de llegar a la tortura física.

#### Terapia de Choque
- **Requisito:** Psicología/Mando 30.
- **Acción que habilita:** Cuando un compañero de equipo o un PNJ entra en estado de "Pánico", "Histeria" o "Bloqueo Mental" (perdiendo su turno por miedo a los incendios, al vacío o a los bots), te permite gastar una acción menor para gritarle o abofetearle y devolverle el control inmediatamente.

#### Chivo Expiatorio (Desviar Culpa)
- **Requisito:** Psicología/Mando 50 + Sigilo 30.
- **Acción que habilita:** Habilita la acción **"Plantar Prueba Falsa"** (tanto física como digitalmente si se combina con alguien de informática). Permite incriminar a un PNJ rival (un político molesto o un jefe de seguridad corrupto) de colaborar con los puristas o de gastar recursos ilegalmente ante el resto de la tripulación.

#### Protocolo de Racionamiento (Ley Marcial)
- **Requisito:** Negociación 55 + Psicología/Mando 45.
- **Acción que habilita:** Habilita la acción **"Requisa Oficial"**. Usando las leyes de emergencia del Gran Consejo, puedes obligar legalmente a PNJs (especialmente del Sector Privado o élites) a entregarte sus suministros privados, armas o códigos de acceso sin que la seguridad de la nave lo considere un robo.

#### Señuelo Acústico (Eco de Conducto)
- **Requisito:** Sigilo 40 + Manitas 20.
- **Acción que habilita:** Habilita la acción **"Crear Distracción Remota"**. Usando un comunicador viejo modulado o golpeando una tubería maestra en el punto exacto, puedes proyectar ruido a través de la estructura de la nave para atraer a todos los bots de una sala hacia el pasillo contrario.

#### Regateador de Raciones \*
- **Requisito:** Negociación 35 + Supervivencia Espacial 25. *(propuesta)*
- **Acción que habilita:** Habilita la acción **"Trueque de Supervivencia"**. Consigue mejores tratos, raciones extra o acceso prioritario a dispensadores de agua.

#### Liquidador de Activos \*
- **Requisito:** Negociación 45 + Psicología/Mando 25. *(propuesta)*
- **Acción que habilita:** Habilita la acción **"Oferta Corporativa"**. Permite prometer pagos, acciones o terrenos en la futura colonia a cambio de recursos, favores o información de PNJs. La deuda queda registrada y puede reclamarse (o volverse en tu contra) más adelante.

---

### 🏛️ Privilegios de Estatus (Exclusivos de Tripulante Acompañante)
*Estos beneficios representan la influencia, recursos y posición social de los personajes que no tienen una formación técnica operativa militar o científica a bordo. Se obtienen automáticamente según la subclase elegida y no consumen puntos de habilidad:*

#### Camarote de Alto Rango (Suite Privada)
- **Subclase:** Enlace Político / Diplomático.
- **Efecto de Juego:** El personaje posee llave biométrica y acceso prioritario a una suite de clase ejecutiva en el sector residencial de la nave. 
  - **Refugio seguro:** Cuenta con mamparo reforzado independiente y sistema de filtrado de aire propio (inmune a despresurizaciones menores o humo del pasillo mientras esté sellado).
  - **Recursos iniciales:** Terminal de comunicaciones de canal institucional (para consultar censos o decretos del Gran Consejo) y un pequeño dispensador privado de agua purificada y raciones de primera clase.

#### Línea de Crédito Corporativa
- **Subclase:** Representante del Sector Privado.
- **Efecto de Juego:** Dispone de un chip financiero con fondos garantizados por megacorporaciones terrestres.
  - **Soborno formal:** 1 vez por sesión, puede superar automáticamente una tirada de persuasión contra cualquier PNJ no purista (guardias de seguridad rasos, técnicos o personal civil) transfiriéndole una compensación económica diferida o prometiendo empleo vitalicio garantizado en la colonia.
  - **Acceso a taquillas corporativas:** Puede abrir terminales de suministros logísticos de empresas privadas asociadas sin necesidad de hackearlas ni forzarlas.

#### Caja Fuerte Personal y Menaje de Lujo
- **Subclase:** Familiar / Heredero de la Élite.
- **Efecto de Juego:** Al no viajar como trabajador sino como pasajero preferente, cuenta con una caja fuerte empotrada en su taquilla personal protegida por código numérico de alta seguridad.
  - **Alijo de inicio:** Comienza la aventura sabiendo que en su camarote hay un alijo intacto con: 1 estimulante médico de lujo (cura instantánea de trauma leve sin secuelas), 1 botella de licor destilado (útil para trueque, calmar histeria o desinfección) y 1 juego de herramientas de precisión compactas (que ocupan la mitad de espacio de inventario normal).
  - **Aura de intocable:** Los bots de seguridad civil tienen órdenes preinstaladas de preservar a los familiares de la élite con prioridad alfa; si un bot de seguridad entra en modo no letal, siempre priorizará apresar o disparar a otros antes que a este personaje.
