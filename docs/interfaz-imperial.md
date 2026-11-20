# Interfaz imperial

La pantalla de Sendas usa una ilustración original de un anfiteatro al atardecer. Las fechas se presentan como sellos sobre placas de marfil. Las pantallas de misión, unidades y apoyos usan paneles azul imperial, retratos enmarcados y acciones principales en carmesí.

La paleta, la tipografía y los espaciados viven en `app/src/main/res/values`. Los botones y los campos muestran el foco en dorado. Los diálogos conservan los controles nativos de Android y la entrada de las cabeceras sigue la preferencia de animación del sistema.

Los títulos usan Cinzel a partir de Android 8 y la familia serif del sistema en Android 7. El cuerpo usa la familia sans-serif de Android. La licencia de Cinzel se incluye en `app/src/main/assets/licenses/Cinzel-OFL.txt`. [Cinzel y su licencia](https://github.com/google/fonts/tree/main/ofl/cinzel)

Los retratos del ZIP proporcionado se empaquetan como miniaturas WebP de 192 × 192 píxeles y se asocian al ID de cada unidad. Se usan en las fichas, el orden de juego, los selectores, las parejas y sus confirmaciones. La entrada de Eshmel usa la variante femenina del ZIP. Los registros antiguos sin retrato conservan su inicial.

## Ilustración de la portada

La herramienta integrada de generación de imágenes creó el recurso `app/src/main/res/drawable-nodpi/imperial_arena.jpg`. La ilustración se empaquetó como JPEG de 1440 × 720 píxeles.

```text
Use case: stylized-concept. Create a new original wide atmospheric illustration for the header of a native Android tactical campaign journal. A low eye-level view across an oval Roman-inspired imperial amphitheatre at late dusk, seen from a shadowed side arcade. A fragment of pale travertine colonnade frames the left foreground, distant curved galleries and two narrow burgundy pennants stand at upper right, small amber braziers reflect on a pale circular mosaic. Warm ivory marble, muted copper-gold lights, smoky aubergine shadows and imperial slate blue. Sophisticated painted Japanese fantasy environment art, precise stone materials, restrained detail, rich believable lighting. Compose in a wide banner ratio, concentrating the arena and brightest highlights on the right and leaving the left third and the bottom darker for overlaid navigation typography. Environment only, with no people, lettering, logos or interface. Design an original building, camera angle and composition. The provided arena image is a reference for warm light and stone only.
```

## Verificación

Pasaron las seis pruebas de la aplicación en un emulador de Android 11. Se revisaron las capturas de Sendas, la ficha de senda, el editor de misión, el selector, el registro de unidades y las parejas de apoyo. El editor se comprobó también a 320 dp de ancho con el tamaño de texto al 130 %. [Pruebas instrumentadas](https://github.com/DavidSM24/FEFW-Manager/actions/runs/44706969073/job/136188896449#step:6:1) · [Capturas del emulador](https://github.com/DavidSM24/FEFW-Manager/actions/runs/44707931556/artifacts/14616470414)
