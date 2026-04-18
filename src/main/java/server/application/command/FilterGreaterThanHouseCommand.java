package server.application.command;

import client.presentation.application.request.FilterGreaterThanHouseRequest;
import client.presentation.application.request.Request;
import server.application.response.Response;
import server.application.service.FlatService;
import domain.model.Flat;
import domain.model.House;

import java.util.List;

/**
 * Команда фильтрации элементов по house (больше заданного).
 *
 * <p>Имя команды: "filter_greater_than_house"</p>
 *
 */
public class FilterGreaterThanHouseCommand implements CommandHandler {

    private final FlatService flatService;

    /**
     * Конструктор команды фильтрации.
     *
     * @param flatService сервис для работы с квартирами
     */
    public FilterGreaterThanHouseCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    /**
     * Выполняет команду фильтрации квартир по house.
     *
     * @param request запрос содержащий объект House для сравнения
     * @return результат выполнения с отфильтрованным списком
     */
    @Override
    public Response handle(Request request) {
        try {

            FilterGreaterThanHouseRequest filterRequest = (FilterGreaterThanHouseRequest) request;

            // House из запроса
            House house = filterRequest.getHouse();

            // Фильтруем квартиры
            List<Flat> filtered = flatService.filterGreaterThanHouse(house);

            // Проверяем результат
            if (filtered.isEmpty()) {
                return new Response(true, "Нет квартир с house больше заданного", null);
            }

            // Преобразуем список в строку для вывода
            StringBuilder output = new StringBuilder();
            for (Flat flat : filtered) {
                output.append(flat).append("\n");
            }

            // Возвращаем результат
            return new Response(true, output.toString(), null);

        } catch (ClassCastException e) {
            return new Response(false, "Ошибка: неверный тип запроса", null);
        } catch (Exception e) {
            return new Response(false, "Ошибка при фильтрации: " + e.getMessage(), null);
        }
    }

    /**
     * Получает имя команды.
     *
     * @return "filter_greater_than_house"
     */
    @Override
    public String getName() {
        return "filter_greater_than_house";
    }

    /**
     * Получает описание команды.
     *
     * @return описание что делает команда
     */
    @Override
    public String getDescription() {
        return "вывести элементы, значение поля house которых больше заданного";
    }
}