package server.application.command;

import common.request.Request;
import common.response.Response;
import server.application.service.FlatService;

public class ExecuteScriptCommand implements CommandHandler {
    private final FlatService flatService;

    public ExecuteScriptCommand(FlatService flatService) {
        this.flatService = flatService;
    }

    @Override
    public Response handle(Request request) {
        try {
            String[] args = request.getArguments();
            if (args == null || args.length == 0) {
                return new Response(false, "Укажите путь к файлу скрипта", null);
            }

            String path = args[0];
            boolean success = flatService.executeScript(path);

            return success
                    ? new Response(true, "Скрипт выполнен успешно", null)
                    : new Response(false, "Ошибка выполнения скрипта", null);

        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }

    @Override public String getName() { return "execute_script"; }
    @Override public String getDescription() { return "считать и исполнить скрипт из указанного файла"; }
}