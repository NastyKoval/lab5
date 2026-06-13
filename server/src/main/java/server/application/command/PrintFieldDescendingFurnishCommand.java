package server.application.command;

import common.request.Request;
import common.response.Response;
import server.application.service.FlatService;

public class PrintFieldDescendingFurnishCommand implements CommandHandler {
    private final FlatService flatService;
    public PrintFieldDescendingFurnishCommand(FlatService flatService) { this.flatService = flatService; }

    @Override
    public Response handle(Request request) {
        try {
            return new Response(true, null, flatService.printFieldDescendingFurnish());
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }
    @Override public String getName() { return "print_field_descending_furnish"; }
    @Override public String getDescription() { return "вывести значения поля furnish по убыванию"; }
}