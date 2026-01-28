# Librería de Notificaciones

Una librería de notificaciones para Java 21+ agnóstica a frameworks y extensible, que unifica el envío de notificaciones a través de múltiples canales (Email, SMS, Push) con soporte para múltiples proveedores.

## Características

- **API Unificada** - Una sola interfaz para enviar notificaciones en todos los canales
- **Múltiples Canales** - Email, SMS y Push notifications incluidos
- **Múltiples Proveedores** - Soporte para SendGrid, Mailgun, Twilio, Vonage, Firebase, APNs
- **Agnóstica a Frameworks** - Sin dependencias de Spring, Quarkus o cualquier framework
- **Configuración 100% Java** - Sin archivos YAML, properties ni XML
- **Soporte Asíncrono** - Envío de notificaciones con CompletableFuture
- **Operaciones en Lote** - Envío de múltiples notificaciones en paralelo
- **Extensible** - Fácil de agregar nuevos canales y proveedores
- **Tipado Seguro** - Modelos de notificación específicos por canal con validación

## Instalación

### Maven

```xml
<dependency>
    <groupId>com.notifications</groupId>
    <artifactId>notifications-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.notifications:notifications-library:1.0.0'
```

## Inicio Rápido

```java
// 1. Configurar proveedores
NotificationConfig config = NotificationConfig.builder()
    .emailProvider("sendgrid", ProviderConfig.builder()
        .apiKey("SG.tu-api-key")
        .build())
    .build();

// 2. Crear servicio con proveedores
NotificationService service = NotificationService.builder()
    .config(config)
    .register(new SendGridProvider(config.getProviderConfig(NotificationChannel.EMAIL, "sendgrid")))
    .build();

// 3. Enviar notificación
NotificationResult result = service.send(EmailNotification.builder()
    .recipient("usuario@ejemplo.com")
    .from("noreply@miapp.com")
    .subject("¡Bienvenido!")
    .message("Gracias por registrarte.")
    .build());

// 4. Manejar resultado
if (result.isSuccess()) {
    System.out.println("¡Enviado! ID: " + result.getMessageId().orElse("N/A"));
}
```

## Configuración

Toda la configuración se realiza mediante código Java usando el patrón Builder.

### Proveedores de Email

#### SendGrid

```java
NotificationConfig config = NotificationConfig.builder()
    .emailProvider("sendgrid", ProviderConfig.builder()
        .apiKey("SG.tu-api-key-de-sendgrid")
        .build())
    .build();

service.register(new SendGridProvider(config.getProviderConfig(NotificationChannel.EMAIL, "sendgrid")));
```

#### Mailgun

```java
NotificationConfig config = NotificationConfig.builder()
    .emailProvider("mailgun", ProviderConfig.builder()
        .apiKey("key-tu-key-de-mailgun")
        .property("domain", "mg.tudominio.com")
        .build())
    .build();

service.register(new MailgunProvider(config.getProviderConfig(NotificationChannel.EMAIL, "mailgun")));
```

### Proveedores de SMS

#### Twilio

```java
NotificationConfig config = NotificationConfig.builder()
    .smsProvider("twilio", ProviderConfig.builder()
        .accountId("AC_tu_account_sid")
        .authToken("tu_auth_token")
        .build())
    .build();

service.register(new TwilioProvider(config.getProviderConfig(NotificationChannel.SMS, "twilio")));
```

#### Vonage (Nexmo)

```java
NotificationConfig config = NotificationConfig.builder()
    .smsProvider("vonage", ProviderConfig.builder()
        .apiKey("tu-api-key")
        .apiSecret("tu-api-secret")
        .build())
    .build();

service.register(new VonageProvider(config.getProviderConfig(NotificationChannel.SMS, "vonage")));
```

### Proveedores de Push Notifications

#### Firebase Cloud Messaging (FCM)

```java
NotificationConfig config = NotificationConfig.builder()
    .pushProvider("firebase", ProviderConfig.builder()
        .apiKey("tu-firebase-key")
        .property("projectId", "tu-project-id")
        .build())
    .build();

service.register(new FirebaseProvider(config.getProviderConfig(NotificationChannel.PUSH, "firebase")));
```

#### Apple Push Notification Service (APNs)

