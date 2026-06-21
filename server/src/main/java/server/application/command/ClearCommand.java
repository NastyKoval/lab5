package server.application.command;

import common.domain.model.User;
import common.request.Request;
import common.response.Response;
import server.application.context.UserContext;
import server.application.service.FlatService;

/**
 * Обработчик команды clear.
 * Очищает только объекты текущего пользователя.
 */
public class ClearCommand implements CommandHandler {

    private final FlatService flatService;

    public ClearCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    @Override
    public Response handle(Request request) {
        try {
            // Получаем текущего пользователя
            User currentUser = UserContext.getUser();

            if (currentUser == null) {
                return new Response(false, "Пользователь не авторизован", null);
            }
            //Удаляем только объекты текущего пользователя
            int removedCount = flatService.removeFlatsByOwnerId(currentUser.getId());

            return new Response(true, "Коллекция очищена (удалено объектов: " + removedCount + ")", null);

        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "очистить коллекцию (только ваши объекты)";
    }
}