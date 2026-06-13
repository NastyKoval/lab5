package server.application.command;

import common.request.Request;
import common.response.Response;
import server.application.service.FlatService;
import common.domain.model.Flat;

public class AddCommand implements CommandHandler {

    private final FlatService flatService;

    public AddCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    @Override
    public Response handle(Request request) {
        try {
            // Достаем объект Flat из поля data
            Flat flat = (Flat) request.getData();

            if (flat == null) {
                return new Response(false, "Данные квартиры не переданы", null);
            }

            flatService.addFlat(flat);

            return new Response(true, "Добавлено с ID=" + flat.getId(), null);

        } catch (ClassCastException e) {
            return new Response(false, "Ошибка типа данных", null);
        } catch (IllegalArgumentException e) {
            return new Response(false, "Ошибка валидации: " + e.getMessage(), null);
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }

    @Override
    public String getName() { return "add"; }
    @Override
    public String getDescription() { return "добавить новый элемент"; }
}