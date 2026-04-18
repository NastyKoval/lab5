package server.application.command;

import client.presentation.application.request.Request;
import server.application.response.Response;
import server.application.service.FlatService;
import domain.model.Flat;

import java.util.Optional;

/**
 * Команда вывода и удаления первого элемента из коллекции.
 *
 * <p>Имя команды: "remove_head"</p>
 *
 */
public class RemoveHeadCommand implements CommandHandler {

    private final FlatService flatService;

    /**
     * Конструктор команды remove_head.
     *
     * @param flatService сервис для работы с квартирами
     */
    public RemoveHeadCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    /**
     * Выполняет команду вывода и удаления первого элемента.
     *
     * @param request запрос (не используется)
     * @return результат выполнения с удалённой квартирой или ошибкой
     */
    @Override
    public Response handle(Request request) {
        try {
            // Получаем и удаляем первый элемент
            Optional<Flat> removed = flatService.removeHead();

            // Проверяем результат
            if (removed.isPresent()) {
                Flat flat = removed.get();
                return new Response(true, "Удалён первый элемент:\n" + flat, null);
            } else {
                return new Response(false, "Коллекция пуста, нечего удалять", null);
            }

        } catch (Exception e) {
            return new Response(false, "Ошибка при удалении первого элемента: " + e.getMessage(), null);
        }
    }

    /**
     * Получает имя команды.
     *
     * @return "remove_head"
     */
    @Override
    public String getName() {
        return "remove_head";
    }

    /**
     * Получает описание команды.
     *
     * @return описание что делает команда
     */
    @Override
    public String getDescription() {
        return "вывести первый элемент из коллекции и удалить его";
    }
}