package server.application.service;

import common.domain.model.User;
import server.infrastructure.repository.UserRepository;

/**
 * Сервис авторизации и регистрации пользователей.
 * Отвечает за бизнес-логику работы с учетными записями.
 */
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Выполняет вход пользователя в систему.
     * Проверяет логин и пароль через репозиторий.
     */
    public User login(String login, String password) {
        return userRepository.validatePassword(login, password);
    }

    /**
     * Регистрирует нового пользователя.
     * @throws IllegalArgumentException если пользователь с таким логином уже существует
     */
    public User register(String login, String password) {
        return userRepository.save(login, password);
    }
}