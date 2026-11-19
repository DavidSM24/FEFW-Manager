# FEFW-Manager

## Desarrollo Android

- Usa Java para el código Android y layouts XML para la interfaz. Antes de editar, identifica el módulo, la variante y las convenciones existentes en `settings.gradle` o `settings.gradle.kts` y en los archivos de compilación del módulo.
- Organiza el código de pantalla por funcionalidad. Deja el renderizado y los eventos de UI en `Activity` o `Fragment`, el estado de pantalla en un `ViewModel` cuando haga falta, y el acceso a red y almacenamiento en la capa de datos.
- Inyecta las dependencias por constructor. Mantén el `ViewModel` libre de referencias a `Activity`, `Fragment`, vistas o `Context`. Expón `LiveData<UiState>` de solo lectura cuando uses LiveData y representa los estados de carga, éxito y error que necesite la pantalla.
- Usa View Binding en las pantallas XML nuevas cuando esté habilitado. En un fragmento, observa el estado con `getViewLifecycleOwner()` y libera el binding en `onDestroyView()`. Coloca los textos de la interfaz en `res/values/strings.xml` y etiqueta los controles con icono para accesibilidad.
- Ejecuta las operaciones bloqueantes fuera del hilo principal con los executors o mecanismos asíncronos del proyecto. Cancela el trabajo ligado a la pantalla cuando proceda y descarta los resultados obsoletos. Usa WorkManager para trabajo persistente que deba continuar fuera de la sesión de UI.
- Reutiliza la navegación, las dependencias y las versiones declaradas en Gradle. Introduce bibliotecas o módulos nuevos cuando el cambio los justifique. Comprueba la compatibilidad con `minSdk` al usar APIs nuevas. Conserva los datos existentes y prueba las migraciones cuando cambie el esquema local.
- Usa HTTPS y solicita solo los permisos necesarios. Declara explícitamente la exportación de componentes y valida las entradas externas. Mantén las credenciales y los datos personales fuera del código, los logs y los intents implícitos.

## Verificación

- Usa el Gradle Wrapper del repositorio. Ejecuta las comprobaciones aplicables al módulo y a la variante afectados. Para una variante `debug`, los nombres habituales son `assembleDebug`, `testDebugUnitTest` y `lintDebug`. En PowerShell usa `.\gradlew.bat` y en macOS o Linux usa `./gradlew`. Confirma los nombres con `tasks --all` si hay dudas.
- Añade o ajusta pruebas según el comportamiento modificado. Prueba la lógica en `src/test` con dependencias falsas o controladas y reserva `src/androidTest` para integraciones que requieran Android y flujos de UI. Usa un emulador o dispositivo para las pruebas conectadas.
- Al terminar, indica qué pruebas y tareas ejecutaste, sus resultados y cualquier verificación pendiente con el motivo. Corrige las incidencias nuevas de lint atribuibles al cambio y respeta la configuración vigente.

Consulta [la guía de Android en Java](docs/android-java.md) para ejemplos de arquitectura, ciclo de vida, seguridad y pruebas.
