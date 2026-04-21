package mn.edu.num.assignmentsystem.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Өгөгдлийн сантай холбогдох Singleton класс.
 * - database.properties файлаас тохиргоо унших
 * - JDBC driver ачаалах
 * - Нэг shared connection үүсгэж удирдах
 */
public class DatabaseConnection {

    /** Singleton instance */
    private static DatabaseConnection instance;

    /** Database connection объект */
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
         * Зөвхөн DB persistence mode үед JDBC driver хэрэгтэй.
         * MEM mode ашиглаж байгаа үед database driver ачаалах шаардлагагүй.
         */
        if ("DB".equalsIgnoreCase(persistenceMode)) {
            loadDriver();
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
    private void loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("database.properties")) {

            if (inputStream == null) {
                throw new RuntimeException("database.properties файл олдсонгүй.");
            }

            properties.load(inputStream);

            this.persistenceMode = properties.getProperty("app.persistence.mode");
            this.driver = properties.getProperty("db.driver");
            this.url = properties.getProperty("db.url");
            this.user = properties.getProperty("db.user");
            this.password = properties.getProperty("db.password");

        } catch (IOException e) {
            throw new RuntimeException("database.properties файлыг унших үед алдаа гарлаа.", e);
        }
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
     * Хэрэв connection байхгүй эсвэл хаагдсан бол шинээр нээнэ.
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