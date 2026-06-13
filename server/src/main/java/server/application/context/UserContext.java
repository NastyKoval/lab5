package server.application.context;

import common.domain.model.User;

/**
 * Хранит текущего авторизованного пользователя для потока.
 */
public class UserContext {

    // ThreadLocal гарантирует, что каждый поток видит только своего пользователя
    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

    // Установить пользователя для текущего потока
    public static void setUser(User user) {
        currentUser.set(user);
    }

    /** Получить текущего пользователя */
    public static User getUser() {
        return currentUser.get();
    }

    /** Очистить после обработки запроса (важно для пула потоков!) */
    public static void clear() {
        currentUser.remove();
    }
}