# Roadmap de FEFW-Manager

La primera versión permitirá seguir en paralelo las sendas de Cai, Theodora, Dietrich y Leda. Cada senda tendrá su propia próxima misión principal, lista de unidades reclutadas y parejas de apoyo. El menú indicará qué senda conviene jugar primero según su fecha límite. [Ficha funcional y pantallas](fire-emblem-fortunes-weave.md)

## Fase 1. Preparar el proyecto Android

- Crear en Android Studio un módulo de aplicación con Java y vistas XML. Definir el nombre de paquete y la versión mínima de Android, e incorporar el Gradle Wrapper al repositorio.
- Incorporar el `AGENTS.md` preparado a la raíz y la guía de Android en Java a `docs/android-java.md`. Registrar en el `README.md` cómo abrir, compilar y ejecutar el proyecto.

**Entregable.** La aplicación de base compila con el wrapper y abre en un emulador.

**Completada el 19 de noviembre de 2026.** El módulo `app` usa Java 17 y vistas XML. Su identificador es `com.davidsm24.fefwmanager`, con Android 7.0 (API 24) como mínimo y API 36 como objetivo. La compilación de depuración y lint han pasado. En un emulador de Android 11 se instaló el APK, se abrió la actividad principal y se comprobaron los nombres de las cuatro sendas en pantalla. [Instrucciones de ejecución](../README.md) · [Prueba de arranque en GitHub Actions](https://github.com/DavidSM24/FEFW-Manager/actions/runs/44545502221)

## Fase 2. Definir los datos y la navegación

- Identificar las cuatro sendas con IDs estables. Guardar por senda la descripción y fecha de la próxima misión, las unidades reclutadas y las parejas de apoyo. Separar el catálogo de unidades del progreso del jugador para poder actualizar el catálogo sin perder sus registros.
- Usar almacenamiento local. La propuesta es Room para relacionar cada senda con sus unidades y parejas y conservar los registros entre sesiones.
- Crear el menú principal y la vista de gestión de senda. Al pulsar el retrato de un protagonista, abrir su senda y mostrar los accesos a **Gestionar unidades**, **Gestionar apoyos** y **Modificar misión principal**.

**Entregable.** Los cuatro retratos abren la senda correcta y sus anotaciones se guardan por separado.

El menú presenta cuatro fichas con iniciales provisionales. Cada ficha abre su propia senda, con resúmenes de la misión principal, las unidades reclutadas y los apoyos. Las fases 3 a 5 incorporan la edición desde esas pantallas.

Room guarda una misión por senda y usa el ID de cada unidad para asociar reclutamientos y parejas. Una unidad puede pertenecer a varias sendas. El orden de selección de los personajes de una pareja no cambia su identidad. El catálogo y los retratos se incorporarán por separado en la fase 4.

**Validada en Android 11.** Tres pruebas instrumentadas comprueban el acceso a las cuatro sendas, la apertura de los tres resúmenes y la persistencia por senda al cerrar y volver a abrir la base. [Resultado en GitHub Actions](https://github.com/DavidSM24/FEFW-Manager/actions/runs/44550018402)

## Fase 3. Editar las misiones y ordenar el menú

- Implementar el modal de misión principal. Mostrar la fecha límite y la descripción actuales, permitir editarlas y guardarlas juntas para la senda seleccionada.
- Comparar las fechas por mes y día del calendario del juego. Tras guardar una nueva fecha, actualizar el resumen y reordenar las cuatro sendas de la más temprana a la más tardía. Definir cómo se mostrarán las sendas sin fecha y cómo se resolverán los empates.

**Entregable.** Con el ejemplo de la ficha, el menú muestra **Cai (10/5) → Theodora (28/5) → Leda (5/6) → Dietrich (13/6)**. Si se cambia la fecha de Leda a 5/5, el menú la sitúa primera al guardar y conserva su nueva descripción al volver a abrir la aplicación.

**Validada en Android 11.** El modal carga la descripción y la fecha guardadas. Los dos campos numéricos de fecha se separan por una barra. **Guardar** valida el día y el mes y actualiza la misión. **Cancelar** descarta los cambios del formulario. Las sendas sin fecha aparecen al final y los empates mantienen el orden del menú inicial. [Resultado en GitHub Actions](https://github.com/DavidSM24/FEFW-Manager/actions/runs/44560882178)

## Fase 4. Incorporar el catálogo y gestionar unidades

- Usar `unidades-fefw.json` como catálogo inicial. Contiene 63 unidades y disponibilidad por senda para las 50 de la primera parte. Revisar los nombres de la localización española, la disponibilidad dudosa y los enlaces a retratos antes de cerrar el catálogo de la primera versión. Usar imágenes provisionales para desarrollar el flujo mientras se validan los retratos.
- Mostrar las unidades reclutadas con su imagen y nombre. En **Añadir unidad**, abrir un modal con las unidades disponibles en la senda, también con imagen y nombre. Permitir quitar unidades del registro de esa senda.

**Entregable.** Se puede añadir y retirar una unidad, la lista evita duplicados dentro de la misma senda y se puede registrar a la misma unidad en otra senda disponible de forma independiente.

## Fase 5. Gestionar parejas de apoyo

- Mostrar las parejas guardadas de la senda seleccionada con los retratos de ambos personajes. Permitir crear y quitar parejas. Definir qué unidades se podrán elegir y cómo se mostrará una pareja si se retira a uno de sus integrantes del registro de unidades.
- Guardar cada pareja asociada a su senda. Identificar el par por sus dos integrantes, de modo que invertir el orden al seleccionarlos no cree un duplicado.

**Entregable.** Se puede crear y quitar una pareja en una senda, y el cambio se conserva al cerrar y volver a abrir la aplicación.

## Fase 6. Probar y preparar una primera versión

- Probar en el emulador el flujo completo desde el menú. Abrir las cuatro sendas, editar una misión, añadir y quitar unidades y gestionar apoyos. Revisar el orden por fechas y la persistencia tras reiniciar. Comprobar también que los retratos y los controles se entiendan en pantallas pequeñas y con lector de pantalla.
- Ejecutar compilación, pruebas unitarias y lint con la variante correspondiente. Usar las pruebas instrumentadas para verificar la navegación y la actualización del menú después de guardar una misión.
- Generar un APK de depuración para una primera prueba en un dispositivo y anotar en el repositorio cómo reproducir la compilación.

**Entregable.** APK instalable y flujo principal verificado de principio a fin con las cuatro sendas.
