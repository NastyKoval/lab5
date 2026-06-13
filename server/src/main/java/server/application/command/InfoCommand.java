package server.application.command;

import common.request.Request;
import common.response.Response;
import server.application.service.FlatService;

public class InfoCommand implements CommandHandler {
    private final FlatService flatService;
    public InfoCommand(FlatService flatService) { this.flatService = flatService; }

    @Override
    public Response handle(Request request) {
        try {
            return new Response(true, null, flatService.getInfo());
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }
    @Override public String getName() { return "info"; }
    @Override public String getDescription() { return "вывести информацию о коллекции"; }
}