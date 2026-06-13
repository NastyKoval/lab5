package server.application.command;

import common.request.Request;
import common.response.Response;
import server.application.service.FlatService;

public class SaveCommand implements CommandHandler {
    private final FlatService flatService;
    public SaveCommand(FlatService flatService) { this.flatService = flatService; }

    @Override
    public Response handle(Request request) {
        try {
            flatService.save();
            return new Response(true, "Коллекция сохранена в БД", null);
        } catch (Exception e) {
            return new Response(false, "Ошибка сохранения: " + e.getMessage(), null);
        }
    }
    @Override public String getName() { return "save"; }
    @Override public String getDescription() { return "сохранить коллекцию в базу данных"; }
}