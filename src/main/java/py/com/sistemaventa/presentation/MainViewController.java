package py.com.sistemaventa.presentation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import py.com.sistemaventa.bootstrap.ApplicationComponents;

/**
 * Controller de la vista minima de arranque.
 *
 * <p>Solo presenta datos ya armados por la capa {@code application}. No accede
 * a repositorios, no ejecuta SQL y no contiene reglas de negocio.</p>
 *
 * <p>Se instancia FXML a traves del atributo {@code fx:controller}; el
 * composition root le inyecta las dependencias con
 * {@link #setApplicationComponents(ApplicationComponents)}.</p>
 */
public final class MainViewController {

    private static final String MENSAJE_INICIO = "La aplicación se inició correctamente.";

    @FXML
    private Label tituloLabel;

    @FXML
    private Label mensajeLabel;

    @FXML
    private Label versionLabel;

    @FXML
    private Label entornoLabel;

    @FXML
    private Label estadoLabel;

    private ApplicationComponents components;

    /**
     * Inyecta las dependencias y completa la vista.
     *
     * @throws IllegalStateException si la vista aun no fue inflada por FXMLLoader
     */
    public void setApplicationComponents(ApplicationComponents applicationComponents) {
        if (applicationComponents == null) {
            throw new IllegalArgumentException("Los componentes no pueden ser null");
        }
        if (tituloLabel == null) {
            throw new IllegalStateException(
                    "La vista MainView.fxml debe cargarse antes de inyectar los componentes");
        }
        this.components = applicationComponents;
        render();
    }

    /** Componentes inyectados, o {@code null} si la vista aun no fue inicializada. */
    public ApplicationComponents components() {
        return components;
    }

    private void render() {
        var systemInfo = components.systemInfo();
        tituloLabel.setText(systemInfo.appName());
        mensajeLabel.setText(MENSAJE_INICIO);
        versionLabel.setText(systemInfo.summary() + " - inicio " + systemInfo.startedAtFormatted());
        entornoLabel.setText(systemInfo.environment());
        estadoLabel.setText("Fase 2 - Proyecto base y arquitectura");
    }
}