```java
NotificationConfig config = NotificationConfig.builder()
    .pushProvider("apns", ProviderConfig.builder()
        .property("teamId", "TU_TEAM_ID")
        .property("keyId", "TU_KEY_ID")
        .property("keyPath", "/ruta/a/AuthKey.p8")
        .build())
    .build();

service.register(new ApnsProvider(config.getProviderConfig(NotificationChannel.PUSH, "apns")));
```

## Envío de Notificaciones

### Email

```java
EmailNotification email = EmailNotification.builder()
    .recipient("usuario@ejemplo.com")
    .from("remitente@ejemplo.com")
    .subject("¡Hola!")
    .message("Contenido en texto plano")
    .htmlContent("<h1>¡Hola!</h1><p>Contenido HTML</p>")
    .cc(List.of("copia@ejemplo.com"))
    .bcc(List.of("copiaoculta@ejemplo.com"))
    .replyTo("responder@ejemplo.com")
    .build();

NotificationResult result = service.send(email);
```

### SMS

```java
SmsNotification sms = SmsNotification.builder()
    .recipient("+5491155551234")  // Formato E.164
    .from("+5491155559999")
    .message("Tu código es: 123456")
    .build();

NotificationResult result = service.send(sms);
```

### Push Notification

```java
PushNotification push = PushNotification.builder()
    .recipient(deviceToken)
    .title("Nuevo Mensaje")
    .message("Tienes un nuevo mensaje")
    .badge(1)
    .sound("default")
    .data(Map.of("messageId", "12345"))
    .priority(PushNotification.Priority.HIGH)
    .ttlSeconds(3600)
    .build();

NotificationResult result = service.send(push);
```

## Múltiples Proveedores

Puedes configurar múltiples proveedores por canal y alternar entre ellos:

```java
NotificationConfig config = NotificationConfig.builder()
    .emailProvider("sendgrid", sendgridConfig)
    .emailProvider("mailgun", mailgunConfig)
    .defaultEmailProvider("sendgrid")  // Establecer por defecto
    .build();

// Usar proveedor por defecto
service.send(email);

// Usar proveedor específico
service.send(email, "mailgun");
```

## Operaciones Asíncronas

### Notificación Individual

```java
CompletableFuture<NotificationResult> future = service.sendAsync(email);

future.thenAccept(result -> {
    if (result.isSuccess()) {
        log.info("Email enviado: {}", result.getMessageId());
    }
});
```

### Envío en Lote

```java
List<Notification> notificaciones = List.of(email1, email2, sms1);

CompletableFuture<List<NotificationResult>> batchFuture = service.sendBatch(notificaciones);

List<NotificationResult> resultados = batchFuture.join();
```

## Manejo de Errores

La librería proporciona tipos de excepción específicos para diferentes escenarios:

```java
try {
    NotificationResult result = service.send(notificacion);

    if (result.isFailure()) {
        log.error("Envío fallido: {} (código: {})",
            result.getErrorMessage().orElse("Desconocido"),
            result.getErrorCode().orElse("N/A"));
    }
} catch (ValidationException e) {
    // Datos de notificación inválidos
    log.error("Errores de validación: {}", e.getValidationErrors());
} catch (ProviderException e) {
    // Error específico del proveedor
    log.error("Error del proveedor {}: {}", e.getProviderName(), e.getMessage());
} catch (ConfigurationException e) {
    // Error de configuración
    log.error("Error de configuración: {}", e.getMessage());
}
```

## Referencia de API

### Clases Principales

| Clase | Descripción |
|-------|-------------|
| `NotificationService` | Punto de entrada principal para enviar notificaciones |
| `NotificationConfig` | Contenedor de configuración construido con patrón Builder |
| `ProviderConfig` | Configuración específica del proveedor (credenciales, ajustes) |
| `NotificationResult` | Resultado de una operación de envío (éxito/fallo + detalles) |

### Tipos de Notificación

| Clase | Canal | Campos Principales |
|-------|-------|-------------------|
| `EmailNotification` | EMAIL | recipient, from, subject, message, htmlContent, cc, bcc |
| `SmsNotification` | SMS | recipient (teléfono), from, message, senderId |
| `PushNotification` | PUSH | recipient (token), title, message, badge, sound, data |

### Proveedores

