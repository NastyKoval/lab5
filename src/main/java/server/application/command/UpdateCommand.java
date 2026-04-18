package server.application.command;

import client.presentation.application.request.UpdateRequest;
import client.presentation.application.request.Request;
import server.application.response.Response;
import server.application.service.FlatService;
import domain.model.Flat;

/**
 * Команда обновления элемента по ID.
 *
 * <p>Имя команды: "update"</p>
 *
 */
public class UpdateCommand implements CommandHandler {

    private final FlatService flatService;

    public UpdateCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    /**
     * Выполняет команду обновления квартиры по ID.
     *
     * @param request запрос содержащий ID и новые данные квартиры
     * @return результат выполнения (успех или ошибка)
     */
    @Override
    public Response handle(Request request) {
        try {
            // Преобразуем Request в UpdateRequest
            UpdateRequest updateRequest = (UpdateRequest) request;

            // Получаем ID и новые данные из запроса
            int id = updateRequest.getId();
            Flat newFlat = updateRequest.getFlat();

            // Попытка обновить
            boolean updated = flatService.updateFlat(id, newFlat);

            if (updated) {
                return new Response(true, "Квартира с ID=" + id + " обновлена", null);
            } else {
                return new Response(false, "Квартира с ID=" + id + " не найдена", null);
            }

        } catch (ClassCastException e) {
            return new Response(false, "Ошибка: неверный тип запроса", null);
        } catch (IllegalArgumentException e) {
            return new Response(false, "Ошибка валидации: " + e.getMessage(), null);
        } catch (Exception e) {
            return new Response(false, "Ошибка при обновлении: " + e.getMessage(), null);
        }
    }

    /**
     * Получает имя команды.
     *
     * @return "update"
     */
    @Override
    public String getName() {
        return "update";
    }

    /**
     * Получает описание команды.
     *
     * @return описание что делает команда
     */
    @Override
    public String getDescription() {
        return "обновить значение элемента коллекции по его идентификатору";
    }
}