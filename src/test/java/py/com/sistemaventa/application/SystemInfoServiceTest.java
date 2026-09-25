package py.com.sistemaventa.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SystemInfoService - informacion del entorno de ejecucion")
class SystemInfoServiceTest {

    private static final Instant INICIO = Instant.parse("2026-01-15T10:30:00Z");

    private static SystemInfoService service(String name, String version) {
        return SystemInfoService.ofFixed(name, version, "21.0.12", "21.0.12", INICIO);
    }

    @Test
    @DisplayName("Expone nombre, version y marca de inicio")
    void exposesEnvironmentData() {
        SystemInfoService service = service("Sistema de Venta", "0.1.0-SNAPSHOT");

        assertAll(
                () -> assertEquals("Sistema de Venta", service.appName()),
                () -> assertEquals("0.1.0-SNAPSHOT", service.appVersion()),
                () -> assertEquals("21.0.12", service.javaVersion()),
                () -> assertEquals("21.0.12", service.javafxVersion()),
                () -> assertEquals(INICIO, service.startedAt()));
    }

    @Test
    @DisplayName("summary() y environment() producen lineas legibles")
    void formatsSummaryAndEnvironment() {
        SystemInfoService service = service("Sistema de Venta", "0.1.0-SNAPSHOT");

        assertAll(
                () -> assertEquals("Sistema de Venta 0.1.0-SNAPSHOT", service.summary()),
                () -> assertEquals("Java 21.0.12 - JavaFX 21.0.12", service.environment()),
                () -> assertTrue(
                        service.startedAtFormatted().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
    }

    @Test
    @DisplayName("Los valores nulos y vacios reciben el valor por defecto")
    void nullAndBlankValuesFallBack() {
        SystemInfoService service = SystemInfoService.ofFixed(null, null, null, "   ", null);

        assertAll(
                () -> assertEquals("Sistema de Venta", service.appName()),
                () -> assertEquals("0.0.0", service.appVersion()),
                () -> assertEquals("desconocida", service.javaVersion()),
                () -> assertEquals("desconocida", service.javafxVersion()),
                () -> assertNotNull(service.startedAt()));
    }

    @Test
    @DisplayName("Las versiones se resuelven en cada lectura, no al construir")
    void versionsAreResolvedLazily() {
        // Es el caso real: javafx.version no existe hasta que arranca el toolkit.
        AtomicReference<String> javafxVersion = new AtomicReference<>(null);
        SystemInfoService service = new SystemInfoService(
                "Sistema de Venta",
                "0.1.0-SNAPSHOT",
                () -> System.getProperty("java.version"),
                javafxVersion::get,
                INICIO);

        assertEquals("desconocida", service.javafxVersion());

        javafxVersion.set("21.0.12");

        assertEquals("21.0.12", service.javafxVersion());
        assertEquals("JavaFX 21.0.12", service.environment().substring(service.environment().indexOf("- ") + 2));
    }

    @Test
    @DisplayName("Un supplier nulo se reemplaza por un valor por defecto")
    void nullSuppliersFallBack() {
        SystemInfoService service = new SystemInfoService("Sistema de Venta", "1.0.0", null, null, INICIO);

        assertAll(
                () -> assertEquals("desconocida", service.javaVersion()),
                () -> assertEquals("desconocida", service.javafxVersion()));
    }
}
