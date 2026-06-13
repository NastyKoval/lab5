package server.application.command;

import common.request.Request;
import common.response.Response;
import server.application.service.FlatService;

public class CountLessThanBathroomsCommand implements CommandHandler {
    private final FlatService flatService;

    public CountLessThanBathroomsCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    @Override
    public Response handle(Request request) {
        try {
            String[] args = request.getArguments();
            if (args == null || args.length == 0) {
                return new Response(false, "Укажите количество ванных комнат", null);
            }

            long value = Long.parseLong(args[0]);
            long count = flatService.countLessThanBathrooms(value);

            return new Response(true, "Количество: " + count, null);

        } catch (NumberFormatException e) {
            return new Response(false, "Неверный формат числа", null);
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }

    @Override
    public String getName() {
        return "count_less_than_number_of_bathrooms";
    }

    @Override
    public String getDescription() {
        return "вывести количество элементов меньше заданного";
    }
}