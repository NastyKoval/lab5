package server.application.command;

import common.request.Request;
import common.response.Response;
import server.application.service.FlatService;

public class RemoveCommand implements CommandHandler {

    private final FlatService flatService;

    public RemoveCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    @Override
    public Response handle(Request request) {
        try {
            // Достаем массив строк(потому что это простое значение)
            String[] args = request.getArguments();

            // Проверяем, что массив не пустой
            if (args == null || args.length == 0) {
                return new Response(false, "ID не указан", null);
            }

            // Превращаем строку "5" в число 5
            int id = Integer.parseInt(args[0]);

            boolean removed = flatService.removeById(id);

            return removed
                    ? new Response(true, "Квартира удалена", null)
                    : new Response(false, "Квартира не найдена или нет прав", null);

        } catch (NumberFormatException e) {
            return new Response(false, "Неверный формат ID (должно быть число)", null);
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }

    @Override
    public String getName() { return "remove_by_id"; }
    @Override
    public String getDescription() { return "удалить элемент по ID"; }
}