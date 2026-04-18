package server.application.command;

import client.presentation.application.request.AddIfMinRequest;
import client.presentation.application.request.Request;
import server.application.response.Response;
import server.application.service.FlatService;
import domain.model.Flat;

/**
 * Команда добавления элемента если его значение меньше минимального.
 *
 * <p>Имя команды: "add_if_min"</p>
 *
 */
public class AddIfMinCommand implements CommandHandler {

    private final FlatService flatService;

    /**
     * Конструктор команды add_if_min.
     *
     * @param flatService сервис для работы с квартирами
     */
    public AddIfMinCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    /**
     * Выполняет команду добавления если элемент меньше минимального.
     *
     * @param request запрос содержащий квартиру для добавления
     * @return результат выполнения (добавлена или нет)
     */
    @Override
    public Response handle(Request request) {
        try {

            AddIfMinRequest addIfMinRequest = (AddIfMinRequest) request;

            // Получаем квартиру из запроса
            Flat flat = addIfMinRequest.getFlat();

            boolean added = flatService.addIfMin(flat);


            if (added) {
                return new Response(true, "Квартира добавлена (меньше минимальной)", null);
            } else {
                return new Response(false, "Квартира не добавлена (не меньше минимальной)", null);
            }

        } catch (ClassCastException e) {
            return new Response(false, "Ошибка: неверный тип запроса",null);
        } catch (IllegalArgumentException e) {
            return new Response(false, "Ошибка валидации: " + e.getMessage(), null);
        } catch (Exception e) {
            return new Response(false, "Ошибка при добавлении: " + e.getMessage(), null);
        }
    }

    /**
     * Получает имя команды.
     *
     * @return "add_if_min"
     */
    @Override
    public String getName() {
        return "add_if_min";
    }

    /**
     * Получает описание команды.
     *
     * @return описание что делает команда
     */
    @Override
    public String getDescription() {
        return "добавить новый элемент в коллекцию если его значение меньше значения наименьшего элемента";
    }
}