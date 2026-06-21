package server.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Менеджер подключений к базе данных PostgreSQL.
 */
public class DatabaseManager {

    private final String url;
    private final String user;
    private final String password;
    private final String driver;

    public DatabaseManager(String url, String user, String password, String driver) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.driver = driver;

        // Загружаем драйвер
        try {
            // Подгружает класс драйвера в память JVM
            // При загрузке этот класс сам регистрируется в DriverManager как доступный драйвер
            Class.forName(driver);
            System.out.println("Драйвер " + driver + " загружен");
        } catch (ClassNotFoundException e) {
            System.err.println("Не удалось загрузить драйвер: " + driver);
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    // Создаём sequence и таблицу flats, если их ещё нет
    public void initFlatsSchema() {
        String createSequence = """
                CREATE SEQUENCE IF NOT EXISTS flats_id_seq
                START WITH 1 INCREMENT BY 1
                """;

        String createTable = """
                CREATE TABLE IF NOT EXISTS flats (
                    id BIGINT PRIMARY KEY DEFAULT nextval('flats_id_seq'),
                    name VARCHAR(255) NOT NULL,
                    coord_x INTEGER NOT NULL,
                    coord_y REAL NOT NULL,
                    creation_date TIMESTAMP NOT NULL,
                    area DOUBLE PRECISION NOT NULL,
                    number_of_rooms INTEGER,
                    number_of_bathrooms BIGINT NOT NULL,
                    furnish VARCHAR(50),
                    view VARCHAR(50) NOT NULL,
                    house_name VARCHAR(255),
                    house_year BIGINT,
                    house_number_of_floors BIGINT,
                    house_number_of_flats_on_floor INTEGER,
                    house_number_of_lifts BIGINT,
                    owner_id INTEGER NOT NULL REFERENCES users(id)
                )
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createSequence);
            stmt.execute(createTable);
            System.out.println("Таблица flats и sequence проверены/созданы");
        } catch (SQLException e) {
            System.err.println("Ошибка создания таблицы flats: " + e.getMessage());
            throw new RuntimeException("Не удалось инициализировать таблицу квартир", e);
        }
    }
}