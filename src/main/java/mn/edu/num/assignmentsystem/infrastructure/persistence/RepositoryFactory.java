package mn.edu.num.assignmentsystem.infrastructure.persistence;

import java.io.InputStream;
import java.util.Properties;

import mn.edu.num.assignmentsystem.core.ports.IAssignmentRepository;

/**
 * RepositoryFactory нь persistence implementation сонгох factory юм.
 *
 * Docker Compose орчинд APP_PERSISTENCE_MODE environment variable-аас уншина.
 * Local Eclipse орчинд database.properties файлаас app.persistence.mode уншина.
 */
public class RepositoryFactory {

    private static String persistenceMode;

    static {
        loadPersistenceMode();
    }

    private static void loadPersistenceMode() {
        try (InputStream inputStream =
                     RepositoryFactory.class.getClassLoader()
                             .getResourceAsStream("database.properties")) {

            Properties properties = new Properties();

            if (inputStream != null) {
                properties.load(inputStream);
            }

            persistenceMode = getConfigValue(
                    "APP_PERSISTENCE_MODE",
                    properties.getProperty("app.persistence.mode")
            );

        } catch (Exception e) {
            throw new RuntimeException("Persistence mode уншихад алдаа гарлаа.", e);
        }
    }

    private static String getConfigValue(String envName, String defaultValue) {
        String envValue = System.getenv(envName);

        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue;
        }

        return defaultValue;
    }

    public static IAssignmentRepository createRepository() {
        if ("DB".equalsIgnoreCase(persistenceMode)) {
            return new JdbcAssignmentRepository();
        }

        if ("MEM".equalsIgnoreCase(persistenceMode)) {
            return new InMemoryAssignmentRepository();
        }

        throw new IllegalArgumentException(
                "Unknown persistence mode: " + persistenceMode
        );
    }
}