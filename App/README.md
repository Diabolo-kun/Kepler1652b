# 📱 App Android Complementaria — Kepler-1652b

Aplicación móvil nativa en **Android (Jetpack Compose + Material 3)** diseñada para la gestión de campañas, control de fichas de colonos en tiempo real y panel de mando para el Director de Juego (Máster) en el juego de rol de ciencia ficción realista **DN&D Kepler-1652b**.

---

## 🛰️ 1. Arquitectura y Tecnologías

- **Lenguaje:** Kotlin 1.9+
- **Interfaz de Usuario:** Jetpack Compose (Material 3) con paleta temática *Deep Space & Cyberpunk* (Void Black, Deep Space Blue, Neon Cyan, Cyan Curvature, Crimson Alert, Warning Amber, Radiation Green).
- **Gestión de Estado:** `StateFlow` reactivo mediante repositorio centralizado ([`CampaignRepository.kt`](file:///c:/Users/manup/OneDrive/Escritorio/Proyectos/Kepler1652b/App/app/src/main/java/com/example/mpergar9/kepler1652b/data/CampaignRepository.kt)).
- **Modelo de Datos:** Clases inmutables con cálculo dinámico de constantes vitales, ranuras de carga y topes biológicos ([`Models.kt`](file:///c:/Users/manup/OneDrive/Escritorio/Proyectos/Kepler1652b/App/app/src/main/java/com/example/mpergar9/kepler1652b/model/Models.kt)).
- **Sincronización en la Nube (Multijugador):** Módulo desacoplado listo para Firebase Cloud Firestore ([`FirebaseKeplerService.kt`](file:///c:/Users/manup/OneDrive/Escritorio/Proyectos/Kepler1652b/App/app/src/main/java/com/example/mpergar9/kepler1652b/data/remote/FirebaseKeplerService.kt) y [`FIREBASE_INTEGRATION_GUIDE.md`](file:///c:/Users/manup/OneDrive/Escritorio/Proyectos/Kepler1652b/App/FIREBASE_INTEGRATION_GUIDE.md)).

---

## 🖥️ 2. Flujo de Pantallas y Funcionalidades

### A. Terminal de Campañas (`CampaignListScreen`)
- **Perfil de Jugador Local:** En la esquina superior derecha se muestra el perfil activo (`👤 [Nombre]`). Al pulsarlo se abre el diálogo para modificar el alias y consultar el identificador único del dispositivo (`playerId`).
- **Creación de Campañas (Máster):** El Máster puede crear salas de juego asignando nombre, descripción e introduciendo su nombre de director. La app genera automáticamente un código de invitación seguro (ej. `KPL-8421`).
- **Unirse con Código:** Los jugadores introducen el código compartido por el Máster para acceder al manifiesto de la nave y crear sus personajes.
- **Eliminación Segura de Campañas:** Cada tarjeta dispone de un botón de papelera (`🗑️`) que solicita confirmación modal mediante `AlertDialog` antes de eliminar permanentemente la campaña y sus fichas vinculadas.

### B. Manifiesto de Tripulación & Fichas (`CharacterListScreen`)
- **Identificación de Personajes Propios:**
  - Pestañas de filtrado superior: `[🌐 Todos]` y `[👤 Mis PJs]`.
  - Los personajes creados por el perfil activo muestran una insignia cian `[👤 MI PERSONAJE]` y un borde resaltado.
- **Estado Vital:** Indicador inmediato de `💚 Vivo` o `💀 Fallecido`.
- **Acceso Directo al Panel Máster:** Botón exclusivo para el creador de la campaña.

### C. Asistente Digital de Creación (`CharacterCreationScreen`)
Wizard guiado en 5 pasos que garantiza el cumplimiento de todas las reglas del manual:
1. **Paso 1 — Datos Identificativos:** Asigna automáticamente el nombre e ID del perfil de jugador activo, solicitando únicamente el nombre del personaje y su bando (*Unión Humana*, *Puristas* o *No alineado*).
2. **Paso 2 — Atributos SPECIAL:** Campo numérico para introducir el resultado de la tirada física de mesa (1d20+10, entre 11 y 30) y asignación táctica de puntos a los 7 atributos.
3. **Paso 3 — Clase y Subclase:** Navegación libre por las 7 clases para examinar sus ramas y requisitos de atributos mínimos necesarios antes de confirmar.
4. **Paso 4 — Habilidades:** Reparto de la bolsa de puntos con incrementos de 1 en 1 (`-1` / `+1`), saltos rápidos (`-5` / `+5`) y barra deslizante (Slider), respetando el **Tope Biológico** vinculado a cada atributo SPECIAL.
5. **Paso 5 — Cierre de Nivel 1:** Selección de las 3 elecciones libres mediante un **menú desplegable filtrado exclusivamente con los perks a los que califica** el personaje o aumentos de +1 a atributos SPECIAL.

### D. Panel del Director de Juego (`MasterDashboardScreen`)
- **Control Ambiental Global (Equilibrio en 0):** Ajuste de Radiación, Toxicidad, Soporte Vital y Cordura en incrementos/decrementos de `+1` / `-1` aplicables simultáneamente a toda la nave.
- **Monitoreo y Modificación Individual:** Ajuste directo de Puntos de Vida, Radiación, Veneno, Soporte Vital y Cordura por personaje.
- **Control de Nivel:** Botones `[-]` y `[+]` para subir o bajar de nivel a cualquier tripulante en tiempo real.
- **Estado Vital:** Alternador de estado `💚 VIVO` / `💀 FALLECIDO`.
- **Eliminación de Personajes:** Botón de papelera con ventana modal de confirmación.

### E. Ficha Individual de Personaje (`PlayerCharacterSheetScreen`)
- Vista detallada del estado del colono durante las sesiones de juego: medidores de salud, radiación, toxicidad, soporte vital y cordura; lista completa de habilidades percentiles (0 a 100), perks activos y notas de trasfondo.

---

## ☁️ 3. Conexión a Firebase (Multijugador en Tiempo Real)

Para habilitar la sincronización entre múltiples teléfonos a través de internet:
1. Sigue los pasos descritos en [`FIREBASE_INTEGRATION_GUIDE.md`](file:///c:/Users/manup/OneDrive/Escritorio/Proyectos/Kepler1652b/App/FIREBASE_INTEGRATION_GUIDE.md).
2. Añade `google-services.json` dentro de `app/`.
3. El servicio desacoplado [`FirebaseKeplerService.kt`](file:///c:/Users/manup/OneDrive/Escritorio/Proyectos/Kepler1652b/App/app/src/main/java/com/example/mpergar9/kepler1652b/data/remote/FirebaseKeplerService.kt) contiene toda la estructura de DTOs y escuchas en tiempo real lista para ser activada por el asistente IA de Android Studio o manualmente.
