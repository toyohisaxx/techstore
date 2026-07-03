# TechStore Chile — API REST desplegada en AWS

API REST de gestión de productos para la tienda ficticia TechStore Chile, desarrollada con Spring Boot y desplegada en una arquitectura nativa en la nube sobre AWS (ECS Fargate, RDS PostgreSQL, SQS, Lambda, API Gateway).

## Arquitectura

```
Cliente (Postman) 
   → API Gateway (HTTP API, público)
   → Application Load Balancer (techstore-alb2)
   → Amazon ECS Fargate (techstore-service, contenedor techstore-api)
   → Amazon RDS PostgreSQL (techstore-db)
   → Amazon SQS (techstore-audit-queue) → AWS Lambda (techstore-audit-logger) → CloudWatch Logs
```

Cada operación de escritura (POST, PUT, DELETE) sobre el catálogo de productos publica de forma asíncrona un evento de auditoría en la cola SQS `techstore-audit-queue`. Esa cola dispara automáticamente la función Lambda `techstore-audit-logger`, que registra la transacción en Amazon CloudWatch Logs, garantizando trazabilidad sin acoplar el flujo de escritura del microservicio a la lógica de auditoría.

## Componentes desplegados

| Componente | Recurso AWS | Nombre |
|---|---|---|
| Contenedor de la API | ECS Fargate | `techstore-cluster` / `techstore-service` |
| Registro de imágenes | ECR | `techstore-api` |
| Base de datos | RDS PostgreSQL | `techstore-db` |
| Cola de auditoría | SQS | `techstore-audit-queue` |
| Función serverless | Lambda | `techstore-audit-logger` |
| Balanceador de carga | ALB | `techstore-alb2` |
| Punto de entrada público | API Gateway | `techstore-api-gateway` |
| CI/CD | GitHub Actions | `.github/workflows/deploy.yml` |

## Escalabilidad, disponibilidad y mantenibilidad en ECS Fargate

### Escalado de réplicas en caliente

El servicio `techstore-service` tiene configurado **ECS Service Auto Scaling** con una política de tipo *Target Tracking* basada en la métrica `ECSServiceAverageCPUUtilization`, con un valor objetivo de **70%**:

- **Mínimo de tareas:** 1
- **Máximo de tareas:** 3
- **Métrica de escalado:** utilización promedio de CPU del servicio

Cuando la utilización de CPU promedio del servicio supera el 70% sostenido, ECS lanza automáticamente tareas adicionales (hasta el máximo configurado) sin intervención manual. Cuando la carga baja, ECS retira tareas de forma controlada, respetando el mínimo configurado. A diferencia de Docker Compose en un entorno local —donde el número de contenedores es fijo y cualquier escalado requiere ejecutar manualmente `docker compose up --scale`— en ECS Fargate el escalado es reactivo, automático y basado en métricas reales de CloudWatch, sin que el desarrollador tenga que intervenir ni reiniciar el servicio.

### Políticas de reinicio y disponibilidad

ECS Fargate gestiona el ciclo de vida de las tareas como un orquestador declarativo: el servicio define un número **deseado** de tareas, y ECS se encarga de mantenerlo constantemente. Si una tarea falla el *health check* configurado en el Target Group (`GET /health`, esperando `200 OK`), o si el proceso del contenedor termina inesperadamente, ECS:

1. Marca la tarea como no saludable y la retira del Target Group del ALB (dejando de enrutarle tráfico).
2. Termina la tarea fallida.
3. Lanza automáticamente una tarea de reemplazo para volver al número deseado.

Esto ocurre sin intervención humana y sin downtime perceptible para el cliente, siempre que exista al menos una tarea sana disponible durante la transición — algo que en un entorno local con Docker Compose no ocurre de forma nativa (un contenedor caído se queda caído hasta que alguien lo reinicie manualmente, salvo que se use `restart: always`, que tampoco reemplaza el contenedor detrás de un balanceador de forma automática).

### Límites de CPU y memoria

La Task Definition `techstore-task` define explícitamente:

- **CPU:** 256 unidades (0.25 vCPU)
- **Memoria:** 512 MiB (0.5 GB)

Estos límites son reservados y garantizados por AWS Fargate para cada tarea — a diferencia de un entorno local, donde todos los contenedores comparten los mismos recursos físicos del host sin aislamiento garantizado, y un contenedor con fuga de memoria puede degradar el rendimiento de los demás. En Fargate, cada tarea corre en su propia unidad de cómputo aislada, con esos límites aplicados de forma estricta por el hipervisor subyacente, lo que facilita predecir costos y comportamiento bajo carga.

### Mantenibilidad

El uso de **Task Definitions versionadas** (cada cambio genera una nueva revisión, ej. `techstore-task:2`) permite hacer rollback inmediato a una versión anterior si un despliegue introduce un error, sin necesidad de reconstruir la imagen. Combinado con el pipeline de CI/CD (`deploy.yml`), cada `push` a `main` reconstruye la imagen, la publica en ECR y fuerza un nuevo despliegue del servicio (`--force-new-deployment`), aplicando un *rolling update*: ECS lanza las tareas nuevas y solo retira las antiguas una vez que las nuevas pasan el health check, evitando caídas del servicio durante el despliegue.

## Pipeline CI/CD

El archivo `.github/workflows/deploy.yml` se activa en cada `push` a la rama `main` y ejecuta, en orden:

1. **Checkout** del código
2. **Configuración de Java 17**
3. **Ejecución de tests** (`mvn clean test`) — valida la lógica de generación y validación de tokens JWT (`JwtUtilTest`) sin depender de infraestructura externa (base de datos, SQS), por lo que puede ejecutarse de forma aislada en el runner de GitHub Actions
4. **Compilación** (`mvn package -DskipTests`, reutilizando el resultado de los tests ya ejecutados)
5. **Configuración de credenciales AWS** (usando `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` y `AWS_SESSION_TOKEN` almacenados como GitHub Secrets — credenciales temporales de AWS Academy)
6. **Login en Amazon ECR**
7. **Build y push de la imagen Docker** con la etiqueta `latest`
8. **Actualización del servicio ECS** (`aws ecs update-service --force-new-deployment`), que dispara el *rolling update* descrito arriba

## Variables de entorno del contenedor

La aplicación Spring Boot lee su configuración desde variables de entorno inyectadas por la Task Definition (no hardcodeadas en el código), lo que permite usar la misma imagen Docker en distintos entornos (local, staging, producción):

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` — conexión a RDS PostgreSQL
- `SQS_QUEUE_URL` — URL de la cola de auditoría: `https://sqs.us-east-1.amazonaws.com/652155226718/techstore-audit-queue`

## Seguridad

- Los endpoints del catálogo están protegidos con autenticación JWT (`JwtFilter`, `SecurityConfig`).
- El endpoint `/health` está expuesto sin autenticación exclusivamente para el *health check* del Application Load Balancer.
- Las tareas ECS solo son accesibles a través del ALB; no exponen IP pública directa para tráfico de aplicación..
