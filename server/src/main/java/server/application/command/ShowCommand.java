package server.application.command;

import common.request.Request;
import common.response.Response;
import server.application.service.FlatService;

public class ShowCommand implements CommandHandler {
    private final FlatService flatService;
    public ShowCommand(FlatService flatService) { this.flatService = flatService; }

    @Override
    public Response handle(Request request) {
        try {
            return new Response(true, null, flatService.getCollection());
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }
    @Override public String getName() { return "show"; }
    @Override public String getDescription() { return "вывести все элементы коллекции"; }
}