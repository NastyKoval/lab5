package server.application.command;

import common.request.Request;
import common.response.Response;
import server.application.service.FlatService;

public class HelpCommand implements CommandHandler {
    private final FlatService flatService;

    public HelpCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    @Override
    public Response handle(Request request) {
        return new Response(true, null, flatService.getHelpText());
    }

    @Override
    public String getName() { return "help"; }
    @Override
    public String getDescription() { return "вывести справку"; }
}