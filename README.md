# 📱 MateMath - Aplicación Educativa de Matemáticas

Una aplicación nativa de Android desarrollada en Kotlin con Jetpack Compose para ayudar a niños de 5-10 años en comunidades latinoamericanas a mejorar sus habilidades matemáticas básicas a través de aprendizaje basado en juegos.

## 🌟 Características Principales

### ✅ Modo Aprendizaje
- **Explicaciones Paso a Paso** de cada operación matemática
- **Ayudas Visuales** con objetos y números
- **Sistema de Pistas** progresivo
- **Retroalimentación Educativa** en español

### ✅ Modo Práctica
- **Generador de Problemas** (suma, resta, multiplicación, división)
- **Interfaz de Respuestas Múltiples**
- **Sistema de Puntuación** con "Yerba Coins"
- **Navegación Fluida** entre problemas

### ✅ Características de Accesibilidad
- **Text-to-Speech en Español**
- **Interfaz Adaptada para Niños**
- **Animaciones Interactivas**

## 🛠 Tecnologías Utilizadas

| Componente | Tecnología |
|------------|------------|
| Lenguaje | Kotlin |
| UI Framework | Jetpack Compose |
| Navegación | Navigation Compose |
| Arquitectura | MVVM con StateFlow |
| Voz | Android Text-to-Speech |
| Animaciones | Compose Animation API |

## 🚀 Configuración del Proyecto

### Prerrequisitos
- Android Studio (versión reciente)
- JDK 17 o superior
- Git

### Clonar el Repositorio
```bash
git clone git@github.com:Aquazh/MateMath.git
cd MateMath
```

### Configurar el Proyecto
1. Abrir en Android Studio
2. Sincronizar el proyecto con Gradle
3. Verificar que el SDK de Android esté instalado

### Ejecutar la Aplicación
1. Conectar un dispositivo Android o iniciar un emulador
2. Presionar "Run" en Android Studio o ejecutar:
```bash
./gradlew installDebug
```

## 👥 Contribución

### Flujo de Trabajo Git
1. **Crear una Nueva Rama**
```bash
git checkout -b feature/nueva-caracteristica
```

2. **Realizar Cambios**
- Seguir las guías de estilo de Kotlin
- Mantener los commits pequeños y enfocados
- Escribir mensajes de commit descriptivos

3. **Subir Cambios**
```bash
git add .
git commit -m "feat: descripción del cambio"
git push origin feature/nueva-caracteristica
```

4. **Crear Pull Request**
- Usar el template de PR
- Incluir descripción detallada
- Solicitar revisión

### Convenciones de Commits
- `feat:` Nueva característica
- `fix:` Corrección de bug
- `docs:` Cambios en documentación
- `style:` Cambios de formato
- `refactor:` Refactorización de código
- `test:` Añadir o modificar tests
- `chore:` Cambios en build o herramientas

## 📝 Licencia
Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

## 🤝 Contacto
Para preguntas o sugerencias, crear un issue o contactar a los mantenedores. 