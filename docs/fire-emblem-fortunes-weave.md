# Fire Emblem: Fortune’s Weave

Ficha de referencia para FEFW-Manager. Fuentes consultadas el **19 de noviembre de 2026**. La historia se resume a partir de la premisa oficial.

## Objetivo de FEFW-Manager

FEFW-Manager ayudará al jugador a llevar en paralelo las sendas de **Cai, Theodora, Dietrich y Leda** mientras alterna entre ellas en el juego. Cada senda tendrá una ficha con su información de referencia y su propio registro de partida.

Las fases de implementación y sus criterios de aceptación se recogen en el [roadmap de desarrollo](roadmap.md).

El jugador anotará y podrá actualizar manualmente en cada senda:

- **Unidades reclutadas.** Lista de las unidades que ya se han unido al equipo en esa senda.
- **Próxima misión principal.** Descripción y fecha límite de la siguiente misión principal de esa senda. La fecha se expresa como día y mes del calendario del juego.
- **Parejas de apoyo (shipeos).** Pares de unidades que el jugador quiere vincular mediante el sistema de apoyo (*Support*) en esa senda.

Al cambiar de senda en la aplicación, el jugador verá el registro de la ruta seleccionada. Una unidad reclutada o una pareja de apoyo puede figurar en varias sendas, con un registro independiente en cada una.

### Menú principal y orden de juego

El menú principal muestra primero las sendas con fecha límite, de la más temprana a la más tardía en el calendario del juego. Las sendas sin fecha quedan al final. Cada ficha muestra el nombre de la senda y su fecha límite, si tiene una.

El panel **Orden de juego** muestra la secuencia recomendada, con un retrato por senda, cuando hay al menos dos sendas con fecha límite. Al guardar una fecha, se actualizan tanto el panel como las fichas.

**Ejemplo de fechas límite (día/mes)**

| Senda | Próxima fecha límite |
| --- | --- |
| Cai | 10/5 |
| Theodora | 28/5 |
| Leda | 5/6 |
| Dietrich | 13/6 |

El menú mostrará **Cai → Theodora → Leda → Dietrich**.

### Pantallas y navegación

1. **Menú principal.** Presenta el resumen de las cuatro sendas en el orden de juego, con el retrato de cada protagonista y la fecha límite de su próxima misión principal. Al pulsar un retrato, se abre la gestión de esa senda.
2. **Gestión de senda.** Desde la senda seleccionada, el jugador puede abrir **Gestionar unidades**, **Gestionar apoyos** o **Modificar misión principal**.
3. **Gestión de unidades.** Muestra las unidades reclutadas en esa senda, con su imagen y nombre. El jugador puede añadir o quitar unidades. Al pulsar **Añadir unidad**, se abre un modal con una lista de unidades identificadas por su imagen y nombre. Al elegir una, se incorpora a la senda seleccionada.
4. **Modificar misión principal.** El botón superior abre el modal de la próxima misión de la senda. La fecha se escribe en dos campos numéricos, día y mes, separados por una barra. La descripción se edita en un área de varias líneas. **Cancelar** descarta los cambios. **Guardar** actualiza la fecha y la descripción juntas y vuelve a ordenar el menú por fecha límite.
5. **Gestión de apoyos (shipeos).** Muestra las parejas de apoyo de la senda seleccionada con el nombre y el retrato de cada integrante. El jugador puede crear una pareja con dos unidades distintas que estén reclutadas en esa senda o quitar una pareja existente. Cuando una unidad sale del registro, aparece marcada como **Fuera del registro** en las parejas guardadas.

## Ficha del juego

| Dato | Información |
| --- | --- |
| Título oficial | *Fire Emblem: Fortune’s Weave* |
| Género | Rol táctico con combates por turnos en cuadrícula |
| Plataforma | Nintendo Switch 2, en exclusiva |
| Lanzamiento del juego base | 17 de septiembre de 2026 |
| Desarrollo | Intelligent Systems y Nintendo |
| Distribución | Nintendo |
| Jugadores | Una persona |
| Modos de juego | Televisor, sobremesa y portátil |
| Clasificación en España | PEGI 12 |
| Idiomas | Incluye español |

