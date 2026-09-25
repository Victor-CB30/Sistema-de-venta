# FASE 2 — Proyecto base y arquitectura

**Estado:** Completada.
**Rama:** `phase/2-project-base`
**Fecha:** 2026-09-25

---

## 1. Objetivo

Crear únicamente la base técnica del proyecto Java: un proyecto Maven funcional sobre Java 21 y JavaFX 21, con la arquitectura por capas declarada en `PROJECT_SPEC.md` preparada, logging configurado, una aplicación JavaFX mínima ejecutable y pruebas automatizadas.

Esta fase no introduce comportamiento de negocio ni acceso a datos.

---

## 2. Alcance

### Incluido

- Proyecto Maven con Java 21 (`maven.compiler.release=21`).
- JavaFX 21 con FXML y CSS.
- Estructura inicial de paquetes: `presentation`, `application`, `domain`, `repository`, `persistence`.
- Componentes transversales: `config`, `logging`, `security`, `validation`, `transaction`, `audit`, `reporting`.
- Composition root (`bootstrap`) y punto de entrada.
- Logging con SLF4J + Logback (consola y archivo con rotación).
- Aplicación JavaFX mínima ejecutable con `mvn javafx:run`.
- JUnit 5 y 25 pruebas automatizadas headless.
- `.gitignore`, `.gitattributes`, configuración de recursos y README técnico.
- Documentación de la fase y actualización del roadmap.

### Excluido deliberadamente

MySQL, MySQL Connector/J, Flyway, HikariCP, repositorios JDBC, autenticación, usuarios, clientes, productos, inventario, ventas, caja, pedidos, delivery, reportes, JasperReports, Apache POI, BCrypt y cualquier regla de negocio.

Ninguna dependencia de base de datos está declarada en `pom.xml`.

---

## 3. Arquitectura

Dirección de dependencias declarada en `PROJECT_SPEC.md`:

```
presentation -> application -> domain <- repository <- persistence
```

`bootstrap` es el único paquete que conoce todas las capas: es el composition root. Ningún otro paquete ensambla objetos.

| Capa | Paquete | Contenido en esta fase |
|------|---------|------------------------|
| presentation | `py.com.sistemaventa.presentation` | `MainViewController`, `MainView.fxml`, `css/application.css` |
| application | `py.com.sistemaventa.application` | `SystemInfoService` |
| domain | `py.com.sistemaventa.domain` | Solo `package-info.java` (reservado, Fase 5) |
| repository | `py.com.sistemaventa.repository` | Solo `package-info.java` (reservado, Fase 4) |
| persistence | `py.com.sistemaventa.persistence` | Solo `package-info.java` (reservado, Fase 4) |
| bootstrap | `py.com.sistemaventa.bootstrap` | `Bootstrap`, `ApplicationComponents` |
| config | `py.com.sistemaventa.config` | `AppConfig` |
| logging | `py.com.sistemaventa.logging` | `LoggingConfigurator` |
| security | `py.com.sistemaventa.security` | Solo `package-info.java` (reservado, Fase 6) |
| validation | `py.com.sistemaventa.validation` | Solo `package-info.java` (reservado) |
| transaction | `py.com.sistemaventa.transaction` | Solo `package-info.java` (reservado, Fase 4) |
| audit | `py.com.sistemaventa.audit` | Solo `package-info.java` (reservado, Fase 7) |
| reporting | `py.com.sistemaventa.reporting` | Solo `package-info.java` (reservado, Fase 25) |

### Decisión sobre los paquetes reservados

Los paquetes sin implementación propia **no se dejaron vacíos con clases-Marcador**. Cada uno contiene un `package-info.java` que fija por Javadoc las dependencias permitidas y las prohibiciones del paquete.

Motivo: `package-info.java` no declara ninguna clase, no genera bytecode en tiempo de ejecución y no infla el proyecto con clases sin comportamiento, pero sí documenta el límite de la capa en el mismo lugar donde alguien va a escribir el código. Cuando la fase correspondiente asigne una primera clase real, el contrato ya está escrito y es verificable.

---

## 4. Estructura de paquetes

