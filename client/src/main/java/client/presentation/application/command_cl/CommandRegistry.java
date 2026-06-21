package client.presentation.application.command_cl;

import common.domain.model.User;
import common.request.CommandType;
import common.request.Request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Реестр команд на стороне клиента.
 * Создаёт универсальные Request объекты.
 */
public class CommandRegistry {

    private final Map<String, CommandType> commandMap;
    private String currentLogin = "";      // Для авторизации
    private String currentPassword = "";
    private User currentUser = null;

    public CommandRegistry() {
        this.commandMap = new HashMap<>();
        registerAllCommands();
    }

    /**
     * Устанавливает текущего пользователя (для авторизации).
     */
    public void setCurrentUser(String login, String password) {
        this.currentLogin = login;
        this.currentPassword = password;
    }

    /**
     * Устанавливает текущего пользователя (объект User).
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.currentLogin = user.getLogin();
    }

    /**
     * Регистрирует все команды (только имена и типы).
     */
    private void registerAllCommands() {
        // Команды без аргументов
        commandMap.put("help", CommandType.HELP);
        commandMap.put("info", CommandType.INFO);
        commandMap.put("show", CommandType.SHOW);
        commandMap.put("clear", CommandType.CLEAR);
        commandMap.put("remove_first", CommandType.REMOVE_FIRST);
        commandMap.put("remove_head", CommandType.REMOVE_HEAD);
        commandMap.put("print_field_descending_furnish", CommandType.PRINT_FIELD_DESCENDING_FURNISH);
        commandMap.put("save", CommandType.SAVE);
        commandMap.put("exit", CommandType.EXIT);

        // Команды с аргументами
        commandMap.put("add", CommandType.ADD);
        commandMap.put("add_if_min", CommandType.ADD_IF_MIN);
        commandMap.put("remove_by_id", CommandType.REMOVE_BY_ID);
        commandMap.put("update", CommandType.UPDATE);
        commandMap.put("count_less_than_number_of_bathrooms", CommandType.COUNT_LESS_THAN_NUMBER_OF_BATHROOMS);
        commandMap.put("filter_greater_than_house", CommandType.FILTER_GREATER_THAN_HOUSE);
        commandMap.put("execute_script", CommandType.EXECUTE_SCRIPT);
        commandMap.put("filter_less_than_furnish", CommandType.FILTER_LESS_THAN_FURNISH);

        // Авторизация пользователя
        commandMap.put("login", CommandType.LOGIN);
        commandMap.put("register", CommandType.REGISTER);
    }

    /**
     * Проверяет существует ли команда.
     */
    public boolean hasCommand(String commandName) {
        return commandMap.containsKey(commandName);
    }

    /**
     * Получает тип команды.
     */
    public CommandType getCommandType(String commandName) {
        CommandType type = commandMap.get(commandName);
        if (type == null) {
            throw new IllegalArgumentException("Неизвестная команда: " + commandName);
        }
        return type;
    }

    /**
     * Создаёт Request объект на основе имени команды и аргументов.
     */
    public Request buildRequest(String commandName, Map<String, Object> args) {
        CommandType type = getCommandType(commandName);

        // Определяем простые аргументы (String[]) и сложные данные (Serializable)
        String[] arguments = null;
        Serializable data = null;

        // Извлекаем аргументы в зависимости от типа команды
        if (type.requiresArguments()) {
            // Простые аргументы (числа, строки) → в массив
            if (args != null && !args.isEmpty()) {
                // Берём первый попавшийся аргумент как строку
                Object arg = args.values().iterator().next();
                arguments = new String[]{arg.toString()};
            }
        }

        if (type.requiresData()) {
            // Сложные объекты (Flat, House) → в data
            if (args != null) {
                if (args.containsKey("flat")) {
                    data = (Serializable) args.get("flat");
                } else if (args.containsKey("house")) {
                    data = (Serializable) args.get("house");
                }
            }
        }

        // Вместо new AddRequest(flat) → new Request(CommandType, String[], Serializable, User)
        return new Request(type, arguments, data, currentUser);
    }
}