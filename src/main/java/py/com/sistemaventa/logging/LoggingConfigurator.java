package py.com.sistemaventa.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import org.slf4j.LoggerFactory;
import py.com.sistemaventa.config.AppConfig;

/**
 * Configurador de logging transversal.
 *
 * <p>Se ejecuta antes de la primera obtencion de un logger con nombre, de modo
 * que la propiedad del sistema {@value #LOG_DIR_PROPERTY} ya este disponible
 * cuando Logback interprete {@code logback.xml}.</p>
 *
 * <p>Los logs se escriben en {@code <home del usuario>/<app.data.directory>/<logging.directory>}
 * y nunca junto al ejecutable, porque en una instalacion de Windows el
 * directorio del programa puede ser de solo lectura.</p>
 */
public final class LoggingConfigurator {

    /** Propiedad del sistema que consume {@code logback.xml}. */
    public static final String LOG_DIR_PROPERTY = "LOG_DIR";

    private LoggingConfigurator() {
        throw new AssertionError("Clase de utilidad");
    }

    /**
     * Configura el logging en el directorio de datos del usuario actual.
     *
     * @return directorio de logs creado
     * @throws IOException si el directorio no puede crearse
     */
    public static Path configure(AppConfig config) throws IOException {
        Objects.requireNonNull(config, "config no puede ser null");
        return configure(config, defaultDataDirectory(config));
    }

    /**
     * Configura el logging bajo el directorio base indicado.
     *
     * @param config       configuracion de la aplicacion
     * @param baseDirectory directorio bajo el cual se crea el directorio de logs
     * @return directorio de logs creado
     * @throws IOException si el directorio no puede crearse
     */
    public static Path configure(AppConfig config, Path baseDirectory) throws IOException {
        Objects.requireNonNull(config, "config no puede ser null");
        Objects.requireNonNull(baseDirectory, "baseDirectory no puede ser null");

        Path logDirectory = baseDirectory.resolve(config.logDirectoryName()).toAbsolutePath();
        Files.createDirectories(logDirectory);

        System.setProperty(LOG_DIR_PROPERTY, logDirectory.toString());
        applyRootLevel(config.logLevel());

        return logDirectory;
    }

    /**
     * Directorio de datos por defecto: {@code <home>/<app.data.directory>}.
     */
    public static Path defaultDataDirectory(AppConfig config) {
        return Path.of(System.getProperty("user.home", ".")).resolve(config.dataDirectoryName());
    }

    /**
     * Aplica el nivel configurado al logger raiz de Logback.
     *
     * @param levelName nombre del nivel; si es desconocido se usa INFO
     */
    static void applyRootLevel(String levelName) {
        Level level = Level.toLevel(normalize(levelName), Level.INFO);
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(level);
    }

    private static String normalize(String levelName) {
        if (levelName == null || levelName.isBlank()) {
            return "INFO";
        }
        return levelName.trim().toUpperCase(Locale.ROOT);
    }
}