```
py.com.sistemaventa
├── SistemaVentaApplication        Application JavaFX: escena, FXML, CSS, ventana
├── package-info                   Descripción de la arquitectura y del árbol de paquetes
├── application
│   ├── SystemInfoService          Nombre/versión de la app, versiones de Java y JavaFX, hora de inicio
│   └── package-info
├── audit
│   └── package-info               Reservado, Fase 7
├── bootstrap
│   ├── Bootstrap                  main(): config -> logging -> composición -> launch
│   ├── ApplicationComponents      record de inyección: (AppConfig, SystemInfoService)
│   └── package-info
├── config
│   ├── AppConfig                  Carga tipada de application.properties
│   └── package-info
├── domain
│   └── package-info               Reservado, Fase 5
├── logging
│   ├── LoggingConfigurator        Directorio de logs + nivel raíz de Logback
│   └── package-info
├── persistence
│   └── package-info               Reservado, Fase 4
├── presentation
│   ├── MainViewController         Controller FXML, sin lógica de negocio
│   └── package-info
├── reporting
│   └── package-info               Reservado, Fase 25
├── repository
│   └── package-info               Reservado, Fase 4
├── security
│   └── package-info               Reservado, Fase 6
├── transaction
│   └── package-info               Reservado, Fase 4
└── validation
    └── package-info               Reservado
```

Recursos bajo `src/main/resources`:

```
application.properties                                  Configuración técnica (filtrada por Maven)
logback.xml                                             Appenders de consola y archivo con rotación
py/com/sistemaventa/presentation/MainView.fxml          Vista mínima de arranque
py/com/sistemaventa/presentation/css/application.css    Hoja de estilos
```

---

## 5. Dependencias

### Producción

| Dependencia | Versión | Motivo |
|-------------|---------|--------|
| `org.openjfx:javafx-base` | 21.0.12 | Runtime de JavaFX |
| `org.openjfx:javafx-graphics` | 21.0.12 | Gráficos, escena y CSS |
| `org.openjfx:javafx-controls` | 21.0.12 | Controles de interfaz |
| `org.openjfx:javafx-fxml` | 21.0.12 | Cargador de vistas FXML |
| `org.slf4j:slf4j-api` | 2.0.20 | Fachada de logging |
| `ch.qos.logback:logback-classic` | 1.5.38 | Implementación de logging |

Se eligieron las últimas versiones estables de la línea JavaFX 21 (21.0.12) y de SLF4J 2 / Logback 1.5 verificadas contra Maven Central. El clasificador de plataforma de los artefactos JavaFX lo resuelve automáticamente el POM padre `org.openjfx:javafx`, por eso no se declara `win` de forma explícita.

### Pruebas

| Dependencia | Versión |
|-------------|---------|
| `org.junit.jupiter:junit-jupiter` | 5.14.4 |

### Plugins

| Plugin | Versión | Motivo |
|--------|---------|--------|
| `maven-resources-plugin` | 3.3.1 | `propertiesEncoding=UTF-8` para el filtrado |
| `maven-compiler-plugin` | 3.13.0 | Compilación con `release=21` |
| `maven-surefire-plugin` | 3.5.6 | Ejecución de JUnit 5 |
| `maven-jar-plugin` | 3.4.2 | `Main-Class` en el manifiesto |
| `org.openjfx:javafx-maven-plugin` | 0.0.8 | `mvn javafx:run` |

### Dependencias prohibidas

No se usa ni se declara Spring, Spring Boot, Hibernate, JPA ni Lombok.

---

## 6. Decisiones tomadas en esta fase

1. **GroupId `py.com.sistemaventa` y artefacto `sistema-venta`**, tal como se solicitó. `finalName` en `sistema-venta`.

2. **Un solo módulo Maven.** El sistema es una aplicación de escritorio, no una biblioteca; los módulos adicionales solo se justificarían si hiciera falta una distribución con distintos perfiles. Se evita la complejidad prematura.

3. **`ApplicationComponents` como record de inyección en lugar de un contenedor de dependencias.** Un framework de DI sería una dependencia prohibida y un contenedor propio sería infraestructura prematura. Con un record, `presentation` recibe colaboradores ya construidos por el composition root, que es exactamente lo que exige `AGENTS.md`.

4. **Literales visibles con acentos en Java, ASCII en el FXML.** El texto que ve el usuario se asigna en `MainViewController`; el FXML queda como esqueleto. Así los literales acentuados viven en fuentes UTF-8, donde la codificación está garantizada por `project.build.sourceEncoding`, y no en un recurso XML que depende de cómo FXMLLoader interprete la declaración de codificación.

