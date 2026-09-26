package py.com.sistemaventa.bootstrap;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import py.com.sistemaventa.SistemaVentaApplication;
import py.com.sistemaventa.config.AppConfig;
import py.com.sistemaventa.logging.LoggingConfigurator;

/**
 * Punto de entrada de la aplicacion.
 *
 * <p>Orden de arranque:</p>
 * <ol>
 *   <li>cargar la configuracion;</li>
 *   <li>configurar el logging (debe ocurrir antes de obtener cualquier logger
 *       con nombre, porque Logback lee {@code logback.xml} en ese momento);</li>
 *   <li>componer las dependencias;</li>
 *   <li>lanzar el toolkit de JavaFX.</li>
 * </ol>
 *
 * <p>Este es el {@code mainClass} configurado en el POM para
 * {@code mvn javafx:run}.</p>
 */
public final class Bootstrap {

    private Bootstrap() {
        throw new AssertionError("Clase de utilidad");
    }

    public static void main(String[] args) {
        AppConfig config = AppConfig.load();

        try {
            LoggingConfigurator.configure(config);
        } catch (IOException e) {
            System.err.println("No se pudo preparar el directorio de logs: " + e.getMessage());
        }

        // El logger se obtiene aqui y no en un campo estatico: un campo estatico
        // inicializado por Logback antes de que exista la propiedad LOG_DIR
        // escribiria los logs en el directorio equivocado.
        Logger log = LoggerFactory.getLogger(Bootstrap.class);

        log.info("Iniciando {} {}", config.appName(), config.appVersion());
        log.info("Configuracion cargada desde {}", AppConfig.CONFIG_RESOURCE);
        log.info("Directorio de logs: {}", System.getProperty(LoggingConfigurator.LOG_DIR_PROPERTY));

        ApplicationComponents components = ApplicationComponents.create(config);
        log.info("Java {}", System.getProperty("java.version"));

        try {
            SistemaVentaApplication.launchApplication(components, args);
        } catch (RuntimeException e) {
            log.error("La aplicacion finalizo con error", e);
            throw e;
        }
    }
}
