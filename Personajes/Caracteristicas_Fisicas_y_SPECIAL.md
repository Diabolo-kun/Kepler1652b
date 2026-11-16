# Características Físicas y Sistema de Atributos SPECIAL

Este documento detalla el funcionamiento mecánico de los **7 Atributos SPECIAL**, las características derivadas (salud, inventario, cordura, etc.) y cómo interactúan las estadísticas directamente con el entorno y las tiradas durante la partida.

---

## 🎲 1. La Escala de Atributos (1 al 10)

Todos los atributos base se miden en una escala humana realista:

| Valor | Nivel de Capacidad | Descripción en Partida |
|:---:|---|---|
| **1** | **Deficiencia Crítica** | Discapacidad severa, atrofia post-crio o fallo orgánico. Dificultad extrema en tareas afines. |
| **2 - 3** | **Bajo / Limitado** | Por debajo del promedio. Requiere esfuerzo evidente o asistencia de herramientas. |
| **4 - 5** | **Promedio Humano Sano** | La media estándar de un colono o tripulante adiestrado sin especialización extrema. |
| **6 - 7** | **Avanzado / Profesional** | Rendimiento notable por entrenamiento riguroso, disciplina militar o académica. |
| **8 - 9** | **Pico Operativo** | Élite de la especie humana (atletas olímpicos, mentes maestras, veteranos de combate). |
| **10** | **Pico Máximo Teórico** | El límite biológico absoluto de un ser humano sin modificaciones transhumanas letales. |

---

## 🧬 2. Valores Derivados del Cuerpo

A partir de los atributos base se calculan las estadísticas de combate y supervivencia:

### A. Puntos de Vida (Salud / Integridad Física)
- **Fórmula:** `PV Máximos = 20 + (END × 5) + (FUE × 2)`
- *Ejemplo:* Un personaje con END 5 y FUE 4 tiene: `20 + 25 + 8 = 53 PV`.
- **Estados de Daño:**
  - **100% - 50% PV:** Operativo pleno.
  - **< 50% PV (Herido Grave):** Hemorragia o dolor agudo. Requiere chequeo de Resistencia ante esfuerzos continuos.
  - **0 PV (Incapacitado / Agonía):** Cae inconsciente. Muere en `END` turnos si no recibe primeros auxilios o el perk *Cirujano de Campaña*.

### B. Capacidad de Carga (Ranuras de Inventario)
- **Fórmula:** `Ranuras de Equipo = FUE + END + 5`
- *Ejemplo:* FUE 5 y END 4 otorgan **14 ranuras**.
- *Reglas de carga:*
  - Objetos pequeños (viales, llaves, tarjetas): 0 ranuras (hasta un límite razonable en bolsillos).
  - Herramientas estándar, pistolas o linternas: 1 ranura.
  - Armas pesadas, botellas de gas, trajes EVA completos: 2 a 3 ranuras.
  - *Perk Espalda de Titanio:* Suma +25% / +50% de capacidad y permite acarrear pesos muertos a una mano.

### C. Cordura y Aplomo Psicológico (Estrés ante el Vacío)
- **Escala:** Percentil **0% a 100%** (todos los tripulantes inician al **100%** de Cordura).
- **Mecánica:** Se deteriora ante horrores cósmicos, aislamiento extremo, corrupción de la IA, mutilaciones de compañeros o fugas descontroladas.
- **Efectos por Umbral:**
  - **100% - 70%:** Mente lúcida y bajo control.
  - **69% - 30% (Estrés Severo):** Alucinaciones auditivas leves, paranoia respecto a los compañeros, temblor en manos (-10% a habilidades de precisión).
  - **29% - 1% (Borde del Colapso):** Fobia irracional, ataques de pánico o bloqueo involuntario de turnos.
  - **0% (Colapso Psicológico):** Locura transitoria, histeria agresiva o catatonía. Requiere intervención inmediata (*Terapia de Choque*, sedantes intravenosos o contención física).

### D. Soporte Vital (Presurización, Mezcla Respiratoria y O2)
- **Escala:** **0% a 100%** (100% = atmósfera enriquecida óptima de la nave o traje EVA).
- **Mecánica:** Sustituye el término genérico de "oxígeno" para representar con precisión el soporte de vida completo (suministro de O2, reciclador de CO2, presión de cabina y control térmico).
- **Autonomía básica en fallo:** `Minutos de resistencia antes de asfixia = END × 0.5 min` (30 segundos por punto de Resistencia).
- **Pérdida Crítica (< 20%):** Hipoxia severa, visión de túnel y pérdida de 1d10 PV por turno no presurizado.

### E. Estado Vital (Vivo / Fallecido)
- Cada personaje cuenta con un identificador de estado vital directo (`💚 VIVO` / `💀 FALLECIDO`).
- Los personajes fallecidos permanecen en el manifiesto de la tripulación para consulta histórica y forense de la campaña, con opción de eliminación permanente por parte del Máster bajo confirmación.

