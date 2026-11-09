# KE Invitaciones - Android Application

Aplicación nativa para Android de **KE Invitaciones**, plataforma de invitaciones digitales interactivas para momentos inolvidables (15 años, casamientos, cumpleaños infantiles, bautismos, baby shower, aniversarios y galas).

## Características principales

1. **Catálogo & Landing (Inicio)**:
   - Explorador de modelos y diseños por categoría (15 años, Casamientos, Cumpleaños infantiles, Bautismos, Baby Shower, Aniversarios, Eventos especiales).
   - Sección "Cómo funciona" paso a paso.
   - Comparativa de Paquetes (Básico vs. Premium) con precios transparentes y detalle de funcionalidades.
   - Preguntas frecuentes interactivas (FAQ).
   - Contacto y contratación directa por WhatsApp.

2. **Experiencia de Invitación Digital Interactiva (Demostración)**:
   - **Sobre digital animado**: Sobre realista con sello de cera dorado monograma KE y apertura interactiva.
   - **Personalización de pases**: Pases asignados por invitado o familia (ej. "Familia Martínez - 4 lugares reservados").
   - **Música de fondo**: Barra de control con reproducción y pausa automática.
   - **Cuenta regresiva en vivo**: Días, horas, minutos y segundos al evento en tiempo real.
   - **Ubicación y mapa**: Salón, dirección y acceso directo a Google Maps.
   - **Dress Code & Regalos**: Información de vestimenta y datos bancarios/alias con botón de copiado en un toque.
   - **Galería fotográfica**: Visualización en cuadrícula con visor pantalla completa (Lightbox).
   - **Confirmación de Asistencia (RSVP)**:
     - Selección de asistencia (Sí / No).
     - Contador de lugares asistidos (limitado a los lugares asignados).
     - Dedicatoria o mensaje personalizado.
     - Aviso de confirmación directo por WhatsApp al anfitrión.

3. **Portal del Cliente (Mi Cuenta)**:
   - Resumen en tiempo real de respuestas recibidas, confirmados, total de personas, cancelaciones y mensajes.
   - Lista detallada de confirmaciones recibidas con filtros y estados.
   - Editor de textos en vivo (bienvenida, mensaje, info adicional, dress code, regalos, WhatsApp).
   - Acceso rápido a vista previa y compartir enlace.

4. **Panel de Gestión y Control (Admin)**:
   - **Dashboard**: Métricas comerciales y operativas (invitaciones creadas, activas, ventas señadas/pagadas, pendientes, ingresos y saldos por cobrar).
   - **Gestor de Invitaciones**: Creación y edición completa de invitaciones, asignación de paquetes y plantillas, activación/desactivación.
   - **Gestor de Invitados**: Carga individual y masiva (`Nombre; cantidad`), generación de enlaces personalizados para WhatsApp.
   - **Control de Pagos y Señas**: Registro de señas, totales, saldos y comprobantes.
   - **Generador de Códigos QR**: Vista previa y descarga/compartir de código QR para tarjetas impresas.

## Arquitectura y Tecnologías

- **Lenguaje**: Kotlin 2.2.10
- **UI Toolkit**: Jetpack Compose con Material Design 3 (M3)
- **Diseño**: Paleta de colores de lujo (`#C9A24B` dorado, `#1A1A1A` carbón, `#FFFAF3` crema, `#F6DDE2` rosa suave) y tipografía serif elegante.
- **Adaptabilidad**: Soporte responsivo para teléfonos (NavigationBar inferior) y tablets/pantallas anchas (NavigationRail lateral).
- **Gestión de Estado**: MVVM con Kotlin StateFlow reactivo en `InvitationRepository`.
- **Carga de imágenes**: Coil Compose.