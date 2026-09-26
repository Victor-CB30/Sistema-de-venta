package py.com.sistemaventa.logging;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;
import py.com.sistemaventa.config.AppConfig;

@DisplayName("LoggingConfigurator - preparacion del directorio y nivel de logs")
class LoggingConfiguratorTest {

    @TempDir
    Path tempDir;

    @AfterEach
    void restoreRootLevel() {
        // configure() modifica el nivel global de Logback: se restaura para no
        // ensuciar la salida del resto de pruebas.
        LoggingConfigurator.applyRootLevel("INFO");
    }

    @Test
    @DisplayName("configure() crea el directorio de logs y publica LOG_DIR")
    void configureCreatesLogDirectory() throws IOException {
        AppConfig config = AppConfig.of(new Properties());

        Path logDirectory = LoggingConfigurator.configure(config, tempDir);

        assertAll(
                () -> assertEquals(tempDir.toAbsolutePath().resolve("logs"), logDirectory),
                () -> assertTrue(Files.isDirectory(logDirectory)),
                () -> assertEquals(logDirectory.toString(),
                        System.getProperty(LoggingConfigurator.LOG_DIR_PROPERTY)));
    }

    @Test
    @DisplayName("configure() respeta el nombre de directorio configurado")
    void configureHonoursConfiguredDirectoryName() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("logging.directory", "registros");
        AppConfig config = AppConfig.of(properties);

        Path logDirectory = LoggingConfigurator.configure(config, tempDir);

        assertTrue(Files.isDirectory(tempDir.toAbsolutePath().resolve("registros")));
        assertEquals(tempDir.toAbsolutePath().resolve("registros"), logDirectory);
    }

    @Test
    @DisplayName("configure() aplica el nivel configurado al logger raiz")
    void configureAppliesRootLevel() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("logging.level", "debug");
        AppConfig config = AppConfig.of(properties);

        LoggingConfigurator.configure(config, tempDir);

        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        assertEquals(Level.DEBUG, rootLogger.getLevel());
    }

    @Test
    @DisplayName("Un nivel desconocido o vacio cae a INFO")
    void unknownLevelFallsBackToInfo() {
        LoggingConfigurator.applyRootLevel("VERBOSISIMO");
        assertEquals(Level.INFO, ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).getLevel());

        LoggingConfigurator.applyRootLevel(null);
        assertEquals(Level.INFO, ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).getLevel());
    }

    @Test
    @DisplayName("El directorio de datos por defecto cuelga del home del usuario")
    void defaultDataDirectoryLivesUnderUserHome() {
        AppConfig config = AppConfig.of(new Properties());

        Path expected = Path.of(System.getProperty("user.home", ".")).resolve(".sistemaventa");

        assertEquals(expected, LoggingConfigurator.defaultDataDirectory(config));
    }
}
