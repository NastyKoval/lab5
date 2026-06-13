package server.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Менеджер подключений к базе данных PostgreSQL.
 * Отвечает за создание таблиц и управление соединениями.
 */
public class DatabaseManager {

    private final String url;
    private final String username;
    private final String password;


    public DatabaseManager(String host, String database, String username, String password) {
        this.url = "jdbc:postgresql://" + host + ":5432/" + database;
        this.username = username;
        this.password = password;
    }


    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Создаёт необходимые таблицы в базе данных
     * Выполняется один раз при запуске сервера
     */
    public void createTables() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                login VARCHAR(255) UNIQUE NOT NULL,
                password_hash VARCHAR(255) NOT NULL
            )
            """;

        String createFlatsTable = """
            CREATE TABLE IF NOT EXISTS flats (
                id INTEGER PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                coordinates_x DOUBLE PRECISION NOT NULL,
                coordinates_y DOUBLE PRECISION NOT NULL,
                creation_date TIMESTAMP NOT NULL,
                area DOUBLE PRECISION NOT NULL,
                number_of_rooms INTEGER,
                number_of_bathrooms INTEGER,
                furnish VARCHAR(50),
                view VARCHAR(50),
                house_name VARCHAR(255),
                house_year INTEGER,
                house_floors INTEGER,
                house_lifts INTEGER,
                owner_id INTEGER NOT NULL,
                FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
            )
            """;

        String createSequence = """
            CREATE SEQUENCE IF NOT EXISTS flat_id_seq
            START WITH 1
            INCREMENT BY 1
            NO MINVALUE
            NO MAXVALUE
            CACHE 1
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            System.out.println("Таблица users создана/проверена");

            stmt.execute(createFlatsTable);
            System.out.println("Таблица flats создана/проверена");

            stmt.execute(createSequence);
            System.out.println("Sequence flat_id_seq создана/проверена");

        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблиц: " + e.getMessage());
            throw new RuntimeException("Не удалось создать таблицы БД", e);
        }
    }

    /**
     * Проверяет подключение к базе данных
     */
    public void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Подключение к БД успешно: " + url);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к БД: " + e.getMessage());
            throw new RuntimeException("Не удалось подключиться к базе данных", e);
        }
    }
}