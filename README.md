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
2. Ubicar el release más reciente (por ejemplo `v1.0.0`).
3. En la sección **Assets** del release, descargar el archivo:

   ```
   app-release.apk
   ```

   > Si se descarga desde el celular, el archivo quedará en la carpeta **Descargas / Downloads**.

### 2. Habilitar la instalación de orígenes desconocidos

Como el APK no proviene de Google Play, Android pedirá permiso explícito para instalarlo:

1. En el dispositivo, ir a **Ajustes → Aplicaciones → Acceso especial → Instalar aplicaciones desconocidas** (la ruta puede variar según la marca y versión de Android).
2. Seleccionar la aplicación desde la cual se va a abrir el APK (normalmente **Chrome**, **Files / Mis archivos** o **Drive**).
3. Activar la opción **Permitir desde esta fuente**.

### 3. Instalar el APK

1. Abrir el gestor de archivos del celular y entrar a la carpeta **Descargas**.
2. Tocar el archivo `app-release.apk`.
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

<!-- ### Publicar un nuevo Release en GitHub

1. Crear y subir un tag con la versión:

   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

2. En GitHub, ir a **Releases → Draft a new release**, seleccionar el tag y adjuntar el archivo `app-release.apk` generado en el paso anterior.
3. Publicar el release. A partir de ese momento, cualquier persona podrá descargar el APK siguiendo los pasos de la sección anterior. -->

---

## ✅ Pruebas

Para compilar y ejecutar todas las pruebas unitarias:

```bash
./gradlew test
```