### F. Variables Ambientales y Equilibrio Central en 0 (Control del Máster)
- Las condiciones del cuadrante y la nave se rigen por un **equilibrio neutro en 0**:
  - **Radiación:** Variaciones por fugas del reactor o tormentas solares.
  - **Toxicidad:** Contaminación por escape de gases criogénicos o refrigerante de plasma.
  - **Soporte Vital:** Despresurizaciones o brechas de casco.
  - **Cordura Colectiva:** Pulsos de estrés o eventos traumáticos de la nave.
- El Director de Juego aplica ajustes en incrementos o decrementos de **+1 / -1 unidad** desde el centro neutro, afectando a toda la sala de juego de forma global o aplicando variaciones personalizadas a cada tripulante según su posición en la nave.


---

## ⚙️ 3. Los 7 Atributos y su Impacto en el Juego

### 1. FUE — Fuerza
*La masa muscular, potencia de palanca y tracción física.*
- **En el Juego:**
  - Daño cuerpo a cuerpo (CQC) e impacto de armas improvisadas.
  - Levantar mamparos caídos, forzar escotillas atascadas manualmente o acarrear escombros.
  - Define las ranuras de inventario junto con Resistencia.
- **Habilidades Afines (0 a 100):**
  - *Contundencia y Combate Cerrado (CQC)*
  - *Logística y Carga*

### 2. PER — Percepción
*La agudeza sensorial, atención al detalle y orientación espacial.*
- **En el Juego:**
  - Detectar fugas acústicas invisibles de gas en conductos y fallos eléctricos en mamparos.
  - Iniciativa al inicio de combates o emboscadas de bots.
  - Puntería balística a larga distancia en corredores o exterior de la nave.
  - Notar incongruencias en testimonios (detectar mentiras o infiltrados puristas).
- **Habilidades Afines (0 a 100):**
  - *Armamento de Precisión y Balística*
  - *Sigilo e Infiltración en Conductos*

### 3. END — Resistencia (Endurance)
*La fortaleza inmunológica, capacidad cardiopulmonar y tolerancia al dolor.*
- **En el Juego:**
  - Base principal para los **Puntos de Vida (PV)**.
  - Recuperación de estados de shock, quemaduras de plasma y hemorragias.
  - Aguante del vómito y ceguera temporal al despertar forzoso de criogenia.
  - Resistencia a trajes EVA perforados y descompresión lenta.
- **Habilidades Afines (0 a 100):**
  - *Supervivencia Espacial y Bioquímica (EVAs y Entorno)*
  - *Medicina y Criogenia*

### 4. CAR — Carisma / Aplomo
*La fuerza de voluntad, compostura mental, presencia escénica y templanza ante el terror.*
- **En el Juego:**
  - Resistencia contra el pánico, claustrofobia y estrés del vacío exterior.
  - Negociación de raciones, diplomacia de facción y mando sobre tripulantes histéricos.
  - Autoridad para impartir órdenes bajo fuego o imponer ley marcial.
- **Habilidades Afines (0 a 100):**
  - *Psicología, Mando y Subterfugio*
  - *Negociación y Reasignación de Recursos*

### 5. AGI — Agilidad
*La destreza motriz, coordinación ojo-mano, reflejos y equilibrio tridimensional.*
- **En el Juego:**
  - Movimiento en microgravedad (Cero-G) y maniobras de evasión de fuego enemigo.
  - Velocidad de desplazamiento y sigilo al deslizarse por conductos de ventilación.
  - Pulso fino al manipular soldadores, cableados expuestos o explosivos industriales.
- **Habilidades Afines (0 a 100):**
  - *Manitas (Técnica de Campo y Desguace)*
  - *Demoliciones y Trampas Industriales*
  - *Sigilo e Infiltración en Conductos*

### 6. INT — Intelecto
*La capacidad analítica, memoria técnica, pensamiento computacional y comprensión científica.*
- **En el Juego:**
  - Diagnóstico de averías estructurales, cálculos de salto de curvatura y telemetría.
  - Velocidad y nivel de acceso en terminales para hackear la IA y subsistemas de la nave.
  - Comprensión de informes de autopsias forenses y formulación de compuestos químicos.
- **Habilidades Afines (0 a 100):**
  - *Informática y Protocolos de Red*
  - *Reparación Estructural y de Sistemas*
  - *Medicina y Criogenia*

