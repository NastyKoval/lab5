package client.presentation.application.command_cl;

import client.presentation.application.request.*;
import domain.model.Flat;
import domain.model.House;

import java.util.HashMap;
import java.util.Map;

/**
 * Реестр команд на стороне клиента.
 * Отвечает за создание Request объектов.
 */
public class CommandRegistry {

    private final Map<String, Request> commandMap;

    /**
     * Конструктор.
     */
    public CommandRegistry() {
        this.commandMap = new HashMap<>();
        registerAllCommands();
    }

    /**
     * Регистрирует все команды как шаблоны.
     */
    private void registerAllCommands() {
        // Команды без аргументов
        commandMap.put("help", new HelpRequest());
        commandMap.put("info", new InfoRequest());
        commandMap.put("show", new ShowRequest());
        commandMap.put("clear", new ClearRequest());
        commandMap.put("save", new SaveRequest());
        commandMap.put("exit", new ExitRequest());
        commandMap.put("remove_first", new RemoveFirstRequest());
        commandMap.put("remove_head", new RemoveHeadRequest());
        commandMap.put("print_field_descending_furnish", new PrintFieldDescendingFurnishRequest());

        // Команды с аргументами (шаблоны с null)
        commandMap.put("add", new AddRequest(null));
        commandMap.put("add_if_min", new AddIfMinRequest(null));
        commandMap.put("remove_by_id", new RemoveRequest(0));
        commandMap.put("update", new UpdateRequest(0, null));
        commandMap.put("count_less_than_number_of_bathrooms", new CountLessThanBathroomsRequest(0L));
        commandMap.put("filter_greater_than_house", new FilterGreaterThanHouseRequest(null));
        commandMap.put("execute_script", new ExecuteScriptRequest(""));
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
    public CommandTypeEnum getCommandType(String commandName) {
        Request template = commandMap.get(commandName);
        if (template == null) {
            throw new IllegalArgumentException("Неизвестная команда: " + commandName);
        }
        return template.getCommandType();
    }

    /**
     * Создаёт Request объект на основе имени команды и аргументов.
     */
    public Request buildRequest(String commandName, Map<String, Object> args) {
        CommandTypeEnum type = getCommandType(commandName);

        switch (type) {
            case NO_ARGS:
                return createNoArgsRequest(commandName);

            case ID_ARG:
                Integer id = (Integer) args.get("id");
                return new RemoveRequest(id);

            case FLAT_ARG:
                Flat flat = (Flat) args.get("flat");
                if ("add".equals(commandName)) {
                    return new AddRequest(flat);
                } else {
                    return new AddIfMinRequest(flat);
                }

            case ID_AND_FLAT:
                Integer updateId = (Integer) args.get("id");
                Flat updateFlat = (Flat) args.get("flat");
                return new UpdateRequest(updateId, updateFlat);

            case LONG_ARG:
                Long value = (Long) args.get("value");
                return new CountLessThanBathroomsRequest(value);

            case HOUSE_ARG:
                House house = (House) args.get("house");
                return new FilterGreaterThanHouseRequest(house);

            case STRING_ARG:
                String fileName = (String) args.get("fileName");
                return new ExecuteScriptRequest(fileName);

            default:
                throw new IllegalArgumentException("Неизвестный тип команды: " + type);
        }
    }

    /**
     * Создаёт запрос без аргументов.
     */
    private Request createNoArgsRequest(String commandName) {
        return switch (commandName) {
            case "show" -> new ShowRequest();
            case "help" -> new HelpRequest();
            case "info" -> new InfoRequest();
            case "clear" -> new ClearRequest();
            case "save" -> new SaveRequest();
            case "exit" -> new ExitRequest();
            case "remove_first" -> new RemoveFirstRequest();
            case "remove_head" -> new RemoveHeadRequest();
            case "print_field_descending_furnish" -> new PrintFieldDescendingFurnishRequest();
            default -> throw new IllegalArgumentException("Неизвестная команда: " + commandName);
        };
    }
}