package server.application.command;

import server.application.response.Response;
import server.application.service.FlatService;
import client.presentation.application.request.Request;

/**
 * Команда save — сохранение коллекции в файл.
 */
public class SaveCommand implements CommandHandler {

    private final FlatService flatService;

    public SaveCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    @Override
    public Response handle(Request request) {
        try {
            // Данные автоматически сохраняются при каждом изменении
            // через FlatRepositoryImpl.saveToFile()
            // Поэтому просто подтверждаем сохранение

            return new Response(true, "Коллекция сохранена в файл", null);

        } catch (Exception e) {
            return new Response(false, "Ошибка при сохранении: " + e.getMessage(), null);
        }
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "сохранить коллекцию в файл";
    }
}