5. **El logger no se guarda en campos `static final`.** Un campo estático se inicializa al cargar la clase, antes de que exista la propiedad de sistema `LOG_DIR`, y Logback lee `logback.xml` exactamente en ese momento. El resultado observado fue que los logs se escribían en `./logs/` relativo al directorio de trabajo en lugar del directorio de datos del usuario. Se usa un método `log()` perezoso. Ver sección 10.

6. **Las versiones de Java y JavaFX se resuelven de forma perezosa.** La propiedad de sistema `javafx.version` no existe hasta que el toolkit de JavaFX se inicializa, es decir, después de que el composition root ya construyó el servicio. Se almacena como `Supplier<String>` y se lee en cada llamada, en lugar de capturarse al construir.

7. **`app.version` por filtrado de Maven.** `application.properties` se procesa con `${project.version}`, de modo que la versión mostrada en pantalla y la del POM no pueden divergir. El filtrado se restringe a ese único archivo porque `logback.xml` usa su propia sintaxis `${...}` y un filtrado global lo corrompería.

8. **Recursos bajo el classpath con el mismo path del paquete** (`py/com/sistemaventa/presentation/...`) en lugar de una raíz `fxml/` o `css/` suelta. Permite resolverlos con `MainViewController.class.getResource(...)` y deja claro a qué capa pertenecen.

9. **`logback-test.xml` en `src/test/resources`.** Desactiva el appender de archivo durante las pruebas. Sin esto, Logback mantiene abierto un archivo dentro del directorio temporal de `@TempDir` y en Windows el borrado del directorio falla, rompiendo la prueba.

10. **`Scene.setTitle` no se usa.** El título pertenece al `Stage`; el `Scene` no expone ese método.

11. **Finales de línea normalizados con `.gitattributes`.** El repositorio se desarrolla en Windows pero el proyecto es portable. La regla general es `* text=auto eol=lf`, más reglas explícitas para `*.java`, `*.xml`, `*.fxml`, `*.css`, `*.properties`, `*.md`, `*.json` y `*.jsonc`. `*.bat` y `*.cmd` quedan en `eol=crlf` porque el intérprete de comandos de Windows los necesita. Los binarios se declaran `binary`. Se aplicó además `git add --renormalize` para reindexar los archivos ya versionados, ya que el repositorio tenía `core.autocrlf=true` y arrastraba conversiones cacheadas.

---

## 7. Archivos creados

### Configuración del proyecto

| Archivo | Descripción |
|---------|-------------|
| `pom.xml` | Proyecto Maven: Java 21, JavaFX 21, SLF4J, Logback, JUnit 5, Surefire, javafx-maven-plugin |
| `.gitignore` | Maven, IDEs, logs, datos locales, `application-local.properties`, artefactos de jpackage |
| `.gitattributes` | Normalización de finales de línea: LF para texto, CRLF para `*.bat` y `*.cmd` |
| `src/main/resources/application.properties` | Configuración técnica (filtrada por Maven) |
| `src/main/resources/logback.xml` | Appenders de consola y archivo con rotación diaria y por tamaño |
| `src/test/resources/logback-test.xml` | Configuración de Logback para pruebas (solo consola) |

### Código principal

