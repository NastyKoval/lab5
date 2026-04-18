package server.application.command;

import client.presentation.application.request.Request;
import server.application.response.Response;
import server.application.service.FlatService;

/**
 * Команда удаления первого элемента из коллекции.
 *
 * <p>Имя команды: "remove_first"</p>
 *
 */
public class RemoveFirstCommand implements CommandHandler {

    private final FlatService flatService;

    /**
     * Конструктор команды удаления первого элемента.
     *
     * @param flatService сервис для работы с квартирами
     */
    public RemoveFirstCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    /**
     * Выполняет команду удаления первого элемента.
     *
     * @param request запрос (не используется)
     * @return результат выполнения (успех или ошибка)
     */
    @Override
    public Response handle(Request request) {
        try {
            // Пытаемся удалить первый элемент
            boolean removed = flatService.removeFirst();

            // Проверяем результат
            if (removed) {
                return new Response(true, "Первый элемент удалён", null);
            } else {
                return new Response(false, "Коллекция пуста, нечего удалять", null);
            }

        } catch (Exception e) {
            return new Response(false, "Ошибка при удалении первого элемента: " + e.getMessage(), null);
        }
    }

    /**
     * Получает имя команды.
     *
     * @return "remove_first"
     */
    @Override
    public String getName() {
        return "remove_first";
    }

    /**
     * Получает описание команды.
     *
     * @return описание что делает команда
     */
    @Override
    public String getDescription() {
        return "удалить первый элемент из коллекции";
    }
}