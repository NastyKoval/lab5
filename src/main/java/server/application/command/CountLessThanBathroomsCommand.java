package server.application.command;

import client.presentation.application.request.CountLessThanBathroomsRequest;
import client.presentation.application.request.Request;
import server.application.response.Response;
import server.application.service.FlatService;

/**
 * Команда подсчёта элементов с numberOfBathrooms меньше заданного.
 *
 * <p>Имя команды: "count_less_than_number_of_bathrooms"</p>
 *
 */
public class CountLessThanBathroomsCommand implements CommandHandler {

    private final FlatService flatService;

    /**
     * Конструктор команды подсчёта.
     *
     * @param flatService сервис для работы с квартирами
     */
    public CountLessThanBathroomsCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    /**
     * Выполняет команду подсчёта квартир с numberOfBathrooms меньше заданного.
     *
     * @param request запрос содержащий пороговое значение numberOfBathrooms
     * @return результат выполнения с количеством квартир
     */
    @Override
    public Response handle(Request request) {
        try {

            CountLessThanBathroomsRequest countRequest = (CountLessThanBathroomsRequest) request;

            // Значение из запроса
            long threshold = countRequest.getNumberOfBathrooms();

            // Считаем количество квартир
            long count = flatService.countLessThanBathrooms(threshold);

            // Возвращаем результат
            return new Response(true, "Найдено квартир с numberOfBathrooms меньше " + threshold + ": " + count, null);

        } catch (ClassCastException e) {
            return new Response(false, "Ошибка: неверный тип запроса", null);
        } catch (Exception e) {
            return new Response(false, "Ошибка при подсчёте: " + e.getMessage(), null);
        }
    }

    /**
     * Получает имя команды.
     *
     * @return "count_less_than_number_of_bathrooms"
     */
    @Override
    public String getName() {
        return "count_less_than_number_of_bathrooms";
    }

    /**
     * Получает описание команды.
     *
     * @return описание что делает команда
     */
    @Override
    public String getDescription() {
        return "вывести количество элементов, значение поля numberOfBathrooms которых меньше заданного";
    }
}