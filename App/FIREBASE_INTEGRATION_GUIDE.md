# 🚀 Guía de Integración con Firebase Firestore (Kepler-1652b)

Esta guía está diseñada tanto para ti como para el **Asistente IA de Android Studio (Gemini in Android Studio / Studio Bot)**. Si le dices a la IA de Android Studio: *"Sigue las instrucciones de FIREBASE_INTEGRATION_GUIDE.md y conecta FirebaseKeplerService con CampaignRepository"*, sabrá exactamente qué hacer paso por paso.

---

## 1. Configuración de Firebase Console
1. Entra en [Firebase Console](https://console.firebase.google.com/) y crea un proyecto llamado **Kepler-1652b**.
2. Añade una aplicación Android con el identificador de paquete:
   ```
   com.example.mpergar9.kepler1652b
   ```
3. Descarga el archivo de configuración **`app/google-services.json`**.
4. Pega `app/google-services.json` directamente dentro de la carpeta:
   ```
   Kepler1652b/App/app/google-services.json
   ```

---

## 2. Dependencias en Gradle (`build.gradle.kts`)

### A. En el archivo raíz `Kepler1652b/App/build.gradle.kts`:
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
```

### B. En el archivo del módulo `Kepler1652b/App/app/build.gradle.kts`:
Al inicio:
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}
```

Dentro del bloque `dependencies { ... }`:
```kotlin
dependencies {
    // Firebase Cloud Firestore
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    // ... resto de dependencias existentes de Jetpack Compose
}
```

---

## 3. Modelo de Datos en Firestore

Las colecciones se organizan jerárquicamente para que múltiples dispositivos se sincronicen en tiempo real:

```
firestore/
  ├── campaigns/
  │     └── {campaignId}/
  │           ├── id: "uuid"
  │           ├── name: "Kepler-1652b: El Éxodo del Cisne"
  │           ├── description: "..."
  │           ├── masterName: "Manu"
  │           ├── masterId: "usr-player-1"
  │           ├── inviteCode: "KPL-8421"
  │           ├── imageBannerId: "kepler_orbit"
  │           └── characters/
  │                 └── {characterId}/
  │                       ├── id: "char-01"
  │                       ├── campaignId: "uuid"
  │                       ├── playerId: "usr-player-1"
  │                       ├── playerName: "Manu"
  │                       ├── characterName: "Dra. Elena Vance"
  │                       ├── faction: "UNION_HUMANA"
  │                       ├── characterClass: "Científico / Médico"
  │                       ├── subclass: "Médico de a bordo y Criogenia"
  │                       ├── level: 1
  │                       ├── currentHp: 41
  │                       ├── radiationLevel: 12
  │                       ├── toxicityPoison: 0
  │                       ├── soporteVitalLevel: 100
  │                       ├── currentSanity: 100
  │                       ├── isAlive: true
  │                       ├── stats: { FUE: 3, PER: 6, END: 5, CAR: 4, AGI: 4, INT: 8, SUE: 5 }
  │                       ├── skills: { "Medicina y Criogenia": 65, ... }
  │                       └── unlockedPerks: [ "Cirujano de Campaña" ]
```

---

## 4. Archivo del Servicio Desacoplado
El código con los DTOs y métodos de conversión listos para Firestore se encuentra en:
👉 [`App/app/src/main/java/com/example/mpergar9/kepler1652b/data/remote/FirebaseKeplerService.kt`](file:///c:/Users/manup/OneDrive/Escritorio/Proyectos/Kepler1652b/App/app/src/main/java/com/example/mpergar9/kepler1652b/data/remote/FirebaseKeplerService.kt)

Una vez que tengas `app/google-services.json` y hayas sincronizado Gradle con Firebase BOM:
1. Descomenta `FirebaseFirestore.getInstance()` en dicho archivo.
2. En `CampaignRepository.kt`, vincula las escuchas en tiempo real (`addSnapshotListener`) para emitir los cambios en `_campaigns` y `_characters`.
3. ¡Cualquier cambio de radiación, soporte vital, vida o personaje que haga el Máster se reflejará instantáneamente en el móvil de cada jugador conectado!