Datos de la edición española y del equipo de desarrollo. [Ficha oficial de Nintendo España](https://www.nintendo.com/es-es/Juegos/Juegos-de-Nintendo-Switch-2/Fire-Emblem-Fortune-s-Weave-2915498.html) · [Entrevista al equipo, capítulo 3](https://www.nintendo.com/es-es/Noticias/2026/Septiembre/Pregunta-al-desarrollador-volumen-23-Capitulo-3-3195813.html)

## Ambientación e historia

En **1454**, el regreso del dios demoniaco **Balor** amenaza al Imperio dagdano. **Sothis** invoca a una figura que recibe ayuda de **Fortuna** para viajar al pasado. En **1449**, en la capital **Dagsion**, cuatro héroes compiten en los **Juegos Heroicos**, un torneo cuyo premio es la promesa de un deseo. Al intervenir en sus sendas, el personaje del jugador puede reunir aliados para enfrentarse a Balor en el futuro. [Sinopsis oficial](https://www.nintendo.com/es-es/Juegos/Juegos-de-Nintendo-Switch-2/Fire-Emblem-Fortune-s-Weave-2915498.html)

La historia se desarrolla en **Dagda**, al sur de **Fódlan**, y comparte mundo con *Fire Emblem: Three Houses*. Los desarrolladores describen la trama de *Fortune’s Weave* como una historia independiente. [Entrevista al equipo, capítulo 1](https://www.nintendo.com/es-es/Noticias/2026/Septiembre/Pregunta-al-desarrollador-volumen-23-Capitulo-1-3195811.html)

## Las cuatro sendas

| Héroe | Motivación | Mecánica propia |
| --- | --- | --- |
| **Cai** | Liberar a su padre, encarcelado en Dagsion. | Captura y cuida animales que puede usar como monturas. También cultiva y comercia con la cosecha. |
| **Dietrich** | Participar en el torneo junto a sus compañeros para seguir combatiendo. | Aprende y mejora técnicas de combate al derrotar enemigos. |
| **Theodora** | Defender la fe y la paz de Saramis, su reino. | Recluta soldados en los asentamientos y los despliega en batallones. |
| **Leda** | Encontrar al asesino de su padre, que planea competir en el torneo. | Actúa en tabernas para conseguir recompensas o pistas sobre lugares secretos. |

Puedes empezar por cualquiera de los cuatro héroes y alternar sendas en la **percha del Cuervo Blanco**, en Dagsion, durante el descanso entre combates. [Perfiles oficiales](https://www.nintendo.com/es-es/Juegos/Juegos-de-Nintendo-Switch-2/Fire-Emblem-Fortune-s-Weave-2915498.html) · [Entrevista al equipo, capítulo 2](https://www.nintendo.com/es-es/Noticias/2026/Septiembre/Pregunta-al-desarrollador-volumen-23-Capitulo-2-3195812.html) · [Guía oficial de inicio](https://www.nintendo.com/es-es/Noticias/2026/Septiembre/Fire-Emblem-Fortune-s-Weave-Siete-consejos-para-salvar-el-mundo-3199100.html)

## Combate, exploración y progreso

- **Combate.** El jugador despliega unidades en una cuadrícula y decide cómo moverlas y atacar. La clase, el arma, la posición y el terreno influyen en el resultado. Cada héroe dispone de técnicas de ignición que consumen puntos de vida. Los Joy-Con 2 admiten modo ratón como opción de control. [Descripción oficial del combate y los controles](https://www.nintendo.com/es-es/Juegos/Juegos-de-Nintendo-Switch-2/Fire-Emblem-Fortune-s-Weave-2915498.html)
- **Preparación y exploración.** Entre combates puedes entrenar, reclutar unidades, completar encargos y salir de Dagsion en busca de armas, materiales o información. El desplazamiento por el mapa consume tiempo. Ganar combates del torneo y completar misiones secundarias aumenta el prestigio y abre instalaciones en la capital. [Guía oficial de inicio](https://www.nintendo.com/es-es/Noticias/2026/Septiembre/Fire-Emblem-Fortune-s-Weave-Siete-consejos-para-salvar-el-mundo-3199100.html)
- **Guardado y repetición.** Un mismo archivo de guardado reúne el progreso de las cuatro sendas y conserva puntos de retorno al comienzo de cada capítulo. Puedes volver a capítulos o combates. Los desarrolladores explican que los atributos de una misma unidad entrenada en sendas o recorridos distintos pueden combinarse más adelante. [Entrevista al equipo, capítulo 2](https://www.nintendo.com/es-es/Noticias/2026/Septiembre/Pregunta-al-desarrollador-volumen-23-Capitulo-2-3195812.html)

## Pase de expansión y actualizaciones

El pase de pago requiere el juego base. Nintendo España publica el siguiente calendario. [Pase de expansión en Nintendo España](https://www.nintendo.com/es-es/Juegos/Juegos-de-Nintendo-Switch-2/Fire-Emblem-Fortune-s-Weave-2915498.html?nsuid=70070000040950)

| Parte | Disponibilidad en España | Contenido |
| --- | --- | --- |
| 1 | Disponible desde el 15 de octubre de 2026 | Cuatro armas prototipo inspiradas en las reliquias de *Three Houses*, una por héroe. Las misiones para obtenerlas aparecen a partir del capítulo 4 de la parte 1 de cada senda. |
| 2 | Anunciada para invierno de 2026 | Misiones para incorporar a Aurora, Smyrnos y Jurah como aliados, y cuatro atuendos. |
| 3 | Anunciada para 2027 | Historias de Bertrand, Talimun, Anatolia y Orchel, con nuevos aliados y clases. |

La última versión que recoge el historial oficial consultado es la **1.1.0**, del **15 de octubre de 2026**, que añade compatibilidad con el pase. La versión **1.0.1**, del día de lanzamiento, añadió armas extra obtenibles al avanzar en la historia. [Historial oficial de actualizaciones](https://www.nintendo.com/es-es/Ayuda/Compras-y-suscripciones/Juegos/Como-actualizar-Fire-Emblem-Fortune-s-Weave-3198923.html)

[Presentación oficial Fire Emblem: Fortune’s Weave Direct, 4 de agosto de 2026](https://www.nintendo.com/es-es/Noticias/Nintendo-Direct/2026/Fire-Emblem-Fortune-s-Weave-Direct-04-08-2026-3190150.html)

## Fichas de las unidades

La app incluye una descripción breve en español para las 63 unidades de su catálogo. Los textos resumen el papel de cada personaje, su grupo o una aptitud reconocible de combate. [Perfiles de personajes de Nintendo Japón](https://www.nintendo.com/jp/games/switch2/aacsa/character/index.html) · [Direct de Nintendo](https://www.nintendo.com/us/whatsnew/fire-emblem-fortunes-weave-direct-threads-together-strategic-combat-with-a-story-of-gods-and-heroes-only-on-nintendo-switch-2/) · [Habilidades personales en Serenes Forest](https://serenesforest.net/fortunes-weave/characters/personal-abilities/) · [Reparto por grupos en The Games Wiki](https://thegameswiki.com/fire-emblem-fortunes-weave/wiki/list-of-characters)

La ficha da prioridad a la clase que el jugador ha guardado para la unidad en esa senda. Si todavía no hay una asignación, muestra una clase de referencia documentada para el personaje. La clase con la que se incorpora puede variar según la senda o el momento. [Enciclopedia de personajes](https://fire-emblem-fortunes-weave.turam.dev/en/category/character/) · [Guía de personajes en LUM.GG](https://lum.gg/fire-emblem-fortunes-weave/characters) · [Fichas de personajes en Mt. Wildwood Wiki](https://fe.mtwildwood.net/en-gb/characters/) · [Hilo de fichas y clases en Serenes Forest](https://forums.serenesforest.net/topic/108278-recruitable-character-details/?comment=6186901&do=findComment)
