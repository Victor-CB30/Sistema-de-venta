package py.com.sistemaventa.bootstrap;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Properties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import py.com.sistemaventa.config.AppConfig;

@DisplayName("ApplicationComponents - composition root")
class ApplicationComponentsTest {

    @Test
    @DisplayName("create() arma los servicios con los datos de la configuracion")
    void createBuildsServicesFromConfig() {
        Properties properties = new Properties();
        properties.setProperty("app.name", "Sistema de Venta");
        properties.setProperty("app.version", "0.1.0-SNAPSHOT");
        AppConfig config = AppConfig.of(properties);

        ApplicationComponents components = ApplicationComponents.create(config);

        assertAll(
                () -> assertEquals(config, components.config()),
                () -> assertEquals("Sistema de Venta", components.systemInfo().appName()),
                () -> assertEquals("0.1.0-SNAPSHOT", components.systemInfo().appVersion()),
                () -> assertNotNull(components.systemInfo().startedAt()));
    }

    @Test
    @DisplayName("La version de Java se obtiene de la propiedad del sistema")
    void readsJavaVersionFromSystemProperty() {
        ApplicationComponents components = ApplicationComponents.create(AppConfig.of(new Properties()));

        assertEquals(System.getProperty("java.version"), components.systemInfo().javaVersion());
    }

    @Test
    @DisplayName("La version de JavaFX se resuelve en cada lectura")
    void resolvesJavaFxVersionOnEveryRead() {
        ApplicationComponents components = ApplicationComponents.create(AppConfig.of(new Properties()));

        // El toolkit de JavaFX todavia no arranca durante las pruebas, por lo que
        // la propiedad no existe todavia. Debe reportarse como desconocida, no
        // fallar, y resolverse correctamente una vez que JavaFX la publique.
        String primerLectura = components.systemInfo().javafxVersion();
        String segundaLectura = components.systemInfo().javafxVersion();

        assertAll(
                () -> assertNotNull(primerLectura),
                () -> assertEquals(primerLectura, segundaLectura),
                () -> assertNotEquals("", primerLectura));

        String delSistema = System.getProperty("javafx.version");
        if (delSistema == null || delSistema.isBlank()) {
            assertEquals("desconocida", primerLectura);
        } else {
            assertEquals(delSistema, primerLectura);
        }
    }

    @Test
    @DisplayName("No se admiten componentes nulos")
    void rejectsNulls() {
        AppConfig config = AppConfig.of(new Properties());

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> ApplicationComponents.create(null)),
                () -> assertThrows(NullPointerException.class,
                        () -> new ApplicationComponents(null, ApplicationComponents.createSystemInfo(config))),
                () -> assertThrows(NullPointerException.class,
                        () -> new ApplicationComponents(config, null)));
    }
}
