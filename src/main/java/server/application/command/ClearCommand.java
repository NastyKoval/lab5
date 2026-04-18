package server.application.command;

import client.presentation.application.request.Request;
import server.application.response.Response;
import server.application.service.FlatService;

/**
 * Команда очистки коллекции.
 *
 * <p>Имя команды: "clear"</p>
 *
 */
public class ClearCommand implements CommandHandler {

    private final FlatService flatService;

    public ClearCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    /**
     * Выполняет команду очистки коллекции.
     *
     * @param request запрос (не используется)
     * @return результат выполнения (успех)
     */
    @Override
    public Response handle(Request request) {
        try {
            // Очищаем коллекцию через сервис
            flatService.clear();

            // Возвращаем true
            return new Response(true, "Коллекция очищена", null);

        } catch (Exception e) {
            return new Response(false, "Ошибка при очистке: " + e.getMessage(), null);
        }
    }

    /**
     * Получает имя команды.
     *
     * @return "clear"
     */
    @Override
    public String getName() {
        return "clear";
    }

    /**
     * Получает описание команды.
     *
     * @return описание что делает команда
     */
    @Override
    public String getDescription() {
        return "очистить коллекцию";
    }
}