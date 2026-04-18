package client.presentation.application.request;
import client.presentation.application.command_cl.CommandTypeEnum;

/**
 * Базовый класс для всех запросов
 * Содержит общую информацию о запросе
 */
public abstract class Request {

    private final String commandName;

    public Request(String commandName) {
        this.commandName = commandName;
    }

    /**
     * Получить имя команды
     * @return имя команды (например "add")
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Получить аргументы запроса
     * @return данные запроса (Flat, Integer, или null)
     */
    public Object getArguments() {
        return null;
    }

    /**
     * Получить тип команды (какие аргументы нужны)
     * @return тип команды (NO_ARGS, ID_ARG, FLAT_ARG, ID_AND_FLAT)
     */
    public abstract CommandTypeEnum getCommandType();

}