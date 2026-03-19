package application.request;

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
}