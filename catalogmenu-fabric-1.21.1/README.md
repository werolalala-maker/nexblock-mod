# Catalog Menu (Fabric 1.21.1)

Pulsa **H** en el juego y se abre el menú de catálogo (marco futurista, 4 botones
a la izquierda y cuadrícula de objetos). Es solo visual: no hace nada más.
Se cierra con **H** o **ESC**. La tecla se puede cambiar en Controles.

## Compilar
Necesitas JDK 21.

    Linux / macOS:  ./gradlew build
    Windows:        gradlew.bat build

El .jar queda en `build/libs/catalogmenu-1.0.0.jar` (NO uses el que termina en `-sources`).
Cópialo a la carpeta `mods` junto con Fabric Loader y Fabric API para 1.21.1.

## Personalizar
- `src/main/resources/assets/catalogmenu/textures/gui/menu.png` (296x164): todo el diseño.
  Puedes pintar encima o reemplazarlo; se amplía con factor entero para verse nítido.
- `.../textures/gui/tab_hover.png`: brillo de los botones al pasar el ratón.
- `CatalogScreen.java`: posiciones, objetos de adorno y `HOVER_EFFECTS` (false = sin brillos).

## Sin instalar nada (GitHub Actions)
Sube esta carpeta a un repositorio de GitHub: se compila solo y el .jar queda en
Actions > último "build" > Artifacts (dentro está `catalogmenu-1.0.0.jar`).
