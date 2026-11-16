# KanDo - App Móvil de Tareas (Prueba Técnica Android / Kotlin)

KanDo es una aplicación móvil desarrollada en Kotlin y Jetpack Compose como parte de la prueba técnica de desarrollo Android. La app permite gestionar tareas con persistencia local offline (SQLite), sincronización inicial desde una API pública (`JSONPlaceholder`), filtros por estado, creación de tareas con formulario validado y cambios de estado dinámicos.

---

## 1. Cómo Ejecutar el Proyecto

1. **Requisitos**:
   - Android Studio (Narwhal / Koala o superior recomendado).
   - JDK 11 o superior.
   - Emulador de Android (API 26+) o dispositivo físico con depuración USB activada.

2. **Pasos**:
   - Clonar o abrir el repositorio en Android Studio.
   - Esperar a que Gradle sincronice las dependencias automáticamente.
   - Seleccionar un dispositivo/emulador en Android Studio.
   - Hacer clic en el botón **Run** (`▶`) o ejecutar en terminal:
     ```bash
     ./gradlew assembleDebug
     ```

3. **Ejecución de Tests**:
   - Para ejecutar los tests unitarios:
     ```bash
     ./gradlew testDebugUnitTest
     ```

---

## 2. Decisiones Técnicas Tomadas

- **Lenguaje & UI**: Desarrollado 100% en **Kotlin** y **Jetpack Compose** con Material 3, garantizando una interfaz moderna, reactiva y adaptativa.
- **Arquitectura (MVVM)**: Separación clara en capas:
  - **Domain**: Modelos de datos (`Task`, `TaskStatus`).
  - **Data**: Repositorio (`TaskRepository`), servicio API REST con **Retrofit + Gson** (`TodoApiService`), y base de datos local **SQLite** (`TaskDbHelper`).
  - **UI**: ViewModels (`TaskViewModel`) manejando estado reactivo (`StateFlow`) y pantallas Compose (`TaskListScreen`, `CreateTaskScreen`).
- **Persistencia Local (Offline-First)**: Uso de SQLite nativo (`SQLiteOpenHelper`) para un rendimiento excelente, almacenamiento offline robusto y cero dependencias de compilación complejas.
- **Sincronización API Pública**: En la primera carga, la app consume tareas desde [https://jsonplaceholder.typicode.com/todos](https://jsonplaceholder.typicode.com/todos), mapeando el estado booleano `completed` a `DONE` o `PENDING`, y las almacena en SQLite.
- **Manejo de Errores de Red**: Captura de excepciones de red y visualización amigable al usuario mediante un banner de advertencia/error con opción de reintento, permitiendo el uso offline continuo de la aplicación con los datos locales existentes.

---

## 3. Qué Haría con Más Tiempo

- **Inyección de Dependencias**: Integrar **Hilt** para inyección de dependencias a escala empresarial.
- **Base de Datos Avanzada**: Migrar a **Room** con migraciones complejas (o SQLDelight) si se requiere tipado reactivo de base de datos con Flow.
- **Sincronización Bidireccional / Cola Offline**: Implementar una cola de sincronización en segundo plano (WorkManager) para subir modificaciones locales cuando se recupere la conexión a internet.
- **Tests Instrumentados y UI Tests**: Añadir tests de UI más exhaustivos con Compose Testing (ej. probar el flujo completo de creación de tareas y filtrado).
- **Animaciones y Accesibilidad**: Mejorar transiciones entre pantallas y etiquetas de accesibilidad (`contentDescription`).

---

## 4. Uso de la Inteligencia Artificial

- **Qué le pediste**:
  - Estructuración inicial de la arquitectura MVVM y diseño de componentes con Jetpack Compose Material 3.
  - Implementación del helper de SQLite y el servicio Retrofit para consumir JSONPlaceholder.
  - Redacción del README y soporte para estructurar los casos de test unitarios y de UI.
- **Qué aceptaste**:
  - La estructura modular limpia (capas `data`, `domain`, `ui`) y la gestión de estados reactivos con `StateFlow`.
  - El diseño de la interfaz con `FilterChip`, tarjetas de tareas (`TaskItemCard`) y manejo de errores con banners animados.
- **Qué corregiste / Ajustaste**:
  - Adaptación de dependencias de compilación para garantizar compatibilidad con la versión de Kotlin y AGP del entorno.
  - Ajuste en el mapeo de estados de la API (`completed` -> `TaskStatus.DONE` / `PENDING`).
  - Validación estricta del formulario de creación de tareas (título obligatorio con mínimo 3 caracteres).

---

## 5. Historial de Commits / Progreso

Siguiendo la recomendación de realizar commits a medida que se avanza:
1. `init: setup project structure and dependencies`
2. `feat: add domain models and SQLite local persistence helper`
3. `feat: add Retrofit API service and TaskRepository for offline-first sync`
4. `feat: implement MVVM TaskViewModel with state management and filtering`
5. `feat: implement Jetpack Compose UI (TaskListScreen & CreateTaskScreen)`
6. `test: add unit tests and UI widget tests`
7. `docs: add comprehensive README with technical test documentation`
