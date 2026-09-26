package py.com.sistemaventa.presentation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javafx.fxml.FXML;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Verifica el contrato entre {@code MainView.fxml} y
 * {@link MainViewController} sin iniciar el toolkit de JavaFX.
 *
 * <p>Detecta de forma temprana la desincronizacion entre el FXML y el
 * controller: un {@code fx:id} huerfano o un campo {@code @FXML} sin
 * {@code fx:id} solo fallarian en tiempo de ejecucion de la ventana.</p>
 */
@DisplayName("MainView - contrato entre FXML, CSS y controller")
class MainViewResourcesTest {

    private static final String VIEW = "MainView.fxml";
    private static final String STYLESHEET = "css/application.css";
    private static final String FX_NAMESPACE = "http://javafx.com/fxml/1";

    private static Document view;
    private static String stylesheet;

    @BeforeAll
    static void loadResources() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        try (InputStream stream = resource(VIEW)) {
            view = factory.newDocumentBuilder().parse(stream);
        }

        try (InputStream stream = resource(STYLESHEET)) {
            stylesheet = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    @DisplayName("El FXML y el CSS estan empaquetados en el classpath")
    void resourcesAreBundled() {
        assertAll(
                () -> assertNotNull(MainViewController.class.getResource(VIEW), "falta el recurso " + VIEW),
                () -> assertNotNull(MainViewController.class.getResource(STYLESHEET),
                        "falta el recurso " + STYLESHEET),
                () -> assertFalse(stylesheet.isBlank(), "el CSS no puede estar vacio"));
    }

    @Test
    @DisplayName("El FXML esta bien formado y declara el controller esperado")
    void fxmlDeclaresExpectedController() {
        assertAll(
                () -> assertEquals("VBox", view.getDocumentElement().getLocalName()),
                () -> assertEquals("http://javafx.com/javafx/21",
                        view.getDocumentElement().getNamespaceURI()),
                () -> assertEquals(MainViewController.class.getName(),
                        view.getDocumentElement().getAttributeNS(FX_NAMESPACE, "controller")));
    }

    @Test
    @DisplayName("Todo fx:id del FXML tiene su campo @FXML en el controller")
    void everyFxmlIdHasAControllerField() {
        Set<String> ids = fxAttributeValues("id");
        Set<String> fields = fxmlFields();

        assertFalse(ids.isEmpty(), "el FXML debe declarar al menos un fx:id");
        assertTrue(fields.containsAll(ids),
                "fx:id sin campo @FXML en el controller: " + difference(ids, fields));
    }

    @Test
    @DisplayName("Todo campo @FXML del controller tiene su fx:id en el FXML")
    void everyControllerFieldExistsInFxml() {
        Set<String> ids = fxAttributeValues("id");
        Set<String> fields = fxmlFields();

        assertFalse(fields.isEmpty(), "el controller debe declarar campos @FXML");
        assertTrue(ids.containsAll(fields),
                "campo @FXML sin fx:id en el FXML: " + difference(fields, ids));
    }

    @Test
    @DisplayName("El CSS define todas las clases de estilo usadas por la vista")
    void stylesheetCoversEveryStyleClass() {
        Set<String> styleClasses = plainAttributeValues("styleClass");
        Set<String> defined = definedStyleClasses();

        assertFalse(styleClasses.isEmpty(), "el FXML debe usar clases de estilo");
        assertTrue(defined.containsAll(styleClasses),
                "clases de estilo usadas y no definidas en el CSS: " + difference(styleClasses, defined));
    }

    private static InputStream resource(String name) {
        return MainViewController.class.getResourceAsStream(name);
    }

    /** Valores de un atributo con prefijo {@code fx:}, por ejemplo {@code fx:id}. */
    private static Set<String> fxAttributeValues(String localName) {
        return attributeValues(localName, true);
    }

    /** Valores de un atributo de propiedad, por ejemplo {@code styleClass}. */
    private static Set<String> plainAttributeValues(String localName) {
        return attributeValues(localName, false);
    }

    private static Set<String> attributeValues(String localName, boolean fxNamespaced) {
        Set<String> values = new LinkedHashSet<>();
        NodeList nodes = view.getElementsByTagName("*");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (!(node instanceof Element element)) {
                continue;
            }
            String value = fxNamespaced
                    ? element.getAttributeNS(FX_NAMESPACE, localName)
                    : element.getAttribute(localName);
            if (!value.isBlank()) {
                values.addAll(Arrays.stream(value.trim().split("\\s+")).toList());
            }
        }
        return values;
    }

    private static Set<String> fxmlFields() {
        return Arrays.stream(MainViewController.class.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(FXML.class))
                .map(Field::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Set<String> definedStyleClasses() {
        Set<String> defined = new LinkedHashSet<>();
        Matcher matcher = Pattern.compile("\\.([A-Za-z][A-Za-z0-9_-]*)\\s*\\{").matcher(stylesheet);
        while (matcher.find()) {
            defined.add(matcher.group(1));
        }
        return defined;
    }

    private static String difference(Set<String> left, Set<String> right) {
        return left.stream().filter(value -> !right.contains(value)).collect(Collectors.joining(", "));
    }
}
