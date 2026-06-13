package client.presentation.application.command_cl;

import common.request.CommandType;

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
     * Регистрирует все команды (только имена и типы).
     */
    private void registerAllCommands() {
        // Команды без аргументов
        commandMap.put("help", CommandType.NO_ARGS);
        commandMap.put("info", CommandType.NO_ARGS);
        commandMap.put("show", CommandType.NO_ARGS);
        commandMap.put("clear", CommandType.NO_ARGS);
        commandMap.put("remove_first", CommandType.NO_ARGS);
        commandMap.put("remove_head", CommandType.NO_ARGS);
        commandMap.put("print_field_descending_furnish", CommandType.NO_ARGS);

        // Команды с аргументами
        commandMap.put("add", CommandType.FLAT_ARG);
        commandMap.put("add_if_min", CommandType.FLAT_ARG);
        commandMap.put("remove_by_id", CommandType.ID_ARG);
        commandMap.put("update", CommandType.ID_AND_FLAT);
        commandMap.put("count_less_than_number_of_bathrooms", CommandType.LONG_ARG);
        commandMap.put("filter_greater_than_house", CommandType.HOUSE_ARG);
        commandMap.put("execute_script", CommandType.STRING_ARG);
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
    public RequestNew buildRequest(String commandName, Map<String, Object> args) {
        CommandType type = getCommandType(commandName);
        Object arguments = extractArguments(type, args);

        // Вместо new AddRequest(flat) → new Request("add", flat, login, password)
        return new RequestNew(commandName, arguments, currentLogin, currentPassword);
    }

    /**
     * Извлекает аргументы в зависимости от типа команды.
     */
    private Object extractArguments(CommandType type, Map<String, Object> args) {
        return switch (type) {
            case NO_ARGS -> null;
            case ID_ARG -> args.get("id");
            case FLAT_ARG -> args.get("flat");
            case ID_AND_FLAT -> {
                // Для update нужен Map с id и flat
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("id", args.get("id"));
                updateData.put("flat", args.get("flat"));
                yield updateData;
            }
            case LONG_ARG -> args.get("value");
            case HOUSE_ARG -> args.get("house");
            case STRING_ARG -> args.get("fileName");
            default -> null;
        };
    }


}