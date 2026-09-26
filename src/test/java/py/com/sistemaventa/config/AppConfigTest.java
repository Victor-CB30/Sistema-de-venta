package py.com.sistemaventa.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AppConfig - carga y acceso tipado a la configuracion")
class AppConfigTest {

    @Test
    @DisplayName("load() lee el recurso empaquetado de la aplicacion")
    void loadReadsBundledResource() {
        AppConfig config = AppConfig.load();

        assertAll(
                () -> assertEquals("Sistema de Venta", config.appName()),
                () -> assertEquals("Sistema de Venta", config.windowTitle()),
                () -> assertTrue(config.appVersion().startsWith("0.1.0"),
                        "La version debe venir del POM por filtrado, pero fue: " + config.appVersion()),
                () -> assertEquals("py.com.sistemaventa", config.appOrganization()),
                () -> assertTrue(config.windowWidth() > 0),
                () -> assertTrue(config.windowHeight() > 0),
                () -> assertTrue(config.windowResizable()),
                () -> assertEquals("INFO", config.logLevel()),
                () -> assertEquals("logs", config.logDirectoryName()),
                () -> assertEquals(".sistemaventa", config.dataDirectoryName()));
    }

    @Test
    @DisplayName("load() falla con mensaje claro si el recurso no existe")
    void loadFailsWhenResourceMissing() {
        IllegalStateException error =
                assertThrows(IllegalStateException.class, () -> AppConfig.load("/no-existe.properties"));

        assertTrue(error.getMessage().contains("/no-existe.properties"));
    }

    @Test
    @DisplayName("Los valores por defecto se aplican cuando la clave no existe")
    void defaultsAreApplied() {
        AppConfig config = AppConfig.of(new Properties());

        assertAll(
                () -> assertEquals("Sistema de Venta", config.appName()),
                () -> assertEquals("0.0.0", config.appVersion()),
                () -> assertEquals(960, config.windowWidth()),
                () -> assertEquals(640, config.windowHeight()),
                () -> assertTrue(config.windowResizable()),
                () -> assertEquals("INFO", config.logLevel()),
                () -> assertEquals("valor-por-defecto", config.get("clave.inexistente", "valor-por-defecto")));
    }

    @Test
    @DisplayName("Los valores en blanco se consideran ausentes")
    void blankValuesFallBackToDefaults() {
        Properties properties = new Properties();
        properties.setProperty("app.name", "   ");
        properties.setProperty("window.width", "");

        AppConfig config = AppConfig.of(properties);

        assertAll(
                () -> assertEquals("Sistema de Venta", config.appName()),
                () -> assertEquals(960, config.windowWidth()));
    }

    @Test
    @DisplayName("Un entero invalido produce un error explicito")
    void invalidIntegerFails() {
        Properties properties = new Properties();
        properties.setProperty("window.width", "ancho");

        AppConfig config = AppConfig.of(properties);

        IllegalStateException error = assertThrows(IllegalStateException.class, config::windowWidth);
        assertTrue(error.getMessage().contains("window.width"));
    }

    @Test
    @DisplayName("getBoolean interpreta true/false en cualquier caja")
    void booleansAreParsed() {
        Properties properties = new Properties();
        properties.setProperty("window.resizable", "FALSE");
        properties.setProperty("otra.clave", "True");

        AppConfig config = AppConfig.of(properties);

        assertAll(
                () -> assertEquals(false, config.windowResizable()),
                () -> assertEquals(true, config.getBoolean("otra.clave", true)));
    }
}
