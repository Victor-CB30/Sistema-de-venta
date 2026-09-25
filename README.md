# Sistema de Venta

Sistema de gestión comercial de escritorio para Windows, orientado a pequeñas y medianas empresas de Paraguay.

La especificación funcional y técnica Frozen se encuentra en `PROJECT_SPEC.md` y en la carpeta `docs/`.

---

## Estado del proyecto

| Fase | Descripción | Estado |
|------|-------------|--------|
| 1 | Análisis del sistema | Completada |
| 2 | Proyecto base y arquitectura | **Completada** |
| 3 | Base de datos y migraciones Flyway | Pendiente |

---

## Requisitos

- **JDK 21 LTS** (obligatorio)
- **Maven 3.9+**
- Windows 10 u 11 (objetivo de despliegue; el desarrollo funciona en Linux y macOS)

No se requiere instalar JavaFX ni MySQL: las dependencias se resuelven desde Maven Central.

Entorno verificado en esta fase: OpenJDK **21.0.12** LTS y Apache Maven **3.9.16**, ambos disponibles en el `PATH`.

> **Importante:** el proyecto **no** compila con Java 8 ni con Java 17.

Comprobar que Maven usa el JDK correcto:

```powershell
java -version
mvn -version   # debe indicar Java version: 21.x
```

Si Maven fuera a tomar otro JDK, `JAVA_HOME` debe apuntar a la instalación 21:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

---

## Comandos

| Comando | Descripción |
|---------|-------------|
| `mvn clean` | Elimina `target/` |
| `mvn compile` | Compila las fuentes |
| `mvn test` | Ejecuta las pruebas unitarias |
| `mvn clean verify` | Compila, prueba y empaqueta `target/sistema-venta.jar` |
| `mvn javafx:run` | **Ejecuta la aplicación** (abre la ventana) |
| `mvn javafx:run --debug` | Ejecuta con depurador JDWP en el puerto 5005 |

`mvn javafx:run` abre una ventana titulada **Sistema de Venta** con un mensaje de inicio correcto, la versión de la aplicación y el entorno de ejecución.

---

## Arquitectura

Dirección de dependencias permitida:

```
presentation -> application -> domain <- repository <- persistence
```

| Paquete | Responsabilidad | Puede depender de |
|---------|-----------------|-------------------|
| `py.com.sistemaventa.presentation` | JavaFX, FXML, CSS y controllers | `application` |
| `py.com.sistemaventa.application` | Casos de uso, permisos, transacciones, DTOs | `domain`, `repository` |
| `py.com.sistemaventa.domain` | Entidades, value objects, reglas y cálculos | nada |
| `py.com.sistemaventa.repository` | Interfaces orientadas al dominio | `domain` |
| `py.com.sistemaventa.persistence` | JDBC, SQL, mapeadores, HikariCP | `domain`, `repository` |
| `py.com.sistemaventa.bootstrap` | Composition root y punto de entrada | todas |
| `py.com.sistemaventa.config` | Carga de configuración transversal | — |
| `py.com.sistemaventa.logging` | Configuración de SLF4J + Logback | `config` |
| `py.com.sistemaventa.security` | Autenticación, RBAC, autorización (Fase 6) | — |
| `py.com.sistemaventa.validation` | Validación de entrada reutilizable | — |
| `py.com.sistemaventa.transaction` | Unidad de trabajo y demarcación | — |
| `py.com.sistemaventa.audit` | Auditoría inmutable (Fase 7) | — |
| `py.com.sistemaventa.reporting` | Reportes, PDF y Excel (Fase 25) | — |

### Reglas que no se deben romper

- `presentation` **nunca** contiene SQL, JDBC ni acceso directo a repositorios.
- `domain` **nunca** depende de JavaFX, JDBC, `persistence` ni `application`.
- `persistence` **nunca** toma decisiones de negocio.
- No hay dependencias circulares.
- El dinero se maneja con `BigDecimal` encapsulado en `Guaranies`; está prohibido `float` y `double`.
- Todo `PreparedStatement`; nunca concatenación de SQL.

Cada límite está documentado en el `package-info.java` del paquete correspondiente.

---

## Estructura del proyecto

```
Sistema-de-venta/
├── pom.xml
├── README.md
├── docs/
│   ├── DECISIONES_NEGOCIO.md
│   ├── FASE_1_ANALISIS.md
│   ├── FASE_2.md
│   └── ROADMAP.md
└── src/
    ├── main/
    │   ├── java/py/com/sistemaventa/
    │   │   ├── SistemaVentaApplication.java      Application JavaFX
    │   │   ├── application/                       Casos de uso
    │   │   ├── audit/                             (reservado)
    │   │   ├── bootstrap/                         Composition root
    │   │   ├── config/                            Configuración
    │   │   ├── domain/                            (reservado)
    │   │   ├── logging/                           Logging
    │   │   ├── persistence/                       (reservado)
    │   │   ├── presentation/                      JavaFX + FXML
    │   │   ├── reporting/                         (reservado)
    │   │   ├── repository/                        (reservado)
    │   │   ├── security/                          (reservado)
    │   │   ├── transaction/                       (reservado)
    │   │   └── validation/                        (reservado)
    │   └── resources/
    │       ├── application.properties
    │       ├── logback.xml
    │       └── py/com/sistemaventa/presentation/
    │           ├── MainView.fxml
    │           └── css/application.css
    └── test/
        ├── java/py/com/sistemaventa/...
        └── resources/logback-test.xml
```

