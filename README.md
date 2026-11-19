# FEFW-Manager

Aplicación Android para seguir en paralelo las cuatro sendas de *Fire Emblem: Fortune’s Weave*. El menú abre la vista de cada senda, con resúmenes de su misión principal, unidades y apoyos. El progreso se almacena en Room.

## Abrir y ejecutar en Android Studio

1. Clona el repositorio y abre la carpeta que contiene `settings.gradle` con **File → Open**.
2. En **SDK Manager**, instala **Android SDK Platform 36**, **Android SDK Build-Tools 35.0.0** y **Android SDK Platform-Tools**. Configura el JDK de Gradle con la versión 17 o 21 y sincroniza el proyecto.
3. En **Device Manager**, crea e inicia un emulador con Android 7.0 (API 24) o superior. Selecciona la configuración **app**, elige el emulador y pulsa **Run**.
4. Al abrir **FEFW Manager**, pulsa la ficha de Cai, Theodora, Dietrich o Leda para abrir su senda. Las iniciales identifican las fichas provisionalmente hasta incorporar los retratos.

## Compilar desde la terminal

Ejecuta los comandos desde la raíz. El wrapper descarga Gradle 8.14.7 en la primera compilación. Se necesita un JDK y un SDK de Android configurado en `ANDROID_HOME` o mediante `sdk.dir` en `local.properties`.

Compilación e instalación en Windows con PowerShell

```powershell
.\gradlew.bat :app:assembleDebug :app:lintDebug :app:testDebugUnitTest
.\gradlew.bat :app:installDebug
```

Compilación e instalación en macOS o Linux

```sh
./gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest
./gradlew :app:installDebug
```

El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`. Para `installDebug`, conecta un dispositivo o arranca el emulador. Puedes abrir la app desde el lanzador o ejecutar el siguiente comando con `adb` en el `PATH`.

```sh
adb shell am start -W -n com.davidsm24.fefwmanager/.home.MainActivity
```

Con un dispositivo o emulador conectado, ejecuta `./gradlew :app:connectedDebugAndroidTest` en macOS o Linux, o `.\gradlew.bat :app:connectedDebugAndroidTest` en Windows, para probar la navegación y la persistencia por senda.

## Proyecto

El módulo `app` usa Java 17, vistas XML y View Binding. El identificador de aplicación es `com.davidsm24.fefwmanager`. La versión mínima es Android 7.0 (API 24) y se compila con la API 36, que también es la versión objetivo.

- [Roadmap de desarrollo](docs/roadmap.md)
- [Descripción del juego, las rutas y las pantallas](docs/fire-emblem-fortunes-weave.md)
- [Reglas para trabajar con Codex](AGENTS.md)
- [Guía de Android en Java](docs/android-java.md)
