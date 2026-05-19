package mn.edu.num.assignmentsystem.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Өгөгдлийн сантай холбогдох Singleton класс.
 *
 * Энэ класс нь:
 * - database.properties файлаас тохиргоо унших
 * - JDBC driver ачаалах
 * - database connection үүсгэх
 * - application startup үед schema.sql ажиллуулах
 *
 * Ингэснээр schema үүсгэх логик repository class-аас салж,
 * infrastructure configuration түвшинд төвлөрнө.
 */
public class DatabaseConnection {

    /** Singleton instance */
    private static DatabaseConnection instance;

    /** Shared database connection */
    private Connection connection;

    /** Config мэдээллүүд */
    private String persistenceMode;
    private String driver;
    private String url;
    private String user;
    private String password;

    /**
     * Private constructor.
     * Гаднаас new хийж болохгүй.
     * Зөвхөн getInstance() ашиглана.
     */
    private DatabaseConnection() {
        loadProperties();

        /*
         * Зөвхөн DB mode үед JDBC driver болон schema initialization хэрэгтэй.
         * MEM mode ашиглаж байвал database dependency шаардахгүй.
         */
        if ("DB".equalsIgnoreCase(persistenceMode)) {
            loadDriver();
            initializeSchema();
        }
    }

    /**
     * Singleton instance буцаана.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * database.properties файлаас тохиргоо уншина.
     */
    private static void loadProperties() {
        try (InputStream inputStream =
                     DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties")) {

            Properties properties = new Properties();

            if (inputStream != null) {
                properties.load(inputStream);
            }

            driver = getConfigValue("DB_DRIVER", properties.getProperty("db.driver"));
            url = getConfigValue("DB_URL", properties.getProperty("db.url"));
            user = getConfigValue("DB_USER", properties.getProperty("db.user"));
            password = getConfigValue("DB_PASSWORD", properties.getProperty("db.password"));

        } catch (Exception e) {
            throw new RuntimeException("Database properties уншихад алдаа гарлаа.", e);
        }
    }

    private static String getConfigValue(String envName, String defaultValue) {
        String envValue = System.getenv(envName);

        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue;
        }

        return defaultValue;
    }

    /**
     * JDBC driver-ийг динамикаар ачаална.
     */
    private void loadDriver() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC driver олдсонгүй: " + driver, e);
        }
    }

    /**
     * Database connection буцаана.
     *
     * Хэрэв connection байхгүй эсвэл хаагдсан бол шинээр нээнэ.
     * Singleton connection ашиглаж байгаа нь startup schema initialization болон
     * repository query-үүд нэг ижил DB config-тай ажиллах нөхцөл болдог.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Өгөгдлийн сантай холбогдох үед алдаа гарлаа.", e);
        }
    }

    /**
     * Application startup үед schema.sql файлыг уншиж ажиллуулна.
     *
     * Энэ нь Project requirement дээрх:
     * "H2 relational schema is created via SQL scripts on startup"
     * гэсэн нөхцөлийг хангаж байна.
     */
    private void initializeSchema() {
        String schemaSql = loadSchemaSqlFromResources();

        /*
         * Хэрэв schema.sql хоосон байвал execute хийхгүй.
         * Ингэснээр config file байхгүй үед илүү ойлгомжтой fail хийнэ.
         */
        if (schemaSql == null || schemaSql.trim().isEmpty()) {
            throw new RuntimeException("schema.sql файл хоосон эсвэл уншигдсангүй.");
        }

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            /*
             * Нэгээс олон SQL statement байж болзошгүй тул ';' тэмдэгтээр хувааж
             * тус бүрийг execute хийж байна.
             */
            String[] statements = schemaSql.split(";");

            for (String sql : statements) {
                String trimmedSql = sql.trim();

                if (!trimmedSql.isEmpty()) {
                    statement.execute(trimmedSql);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("schema.sql ажиллуулах үед алдаа гарлаа.", e);
        }
    }

    /**
     * Resources дотроос schema.sql файлыг уншиж String болгон буцаана.
     */
    private String loadSchemaSqlFromResources() {
        try (InputStream inputStream =
                     getClass().getClassLoader().getResourceAsStream("schema.sql")) {

            if (inputStream == null) {
                throw new RuntimeException("schema.sql файл resources дотор олдсонгүй.");
            }

            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("schema.sql файлыг унших үед алдаа гарлаа.", e);
        }
    }

    /**
     * Persistence mode-г буцаана.
     */
    public String getPersistenceMode() {
        return persistenceMode;
    }

    /**
     * Database URL-г шалгах зорилгоор авах getter.
     */
    public String getUrl() {
        return url;
    }
}