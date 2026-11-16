# Plan de 8 Correcciones Clave (Kepler-1652b)

Implementación de 8 ajustes de interfaz visual (UI) y lógica solicitados por el usuario para pulir la experiencia de juego tanto para jugadores como para el Máster.

## Proposed Changes

### 1. Superposición con la barra de estado (UI)
- **[MODIFY]** Pantallas principales (`CampaignListScreen.kt`, `MasterCharacterDetailScreen.kt`, etc.): Asegurar `statusBarsPadding()` o el manejo correcto de insets para que el header comience debajo de la barra de notificaciones.

### 2. Inestabilidad del layout en sliders de habilidades (UI)
- **[MODIFY]** [CharacterCreationScreen.kt](file:///C:/Users/manup/OneDrive/Escritorio/Proyectos/Kepler1652b/App/app/src/main/java/com/example/mpergar9/kepler1652b/ui/screens/CharacterCreationScreen.kt): Asignar anchos fijos a los contenedores de valores numéricos (`11 / 56`) y etiquetas de tope para evitar saltos al cambiar de dígitos.

### 3. Catálogo de Perks menor / universal en Creación de Personaje
- **[MODIFY]** [KeplerGameData.kt](file:///C:/Users/manup/OneDrive/Escritorio/Proyectos/Kepler1652b/App/app/src/main/java/com/example/mpergar9/kepler1652b/data/KeplerGameData.kt): Añadir perks menores con requisitos nulos o mínimos para garantizar que el catálogo nunca aparezca vacío.

### 4. Limpieza visual en estadísticas de fichas (`CharacterListScreen.kt`)
- **[MODIFY]** [CharacterListScreen.kt](file:///C:/Users/manup/OneDrive/Escritorio/Proyectos/Kepler1652b/App/app/src/main/java/com/example/mpergar9/kepler1652b/ui/screens/CharacterListScreen.kt): Eliminar los textos "Cordura:" y "Soporte:", dejando solo iconos y porcentajes.

### 5. Ajuste del selector de pestañas del Máster (`MasterCharacterDetailScreen.kt`)
- **[MODIFY]** [MasterCharacterDetailScreen.kt](file:///C:/Users/manup/OneDrive/Escritorio/Proyectos/Kepler1652b/App/app/src/main/java/com/example/mpergar9/kepler1652b/ui/screens/master/MasterCharacterDetailScreen.kt`): Alinear el `ScrollableTabRow` al borde izquierdo sin padding vacío y añadir indicador/flecha visual a la derecha.

### 6. Rediseño del control de Puntos de Vida del Máster
- **[MODIFY]** [MasterCharacterDetailScreen.kt](file:///C:/Users/manup/OneDrive/Escritorio/Proyectos/Kepler1652b/App/app/src/main/java/com/example/mpergar9/kepler1652b/ui/screens/master/MasterCharacterDetailScreen.kt`): Cambiar etiqueta a "PV", centrar el valor actual entre botones simétricos: `PV | [-5] [-1] [ 48/48 ] [+1] [+5]`.

### 7. Reestructuración de Variables Ambientales en 2 niveles (Máster)
- **[MODIFY]** [MasterCharacterDetailScreen.kt](file:///C:/Users/manup/OneDrive/Escritorio/Proyectos/Kepler1652b/App/app/src/main/java/com/example/mpergar9/kepler1652b/ui/screens/master/MasterCharacterDetailScreen.kt`): Dividir cada variable ambiental en 2 filas:
  - Fila superior: Título con icono y valor.
  - Fila inferior: `[-5] [-1] [ Slider Horizontal ] [+1] [+5]`.

### 8. Filtrado y Categorización de Perks en Panel del Máster
- **[MODIFY]** [MasterCharacterDetailScreen.kt](file:///C:/Users/manup/OneDrive/Escritorio/Proyectos/Kepler1652b/App/app/src/main/java/com/example/mpergar9/kepler1652b/ui/screens/master/MasterCharacterDetailScreen.kt`): Organizar la pestaña de Perks en secciones: **Disponibles**, **Próximos a desbloquear** (a 1-2 puntos de distancia) y opción colapsable "Mostrar todo".

## Verification Plan
- Compilación completa de la aplicación (`app:assembleDebug`).
