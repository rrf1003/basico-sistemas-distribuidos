# 🚀 Práctica 2 - Sistema de Gestión de Excepciones e Integración de APIs

Este proyecto implementa una arquitectura basada en microservicios contenerizados para demostrar la correcta gestión de excepciones globales, la integración entre diferentes lenguajes (Java y Python) y la persistencia de datos.

## 🏗️ 1. Arquitectura del Sistema

El ecosistema se orquesta al completo mediante **Docker** y `docker-compose`, y se compone de tres piezas fundamentales:
* **☕ Backend Principal (Spring Boot):** Actúa como el núcleo de la aplicación. Gestiona la seguridad (Spring Security), el renderizado de vistas (Thymeleaf) y la comunicación HTTP con otros servicios.
* **🐬 Base de Datos (MySQL):** Almacena de forma persistente los usuarios y sus roles. Se inicializa automáticamente con un usuario administrador por defecto.
* **🐍 Microservicio Externo (Python/Flask):** Una API secundaria creada específicamente para simular integraciones con terceros y forzar fallos controlados.

## 💥 2. Microservicio de Python (Simulador de Errores)

Se ha desarrollado una API en Python utilizando el *framework* Flask. Este microservicio expone tres *endpoints* diseñados para fallar y devolver distintos códigos de estado HTTP, permitiendo probar la resiliencia del sistema principal:

1. **Error de API de Terceros (`/api/pokemon/<nombre>`):** Intenta consumir la PokéAPI real. Al pasarle un nombre falso, atrapa el fallo de la API externa y devuelve un error **HTTP 404 (Not Found)**.
2. **Error de Lectura de Archivos (`/api/error-archivo`):** Fuerza una excepción `FileNotFoundError` al intentar abrir un documento inexistente, devolviendo un error **HTTP 500 (Internal Server Error)**.
3. **Error de Base de Datos (`/api/error-bd`):** Fuerza un fallo de conexión `mysql.connector.Error` al intentar conectar con credenciales y bases de datos falsas, devolviendo también un **HTTP 500**.

## 🛡️ 3. Consumo y Manejo Global de Excepciones (Spring Boot)

Para consumir la API de Python, se ha implementado el servicio `PythonApiService` utilizando `RestTemplate`. Cuando Python devuelve errores (404 o 500), `RestTemplate` lanza automáticamente una excepción `HttpClientErrorException`.

Para evitar que la aplicación colapse, se ha implementado un patrón de intercepción global:
* Se ha creado la clase `GlobalExceptionHandler` anotada con `@ControllerAdvice`.
* Esta clase "escucha" y captura excepciones específicas (`SQLException`, `HttpClientErrorException`, `ResourceAccessException`).
* Inyecta un título, un mensaje amigable y el detalle técnico real dentro del `Model`, y redirige el flujo hacia una vista de error personalizada.

## 🖥️ 4. Interfaz de Usuario (Frontend)

El frontend se ha construido utilizando **Thymeleaf** y **Bootstrap 5** para garantizar un diseño *responsive*. Destacan tres pantallas clave:
* **Login (`login.html`):** Autentica al usuario contra la base de datos MySQL usando contraseñas encriptadas con BCrypt.
* **Panel de Pruebas (`api-test.html`):** Contiene los botones de acción que disparan las peticiones hacia el controlador de Spring Boot.
* **Pantalla de Error Amigable (`error-amigable.html`):** Interfaz que muestra un mensaje tranquilizador al usuario y un botón colapsable para que los desarrolladores puedan visualizar el *log* técnico real devuelto por Python.

## ⚙️ 5. Instrucciones de Ejecución

Para levantar todo el ecosistema de microservicios, sitúate en la raíz del proyecto y ejecuta:

```bash
docker compose up -d --build