Los paquetes marcados como *reservado* existen con `package-info.java` para fijar el contrato de cada capa sin introducir clases vacías.

### Finales de línea

Definidos en `.gitattributes` para evitar diffs espurios entre sistemas:

| Tipo | Fin de línea |
|------|--------------|
| `*.java`, `*.xml`, `*.fxml`, `*.css`, `*.properties`, `*.md`, `*.json`, `*.jsonc` | **LF** |
| `*.bat`, `*.cmd` | **CRLF** (lo requiere el intérprete de comandos de Windows) |
| Binarios (`*.jar`, `*.png`, `*.exe`, …) | sin conversión |

La regla general es `* text=auto eol=lf`. Si Git quedó con conversiones cacheadas de una configuración anterior, se corrige con:

```powershell
git add --renormalize .
```

---

## Dependencias

**Producción**

| Dependencia | Versión | Uso |
|-------------|---------|-----|
| `org.openjfx:javafx-base` | 21.0.12 | Runtime de JavaFX |
| `org.openjfx:javafx-graphics` | 21.0.12 | Gráficos y CSS |
| `org.openjfx:javafx-controls` | 21.0.12 | Controles de interfaz |
| `org.openjfx:javafx-fxml` | 21.0.12 | Cargador de vistas FXML |
| `org.slf4j:slf4j-api` | 2.0.20 | Fachada de logging |
| `ch.qos.logback:logback-classic` | 1.5.38 | Implementación de logging |

**Pruebas**

| Dependencia | Versión |
|-------------|---------|
| `org.junit.jupiter:junit-jupiter` | 5.14.4 |

**Plugins**

| Plugin | Versión |
|--------|---------|
| `maven-resources-plugin` | 3.3.1 |
| `maven-compiler-plugin` | 3.13.0 |
| `maven-surefire-plugin` | 3.5.6 |
| `maven-jar-plugin` | 3.4.2 |
| `openjfx:javafx-maven-plugin` | 0.0.8 |

**No incluidos todavía** (por fase): MySQL Connector/J, HikariCP, Flyway, JasperReports, Apache POI, BCrypt.

No se usa ni se permite Spring, Spring Boot, Hibernate, JPA ni Lombok.

---

## Configuración

`src/main/resources/application.properties`:

| Clave | Por defecto | Descripción |
|-------|--------------|-------------|
| `app.name` | `Sistema de Venta` | Nombre visible |
| `app.version` | *(desde el POM)* | Versión, por filtrado de Maven |
| `app.organization` | `py.com.sistemaventa` | Identificador del producto |
| `window.title` | `Sistema de Venta` | Título de la ventana |
| `window.width` | `960` | Ancho inicial |
| `window.height` | `640` | Alto inicial |
| `window.resizable` | `true` | Ventana redimensionable |
| `logging.level` | `INFO` | Nivel raíz: TRACE, DEBUG, INFO, WARN, ERROR |
| `logging.directory` | `logs` | Nombre del directorio de logs |
| `app.data.directory` | `.sistemaventa` | Directorio de datos, relativo al home del usuario |

### Logs

Se escriben en:

```
%USERPROFILE%\.sistemaventa\logs\sistema-venta.log
```

Nunca junto al ejecutable, porque en una instalación de Windows el directorio del programa puede ser de solo lectura. La rotación es diaria con tope de 10 MB por archivo, 30 archivos históricos y 500 MB totales.

`logging.level` se puede subir a `DEBUG` durante el desarrollo. En las pruebas, `src/test/resources/logback-test.xml` desactiva el appender de archivo.

---

## Pruebas

```powershell
mvn test
mvn clean verify
```

Las pruebas son **headless**: no abren el toolkit de JavaFX. El contrato entre `MainView.fxml` y `MainViewController` se valida por reflexión sobre los campos `@FXML` y por análisis del XML, de modo que un `fx:id` desincronizado se detecta en `mvn test` y no al abrir la ventana.

Reportes en `target/surefire-reports/`.

---

## Documentación

| Documento | Contenido |
|-----------|-----------|
| `PROJECT_SPEC.md` | Especificación funcional y técnica congelada |
| `docs/ROADMAP.md` | Roadmap de las 27 fases |
| `docs/DECISIONES_NEGOCIO.md` | Decisiones de negocio aprobadas |
| `docs/FASE_1_ANALISIS.md` | Análisis del sistema |
| `docs/FASE_2.md` | Documentación de la Fase 2 |

---

## Flujo de trabajo

El proyecto se desarrolla por fases, cada una en su propia rama `phase/N-descripcion`. Al terminar cada fase se ejecutan `mvn test` y `mvn clean verify`, se documenta el resultado en `docs/FASE_N.md` y se actualiza `docs/ROADMAP.md`. Las instrucciones completas están en `AGENTS.md`.
