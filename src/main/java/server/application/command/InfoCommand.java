package server.application.command;

import client.presentation.application.request.Request;
import server.application.response.Response;
import server.application.service.FlatService;

/**
 * Команда показа информации о коллекции.
 *
 * <p>Имя команды: "info"</p>
 *
 * @author Student
 * @version 1.0
 */
public class InfoCommand implements CommandHandler {

    private final FlatService flatService;

    /**
     * Конструктор команды info.
     *
     * @param flatService сервис для работы с квартирами
     */
    public InfoCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    /**
     * Выполняет команду показа информации о коллекции.
     *
     * @param request запрос (не используется)
     * @return результат выполнения с информацией о коллекции
     */
    @Override
    public Response handle(Request request) {
        try {
            // Получаем информацию из сервиса
            String info = flatService.getInfo();

            // Возвращаем результат
            return new Response(true, info, null);

        } catch (Exception e) {
            return new Response(false, "Ошибка при получении информации: " + e.getMessage(), null);
        }
    }

    /**
     * Получает имя команды.
     *
     * @return "info"
     */
    @Override
    public String getName() {
        return "info";
    }

    /**
     * Получает описание команды.
     *
     * @return описание что делает команда
     */
    @Override
    public String getDescription() {
        return "вывести в стандартный поток вывода информацию о коллекции";
    }
}