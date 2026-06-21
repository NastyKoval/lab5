package server.infrastructure.repository;

import common.domain.model.User;
import common.util.PasswordHasher;
import server.infrastructure.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Репозиторий для работы с пользователями в базе данных.
 */
public class UserRepository {

    private final DatabaseManager dbManager;

    public UserRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        // Создаём таблицу при инициализации репозитория
        createTableIfNotExists();
    }

    /**
     * Создаёт таблицу users, если она ещё не существует.
     */
    private void createTableIfNotExists() {
        // SERIAL - это специальный тип PostgreSQL для автоинкремента,
        // он сам создаёт sequence внутри себя автоматически
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                login TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL
            )
            """;

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица users проверена/создана");
        } catch (SQLException e) {
            System.err.println("Ошибка создания таблицы users: " + e.getMessage());
            throw new RuntimeException("Не удалось инициализировать таблицу пользователей", e);
        }
    }

    public User findByLogin(String login) {
        String sql = "SELECT id, login, password_hash FROM users WHERE login = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Подготовленное заявление

            // Устанавливаем параметр запроса (вместо ?)
            stmt.setString(1, login);

            try (ResultSet rs = stmt.executeQuery()) { // Выполняет запрос с подставленными данными
                if (rs.next()) {
                    // Если нашли запись то создаём User
                    int id = rs.getInt("id");
                    String dbLogin = rs.getString("login");
                    String passwordHash = rs.getString("password_hash");

                    return new User(id, dbLogin, passwordHash);
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка поиска пользователя: " + e.getMessage());
            throw new RuntimeException("Не удалось найти пользователя", e);
        }
    }

    /**
     * Сохраняет (регистрирует) нового пользователя.
     *
     * @param login логин
     * @param password пароль в открытом виде
     * @return User созданный пользователь
     * @throws IllegalArgumentException если логин занят
     */
    public User save(String login, String password) {
        // Проверяем не занят ли логин
        User existingUser = findByLogin(login);
        if (existingUser != null) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        String passwordHash = PasswordHasher.hash(password);

        // SQL для вставки нового пользователя
        // Используем RETURN_GENERATED_KEYS для получения сгенерированного ID (SQLite)
        String sql = "INSERT INTO users (login, password_hash) VALUES (?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Устанавливаем параметры
            stmt.setString(1, login);
            stmt.setString(2, passwordHash);

            // Выполняем вставку
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Не удалось зарегистрировать пользователя, нет затронутых строк");
            }

            // Получаем сгенерированный ID через getGeneratedKeys()
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Пользователь зарегистрирован: " + login + " (ID: " + id + ")");
                    return new User(id, login, passwordHash);
                } else {
                    throw new SQLException("Не удалось получить ID пользователя");
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка регистрации пользователя: " + e.getMessage());
            throw new RuntimeException("Не удалось зарегистрировать пользователя", e);
        }
    }

    /**
     * Проверяет правильность пароля для пользователя.
     *
     * @param login логин
     * @param password пароль в открытом виде
     * @return User если пароль верный, null если неверный
     */
    public User validatePassword(String login, String password) {
        User user = findByLogin(login);

        if (user == null) {
            // Пользователь не найден
            return null;
        }

        // Проверяем пароль
        boolean isCorrect = PasswordHasher.verify(password, user.getPasswordHash());

        if (isCorrect) {
            System.out.println("Пользователь " + login + " успешно вошёл в систему");
            return user;
        } else {
            System.out.println("Неверный пароль для пользователя: " + login);
            return null;
        }
    }

    /**
     * Находит пользователя по ID.
     *
     * @param id идентификатор
     * @return User или null
     */
    public User findById(int id) {
        String sql = "SELECT id, login, password_hash FROM users WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String login = rs.getString("login");
                    String passwordHash = rs.getString("password_hash");

                    return new User(userId, login, passwordHash);
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка поиска пользователя по ID: " + e.getMessage());
            throw new RuntimeException("Не удалось найти пользователя", e);
        }
    }
}