| Archivo | Descripción |
|---------|-------------|
| `src/main/java/py/com/sistemaventa/SistemaVentaApplication.java` | `Application` JavaFX: carga FXML, aplica CSS, configura la ventana |
| `src/main/java/py/com/sistemaventa/package-info.java` | Descripción de la arquitectura y del árbol de paquetes |
| `src/main/java/py/com/sistemaventa/bootstrap/Bootstrap.java` | `main()`: config → logging → composición → `Application.launch` |
| `src/main/java/py/com/sistemaventa/bootstrap/ApplicationComponents.java` | `record` de inyección `(AppConfig, SystemInfoService)` |
| `src/main/java/py/com/sistemaventa/bootstrap/package-info.java` | Contrato del composition root |
| `src/main/java/py/com/sistemaventa/config/AppConfig.java` | Carga y acceso tipado a `application.properties` |
| `src/main/java/py/com/sistemaventa/config/package-info.java` | Contrato del componente de configuración |
| `src/main/java/py/com/sistemaventa/logging/LoggingConfigurator.java` | Directorio de logs y nivel raíz de Logback |
| `src/main/java/py/com/sistemaventa/logging/package-info.java` | Contrato del componente de logging |
| `src/main/java/py/com/sistemaventa/application/SystemInfoService.java` | Información del entorno de ejecución, sin dependencia de JavaFX |
| `src/main/java/py/com/sistemaventa/application/package-info.java` | Contrato de la capa application |
| `src/main/java/py/com/sistemaventa/presentation/MainViewController.java` | Controller FXML |
| `src/main/java/py/com/sistemaventa/presentation/package-info.java` | Contrato de la capa presentation |
| `src/main/java/py/com/sistemaventa/domain/package-info.java` | Contrato reservado de `domain` |
| `src/main/java/py/com/sistemaventa/repository/package-info.java` | Contrato reservado de `repository` |
| `src/main/java/py/com/sistemaventa/persistence/package-info.java` | Contrato reservado de `persistence` |
| `src/main/java/py/com/sistemaventa/security/package-info.java` | Contrato reservado de `security` |
| `src/main/java/py/com/sistemaventa/validation/package-info.java` | Contrato reservado de `validation` |
| `src/main/java/py/com/sistemaventa/transaction/package-info.java` | Contrato reservado de `transaction` |
| `src/main/java/py/com/sistemaventa/audit/package-info.java` | Contrato reservado de `audit` |
| `src/main/java/py/com/sistemaventa/reporting/package-info.java` | Contrato reservado de `reporting` |

### Recursos de interfaz

| Archivo | Descripción |
|---------|-------------|
| `src/main/resources/py/com/sistemaventa/presentation/MainView.fxml` | Vista mínima: `VBox` con título, mensaje de inicio, versión, entorno y fase |
| `src/main/resources/py/com/sistemaventa/presentation/css/application.css` | Clases `root`, `titulo`, `mensaje`, `detalle`, `estado` |

### Pruebas

| Archivo | Cobertura |
|---------|-----------|
| `src/test/java/py/com/sistemaventa/config/AppConfigTest.java` | Carga del recurso empaquetado, recurso inexistente, valores por defecto, valores en blanco, entero inválido, booleanos |
| `src/test/java/py/com/sistemaventa/logging/LoggingConfiguratorTest.java` | Creación del directorio, directorio configurado, aplicación del nivel raíz, nivel inválido, directorio de datos por defecto |
| `src/test/java/py/com/sistemaventa/application/SystemInfoServiceTest.java` | Datos del entorno, formateo de `summary`/`environment`/fecha, valores nulos y vacíos, resolución perezosa de versiones |
| `src/test/java/py/com/sistemaventa/bootstrap/ApplicationComponentsTest.java` | Composición desde la configuración, lectura de versiones del sistema, rechazo de nulos |
| `src/test/java/py/com/sistemaventa/presentation/MainViewResourcesTest.java` | Recursos empaquetados, FXML bien formado y `fx:controller` correcto, correspondencia bidireccional entre `fx:id` y campos `@FXML`, cobertura de clases de estilo del CSS |

### Documentación

| Archivo | Descripción |
|---------|-------------|
| `docs/FASE_2.md` | Este documento |

---

## 8. Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `README.md` | Reemplazado por el README técnico: requisitos, comandos, arquitectura, estructura, dependencias, configuración, logs, pruebas y flujo de trabajo |
| `docs/ROADMAP.md` | Fase 2 marcada como completada |

---

## 9. Comandos utilizados

```powershell
mvn test
mvn clean verify
mvn javafx:run
git status
git diff
```

### Nota de entorno

El entorno de desarrollo utiliza:

- OpenJDK **21.0.12** LTS
- Apache Maven **3.9.16**

Ambos están disponibles en el `PATH`, por lo que los comandos se ejecutan directamente. **No se modificó ninguna variable de sistema ni el `PATH` del usuario.**

Si en otra máquina Maven detectara un JDK distinto de 21, hay que apuntar `JAVA_HOME` a la instalación 21 antes de compilar:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

---

## 10. Pruebas

### Pruebas automatizadas