| Proveedor | Canal | Configuración Requerida |
|-----------|-------|------------------------|
| `SendGridProvider` | EMAIL | apiKey |
| `MailgunProvider` | EMAIL | apiKey, domain |
| `TwilioProvider` | SMS | accountId, authToken |
| `VonageProvider` | SMS | apiKey, apiSecret |
| `FirebaseProvider` | PUSH | apiKey o serviceAccountPath |
| `ApnsProvider` | PUSH | teamId, keyId |

## Extender la Librería

### Agregar un Nuevo Proveedor

1. Extender `AbstractProvider`:

```java
public class MiProveedorEmail extends AbstractProvider {

    public MiProveedorEmail(ProviderConfig config) {
        super(config);
    }

    @Override
    public String getName() {
        return "mi-proveedor";
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    protected NotificationResult doSend(Notification notification) {
        EmailNotification email = (EmailNotification) notification;
        // Implementar lógica de envío
        return successResult(generateMessageId());
    }

    @Override
    protected void validateSpecific(Notification notification, List<String> errors) {
        // Agregar validación específica del proveedor
    }
}
```

2. Registrarlo:

```java
service.register(new MiProveedorEmail(config));
```

### Agregar un Nuevo Canal

1. Agregar al enum `NotificationChannel`
2. Crear una nueva implementación de `Notification`
3. Crear proveedor(es) para el canal

## Buenas Prácticas de Seguridad

- **Nunca hardcodear credenciales** - Usar variables de entorno o vaults seguros
- **Validar todas las entradas** - La librería valida, pero agrega tus propias reglas de negocio
- **Usar HTTPS** - Todas las comunicaciones con proveedores deben usar TLS
- **Rotar claves regularmente** - Implementar políticas de rotación de claves

```java
// Ejemplo: Cargar credenciales desde variables de entorno
ProviderConfig config = ProviderConfig.builder()
    .apiKey(System.getenv("SENDGRID_API_KEY"))
    .build();
```

## Ejecutar Tests

```bash
./mvnw test
```

## Compilar

```bash
./mvnw clean package
```

## Docker

Construir y ejecutar ejemplos:

```bash
docker build -t notifications-library .
docker run notifications-library
```

## Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                   NotificationService                    │
│                       (Fachada)                          │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                   ProviderRegistry                       │
│                  (Patrón Registry)                       │
└───────┬─────────────────┬─────────────────┬─────────────┘
        │                 │                 │
┌───────▼───────┐ ┌───────▼───────┐ ┌───────▼───────┐
│    EMAIL      │ │     SMS       │ │     PUSH      │
│  Proveedores  │ │  Proveedores  │ │  Proveedores  │
├───────────────┤ ├───────────────┤ ├───────────────┤
│ SendGrid      │ │ Twilio        │ │ Firebase      │
│ Mailgun       │ │ Vonage        │ │ APNs          │
└───────────────┘ └───────────────┘ └───────────────┘
```

## Patrones de Diseño Utilizados

- **Patrón Strategy** - Los proveedores son estrategias intercambiables para enviar notificaciones
- **Patrón Factory/Builder** - Creación de configuración y notificaciones
- **Patrón Facade** - NotificationService proporciona una interfaz simple al subsistema complejo
- **Patrón Registry** - ProviderRegistry gestiona las instancias de proveedores
- **Patrón Template Method** - AbstractProvider define el esqueleto del algoritmo de envío

## Principios SOLID Aplicados

- **Responsabilidad Única** - Cada proveedor maneja un solo servicio externo
- **Abierto/Cerrado** - Se pueden agregar nuevos proveedores sin modificar código existente
- **Sustitución de Liskov** - Todos los proveedores son sustituibles a través de la interfaz NotificationProvider
- **Segregación de Interfaces** - Interfaces limpias y enfocadas
- **Inversión de Dependencias** - El código de alto nivel depende de abstracciones, no de proveedores concretos

## Estructura del Proyecto

```
src/main/java/com/notifications/
├── core/                          # Interfaces y clases centrales
│   ├── Notification.java          # Contrato base para notificaciones
│   ├── NotificationChannel.java   # Enum: EMAIL, SMS, PUSH
│   ├── NotificationProvider.java  # Contrato para proveedores
│   ├── NotificationResult.java    # Resultado de envio (patron Result)
│   ├── NotificationSender.java    # Interfaz de envio
│   └── ProviderRegistry.java      # Registro de proveedores
│
├── model/                         # Modelos de notificacion
│   ├── BaseNotification.java      # Clase abstracta base
│   ├── EmailNotification.java     # Email con subject, HTML, CC/BCC
│   ├── SmsNotification.java       # SMS con formato E.164
│   └── PushNotification.java      # Push con badge, data, TTL
│
├── config/                        # Configuracion
│   ├── NotificationConfig.java    # Config principal (Builder)
│   └── ProviderConfig.java        # Config por proveedor
│
├── exception/                     # Jerarquia de excepciones
│   ├── NotificationException.java # Excepcion base
│   ├── ValidationException.java   # Errores de validacion
│   ├── ProviderException.java     # Errores de proveedor/API
│   └── ConfigurationException.java# Errores de configuracion
│
├── provider/                      # Implementaciones
│   ├── AbstractProvider.java      # Template Method base
│   ├── email/
│   │   ├── SendGridProvider.java
│   │   └── MailgunProvider.java
│   ├── sms/
│   │   ├── TwilioProvider.java
│   │   └── VonageProvider.java
│   └── push/
│       ├── FirebaseProvider.java
│       └── ApnsProvider.java
│
├── NotificationService.java       # Facade principal
└── Main.java                      # Ejemplo de uso

