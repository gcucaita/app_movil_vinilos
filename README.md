# Vinilos App

Aplicación móvil Android desarrollada para la materia **Ingeniería de software para aplicaciones móviles** de la maestría MISO de la Universidad de Los Andes. Permite a los amantes de los vinilos explorar el catálogo de álbumes, ver el detalle de cada uno y consultar la lista de canciones, consumiendo el backend de Vinilos desplegado en Render.

- **Repositorio:** [gcucaita/app_movil_vinilos](https://github.com/gcucaita/app_movil_vinilos)
- **API consumida:** `https://backvynils-rols.onrender.com/`
- **Mínima versión de Android soportada:** Android 5.0 (Lollipop, API 21)
- **Versión objetivo:** Android 14 (API 34)

---

## 📦 Ejecutar la aplicación con el APK

La forma recomendada de probar la aplicación es descargando el APK publicado en la sección de **Releases** del repositorio en GitHub. No es necesario clonar el proyecto ni instalar Android Studio.

### 1. Descargar el APK desde el Release

1. Abrir la página de releases del repositorio:
   👉 [https://github.com/gcucaita/app_movil_vinilos/releases](https://github.com/gcucaita/app_movil_vinilos/releases)
2. Ubicar el release más reciente (por ejemplo `v3.0.0`).
3. En la sección **Assets** del release, descargar el archivo:

   ```
   vinilos-app-v.2.0.0.apk

   ```

   > Si se descarga desde el celular, el archivo quedará en la carpeta **Descargas / Downloads**.

### 2. Habilitar la instalación de orígenes desconocidos

Como el APK no proviene de Google Play, Android pedirá permiso explícito para instalarlo:

1. En el dispositivo, ir a **Ajustes → Aplicaciones → Acceso especial → Instalar aplicaciones desconocidas** (la ruta puede variar según la marca y versión de Android).
2. Seleccionar la aplicación desde la cual se va a abrir el APK (normalmente **Chrome**, **Files / Mis archivos** o **Drive**).
3. Activar la opción **Permitir desde esta fuente**.

### 3. Instalar el APK

1. Abrir el gestor de archivos del celular y entrar a la carpeta **Descargas**.
2. Tocar el archivo `app-debug.apk`.
3. Confirmar la instalación tocando **Instalar**.
4. Una vez finalizada, tocar **Abrir** o buscar el ícono **Vinilos App** en el cajón de aplicaciones.

### 4. Requisitos para que la app funcione correctamente

- Dispositivo con **Android 5.0 (API 21)** o superior.
- **Conexión a internet** activa: el catálogo de álbumes se obtiene desde el backend en Render.
- Espacio libre suficiente para una app Android estándar (~30 MB).

> 💡 La primera carga puede tardar unos segundos mientras el backend en Render despierta.

- Al consumir directamente la API desplegada, no es necesario levantar el backend a través de Docker.

---

## 🛠️ (Opcional) Generar el APK desde el código fuente

Si se desea regenerar el APK localmente en lugar de usar el del release:

### Requisitos

- **Android Studio** (Giraffe / Hedgehog o superior).
- **JDK 11**.
- **Android SDK 34** instalado.

### Pasos

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/gcucaita/app_movil_vinilos.git
   cd app_movil_vinilos
   ```

2. Abrir el proyecto en Android Studio y dejar que sincronice Gradle.
3. Generar el APK de release por línea de comandos:

   ```bash
   ./gradlew assembleRelease
   ```

   El APK quedará en:

   ```
   app/build/outputs/apk/release/app-release.apk
   ```

4. (Alternativa) Para una versión de depuración rápida:

   ```bash
   ./gradlew assembleDebug
   ```

   APK generado en `app/build/outputs/apk/debug/app-debug.apk`.

---

# Guía de Pruebas y Cobertura

Documentación completa para ejecutar las pruebas unitarias, tests de instrumentación (Espresso) y generar reportes de cobertura con JaCoCo en **Windows**.

---

## Requisitos previos

- **Android Studio** (Giraffe / Hedgehog o superior)
- **JDK 11** instalado
- **Android SDK 34**
- **Emulador Android con API 34** (para Espresso tests)
  - Los tests requieren API 34 por compatibilidad con `androidx.collection.LruCache`

---

## 1️. Ejecutar pruebas unitarias

Las pruebas unitarias se ejecutan sin emulador:

```powershell
.\gradlew.bat testDebugUnitTest
```

Esto ejecuta todos los tests unitarios de:
- **ViewModels**: AlbumListViewModel, AlbumDetailViewModel, MusicianListViewModel, MusicianDetailViewModel, CollectorListViewModel
- **Repositories**: AlbumRepository, MusicianRepository, CollectorRepository
- **ServiceAdapters**: AlbumServiceAdapter, MusicianServiceAdapter, CollectorServiceAdapter

---

## 2️. Ejecutar pruebas de instrumentación (Espresso)

Requiere un emulador ejecutándose con **API 34** por lo que es necesario verificar que tu emulador esté corriendo antes de ejecutar el siguiente comando:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

Esto ejecuta todos los tests de UI usando Espresso:
- **AlbumListUiTest**: Lista de álbumes, refresh, navegación
- **AlbumDetailUiTest**: Detalles del álbum, comentarios
- **MusicianListUiTest**: Lista de artistas, búsqueda
- **MusicianDetailUiTest**: Detalles del artista
- **CollectorListUiTest**: Lista de coleccionistas, búsqueda

---

## 3️. Generar reporte de cobertura con JaCoCo

Para generar el reporte combinado de cobertura (unit + Espresso):

```powershell
.\gradlew.bat jacocoTestReport
```

**Importante**: Este comando ejecuta automáticamente `testDebugUnitTest` y espera que `connectedDebugAndroidTest` haya sido ejecutado previamente. Si no se han ejecutado las pruebas de Espresso, solo se mostrará la cobertura de pruebas unitarias.

---

## 4️. Ver el reporte de cobertura

Una vez generado el reporte, abrir el archivo HTML en tu navegador:

```powershell
start app/build/reports/jacoco/jacocoTestReport/html/index.html
```

El reporte muestra:
- **Overall Coverage**: Cobertura total del código (~83%)
- **Detalles por paquete**: `data.cache`, `data.network`, `data.repository`, `data.serviceadapter`, `domain.model`, `presentation.uistate`, `ui.*`
- **Instrucciones cubiertas**: Número de líneas ejecutadas vs. totales

---

##  Flujo completo (recomendado)

Para una ejecución completa desde cero:

```powershell
# 1. Abrir PowerShell en la raíz del proyecto

# 2. Limpiar builds previos
.\gradlew.bat clean

# 3. Ejecutar pruebas unitarias
.\gradlew.bat testDebugUnitTest

# 4. Ejecuta pruebas de instrumentación
# Asegurarse de tener emulador API 34 corriendo
.\gradlew.bat connectedDebugAndroidTest

# 5. Generar reporte de cobertura
.\gradlew.bat jacocoTestReport

# 6. Abrir reporte en navegador
start app/build/reports/jacoco/jacocoTestReport/html/index.html
```

---

## Estructura de archivos de pruebas

```
app/src/
├── test/java/com/example/vinilosapp/
│   ├── data/repository/
│   │   ├── AlbumRepositoryTest.kt
│   │   ├── CollectorRepositoryTest.kt
│   │   └── MusicianRepositoryTest.kt
│   ├── data/serviceadapter/
│   │   ├── AlbumServiceAdapterTest.kt
│   │   ├── CollectorServiceAdapterTest.kt
│   │   └── MusicianServiceAdapterTest.kt
│   └── presentation/viewmodel/
│       ├── AlbumListViewModelTest.kt
│       ├── AlbumDetailViewModelTest.kt
│       ├── MusicianListViewModelTest.kt
│       ├── MusicianDetailViewModelTest.kt
│       ├── CollectorListViewModelTest.kt
│       ├── CreateAlbumFormValidator.kt
│       └── CreateAlbumViewModelTest.kt
│
└── androidTest/java/com/example/vinilosapp/
    └── ui/
        ├── albums/
        │   ├── list/AlbumListUiTest.kt
        │   ├── detail/AlbumDetailUiTest.kt
        │   ├── AddTrackUiTest.kt
        │   └── CreateAlbumUiTest.kt
        ├── musicians/
        │   ├── MusicianListUiTest.kt
        │   └── MusicianDetailUiTest.kt
        └── collectors/
            ├── CollectorDetailUiTest.kt
            └── CollectorListUiTest.kt
```

---

## Cobertura por módulo

| Módulo | Tests Unitarios | Tests Espresso | Cobertura |
|--------|-----------------|----------------|-----------|
| ViewModel | ✅ | ✅ | ~90% |
| Repository | ✅ | - | ~85% |
| ServiceAdapter | ✅ | - | ~80% |
| UI (Activities) | - | ✅ | ~75% |
| Cache Manager | ✅ | - | ~95% |
| **TOTAL** | | | **~83%** |

---

## Historias de usuario cubiertas
- **HU01**: Consultar catálogo de álbumes ✅
- **HU02**: Consultar la información detallada de un álbum ✅
- **HU03**: Consultar el listado de artistas ✅
- **HU04**: Consultar la información detallada de un artista ✅
- **HU05**: Consultar listado de coleccionistas ✅
- **HU06**: Consultar la información detallada de coleccionista ✅
- **HU07**: Crear un álbum ✅
- **HU08**: Asociar tracks con un álbum ✅

---

## Troubleshooting

### ❌ El reporte dice "0% de cobertura"
**Solución**: Asegurarse de haber ejecutado `testDebugUnitTest` y `connectedDebugAndroidTest` antes de `jacocoTestReport`.

### ❌ `connectedDebugAndroidTest` falla con "Unable to find instrumentation target"
**Solución**: Verificar que haya un emulador corriendo con API 34. Ejecutar en otra ventana de PowerShell:
```powershell
adb devices
```

### ❌ Los tests de Espresso timeout o fallan
**Solución**: Probar con un emulador con más recursos (8GB RAM, 4 cores). Los tests tardan de 3-5 minutos.

### ❌ `gradlew.bat` no es reconocido
**Solución**: Asegurarse de estar en la carpeta raíz del proyecto donde está `gradlew.bat`. Si aún falla, usar:
```powershell
gradlew.bat testDebugUnitTest
```

### ❌ Error "gradle wrapper not found"
**Solución**: Descargar el wrapper:
```powershell
gradle wrapper --gradle-version 8.3
```

---