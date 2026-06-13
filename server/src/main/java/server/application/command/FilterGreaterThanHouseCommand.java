package server.application.command;

import common.domain.model.House;
import common.request.Request;
import common.response.Response;
import server.application.service.FlatService;

public class FilterGreaterThanHouseCommand implements CommandHandler {
    private final FlatService flatService;

    public FilterGreaterThanHouseCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    @Override
    public Response handle(Request request) {
        try {
            House house = (House) request.getData();
            if (house == null) {
                return new Response(false, "Данные дома не переданы", null);
            }

            var result = flatService.filterGreaterThanHouse(house);
            return new Response(true, null, result);

        } catch (ClassCastException e) {
            return new Response(false, "Неверный тип данных. Ожидается House", null);
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }

    @Override
    public String getName() { return "filter_greater_than_house"; }

    @Override
    public String getDescription() {
        return "вывести элементы, значение поля house которых больше заданного";
    }
}