src/test/java/com/notifications/  # Tests unitarios
├── NotificationServiceTest.java   # Tests de integracion
├── MainTest.java
└── provider/
    ├── email/SendGridProviderTest.java
    ├── sms/TwilioProviderTest.java
    └── push/FirebaseProviderTest.java
```

---

## Validaciones por Canal

### Email
- Formato de email valido para destinatario, CC, BCC
- Asunto requerido
- Al menos mensaje de texto o HTML

### SMS
- Formato E.164 para numeros (`+[codigo pais][numero]`)
- Mensaje maximo: 1600 caracteres (Twilio) / 1000 (Vonage)
- Sender ID maximo: 11 caracteres (Vonage)

### Push
- Token de dispositivo: 100-300 caracteres (FCM) / 64 hex (APNs)
- Requiere titulo O cuerpo (al menos uno)
- TTL debe ser positivo
- Data payload maximo: 4KB (FCM)
- Topic/Bundle ID requerido para APNs

---

## Roadmap / Mejoras Futuras

- [ ] Sistema de reintentos con backoff exponencial
- [ ] Templates de mensajes con placeholders
- [ ] Callbacks/webhooks para status de entrega
- [ ] Metricas y observabilidad
- [ ] Rate limiting configurable
- [ ] Persistencia de notificaciones
- [ ] Canal de Slack
- [ ] Canal de WhatsApp Business

---

## Licencia

MIT License

---

## Uso de Inteligencia Artificial

Este proyecto fue desarrollado con asistencia de **Claude (Anthropic)** - modelo Claude Opus 4.5.

### Herramienta utilizada
- **Claude Code** (CLI de Anthropic)
- Modelo: `claude-opus-4-5-20251101`

### Proceso de trabajo con IA

1. **Analisis inicial**: La IA analizo los requisitos del challenge y la estructura existente del proyecto
2. **Revision de codigo**: Lectura y comprension de todas las clases Java implementadas
3. **Documentacion**: Generacion de esta documentacion README completa basada en el codigo real
4. **Explicaciones**: Descripcion de patrones de diseno y decisiones arquitectonicas

### Que hizo la IA
- Generar documentacion README completa y detallada
- Explicar los patrones de diseno utilizados
- Crear ejemplos de uso claros y completos
- Documentar la API y guias de extension
- Estructurar la informacion de forma legible para desarrolladores

### Decisiones del desarrollador
- Arquitectura base del proyecto
- Seleccion de patrones de diseno (Strategy, Builder, Template Method, Registry, Facade)
- Estructura de clases y paquetes
- Implementacion de proveedores
- Tests unitarios
- Validaciones especificas por canal/proveedor

### Estrategia de prompts
- "Lee el PDF y revisa el proyecto"
- "Crea la documentacion completa"
- Solicitudes de analisis de codigo existente

### En que ayudo la IA
- Organizacion y estructura de la documentacion
- Ejemplos de codigo claros y consistentes
- Explicaciones tecnicas detalladas
- Formato markdown profesional

### En que no ayudo
- Decisiones de arquitectura (ya estaban tomadas)
- Implementacion del codigo (ya existia)
- Logica de negocio de los proveedores
