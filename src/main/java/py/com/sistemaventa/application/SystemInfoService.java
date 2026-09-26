package py.com.sistemaventa.application;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Servicio de informacion tecnica del entorno de ejecucion.
 *
 * <p>Purpose concreto: la vista de arranque necesita mostrar version de la
 * aplicacion, version de Java, version de JavaFX y marca de inicio. Al residir
 * en {@code application} y depender solo de cadenas primitivas, la capa
 * {@code presentation} no necesita tocar {@link py.com.sistemaventa.config.AppConfig}
 * ni conocer el toolkit de JavaFX.</p>
 *
 * <p>Las versiones se resuelven de forma perezosa porque la propiedad del
 * sistema {@code javafx.version} todavia no existe hasta que el toolkit de
 * JavaFX se inicializa, es decir, despues de que este servicio ya fue
 * construido por el composition root.</p>
 *
 * <p>Es un valor inmutable y no contiene ninguna regla de negocio.</p>
 */
public final class SystemInfoService {

    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final String appName;
    private final String appVersion;
    private final Supplier<String> javaVersion;
    private final Supplier<String> javafxVersion;
    private final Instant startedAt;

    public SystemInfoService(String appName,
                             String appVersion,
                             Supplier<String> javaVersion,
                             Supplier<String> javafxVersion,
                             Instant startedAt) {
        this.appName = Objects.requireNonNullElse(appName, "Sistema de Venta");
        this.appVersion = Objects.requireNonNullElse(appVersion, "0.0.0");
        this.javaVersion = Objects.requireNonNullElseGet(javaVersion, () -> () -> "desconocida");
        this.javafxVersion = Objects.requireNonNullElseGet(javafxVersion, () -> () -> "desconocida");
        this.startedAt = Objects.requireNonNullElseGet(startedAt, Instant::now);
    }

    /**
     * Crea el servicio con versiones fijas ya conocidas.
     * Util para pruebas unitarias.
     */
    public static SystemInfoService ofFixed(String appName,
                                            String appVersion,
                                            String javaVersion,
                                            String javafxVersion,
                                            Instant startedAt) {
        return new SystemInfoService(appName, appVersion, () -> javaVersion, () -> javafxVersion, startedAt);
    }

    public String appName() {
        return appName;
    }

    public String appVersion() {
        return appVersion;
    }

    public String javaVersion() {
        return resolve(javaVersion);
    }

    public String javafxVersion() {
        return resolve(javafxVersion);
    }

    public Instant startedAt() {
        return startedAt;
    }

    /** Linea resumida, por ejemplo: {@code "Sistema de Venta 0.1.0-SNAPSHOT"}. */
    public String summary() {
        return appName + " " + appVersion;
    }

    /** Linea de entorno, por ejemplo: {@code "Java 21.0.12 - JavaFX 21.0.12"}. */
    public String environment() {
        return "Java " + javaVersion() + " - JavaFX " + javafxVersion();
    }

    /** Marca de inicio formateada con la zona horaria del sistema. */
    public String startedAtFormatted() {
        return FECHA_HORA.format(startedAt);
    }

    private static String resolve(Supplier<String> supplier) {
        String value = supplier.get();
        return (value == null || value.isBlank()) ? "desconocida" : value;
    }
}
