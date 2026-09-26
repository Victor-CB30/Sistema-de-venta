package py.com.sistemaventa.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * Configuracion tecnica de la aplicacion, cargada desde
 * {@value #CONFIG_RESOURCE}.
 *
 * <p>Es inmutable y se resuelve una unica vez en el composition root
 * ({@code py.com.sistemaventa.bootstrap.Bootstrap}).</p>
 *
 * <p>No contiene credenciales. Segun {@code PROJECT_SPEC.md} (Seguridad), las
 * credenciales de base de datos se resolveran en la Fase 3 desde un archivo
 * externo al ejecutable.</p>
 */
public final class AppConfig {

    /** Recurso empaquetado con la configuracion por defecto. */
    public static final String CONFIG_RESOURCE = "/application.properties";

    private static final String KEY_APP_NAME = "app.name";
    private static final String KEY_APP_VERSION = "app.version";
    private static final String KEY_APP_ORGANIZATION = "app.organization";
    private static final String KEY_DATA_DIRECTORY = "app.data.directory";
    private static final String KEY_WINDOW_TITLE = "window.title";
    private static final String KEY_WINDOW_WIDTH = "window.width";
    private static final String KEY_WINDOW_HEIGHT = "window.height";
    private static final String KEY_WINDOW_RESIZABLE = "window.resizable";
    private static final String KEY_LOG_LEVEL = "logging.level";
    private static final String KEY_LOG_DIRECTORY = "logging.directory";

    private final Properties properties;

    private AppConfig(Properties properties) {
        this.properties = properties;
    }

    /**
     * Carga la configuracion desde {@value #CONFIG_RESOURCE}.
     *
     * @throws IllegalStateException si el recurso no existe o no puede leerse
     */
    public static AppConfig load() {
        return load(CONFIG_RESOURCE);
    }

    /**
     * Carga la configuracion desde el recurso indicado del classpath.
     *
     * @param resource ruta absoluta del recurso, por ejemplo {@code "/application.properties"}
     * @throws IllegalStateException si el recurso no existe o no puede leerse
     */
    public static AppConfig load(String resource) {
        Objects.requireNonNull(resource, "resource no puede ser null");

        Properties loaded = new Properties();
        Class<?> anchor = AppConfig.class;

        try (InputStream stream = anchor.getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IllegalStateException(
                        "No se encontro el recurso de configuracion: " + resource);
            }
            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                loaded.load(reader);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer la configuracion: " + resource, e);
        }

        return new AppConfig(loaded);
    }

    /**
     * Crea una configuracion a partir de propiedades ya cargadas.
     * Util para pruebas unitarias.
     */
    public static AppConfig of(Properties properties) {
        Objects.requireNonNull(properties, "properties no puede ser null");
        return new AppConfig(properties);
    }

    public String get(String key, String defaultValue) {
        Objects.requireNonNull(key, "key no puede ser null");
        String value = properties.getProperty(key);
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }

    public int getInt(String key, int defaultValue) {
        String value = get(key, null);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("El valor de '" + key + "' no es un entero: " + value, e);
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key, null);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    /** Nombre visible de la aplicacion. */
    public String appName() {
        return get(KEY_APP_NAME, "Sistema de Venta");
    }

    /** Version de la aplicacion, sincronizada con el POM por filtrado de Maven. */
    public String appVersion() {
        return get(KEY_APP_VERSION, "0.0.0");
    }

    /** Identificador del producto. */
    public String appOrganization() {
        return get(KEY_APP_ORGANIZATION, "py.com.sistemaventa");
    }

    /** Nombre del directorio de datos, relativo al home del usuario. */
    public String dataDirectoryName() {
        return get(KEY_DATA_DIRECTORY, ".sistemaventa");
    }

    /** Titulo de la ventana principal. */
    public String windowTitle() {
        return get(KEY_WINDOW_TITLE, appName());
    }

    public int windowWidth() {
        return getInt(KEY_WINDOW_WIDTH, 960);
    }

    public int windowHeight() {
        return getInt(KEY_WINDOW_HEIGHT, 640);
    }

    public boolean windowResizable() {
        return getBoolean(KEY_WINDOW_RESIZABLE, true);
    }

    /** Nivel raiz de logging: TRACE, DEBUG, INFO, WARN o ERROR. */
    public String logLevel() {
        return get(KEY_LOG_LEVEL, "INFO");
    }

    /** Nombre del directorio de logs, relativo al directorio de datos. */
    public String logDirectoryName() {
        return get(KEY_LOG_DIRECTORY, "logs");
    }
}
