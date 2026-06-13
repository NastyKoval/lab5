package server.application.command;

import common.request.Request;
import common.response.Response;
import server.application.service.FlatService;

public class RemoveFirstCommand implements CommandHandler {
    private final FlatService flatService;
    public RemoveFirstCommand(FlatService flatService) { this.flatService = flatService; }

    @Override
    public Response handle(Request request) {
        try {
            flatService.removeFirst();
            return new Response(true, "Первый элемент удалён", null);
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }
    @Override public String getName() { return "remove_first"; }
    @Override public String getDescription() { return "удалить первый элемент коллекции"; }
}