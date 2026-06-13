package server.application.command;

import common.domain.model.Flat;
import common.request.Request;
import common.response.Response;
import server.application.service.FlatService;

public class AddIfMinCommand implements CommandHandler {
    private final FlatService flatService;

    public AddIfMinCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    @Override
    public Response handle(Request request) {
        try {
            Flat flat = (Flat) request.getData();
            if (flat == null) {
                return new Response(false, "Данные квартиры не переданы", null);
            }

            boolean added = flatService.addIfMin(flat);

            return added
                    ? new Response(true, "Элемент добавлен (его значение меньше минимального)", null)
                    : new Response(false, "Элемент не добавлен (его значение не меньше минимального)", null);

        } catch (ClassCastException e) {
            return new Response(false, "Неверный тип данных. Ожидается Flat", null);
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }

    @Override
    public String getName() { return "add_if_min"; }

    @Override
    public String getDescription() {
        return "добавить новый элемент в коллекцию, если его значение меньше значения наименьшего элемента";
    }
}