package server.application.command;

import common.request.Request;
import common.response.Response;
import server.application.service.FlatService;

public class ClearCommand implements CommandHandler {
    private final FlatService flatService;
    public ClearCommand(FlatService flatService) { this.flatService = flatService; }

    @Override
    public Response handle(Request request) {
        try {
            flatService.clear();
            return new Response(true, "Коллекция очищена", null);
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }
    @Override public String getName() { return "clear"; }
    @Override public String getDescription() { return "очистить коллекцию"; }
}