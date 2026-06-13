package common.request;

import common.domain.model.User;
import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CommandType type;
    private final String[] arguments; // Простые данные: ID, числа, строки
    private final Serializable data;  // Сложные объекты: Flat, House
    private final User user;

    public Request(CommandType type, String[] arguments, Serializable data, User user) {
        this.type = type;
        this.arguments = arguments;
        this.data = data;
        this.user = user;
    }

    public CommandType getType() { return type; }
    public String[] getArguments() { return arguments; }
    public Serializable getData() { return data; }
    public User getUser() { return user; }

    @Override
    public String toString() {
        return "Request{type=" + type + ", user=" + (user != null ? user.getLogin() : "guest") + "}";
    }
}