# BodrioPopcorn

Aplicación web de películas, series y reseñas.

## Tecnologías

- Java 21 y Spring Boot
- MySQL 8 y Flyway
- React y Vite

## Preparación

1. Crea una base de datos MySQL llamada `bodriopopcorn`.
2. Copia `backend/src/main/resources/application.properties.example`
   como `backend/src/main/resources/application.properties`.
3. Configura la conexión a MySQL y una clave JWT privada en Base64 de
   al menos 32 bytes. No subas el archivo de configuración.
4. Si necesitas Google, configura un cliente OAuth de tipo aplicación web,
   sus orígenes autorizados y el mismo ID en el backend y frontend.
5. TMDB, correo, SMS y WhatsApp requieren sus propias credenciales privadas.

Flyway crea el esquema y carga datos de demostración. Las cuentas de
demostración no deben quedar habilitadas públicamente sin revisión.

## Ejecución

En `backend`, ejecuta `.\mvnw.cmd spring-boot:run`.

En `frontend`, ejecuta `npm ci` y después `npm run dev`.

El backend usa el puerto 8089; Vite muestra la dirección del frontend.