package application.command;

import application.request.Request;
import application.response.Response;

/**
 * Интерфейс CommandHandler определяет контракт для всех команд.
 */
public interface CommandHandler {

    /**
     * Выполняет команду с переданным запросом.
     * @param request запрос содержащий данные для выполнения команды
     * @return результат выполнения команды (успех или ошибка)
     */
    Response handle(Request request);

    String getName();

    /**
     * Получает описание команды.
     *
     * @return краткое описание что делает команда
     */
    default String getDescription() {
        return "Нет описания";
    }
}