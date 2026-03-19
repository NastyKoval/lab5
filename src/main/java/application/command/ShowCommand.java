package application.command;

import application.request.Request;
import application.response.Response;
import application.service.FlatService;
import domain.model.Flat;

import java.util.List;

/**
 * Команда показа всех элементов коллекции.
 *
 * <p>Имя команды: "show"</p>
 *
 * @author Student
 * @version 1.0
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
                return new Response(true, "Коллекция пуста");
            }

            // Преобразуем список в строку для вывода
            StringBuilder output = new StringBuilder();
            for (Flat flat : flats) {
                output.append(flat).append("\n");
            }

            return new Response(true, output.toString());

        } catch (Exception e) {
            return new Response(false, "Ошибка при выводе: " + e.getMessage());
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