```
Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

| Suite | Tests | Resultado |
|-------|-------|-----------|
| `SystemInfoServiceTest` | 5 | OK |
| `ApplicationComponentsTest` | 4 | OK |
| `AppConfigTest` | 6 | OK |
| `LoggingConfiguratorTest` | 5 | OK |
| `MainViewResourcesTest` | 5 | OK |

Las pruebas son headless: ninguna inicia el toolkit de JavaFX, por lo que `mvn test` funciona sin sesión gráfica.

`MainViewResourcesTest` es la prueba que evita la mayor parte de los errores típicos de JavaFX: valida por reflexión y análisis del XML que cada `fx:id` del FXML tiene su campo `@FXML` y viceversa, y que cada `styleClass` usada está definida en el CSS. Sin ella, esa desincronización solo aparecería al abrir la ventana.

### Verificación manual de `mvn javafx:run`

Resultado de la ejecución real sobre el código final, tomado del archivo de log generado:

```
2026-09-25 15:18:39.426 INFO  [main] p.c.sistemaventa.bootstrap.Bootstrap - Iniciando Sistema de Venta 0.1.0-SNAPSHOT
2026-09-25 15:18:39.473 INFO  [main] p.c.sistemaventa.bootstrap.Bootstrap - Configuracion cargada desde /application.properties
2026-09-25 15:18:39.473 INFO  [main] p.c.sistemaventa.bootstrap.Bootstrap - Directorio de logs: C:\Users\Córdoba\.sistemaventa\logs
2026-09-25 15:18:39.504 INFO  [main] p.c.sistemaventa.bootstrap.Bootstrap - Java 21.0.12
2026-09-25 15:18:45.870 INFO  [JavaFX Application Thread] p.c.SistemaVentaApplication - Entorno: Java 21.0.12 - JavaFX 21.0.12
2026-09-25 15:18:45.875 INFO  [JavaFX Application Thread] p.c.SistemaVentaApplication - Ventana principal iniciada: Sistema de Venta (960x640)
```

Verificado:

- El proceso permanece vivo con la ventana abierta, sin excepciones ni salida de error.
- El título de la ventana es `Sistema de Venta`.
- FXML y CSS se cargan correctamente.
- El log se escribe en `%USERPROFILE%\.sistemaventa\logs\sistema-venta.log`.
- No se crea ningún directorio `logs/` dentro del proyecto.
- JavaFX 21.0.12 se reporta correctamente.

### `mvn clean verify`

```
[INFO] Building jar: C:\Users\Córdoba\Desktop\Sistema-de-venta\target\sistema-venta.jar
[INFO] Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Manifiesto del JAR generado:

```
Manifest-Version: 1.0
Created-By: Maven JAR Plugin 3.4.2
Build-Jdk-Spec: 21
Main-Class: py.com.sistemaventa.bootstrap.Bootstrap
```

---

## 11. Problemas encontrados y soluciones

| # | Problema | Causa | Solución |
|---|----------|-------|----------|
| 1 | `package ch.qos.logback.classic does not exist` | `logback-classic` estaba declarado en scope `runtime`, pero `LoggingConfigurator` usa su API (`Level`, `Logger`) | Se cambió a scope `compile` |
| 2 | `cannot find symbol: method setTitle(String) location: variable scene` | `Scene` no expone `setTitle`; el título pertenece al `Stage` | Se eliminó la llamada; el título se aplica solo en el `Stage` |
| 3 | `cannot find symbol: variable SistemaVentaApplication` en `Bootstrap` | Faltaba el `import py.com.sistemaventa.SistemaVentaApplication` | Se agregó el import |
| 4 | `No se encontro el recurso de configuracion: /application.properties` | `ClassLoader.getResourceAsStream` no acepta barra inicial; el recurso era correcto en `target/classes` | Se cambió a `AppConfig.class.getResourceAsStream(...)` |
| 5 | `expected: <root> but was: <VBox>` | Aserción mal escrita en el test: `root` es la clase de estilo, no el nombre del elemento raíz | Se corrigió la aserción a `VBox` |
| 6 | `el FXML debe usar clases de estilo` — conjunto vacío | `styleClass` es un atributo de propiedad, no un atributo del espacio de nombres `fx:`; se leía con `getAttributeNS(FX_NAMESPACE, ...)` | Se separó en dos helpers: `fxAttributeValues` y `plainAttributeValues` |
| 7 | `JUnit Failed to close extension context` en `LoggingConfiguratorTest` | Logback, configurado globalmente, mantenía abierto el archivo de log dentro del directorio temporal de `@TempDir`; en Windows el archivo abierto impide borrarlo | Se agregó `src/test/resources/logback-test.xml` sin appender de archivo, y un `@AfterEach` restaura el nivel raíz a `INFO` |
| 8 | **Los logs se escribían en `./logs/` del proyecto, no en el directorio de datos** | Un campo `private static final Logger LOG` en `Bootstrap` se inicializa al cargar la clase, y esa inicialización **es** la que dispara la auto-configuración de Logback. En ese instante la propiedad `LOG_DIR` todavía no existía, así que `logback.xml` resolvía `${LOG_DIR:-logs}` a `logs` relativo al directorio de trabajo | Se eliminaron todos los campos `Logger` estáticos. `Bootstrap` obtiene el logger como variable local después de `LoggingConfigurator.configure(...)` y `SistemaVentaApplication` usa un método `log()` perezoso |
| 9 | El log reportaba `JavaFX null` | `Bootstrap` registraba `System.getProperty("javafx.version")` antes de que arrancara el toolkit de JavaFX, que es quien publica esa propiedad | `SystemInfoService` almacena `Supplier<String>` y resuelve las versiones en cada lectura, dentro de `start()`, cuando el toolkit ya está inicializado |

