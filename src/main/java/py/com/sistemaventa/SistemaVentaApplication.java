package py.com.sistemaventa;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import py.com.sistemaventa.bootstrap.ApplicationComponents;
import py.com.sistemaventa.config.AppConfig;
import py.com.sistemaventa.presentation.MainViewController;

/**
 * Aplicacion JavaFX.
 *
 * <p>Responsabilidades de la capa {@code presentation} en esta fase:</p>
 * <ul>
 *   <li>construir la escena a partir de FXML y aplicar el CSS;</li>
 *   <li>configurar la ventana con los valores de {@link AppConfig};</li>
 *   <li>inyectar los componentes ya compuestos en el controller.</li>
 * </ul>
 *
 * <p>No contiene SQL, JDBC, acceso a repositorios ni reglas de negocio.</p>
 */
public class SistemaVentaApplication extends Application {

    private static final String VIEW_RESOURCE = "MainView.fxml";
    private static final String STYLESHEET_RESOURCE = "css/application.css";

    private static ApplicationComponents components;

    /**
     * Logger obtenido de forma perezosa, nunca en un campo estatico: el campo se
     * inicializaria al cargar la clase, antes de que
     * {@link py.com.sistemaventa.logging.LoggingConfigurator} inyecte
     * {@code LOG_DIR}.
     */
    private static Logger log() {
        return LoggerFactory.getLogger(SistemaVentaApplication.class);
    }

    /**
     * Lanza el toolkit de JavaFX inyectando los componentes ya construidos por
     * el composition root.
     *
     * @throws IllegalStateException si JavaFX ya fue inicializado
     */
    public static void launchApplication(ApplicationComponents applicationComponents, String[] args) {
        components = Objects.requireNonNull(applicationComponents, "componentes no pueden ser null");
        Application.launch(SistemaVentaApplication.class, args);
    }

    /**
     * Componentes inyectados por el composition root.
     *
     * @throws IllegalStateException si la aplicacion se instancio sin bootstrap
     */
    public static ApplicationComponents components() {
        if (components == null) {
            throw new IllegalStateException(
                    "La aplicacion debe iniciarse desde py.com.sistemaventa.bootstrap.Bootstrap");
        }
        return components;
    }

    @Override
    public void start(Stage stage) {
        AppConfig config = components().config();

        FXMLLoader loader = new FXMLLoader(requireResource(VIEW_RESOURCE));
        Parent root;
        try (InputStream view = loader.getLocation().openStream()) {
            root = loader.load(view);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar la vista " + VIEW_RESOURCE, e);
        }

        MainViewController controller = loader.getController();
        controller.setApplicationComponents(components());

        Scene scene = new Scene(root, config.windowWidth(), config.windowHeight());
        scene.getStylesheets().add(requireResource(STYLESHEET_RESOURCE).toExternalForm());

        stage.setTitle(config.windowTitle());
        stage.setScene(scene);
        stage.setResizable(config.windowResizable());
        stage.show();

        log().info("Entorno: {}", components().systemInfo().environment());
        log().info("Ventana principal iniciada: {} ({}x{})",
                config.windowTitle(), config.windowWidth(), config.windowHeight());
    }

    @Override
    public void stop() {
        log().info("Aplicacion finalizada");
    }

    private static URL requireResource(String name) {
        URL url = MainViewController.class.getResource(name);
        if (url == null) {
            throw new IllegalStateException("No se encontro el recurso: " + name);
        }
        return url;
    }
}
