package py.com.sistemaventa.bootstrap;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;
import py.com.sistemaventa.application.SystemInfoService;
import py.com.sistemaventa.config.AppConfig;

/**
 * Composition root de la aplicacion.
 *
 * <p>Es el unico lugar donde se ensamblan las dependencias. A partir de aqui
 * {@code presentation} recibe colaboradores ya construidos y no conoce ni
 * {@link AppConfig} ni la construccion de servicios.</p>
 *
 * @param config     configuracion tecnica ya cargada
 * @param systemInfo servicio de informacion del entorno de ejecucion
 */
public record ApplicationComponents(AppConfig config, SystemInfoService systemInfo) {

    public ApplicationComponents {
        Objects.requireNonNull(config, "config no puede ser null");
        Objects.requireNonNull(systemInfo, "systemInfo no puede ser null");
    }

    /**
     * Construye los componentes a partir de la configuracion cargada.
     */
    public static ApplicationComponents create(AppConfig config) {
        Objects.requireNonNull(config, "config no puede ser null");
        return new ApplicationComponents(config, createSystemInfo(config));
    }

    /**
     * Construye el servicio de informacion del entorno con la hora actual de
     * arranque.
     *
     * <p>Las versiones de Java y JavaFX se leen de propiedades del sistema, no de
     * APIs de JavaFX, para que la capa {@code application} permanezca
     * independiente del toolkit. La lectura es perezosa porque
     * {@code javafx.version} todavia no esta definida antes de que el toolkit
     * de JavaFX se inicialice.</p>
     */
    public static SystemInfoService createSystemInfo(AppConfig config) {
        Supplier<String> javaVersion = () -> System.getProperty("java.version");
        Supplier<String> javafxVersion = () -> System.getProperty("javafx.version");
        return new SystemInfoService(
                config.appName(),
                config.appVersion(),
                javaVersion,
                javafxVersion,
                Instant.now());
    }
}
