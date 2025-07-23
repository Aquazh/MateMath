# 📱 MateMath - Aplicación Educativa de Matemáticas

Una aplicación nativa de Android desarrollada en Kotlin con Jetpack Compose para ayudar a niños de 5-10 años en comunidades latinoamericanas a mejorar sus habilidades matemáticas básicas a través de aprendizaje basado en juegos.

## 🌟 Características Completadas

### ✅ Funcionalidades Básicas
- **Pantalla de Bienvenida** con mascota llama animada 🦙
- **Generador de Problemas Matemáticos** (suma y resta)
- **Interfaz de Respuestas Múltiples** con 4 opciones por pregunta
- **Sistema de Puntuación** con puntos y "Yerba Coins" 🪙
- **Navegación Fluida** entre pantallas

### ✅ Características de Accesibilidad
- **Text-to-Speech en Español** 🔊
  - Lee automáticamente cada pregunta matemática
  - Proporciona retroalimentación vocal ("¡Muy bien!", "¡Inténtalo de nuevo!")
  - Botón de repetir pregunta con ícono de altavoz
- **Configuración optimizada para niños**
  - Velocidad de habla reducida (0.8x)
  - Tono más agudo y amigable (1.2x)

### ✅ Interfaz Gamificada
- **Animaciones Atractivas**
  - Animación de "respiración" de la mascota llama
  - Efectos de escala y deslizamiento para botones
  - Animaciones de entrada escalonadas para opciones de respuesta
  - Efectos visuales para respuestas correctas (🎉, 🌟)
- **Diseño Child-Friendly**
  - Colores vibrantes y contrastantes
  - Tipografía grande y legible
  - Elementos visuales divertidos (emojis)

### ✅ Lógica de Juego
- **Generación Inteligente de Problemas**
  - Suma: números del 1-10
  - Resta: garantiza resultados positivos
  - Opciones de respuesta realistas (respuesta correcta + 3 incorrectas cercanas)
- **Sistema de Recompensas**
  - +10 puntos por respuesta correcta
  - +1 Yerba Coin por respuesta correcta
  - Retroalimentación inmediata

## 🛠 Tecnologías Utilizadas

| Componente | Tecnología |
|------------|------------|
| Lenguaje | Kotlin |
| UI Framework | Jetpack Compose |
| Navegación | Navigation Compose |
| Arquitectura | MVVM con StateFlow |
| Voz | Android Text-to-Speech |
| Animaciones | Compose Animation API |

## 🚀 Cómo Ejecutar la Aplicación

### Prerrequisitos
- Android Studio (versión reciente)
- Java 17 (ya instalado vía Homebrew)
- Dispositivo Android o emulador con API nivel 24+

### Pasos para Ejecutar

1. **Abrir en Android Studio**
   ```bash
   # Desde la terminal
   open -a "Android Studio" /Users/augusto/AndroidStudioProjects/MateMath
   ```

2. **Sincronizar Proyecto**
   - Android Studio sincronizará automáticamente las dependencias
   - Espera a que termine la indexación

3. **Ejecutar la Aplicación**
   - Conecta un dispositivo Android o inicia un emulador
   - Presiona el botón "Run" (▶️) en Android Studio
   - O usa el atajo: `Ctrl+R` (Mac: `Cmd+R`)

### Desde Terminal (Opcional)
```bash
cd /Users/augusto/AndroidStudioProjects/MateMath

# Construir la aplicación
./gradlew build

# Instalar en dispositivo conectado
./gradlew installDebug
```

## 📱 Funcionalidades de la Aplicación

### Pantalla de Bienvenida
- Mascota llama animada con efecto de "respiración"
- Título y subtítulo con animaciones de entrada
- Botón "¡Empezar a Jugar!" con efecto de presión

### Pantalla de Problemas Matemáticos
- **Cabecera**: Muestra puntuación actual y Yerba Coins
- **Pregunta**: Problema matemático con botón de repetir audio
- **Opciones**: 4 tarjetas con animaciones y retroalimentación visual
- **Resultado**: Mensaje animado con emojis y botón "Siguiente Pregunta"

### Sistema de Text-to-Speech
- Se activa automáticamente al mostrar nueva pregunta
- Botón manual para repetir la pregunta
- Retroalimentación vocal para respuestas correctas/incorrectas

## 🔮 Próximas Características (Roadmap)

### Fase 2: IA y Adaptabilidad
- [ ] Integración con GPT-4 API para explicaciones en español simple
- [ ] Algoritmo de dificultad adaptativa basado en rendimiento
- [ ] Más tipos de problemas (multiplicación, división, fracciones)

### Fase 3: Persistencia y Analytics
- [ ] Base de datos local (Room/SQLite)
- [ ] Integración con Firebase para:
  - Autenticación de usuarios
  - Sincronización de progreso
  - Analytics de aprendizaje
- [ ] Perfiles de estudiante y seguimiento de progreso

### Fase 4: Funcionalidades Avanzadas
- [ ] Modo offline completo
- [ ] Más mini-juegos matemáticos
- [ ] Sistema de logros y badges
- [ ] Integración con currículum educativo argentino
- [ ] Modo multijugador local

### Fase 5: Localización y Expansión
- [ ] Soporte para otras variantes del español (México, Colombia, etc.)
- [ ] Modo para padres/maestros con estadísticas
- [ ] Exportación de reportes de progreso
- [ ] Integración con plataformas educativas

## 🏗 Arquitectura del Código

```
app/src/main/java/com/example/matemath/
├── MainActivity.kt              # Actividad principal con navegación
├── MateMathViewModel.kt         # Lógica de negocio y estado del juego
├── MateMathScreens.kt          # Composables de UI (pantallas)
├── MathModels.kt               # Modelos de datos y generador de problemas
├── TextToSpeechManager.kt      # Gestión de Text-to-Speech
└── ui/theme/                   # Tema y colores de la aplicación
```

## 🎨 Personalización

### Cambiar Colores
Edita `app/src/main/java/com/example/matemath/ui/theme/Color.kt`

### Modificar Strings
Edita `app/src/main/res/values/strings.xml` para textos en español

### Ajustar Dificultad
Modifica `MathProblemGenerator.generateProblem()` en `MathModels.kt`

### Personalizar TTS
Ajusta configuraciones en `TextToSpeechManager.kt`:
- `setSpeechRate()`: Velocidad de habla
- `setPitch()`: Tono de voz

## 🐛 Problemas Conocidos y Soluciones

### TTS no funciona
- Verificar que el dispositivo tenga español instalado
- Ir a Configuración > Idioma > Text-to-Speech > Instalar datos de voz

### Construcción falló
```bash
# Limpiar y reconstruir
./gradlew clean
./gradlew build
```

### Java no encontrado
```bash
# Verificar JAVA_HOME
echo $JAVA_HOME
# Debería mostrar: /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
```

## 👥 Contribución

Este proyecto fue desarrollado como parte de un esfuerzo por democratizar el acceso a educación matemática de calidad en comunidades latinoamericanas. 

### Cómo Contribuir
1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

## 🤝 Reconocimientos

- Desarrollado con ❤️ para comunidades educativas de Argentina
- Mascota llama 🦙 inspirada en la fauna sudamericana
- "Yerba Coins" como guiño cultural argentino 🧉

---

**¡Que disfruten aprendiendo matemáticas! 🎓📚** 