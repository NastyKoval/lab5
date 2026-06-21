package common.domain.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Пользователь системы.
 */
public class User implements Serializable {

    private final int id;
    private final String login;
    private final String passwordHash;

    private static final long serialVersionUID = 1L;

    /**
     * Конструктор.
     *
     * @param id идентификатор пользователя
     * @param login логин (уникальный)
     * @param passwordHash хеш пароля
     */
    public User(int id, String login, String passwordHash) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Логин не может быть пустым");
        }
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        this.id = id;
        this.login = login.trim();
        this.passwordHash = passwordHash.trim();
    }


    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                '}';
    }
}