Los problemas 8 y 9 solo se detectaron ejecutando la aplicación real; ninguna de las pruebas automatizadas los cubría. Conviene agregar en fases posteriores una prueba de humo de arranque cuando exista la infraestructura de CI con sesión gráfica.

---

## 12. Verificaciones de alcance

| Verificación | Estado |
|--------------|--------|
| `mvn test` pasa | Sí — 25/25 |
| `mvn clean verify` pasa | Sí — BUILD SUCCESS |
| `mvn javafx:run` abre la ventana | Sí — verificado en ejecución real |
| Título de ventana `Sistema de Venta` | Sí |
| FXML y CSS se cargan | Sí |
| `.gitignore` cubre `target/`, IDEs y logs | Sí |
| `.gitattributes` fuerza LF en texto y CRLF en `*.bat`/`*.cmd` | Sí |
| Ningún archivo de texto queda con CRLF en el working tree | Sí — 39 revisados, 0 con CRLF |
| Ninguna dependencia de base de datos en el POM | Sí |
| Ninguna clase de negocio creada | Sí |
| `target/` y `logs/` no aparecen en `git status` | Sí |
| No se modificó `PROJECT_SPEC.md` | Sí |
| No se hizo push, merge ni trabajo sobre `main` | Sí |

---

## 13. Estado final

La Fase 2 está **completada**. El proyecto compila, las 25 pruebas pasan, `mvn clean verify` produce un JAR ejecutable y `mvn javafx:run` abre la ventana mínima con FXML y CSS.

La arquitectura por capas de `PROJECT_SPEC.md` está materializada en el árbol de paquetes y cada frontera está documentada en su `package-info.java`. Los componentes transversales que todavía no tienen implementación (`security`, `validation`, `transaction`, `audit`, `reporting`) y las capas `domain`, `repository` y `persistence` están reservados y sin clases vacías.

No hay ninguna regla de negocio, entidad, tabla ni conexión de base de datos en el proyecto.

---

## 14. Próximos pasos

**Fase 3 — Base de datos y migraciones Flyway.**

Se espera:

- MySQL 8.4 LTS y el driver `mysql-connector-j`.
- Flyway como único mecanismo de esquema.
- Migración inicial `V1__baseline.sql` y el historial de versiones.
- Diseño de tablas alineado con `PROJECT_SPEC.md`: `Documento` como entidad base, `Guaranies` en `DECIMAL(15,0)`, `Cantidad` en `DECIMAL(12,3)`, porcentajes en `DECIMAL(5,2)`, costos en `DECIMAL(15,4)`.
- Separación de usuarios de base de datos: uno de aplicación con privilegios mínimos y otro para migraciones, con credenciales fuera del código.

Decisión pendiente antes de empezar la Fase 3: la biblioteca concreta que PROVIDe BCrypt y el artifact a declarar en el POM. `PROJECT_SPEC.md` fija BCrypt con costo 12, pero no el origen de la implementación; conviene resolverlo al abrir la fase para no bloquear la Fase 6.
