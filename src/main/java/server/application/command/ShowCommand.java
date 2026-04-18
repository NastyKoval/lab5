package server.application.command;

import client.presentation.application.request.Request;
import server.application.response.Response;
import server.application.service.FlatService;
import domain.model.Flat;

import java.util.List;

/**
 * Команда показа всех элементов коллекции.
 *
 * <p>Имя команды: "show"</p>
 *
 */
public class ShowCommand implements CommandHandler {

    private final FlatService flatService;

    /**
     * Конструктор команды показа.
     *
     * @param flatService сервис для работы с квартирами
     */
    public ShowCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    /**
     * Выполняет команду показа всех квартир.
     *
     * @param request запрос (не используется, так как команда не требует параметров)
     * @return результат выполнения со списком всех квартир
     */
    @Override
    public Response handle(Request request) {
        try {
            // Получаем все квартиры из сервиса
            List<Flat> flats = flatService.getAllFlats();

            if (flats.isEmpty()) {
                return new Response(true, "Коллекция пуста", null);
            }

            // Преобразуем список в строку для вывода
            StringBuilder output = new StringBuilder();
            for (Flat flat : flats) {
                output.append(flat).append("\n");
            }

            return new Response(true, output.toString(), null);

        } catch (Exception e) {
            return new Response(false, "Ошибка при выводе: " + e.getMessage(), null);
        }
    }

    /**
     * Получает имя команды.
     *
     * @return "show"
     */
    @Override
    public String getName() {
        return "show";
    }

    /**
     * Получает описание команды.
     *
     * @return описание что делает команда
     */
    @Override
    public String getDescription() {
        return "вывести все элементы коллекции в строковом представлении";
    }
}