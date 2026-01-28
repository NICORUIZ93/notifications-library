# Notifications Library

Librería Java para el envío unificado de notificaciones a través de múltiples canales (Email, SMS, Push).

## Características

- **Agnóstica a frameworks**: No requiere Spring, Quarkus ni ningún framework específico
- **Extensible**: Permite agregar nuevos canales sin modificar código existente
- **Configuración en código**: Type-safe, sin archivos de configuración externos
- **Manejo de errores estructurado**: Diferencia entre errores de validación, configuración y envío
- **Soporte asíncrono**: Envío no bloqueante mediante CompletableFuture

## Requisitos

- Java 21 o superior
- Maven 3.6+

## Instalación

Agregar la dependencia en el archivo `pom.xml`:

```xml
<dependency>
    <groupId>com.notifications</groupId>
    <artifactId>notifications-library</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Quick Start

```java
// 1. Crear el servicio con los canales deseados
NotificationService service = new NotificationService.Builder()
    .withEmailChannel(new SendGridProvider("tu-api-key"))
    .withSmsChannel(new TwilioProvider("account-sid", "auth-token", "+1234567890"))
    .withPushChannel(new FirebaseProvider("service-account-key"))
    .build();

// 2. Crear una notificación
Notification notification = Notification.builder()
    .id("notif-001")
    .recipient("usuario@email.com")
    .subject("Bienvenido")
    .content("Gracias por registrarte en nuestra plataforma")
    .build();

// 3. Enviar la notificación
NotificationResult result = service.send(notification);

if (result.isSuccess()) {
    System.out.println("Notificación enviada: " + result.getProviderMessageId());
} else {
    System.out.println("Error: " + result.getMessage());
}
```

## Configuración de Canales

### Email (SendGrid)

```java
EmailProvider emailProvider = new SendGridProvider("tu-sendgrid-api-key");

NotificationService service = new NotificationService.Builder()
    .withEmailChannel(emailProvider)
    .build();
```

### SMS (Twilio)

```java
SmsProvider smsProvider = new TwilioProvider(
    "tu-account-sid",
    "tu-auth-token",
    "+1234567890"  // Número de origen
);

NotificationService service = new NotificationService.Builder()
    .withSmsChannel(smsProvider)
    .build();
```

### Push (Firebase Cloud Messaging)

```java
PushProvider pushProvider = new FirebaseProvider("tu-service-account-key");

NotificationService service = new NotificationService.Builder()
    .withPushChannel(pushProvider)
    .build();
```

### Configuración completa

```java
NotificationService service = new NotificationService.Builder()
    .withEmailChannel(new SendGridProvider("sendgrid-api-key"))
    .withSmsChannel(new TwilioProvider("account-sid", "auth-token", "+1234567890"))
    .withPushChannel(new FirebaseProvider("firebase-key"))
    .build();
```

## Proveedores Soportados

| Canal | Proveedor | Clase |
|-------|-----------|-------|
| Email | SendGrid | `SendGridProvider` |
| SMS | Twilio | `TwilioProvider` |
| Push | Firebase Cloud Messaging | `FirebaseProvider` |

## API Reference

### NotificationService

| Método | Descripción |
|--------|-------------|
| `send(Notification)` | Envía una notificación de forma síncrona |
| `sendAsync(Notification)` | Envía una notificación de forma asíncrona |
| `sendBatch(List<Notification>)` | Envía múltiples notificaciones |

### Notification.builder()

| Método | Descripción |
|--------|-------------|
| `.id(String)` | Identificador único de la notificación |
| `.recipient(String)` | Agrega un destinatario |
| `.recipients(Set<String>)` | Establece múltiples destinatarios |
| `.subject(String)` | Asunto (usado en email y push) |
| `.content(String)` | Contenido del mensaje |
| `.metadata(String, Object)` | Metadatos adicionales |
| `.preferredChannel(ChannelType)` | Canal preferido para el envío |
| `.build()` | Construye la notificación |

### NotificationResult

| Método | Descripción |
|--------|-------------|
| `isSuccess()` | Indica si el envío fue exitoso |
| `getMessage()` | Mensaje descriptivo del resultado |
| `getProviderMessageId()` | ID asignado por el proveedor |
| `getChannelType()` | Tipo de canal utilizado |
| `getTimestamp()` | Fecha y hora del envío |

## Extensibilidad

Para agregar un nuevo canal, implementar la interfaz `NotificationChannel`:

```java
public class MiCanalPersonalizado implements NotificationChannel {

    @Override
    public NotificationResult send(Notification notification) throws NotificationException {
        // Implementación del envío
    }

    @Override
    public boolean supports(Notification notification) {
        // Lógica para determinar si este canal puede procesar la notificación
    }

    @Override
    public ChannelType getType() {
        return ChannelType.CUSTOM;
    }
}
```

Luego agregarlo al servicio:

```java
NotificationService service = new NotificationService.Builder()
    .withCustomChannel(ChannelType.CUSTOM, new MiCanalPersonalizado())
    .build();
```

## Manejo de Errores

La librería utiliza `NotificationException` con tipos de error específicos:

```java
try {
    service.send(notification);
} catch (NotificationException e) {
    switch (e.getErrorType()) {
        case VALIDATION_ERROR:
            // Datos de entrada inválidos
            break;
        case CONFIGURATION_ERROR:
            // Error en la configuración del servicio
            break;
        case SEND_ERROR:
            // Error durante el envío
            break;
        case PROVIDER_ERROR:
            // Error reportado por el proveedor externo
            break;
    }
}
```

## Seguridad

### Mejores prácticas para el manejo de credenciales:

1. **Nunca hardcodear credenciales** en el código fuente
2. **Usar variables de entorno** para configuración sensible:
   ```java
   String apiKey = System.getenv("SENDGRID_API_KEY");
   EmailProvider provider = new SendGridProvider(apiKey);
   ```
3. **Usar gestores de secretos** como AWS Secrets Manager, HashiCorp Vault o Azure Key Vault
4. **Rotar credenciales** periódicamente
5. **Limitar permisos** de las API keys al mínimo necesario

## Estructura del Proyecto

```
src/main/java/com/notifications/
├── core/                          # Clases base
│   ├── Notification.java          # Modelo de notificación
│   ├── NotificationChannel.java   # Interfaz de canal
│   ├── NotificationResult.java    # Resultado del envío
│   ├── NotificationException.java # Excepciones
│   └── ChannelType.java           # Tipos de canal
├── channels/
│   ├── email/                     # Canal de email
│   ├── sms/                       # Canal de SMS
│   └── push/                      # Canal de push
├── providers/config/              # Configuración
└── NotificationService.java       # Servicio principal
```

## Compilación

```bash
mvn clean compile
```

## Tests

```bash
mvn test
```

## Docker

### Construir la imagen

```bash
docker build -t notifications-library .
```

### Ejecutar los ejemplos

```bash
docker run notifications-library
```

### Salida esperada

```
===========================================
   Ejemplos de Notifications Library
===========================================

1. Envío de Email
-----------------
   Resultado: Exitoso
   ID del proveedor: sg_1234567890

2. Envío de SMS
---------------
   Resultado: Exitoso
   ID del proveedor: tw_1234567890

3. Envío de Push Notification
-----------------------------
   Resultado: Exitoso
   ID del proveedor: fcm_1234567890

4. Envío Múltiple (Batch)
-------------------------
   Notificaciones enviadas: 3

===========================================
   Ejemplos completados exitosamente
===========================================
```

---

Este proyecto fue desarrollado con asistencia de **Claude Code (Claude Opus 4.5)** de Anthropic.
