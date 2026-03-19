package application.command;

import application.request.AddRequest;
import application.request.Request;
import application.response.Response;
import application.service.FlatService;

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
 * @author Student
 * @version 1.0
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
            // Преобразуем Request в AddRequest
            AddRequest addRequest = (AddRequest) request;

            // Получаем квартиру из запроса
            var flat = addRequest.getFlat();

            // Добавляем через сервис
            flatService.addFlat(flat);

            // Возвращаем успех
            return new Response(true, "Добавлено с ID=" + flat.getId());

        } catch (ClassCastException e) {
            // Если передан не AddRequest
            return new Response(false, "Ошибка: неверный тип запроса");

        } catch (IllegalArgumentException e) {
            // Если данные невалидны
            return new Response(false, "Ошибка: " + e.getMessage());

        } catch (Exception e) {
            // Любая другая ошибка
            return new Response(false, "Ошибка при добавлении: " + e.getMessage());
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