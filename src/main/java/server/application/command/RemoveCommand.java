package server.application.command;

import client.presentation.application.request.RemoveRequest;
import client.presentation.application.request.Request;
import server.application.response.Response;
import server.application.service.FlatService;

/**
 * Команда удаления элемента по ID.
 *
 * <p>Имя команды: "remove_by_id"</p>
 *
 */
public class RemoveCommand implements CommandHandler {

    private final FlatService flatService;

    public RemoveCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    /**
     * Выполняет команду удаления квартиры по ID.
     *
     * @param request запрос содержащий ID квартиры для удаления
     * @return результат выполнения (успех или ошибка)
     */
    @Override
    public Response handle(Request request) {
        try {
            // Преобразуем Request в RemoveRequest
            RemoveRequest removeRequest = (RemoveRequest) request;

            // Получаем ID из запроса
            int id = removeRequest.getId();

            // удалить
            boolean removed = flatService.removeById(id);

            // Проверяем результат
            if (removed) {
                return new Response(true, "Квартира с ID=" + id + " удалена", null);
            } else {
                return new Response(false, "Квартира с ID=" + id + " не найдена", null);
            }

        } catch (ClassCastException e) {
            return new Response(false, "Ошибка: неверный тип запроса", null);
        } catch (Exception e) {
            return new Response(false, "Ошибка при удалении: " + e.getMessage(), null);
        }
    }

    /**
     * Получает имя команды.
     *
     * @return "remove_by_id"
     */
    @Override
    public String getName() {
        return "remove_by_id";
    }

    /**
     * Получает описание команды.
     *
     * @return описание что делает команда
     */
    @Override
    public String getDescription() {
        return "удалить элемент из коллекции по его идентификатору";
    }
}