package server.application.command;

import client.presentation.application.request.AddRequest;
import client.presentation.application.request.Request;
import server.application.response.Response;
import server.application.service.FlatService;

/**
 * Команда добавления нового элемента (квартиры) в коллекцию.
 *
 * <p>Имя команды: "add"</p>
 *
 * <p>Пример использования:</p>
 * <pre>
 *     add {element}
 * </pre>
 *
 */
public class AddCommand implements CommandHandler {

    private final FlatService flatService;

    /**
     * Конструктор команды добавления.
     *
     * @param flatService сервис для работы с квартирами
     */
    public AddCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    /**
     * Выполняет команду добавления квартиры.
     *
     * @param request запрос содержащий данные квартиры для добавления
     * @return результат выполнения (успех или ошибка)
     */
    @Override
    public Response handle(Request request) {
        try {
            // Преобразуем Request в AddRequest(приведение типа())
            AddRequest addRequest = (AddRequest) request;

            // Получаем квартиру из запроса
            var flat = addRequest.getFlat();

            // Добавляем через сервис
            flatService.addFlat(flat);

            // Возвращаем успех
            return new Response(true, "Добавлено с ID=" + flat.getId(), null);

        } catch (ClassCastException e) {
            // Если передан не AddRequest
            return new Response(false, "Ошибка: неверный тип запроса", null);

        } catch (IllegalArgumentException e) {
            // Если данные невалидны
            return new Response(false, "Ошибка: " + e.getMessage(), null);

        } catch (Exception e) {
            // Любая другая ошибка
            return new Response(false, "Ошибка при добавлении: " + e.getMessage(), null);
        }
    }

    /**
     * Получает имя команды.
     *
     * @return "add"
     */
    @Override
    public String getName() {
        return "add";
    }

    /**
     * Получает описание команды.
     *
     * @return описание что делает команда
     */
    @Override
    public String getDescription() {
        return "добавить новый элемент в коллекцию";
    }
}