### 7. SUE — Suerte
*La probabilidad estadística impredecible, sincronicidades cuánticas y supervivencia inexplicable.*
- **En el Juego:**
  - **Tirada de Fortuna (d100 bajo Suerte × 10):** Permite al Director de Juego resolver imprevistos (¿queda un último cartucho en el cargador?, ¿la batería rota tiene suficiente jugo para 1 minuto de linterna?).
  - **Rango de Crítico:** Los personajes con alta Suerte amplían el margen de éxito crítico en tiradas de habilidad (ejemplo: con Suerte 7+, los resultados de 01 a 07 en d100 son éxitos críticos absolutos).
  - **Salvación de la Muerte:** 1 vez por sesión, si un impacto mortal fuera a matar al personaje, puede gastar un chequeo de Suerte para quedar con 1 PV milagrosamente (la bala golpea la petaca, el sensor del bot parpadea justo a tiempo).

---

## 📐 4. Relación entre Atributos SPECIAL (1 al 10) y Habilidades (0 al 100)

Es fundamental distinguir claramente las dos escalas mecánicas del juego:

1. **Atributos SPECIAL (1 al 10):** Representan el potencial genético y biológico humano.
   - Escala: 1 (mínimo / deficiencia) a 10 (pico biológico humano).
   - Determinan características físicas directas (PV, ranuras de carga, cordura, minutos de O2).
2. **Habilidades (0 al 100):** **TODAS** las habilidades van de **0 a 100**.
   - No existe ninguna habilidad que vaya hasta 10. Cualquier valor de habilidad se mide en percentil (d100).
   - Escala: 0 (lego total), 30 (profesional medio), 60 (especialista consumado), 100 (referente o genio absoluto).
   - Se tiran siempre con **1d100**: para tener éxito, el resultado debe ser **menor o igual al valor de la habilidad**.

### Tabla de Asociación Atributo-Habilidad:

| Habilidad (Escala 0 - 100) | Atributo Vinculado (Escala 1 - 10) | Uso del Atributo |
|---|:---:|---|
| **Manitas (Cerrajería y Desguace)** | **AGI** | Pruebas de pulso fino e improvisación directa |
| **Reparación Estructural y Sistemas** | **INT** | Diagnóstico rápido sin planos |
| **Informática y Protocolos de Red** | **INT** | Comprensión de algoritmos de la IA |
| **Logística y Carga (Inventario)** | **FUE** | Capacidad de estiba y transporte pesado |
| **Negociación y Recursos** | **CAR** | Imposición de autoridad y diplomacia |
| **Medicina y Criogenia** | **INT** / **END** | Cirugía analítica y tolerancia biológica |
| **Armamento de Precisión y Balística** | **PER** | Puntería y cálculo balístico visual |
| **Contundencia y CQC** | **FUE** | Fuerza de impacto e inercia de golpe |
| **Demoliciones y Trampas** | **AGI** / **INT** | Colocación precisa y circuitos de detonación |
| **Sigilo e Infiltración** | **AGI** / **PER** | Evasión de sensores y lectura del entorno |
| **Psicología, Mando y Subterfugio** | **CAR** | Templanza bajo pánico y serenidad |
| **Supervivencia Espacial y Entorno** | **END** | Tolerancia a la descompresión y radiación |

> **Tope Biológico por Atributo Vinculado:** Aunque todas las habilidades van de 0 a 100, el cuerpo y la mente imponen un límite al aprendizaje. Un personaje que es deficiente o inútil en un atributo (ej. AGI 1 o INT 2) **no puede alcanzar el 100** en habilidades de esa rama, ya que carece de la capacidad biológica basal.
> - **Fórmula de Tope:** `Tope de Habilidad = 28 + (Atributo × 7.2)` (redondeado, mínimo 35, máximo 100).
> - **Atributo 1 (Deficiencia):** Tope máximo de habilidad: **35**.
> - **Atributo 3 (Bajo):** Tope máximo de habilidad: **50**.
> - **Atributo 5 (Humano promedio):** Tope máximo de habilidad: **64**.
> - **Atributo 7 (Profesional):** Tope máximo de habilidad: **78**.
> - **Atributo 10 (Pico biológico):** Tope máximo de habilidad: **100** (maestría absoluta).

---

## 🛠️ 5. Pruebas de Atributo Directas (Chequeos sin Habilidad)

Cuando una acción depende del puro físico o instinto sin intervención de técnica aprendida (ejemplo: resistir un gas venenoso repentino, evitar caer por el hueco del ascensor al romperse un peldaño o recordar un código visto de pasada):

1. **Mecánica Estándar:** Se tira **1d100** o **1d20**:
   - En sistema percentil (d100): El objetivo es sacar **menor o igual a `Atributo × 10`**.
   - En sistema d20: El objetivo es sacar **menor o igual a `Atributo × 2`**.
2. **Pruebas Enfrentadas (Atributo vs Atributo):**
   - Forzar una puerta contra un bot o pulso de fuerza: `FUE vs FUE`.
   - Engañar a un tripulante en un careo directo: `CAR vs PER`.
   - Vencer en iniciativa ante un autómata: `AGI + PER`.
