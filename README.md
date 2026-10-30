# AgroFeed 360 · Monitoreo de Alimento en Silos
**Asignatura:** DSY1105 – Desarrollo de Aplicaciones Móviles  
**Institución:** Duoc UC – Escuela de Informática y Telecomunicaciones  
**Sede:** Sede Melipilla  
**Evaluación:** Evaluación Parcial 2 (EA2)  

---

## 1. Descripción del Proyecto

Aplicación móvil nativa para Android desarrollada en **Kotlin** y **Jetpack Compose** que resuelve la necesidad de centralización, monitoreo y telemetría de alimento en silos para empresas del sector agroalimentario (avícola y ganadero).

Permite a los **operarios en terreno**, **supervisores de planta** y **jefaturas de logística**:
* Consultar en tiempo real el nivel de alimento y porcentaje de capacidad de los silos por granja y pabellón.
* Validar la existencia física de los silos mediante un **Registro Visual Fotográfico**.
* Registrar movimientos de carga, descarga y consumo con evidencia de cámara y coordenadas GPS.
* Identificar oportunamente silos en nivel crítico (< 20%) y recibir **notificaciones prioritarias** en el sistema Android.
* Operar de forma continua con estrategia **Offline-First** mediante persistencia local en SQLite ante conectividad intermitente en faenas rurales.
* Gestionar la imagen de perfil de los usuarios con selección desde galería y captura de cámara nativa (Guía 13).

---

## 2. Arquitectura del Software

El proyecto sigue estrictamente el patrón **MVVM (Model - View - ViewModel)** combinado con el **Repository Pattern**:

```
app/src/main/java/com/duoc/silos/
├── data/
│   ├── local/          # SilosDbHelper (SQLite nativo con SQLiteOpenHelper)
│   ├── model/          # Data classes (Silo, Movimiento, Alerta, Usuario, Enums)
│   ├── remote/         # MockSilosApiService (API simulada con Kotlin Coroutines)
│   └── repository/     # SilosRepository (Fuente única de verdad Offline-First)
├── notification/       # SiloNotificationHelper (Canal y notificaciones nativas)
└── ui/
    ├── components/     # ImagenInteligente.kt (Foto circular Guía 13)
    ├── navigation/     # AppNavigation.kt y Screen.kt (Jetpack Compose Navigation)
    ├── screens/        # Pantallas: auth, dashboard, detalle, crear, movimiento, despacho, alertas, jefatura, perfil
    ├── theme/          # Paleta de colores AgroFeed Material 3 y tipografías
    └── viewmodel/      # SilosViewModel y PerfilViewModel (StateFlow reactivo)
```

---

## 3. Cumplimiento de Indicadores de Logro (Rúbrica EA2)

* **IE 2.1.1 (Diseño y Navegación):** Jerarquía visual clara con Material 3 y `NavHost` centralizado con paso de argumentos.
* **IE 2.1.2 (Formularios y Validaciones):** Formularios con validaciones por campo, íconos y feedback de error inmediato (Login, Registro, Registro Visual de Silo, Carga/Consumo).
* **IE 2.2.1 (Lógica Desacoplada):** Gestión del estado centralizada en `ViewModel` usando `StateFlow` y `MutableStateFlow`.
* **IE 2.2.2 (Animaciones Visuales):** Animación fluida de la barra de llenado de alimento con `animateFloatAsState(tween(800-1000))` y transiciones de alerta con `AnimatedVisibility(fadeIn + expandVertically)`.
* **IE 2.3.1 (Modularidad y Persistencia Local):** Persistencia en base de datos SQLite relacional (`silos_agrofeed360.db`) que asegura la retención de datos offline.
* **IE 2.4.1 (Recursos Nativos):** Acceso seguro y funcional a:
  1. **Cámara:** `ActivityResultContracts.TakePicture()` y `TakePicturePreview()` con `FileProvider`.
  2. **Galería:** `ActivityResultContracts.GetContent()`.
  3. **Notificaciones del Sistema:** `NotificationManager` y canal `canal_silos_alertas` con `IMPORTANCE_HIGH`.
  4. **Ubicación GPS:** Captura de coordenadas en terreno e integración con Google Maps / Waze.

---

## 4. Requisitos para Ejecutar el Proyecto

1. **Entorno:** Android Studio Ladybug (o superior).
2. **JDK:** Java 17 o superior.
3. **Android SDK:** 
   * `compileSdk = 35`
   * `minSdk = 24` (Compatible con Android 7.0 hasta Android 15)
   * `targetSdk = 35`
4. **Pasos para ejecutar:**
   * Clonar o abrir la carpeta `MonitoreoSilos` en Android Studio.
   * Esperar a que Gradle sincronice las dependencias.
   * Ejecutar en un emulador Android (API 30+) o conectar un dispositivo físico con depuración USB activada.
   * Presionar el botón **Run 'app'** (ícono de reproducir verde ▶️).
