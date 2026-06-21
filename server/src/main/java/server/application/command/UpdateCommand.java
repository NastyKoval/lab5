package server.application.command;

import common.request.Request;
import common.response.Response;
import server.application.service.FlatService;
import common.domain.model.Flat;

public class UpdateCommand implements CommandHandler {

    private final FlatService flatService;

    public UpdateCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    @Override
    public Response handle(Request request) {
        try {
            // Достаем ID из аргументов
            String[] args = request.getArguments();
            if (args == null || args.length == 0) {
                return new Response(false, "ID не указан", null);
            }
            int id = Integer.parseInt(args[0]);

            // Достаем новые данные квартиры из поля data
            Flat newFlat = (Flat) request.getData();
            if (newFlat == null) {
                return new Response(false, "Новые данные квартиры не переданы", null);
            }

            // ID пользователя, который выполняет запрос (для проверки прав)
            int requesterId = request.getUser().getId();

            boolean updated = flatService.updateFlat(id, newFlat, requesterId);

            return updated
                    ? new Response(true, "Квартира обновлена", null)
                    : new Response(false, "Квартира с таким ID не найдена", null);

        } catch (NumberFormatException e) {
            return new Response(false, "Неверный формат ID", null);
        } catch (ClassCastException e) {
            return new Response(false, "Ошибка типа данных", null);
        } catch (SecurityException e) {
            // объект существует, но принадлежит другому пользователю
            return new Response(false, e.getMessage(), null);
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }

    @Override
    public String getName() { return "update"; }
    @Override
    public String getDescription() { return "обновить элемент по ID"; }
}