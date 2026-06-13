package server.application.service;

import common.domain.model.User;
import common.util.PasswordHasher;
import server.application.context.UserContext;
import server.infrastructure.repository.UserRepository;

/**
 * Сервис авторизации и регистрации юсера.
 */
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Вход в систему. Проверяет логин и пароль.
     * Если успешно — сохраняет пользователя в UserContext.
     */
    public User login(String login, String password) {
        User user = userRepository.validatePassword(login, password);

        if (user == null) {
            throw new SecurityException("Неверный логин или пароль");
        }

        UserContext.setUser(user);
        return user;
    }

    /**
     * Регистрация нового пользователя.
     */
    public User register(String login, String password) {
        try {
            User newUser = userRepository.save(login, password);
            UserContext.setUser(newUser);
            return newUser;
        } catch (IllegalArgumentException e) {
            throw new SecurityException("Пользователь с таким логином уже существует");
        }
    }

    /**
     * Проверка, авторизован ли текущий запрос.
     */
    public static boolean isAuthenticated() {
        return UserContext.getUser() != null;
    }

    /**
     * Получить текущего пользователя (выбросит ошибку, если не вошёл).
     */
    public static User requireAuthenticated() {
        User user = UserContext.getUser();
        if (user == null) {
            throw new SecurityException("Доступ запрещён: выполните вход");
        }
        return user;
    }
}