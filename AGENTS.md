# Sistema de Venta - Instrucciones para agentes

## Fuente de verdad

Antes de modificar código:

1. Leer completamente `PROJECT_SPEC.md`.
2. Leer `docs/ROADMAP.md`.
3. Leer la documentación de la fase actual.
4. Inspeccionar el repositorio.
5. Ejecutar `git status`.

`PROJECT_SPEC.md` contiene las decisiones técnicas y funcionales aprobadas.

No modificar estas decisiones sin autorización.

## Tecnologías obligatorias

- Java 21 LTS
- JavaFX 21
- Maven
- MySQL 8.4 LTS
- JDBC
- HikariCP
- Flyway
- SLF4J + Logback
- JUnit 5

No introducir:

- Spring
- Spring Boot
- Hibernate
- JPA
- Lombok

salvo autorización explícita.

## Arquitectura

Respetar:

presentation -> application -> domain <- repository <- persistence

### presentation
JavaFX, FXML, CSS y controllers.

Prohibido:
- SQL
- JDBC
- acceso directo a repositories
- lógica de negocio compleja

### application
Casos de uso, servicios, transacciones, permisos y coordinación.

### domain
Entidades, objetos de valor, reglas y cálculos.

No depende de JavaFX, JDBC ni persistence.

### repository
Interfaces orientadas al dominio.

### persistence
JDBC, SQL, mapeadores y repositorios concretos.

## Reglas de trabajo

Trabajar únicamente sobre la fase solicitada.

No implementar anticipadamente fases futuras.

No ampliar el MVP.

Antes de modificar:
- inspeccionar el repositorio;
- revisar la rama;
- presentar un plan breve.

Después de modificar:
- ejecutar `mvn test`;
- ejecutar `mvn clean verify`;
- revisar `git diff`;
- documentar la fase.

## Git

No trabajar directamente en `main`.

Cada fase usa su propia rama.

No ejecutar:
- `git push --force`
- `git reset --hard`
- merge automático a main

No iniciar la siguiente fase automáticamente.

## Documentación

Cada fase terminada debe generar:

`docs/FASE_N.md`

incluyendo:
- objetivo;
- alcance;
- archivos creados;
- archivos modificados;
- decisiones;
- pruebas;
- resultados;
- problemas encontrados;
- próximos pasos.

Actualizar también `docs/ROADMAP.md`.

## Regla crítica

Si una solicitud contradice `PROJECT_SPEC.md`, detener esa parte del trabajo y explicarlo antes de modificar la arquitectura, base de datos, dinero, stock, seguridad o auditoría.